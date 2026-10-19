package com.fundacioncana.excepciones;

/**
 * Envuelve los errores de JDBC (SQLException) para que las capas superiores
 * no dependan de los detalles de la base de datos.
 */
public class PersistenciaException extends SistemaException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
