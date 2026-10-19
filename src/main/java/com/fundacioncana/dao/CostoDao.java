package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Costo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CostoDao implements CrudDao<Costo> {

    private static final String SELECT_BASE =
            "SELECT id_costo, id_hito, fecha, monto, descripcion FROM costos ";

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    @Override
    public List<Costo> listar() throws PersistenciaException {
        return consultar(SELECT_BASE + "ORDER BY fecha", null);
    }

    public List<Costo> listarPorHito(int idHito) throws PersistenciaException {
        return consultar(SELECT_BASE + "WHERE id_hito = ? ORDER BY fecha", idHito);
    }

    @Override
    public Optional<Costo> buscarPorId(int id) throws PersistenciaException {
        List<Costo> costos = consultar(SELECT_BASE + "WHERE id_costo = ?", id);
        return costos.isEmpty() ? Optional.empty() : Optional.of(costos.get(0));
    }

    @Override
    public void insertar(Costo costo) throws PersistenciaException {
        String sql = "INSERT INTO costos (id_hito, fecha, monto, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, costo.getIdHito());
            ps.setDate(2, Date.valueOf(costo.getFecha()));
            ps.setBigDecimal(3, costo.getMonto());
            ps.setString(4, costo.getDescripcion().trim());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    costo.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el costo.", e);
        }
    }

    /** Los costos son registros históricos: no se modifican, se eliminan y se vuelven a cargar. */
    @Override
    public void actualizar(Costo costo) {
        throw new UnsupportedOperationException("Los costos registrados no se modifican.");
    }

    @Override
    public void eliminar(int id) throws PersistenciaException {
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM costos WHERE id_costo = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al eliminar el costo.", e);
        }
    }

    private List<Costo> consultar(String sql, Integer parametro) throws PersistenciaException {
        List<Costo> costos = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    costos.add(new Costo(
                            rs.getInt("id_costo"),
                            rs.getInt("id_hito"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getBigDecimal("monto"),
                            rs.getString("descripcion")));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar los costos.", e);
        }
        return costos;
    }
}
