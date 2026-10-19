package com.fundacioncana.util;

import com.fundacioncana.modelo.Proyecto;

import java.util.Comparator;

/** Criterios disponibles para ordenar la lista de proyectos. */
public enum CriterioOrden {
    ID("Código", Comparator.comparingInt(Proyecto::getId)),
    NOMBRE("Nombre", Comparator.comparing(p -> p.getNombre().toLowerCase())),
    AVANCE("Mayor avance", Comparator.comparing(Proyecto::calcularAvance).reversed()),
    PRESUPUESTO("Mayor presupuesto", Comparator.comparing(Proyecto::getPresupuestoInicial).reversed()),
    FECHA_INICIO("Fecha de inicio", Comparator.comparing(Proyecto::getFechaInicio));

    private final String descripcion;
    private final Comparator<Proyecto> comparador;

    CriterioOrden(String descripcion, Comparator<Proyecto> comparador) {
        this.descripcion = descripcion;
        this.comparador = comparador;
    }

    public Comparator<Proyecto> getComparador() {
        return comparador;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
