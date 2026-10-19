package com.fundacioncana.modelo;

public enum EstadoProyecto {
    PLANIFICACION("Planificación"),
    EN_PROGRESO("En progreso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoProyecto(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean estaActivo() {
        return this == PLANIFICACION || this == EN_PROGRESO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
