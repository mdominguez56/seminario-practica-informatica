package com.fundacioncana.modelo;

/** Roles definidos en la tabla roles. El nombre del enum coincide con roles.nombre. */
public enum Rol {
    DIRECTIVO("Miembro del Consejo Directivo"),
    LIDER_PROYECTO("Líder de proyecto"),
    VOLUNTARIO("Voluntario");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
