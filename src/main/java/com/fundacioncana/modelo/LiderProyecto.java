package com.fundacioncana.modelo;

import java.util.List;

public class LiderProyecto extends Usuario {

    public LiderProyecto(int idUsuario, String nombre, String email, boolean activo) {
        super(idUsuario, nombre, email, activo);
    }

    @Override
    public Rol getRol() {
        return Rol.LIDER_PROYECTO;
    }

    @Override
    public List<OpcionMenu> getOpcionesMenu() {
        return List.of(OpcionMenu.PROYECTOS, OpcionMenu.TABLERO);
    }

    @Override
    public boolean puedeGestionarProyectos() {
        return true;
    }

    /** El líder controla la información operativa de todos los hitos. */
    @Override
    public boolean puedeActualizarAvance(Hito hito) {
        return true;
    }
}
