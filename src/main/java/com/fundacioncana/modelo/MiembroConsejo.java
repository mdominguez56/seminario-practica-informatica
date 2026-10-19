package com.fundacioncana.modelo;

import java.util.List;

public class MiembroConsejo extends Usuario {

    public MiembroConsejo(int idUsuario, String nombre, String email, boolean activo) {
        super(idUsuario, nombre, email, activo);
    }

    @Override
    public Rol getRol() {
        return Rol.DIRECTIVO;
    }

    /** El Consejo consulta el tablero y los proyectos en modo de solo lectura. */
    @Override
    public List<OpcionMenu> getOpcionesMenu() {
        return List.of(OpcionMenu.TABLERO, OpcionMenu.PROYECTOS);
    }

    @Override
    public boolean puedeActualizarAvance(Hito hito) {
        return false;
    }
}
