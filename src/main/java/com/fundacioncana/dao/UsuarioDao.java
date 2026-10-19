package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Rol;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.modelo.UsuarioFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.nombre, u.email, u.activo, r.nombre AS rol "
            + "FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol ";

    private final ConexionDB conexionDB = ConexionDB.getInstancia();

    /** Busca un usuario activo con el email y el hash de contraseña indicados. */
    public Optional<Usuario> autenticar(String email, String passwordHash) throws PersistenciaException {
        String sql = SELECT_BASE + "WHERE u.email = ? AND u.password_hash = ? AND u.activo = TRUE";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al validar las credenciales.", e);
        }
    }

    public List<Usuario> listarPorRol(Rol rol) throws PersistenciaException {
        String sql = SELECT_BASE + "WHERE r.nombre = ? AND u.activo = TRUE ORDER BY u.nombre";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar usuarios.", e);
        }
        return usuarios;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return UsuarioFactory.crear(
                Rol.valueOf(rs.getString("rol")),
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getBoolean("activo"));
    }
}
