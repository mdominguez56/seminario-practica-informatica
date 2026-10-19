package com.fundacioncana.modelo;

public enum EstadoHito {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    COMPLETADO("Completado");

    private final String descripcion;

    EstadoHito(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
