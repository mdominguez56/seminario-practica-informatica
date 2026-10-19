package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Costo;
import com.fundacioncana.modelo.EstadoHito;
import com.fundacioncana.modelo.Hito;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HitoDao implements CrudDao<Hito> {

    /** El responsable vigente es la asignación que todavía no tiene fecha de desasignación. */
    private static final String SELECT_BASE =
            "SELECT h.id_hito, h.id_proyecto, h.nombre, h.descripcion, h.fecha_limite, "
            + "h.porcentaje_avance, h.estado, a.id_usuario AS id_responsable, u.nombre AS responsable "
            + "FROM hitos h "
            + "LEFT JOIN asignaciones a ON a.id_hito = h.id_hito AND a.fecha_desasignacion IS NULL "
            + "LEFT JOIN usuarios u ON u.id_usuario = a.id_usuario ";

    private final ConexionDB conexionDB = ConexionDB.getInstancia();
    private final CostoDao costoDao;

    public HitoDao(CostoDao costoDao) {
        this.costoDao = costoDao;
    }

    @Override
    public List<Hito> listar() throws PersistenciaException {
        return consultar(SELECT_BASE + "ORDER BY h.fecha_limite", null);
    }

    public List<Hito> listarPorProyecto(int idProyecto) throws PersistenciaException {
        return consultar(SELECT_BASE + "WHERE h.id_proyecto = ? ORDER BY h.fecha_limite", idProyecto);
    }

    public List<Hito> listarPorResponsable(int idUsuario) throws PersistenciaException {
        return consultar(SELECT_BASE + "WHERE a.id_usuario = ? ORDER BY h.fecha_limite", idUsuario);
    }

    @Override
    public Optional<Hito> buscarPorId(int id) throws PersistenciaException {
        List<Hito> hitos = consultar(SELECT_BASE + "WHERE h.id_hito = ?", id);
        return hitos.isEmpty() ? Optional.empty() : Optional.of(hitos.get(0));
    }

    /** Inserta el hito y su asignación en una única transacción. */
    @Override
    public void insertar(Hito hito) throws PersistenciaException {
        String sql = "INSERT INTO hitos (id_proyecto, nombre, descripcion, fecha_limite, porcentaje_avance, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, hito.getIdProyecto());
                ps.setString(2, hito.getNombre().trim());
                ps.setString(3, hito.getDescripcion());
                ps.setDate(4, Date.valueOf(hito.getFechaLimite()));
                ps.setBigDecimal(5, hito.getPorcentajeAvance());
                ps.setString(6, hito.getEstado().name());
                ps.executeUpdate();
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        hito.setId(claves.getInt(1));
                    }
                }
                asignarResponsable(con, hito.getId(), hito.getIdResponsable());
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el hito.", e);
        }
    }

    /** Actualiza los datos del hito y, si cambió el responsable, registra la reasignación. */
    @Override
    public void actualizar(Hito hito) throws PersistenciaException {
        String sql = "UPDATE hitos SET nombre = ?, descripcion = ?, fecha_limite = ?, "
                + "porcentaje_avance = ?, estado = ? WHERE id_hito = ?";
        try (Connection con = conexionDB.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, hito.getNombre().trim());
                ps.setString(2, hito.getDescripcion());
                ps.setDate(3, Date.valueOf(hito.getFechaLimite()));
                ps.setBigDecimal(4, hito.getPorcentajeAvance());
                ps.setString(5, hito.getEstado().name());
                ps.setInt(6, hito.getId());
                ps.executeUpdate();
                asignarResponsable(con, hito.getId(), hito.getIdResponsable());
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el hito.", e);
        }
    }

    public void actualizarAvance(Hito hito) throws PersistenciaException {
        String sql = "UPDATE hitos SET porcentaje_avance = ?, estado = ? WHERE id_hito = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, hito.getPorcentajeAvance());
            ps.setString(2, hito.getEstado().name());
            ps.setInt(3, hito.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el avance.", e);
        }
    }

    /** Las asignaciones y costos del hito se eliminan por ON DELETE CASCADE. */
    @Override
    public void eliminar(int id) throws PersistenciaException {
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM hitos WHERE id_hito = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el hito.", e);
        }
    }

    /**
     * Cierra la asignación vigente (si pertenece a otro usuario) y registra la nueva.
     * Si el usuario ya había sido responsable del hito, se reactiva su asignación.
     */
    private void asignarResponsable(Connection con, int idHito, int idUsuario) throws SQLException {
        String cerrar = "UPDATE asignaciones SET fecha_desasignacion = CURRENT_DATE "
                + "WHERE id_hito = ? AND id_usuario <> ? AND fecha_desasignacion IS NULL";
        String asignar = "INSERT INTO asignaciones (id_hito, id_usuario, fecha_asignacion) "
                + "VALUES (?, ?, CURRENT_DATE) "
                + "ON DUPLICATE KEY UPDATE fecha_desasignacion = NULL";
        try (PreparedStatement psCerrar = con.prepareStatement(cerrar);
             PreparedStatement psAsignar = con.prepareStatement(asignar)) {
            psCerrar.setInt(1, idHito);
            psCerrar.setInt(2, idUsuario);
            psCerrar.executeUpdate();
            psAsignar.setInt(1, idHito);
            psAsignar.setInt(2, idUsuario);
            psAsignar.executeUpdate();
        }
    }

    private List<Hito> consultar(String sql, Integer parametro) throws PersistenciaException {
        List<Hito> hitos = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hitos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar los hitos.", e);
        }
        for (Hito hito : hitos) {
            for (Costo costo : costoDao.listarPorHito(hito.getId())) {
                hito.agregarCosto(costo);
            }
        }
        return hitos;
    }

    private Hito mapear(ResultSet rs) throws SQLException {
        return new Hito(
                rs.getInt("id_hito"),
                rs.getInt("id_proyecto"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDate("fecha_limite").toLocalDate(),
                rs.getBigDecimal("porcentaje_avance"),
                EstadoHito.valueOf(rs.getString("estado")),
                rs.getInt("id_responsable"),
                rs.getString("responsable"));
    }
}
