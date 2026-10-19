package com.fundacioncana.excepciones;

/** Se lanza cuando las credenciales ingresadas no son válidas. */
public class AutenticacionException extends SistemaException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
