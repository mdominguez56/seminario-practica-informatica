package com.fundacioncana.modelo;

/** Opciones del menú principal. Cada rol habilita un subconjunto de ellas. */
public enum OpcionMenu {
    TABLERO("Tablero de control"),
    PROYECTOS("Proyectos e hitos"),
    MIS_HITOS("Mis hitos asignados");

    private final String titulo;

    OpcionMenu(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }
}
