package com.fundacioncana.excepciones;

/** Se lanza cuando un dato ingresado no cumple una regla de negocio. */
public class ValidacionException extends SistemaException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
