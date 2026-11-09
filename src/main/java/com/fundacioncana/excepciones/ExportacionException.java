package com.fundacioncana.excepciones;

/**
 * Se lanza cuando no es posible escribir o leer el archivo de exportación.
 * Encapsula la IOException para que la capa de vista no dependa de java.io.
 */
public class ExportacionException extends SistemaException {

    public ExportacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
