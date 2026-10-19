package com.fundacioncana.excepciones;

/** Se lanza cuando el rol del usuario no le permite realizar una operación. */
public class PermisoDenegadoException extends SistemaException {

    public PermisoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
