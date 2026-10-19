package com.fundacioncana.modelo;

import java.math.BigDecimal;

/** Valores consolidados que se muestran en las tarjetas del tablero de control (RF09/RF10). */
public class IndicadoresTablero {

    private final int proyectosActivos;
    private final int totalProyectos;
    private final BigDecimal avancePromedio;
    private final BigDecimal presupuestoTotal;
    private final BigDecimal costoTotal;
    private final int hitosAtrasados;

    public IndicadoresTablero(int proyectosActivos, int totalProyectos, BigDecimal avancePromedio,
                              BigDecimal presupuestoTotal, BigDecimal costoTotal, int hitosAtrasados) {
        this.proyectosActivos = proyectosActivos;
        this.totalProyectos = totalProyectos;
        this.avancePromedio = avancePromedio;
        this.presupuestoTotal = presupuestoTotal;
        this.costoTotal = costoTotal;
        this.hitosAtrasados = hitosAtrasados;
    }

    public int getProyectosActivos() {
        return proyectosActivos;
    }

    public int getTotalProyectos() {
        return totalProyectos;
    }

    public BigDecimal getAvancePromedio() {
        return avancePromedio;
    }

    public BigDecimal getPresupuestoTotal() {
        return presupuestoTotal;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public int getHitosAtrasados() {
        return hitosAtrasados;
    }
}
