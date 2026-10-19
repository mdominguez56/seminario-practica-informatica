package com.fundacioncana.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AlertaAtraso extends Alerta {

    private final Hito hito;
    private final long diasAtraso;

    public AlertaAtraso(String proyecto, Hito hito, LocalDate hoy) {
        super(proyecto);
        this.hito = hito;
        this.diasAtraso = ChronoUnit.DAYS.between(hito.getFechaLimite(), hoy);
    }

    @Override
    public String getTipo() {
        return "Atraso";
    }

    @Override
    public String getMensaje() {
        return "El hito \"" + hito.getNombre() + "\" lleva " + diasAtraso + " días de atraso ("
                + hito.getPorcentajeAvance().stripTrailingZeros().toPlainString() + " % de avance).";
    }

    /** Cada día de atraso suma un punto de prioridad. */
    @Override
    public int getPrioridad() {
        return (int) diasAtraso;
    }
}
