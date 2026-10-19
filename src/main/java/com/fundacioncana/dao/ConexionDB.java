package com.fundacioncana.dao;

import com.fundacioncana.excepciones.PersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Crea conexiones JDBC con MySQL. Las credenciales no están escritas en el código:
 * se leen del archivo db.properties (junto al programa) o de variables de entorno.
 */
public final class ConexionDB {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";
    private static ConexionDB instancia;

    private final String url;
    private final String usuario;
    private final String password;

    private ConexionDB() {
        Properties propiedades = new Properties();
        Path archivo = Path.of(ARCHIVO_CONFIGURACION);
        if (Files.exists(archivo)) {
            try (InputStream entrada = Files.newInputStream(archivo)) {
                propiedades.load(entrada);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIGURACION + ": " + e.getMessage());
            }
        }
        url = valor(propiedades, "db.url", "FUNDACION_DB_URL",
                "jdbc:mysql://localhost:3306/fundacion_cana?serverTimezone=America/Argentina/Buenos_Aires");
        usuario = valor(propiedades, "db.usuario", "FUNDACION_DB_USUARIO", "root");
        password = valor(propiedades, "db.password", "FUNDACION_DB_PASSWORD", "");
    }

    /** Patrón Singleton: la configuración se lee una sola vez. */
    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    private static String valor(Properties propiedades, String clave, String variableEntorno, String porDefecto) {
        String entorno = System.getenv(variableEntorno);
        if (entorno != null && !entorno.isBlank()) {
            return entorno;
        }
        return propiedades.getProperty(clave, porDefecto);
    }

    public Connection getConexion() throws PersistenciaException {
        try {
            return DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo conectar con la base de datos. Verifique que MySQL esté iniciado.", e);
        }
    }
}
