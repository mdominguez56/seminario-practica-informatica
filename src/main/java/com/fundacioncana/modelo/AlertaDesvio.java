package com.fundacioncana.modelo;

import java.math.BigDecimal;

public class AlertaDesvio extends Alerta {

    private final BigDecimal ejecucion;

    public AlertaDesvio(Proyecto proyecto) {
        super(proyecto.getNombre());
        this.ejecucion = proyecto.calcularEjecucionPresupuestaria();
    }

    @Override
    public String getTipo() {
        return "Desvío presupuestario";
    }

    @Override
    public String getMensaje() {
        return "El costo ejecutado alcanza el " + ejecucion.stripTrailingZeros().toPlainString()
                + " % del presupuesto aprobado.";
    }

    /** Un desvío presupuestario se considera más grave que un atraso. */
    @Override
    public int getPrioridad() {
        return 100 + ejecucion.intValue();
    }
}
