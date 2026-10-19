package com.fundacioncana.modelo;

import java.util.List;

/**
 * Clase abstracta que representa a cualquier persona que usa el sistema.
 * Las subclases definen el rol, las opciones de menú y los permisos (polimorfismo).
 */
public abstract class Usuario {

    private final int idUsuario;
    private final String nombre;
    private final String email;
    private final boolean activo;

    protected Usuario(int idUsuario, String nombre, String email, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
    }

    public abstract Rol getRol();

    /** Opciones del menú principal que corresponden al rol. */
    public abstract List<OpcionMenu> getOpcionesMenu();

    /** Indica si el usuario puede crear y modificar proyectos e hitos. */
    public boolean puedeGestionarProyectos() {
        return false;
    }

    /** Indica si el usuario puede actualizar el avance del hito recibido. */
    public abstract boolean puedeActualizarAvance(Hito hito);

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
