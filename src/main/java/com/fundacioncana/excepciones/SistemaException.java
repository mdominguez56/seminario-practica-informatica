package com.fundacioncana.excepciones;

/**
 * Excepción base del sistema. Todas las excepciones propias heredan de esta clase,
 * lo que permite capturarlas en un único bloque catch en la capa de vista.
 */
public class SistemaException extends Exception {

    public SistemaException(String mensaje) {
        super(mensaje);
    }

    public SistemaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
