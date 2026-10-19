package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.EstadoProyecto;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Proyecto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProyectoDao implements CrudDao<Proyecto> {

    private static final String SELECT_BASE =
            "SELECT p.id_proyecto, p.id_lider, u.nombre AS lider, p.nombre, p.objetivo, "
            + "p.presupuesto_inicial, p.fecha_inicio, p.fecha_fin, p.estado "
            + "FROM proyectos p JOIN usuarios u ON u.id_usuario = p.id_lider ";

    private final ConexionDB conexionDB = ConexionDB.getInstancia();
    private final HitoDao hitoDao;

    public ProyectoDao(HitoDao hitoDao) {
        this.hitoDao = hitoDao;
    }

    /** Devuelve los proyectos con sus hitos y costos, listos para calcular indicadores. */
    @Override
    public List<Proyecto> listar() throws PersistenciaException {
        List<Proyecto> proyectos = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "ORDER BY p.id_proyecto");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                proyectos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los proyectos.", e);
        }
        for (Proyecto proyecto : proyectos) {
            cargarHitos(proyecto);
        }
        return proyectos;
    }

    @Override
    public Optional<Proyecto> buscarPorId(int id) throws PersistenciaException {
        Proyecto proyecto = null;
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE p.id_proyecto = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proyecto = mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el proyecto.", e);
        }
        if (proyecto != null) {
            cargarHitos(proyecto);
        }
        return Optional.ofNullable(proyecto);
    }

    @Override
    public void insertar(Proyecto proyecto) throws PersistenciaException {
        String sql = "INSERT INTO proyectos (id_lider, nombre, objetivo, presupuesto_inicial, "
                + "fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            completarParametros(ps, proyecto);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    proyecto.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el proyecto.", e);
        }
    }

    @Override
    public void actualizar(Proyecto proyecto) throws PersistenciaException {
        String sql = "UPDATE proyectos SET id_lider = ?, nombre = ?, objetivo = ?, presupuesto_inicial = ?, "
                + "fecha_inicio = ?, fecha_fin = ?, estado = ? WHERE id_proyecto = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            completarParametros(ps, proyecto);
            ps.setInt(8, proyecto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el proyecto.", e);
        }
    }

    /**
     * Regla de negocio: un proyecto con datos históricos no se borra.
     * La "eliminación" es una baja lógica que cambia el estado a CANCELADO.
     */
    @Override
    public void eliminar(int id) throws PersistenciaException {
        String sql = "UPDATE proyectos SET estado = ? WHERE id_proyecto = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, EstadoProyecto.CANCELADO.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al dar de baja el proyecto.", e);
        }
    }

    private void cargarHitos(Proyecto proyecto) throws PersistenciaException {
        proyecto.limpiarHitos();
        for (Hito hito : hitoDao.listarPorProyecto(proyecto.getId())) {
            proyecto.agregarHito(hito);
        }
    }

    private void completarParametros(PreparedStatement ps, Proyecto p) throws SQLException {
        ps.setInt(1, p.getIdLider());
        ps.setString(2, p.getNombre().trim());
        ps.setString(3, p.getObjetivo().trim());
        ps.setBigDecimal(4, p.getPresupuestoInicial());
        ps.setDate(5, Date.valueOf(p.getFechaInicio()));
        if (p.getFechaFin() != null) {
            ps.setDate(6, Date.valueOf(p.getFechaFin()));
        } else {
            ps.setNull(6, Types.DATE);
        }
        ps.setString(7, p.getEstado().name());
    }

    private Proyecto mapear(ResultSet rs) throws SQLException {
        Date fin = rs.getDate("fecha_fin");
        return new Proyecto(
                rs.getInt("id_proyecto"),
                rs.getInt("id_lider"),
                rs.getString("lider"),
                rs.getString("nombre"),
                rs.getString("objetivo"),
                rs.getBigDecimal("presupuesto_inicial"),
                rs.getDate("fecha_inicio").toLocalDate(),
                fin == null ? null : fin.toLocalDate(),
                EstadoProyecto.valueOf(rs.getString("estado")));
    }
}
