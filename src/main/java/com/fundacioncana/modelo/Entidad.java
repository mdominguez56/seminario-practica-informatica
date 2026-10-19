package com.fundacioncana.modelo;

import com.fundacioncana.excepciones.ValidacionException;

/**
 * Superclase abstracta de las entidades persistentes (Proyecto, Hito y Costo).
 * Concentra el identificador y obliga a cada subclase a definir sus propias reglas de validación.
 */
public abstract class Entidad {

    private int id;

    protected Entidad(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** Una entidad es nueva mientras no tenga un id asignado por la base de datos. */
    public boolean esNueva() {
        return id == 0;
    }

    /** Cada entidad valida sus datos según las reglas de negocio que le corresponden. */
    public abstract void validar() throws ValidacionException;

    /** Método auxiliar reutilizado por las subclases. */
    protected static void requerirTexto(String valor, String campo) throws ValidacionException {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo \"" + campo + "\" es obligatorio.");
        }
    }
}
