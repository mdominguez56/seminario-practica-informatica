package com.fundacioncana.modelo;

import java.util.List;

public class Voluntario extends Usuario {

    public Voluntario(int idUsuario, String nombre, String email, boolean activo) {
        super(idUsuario, nombre, email, activo);
    }

    @Override
    public Rol getRol() {
        return Rol.VOLUNTARIO;
    }

    @Override
    public List<OpcionMenu> getOpcionesMenu() {
        return List.of(OpcionMenu.MIS_HITOS);
    }

    /** Un voluntario solo puede actualizar los hitos que tiene asignados. */
    @Override
    public boolean puedeActualizarAvance(Hito hito) {
        return hito != null && hito.getIdResponsable() == getIdUsuario();
    }
}
