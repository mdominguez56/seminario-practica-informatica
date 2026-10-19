package com.fundacioncana.modelo;

/**
 * Situación que requiere la atención del Consejo Directivo (RF11).
 * Implementa Comparable para que la cola de prioridad ordene primero las alertas más graves.
 */
public abstract class Alerta implements Comparable<Alerta> {

    private final String proyecto;

    protected Alerta(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getProyecto() {
        return proyecto;
    }

    public abstract String getTipo();

    public abstract String getMensaje();

    /** Valor numérico de gravedad: cuanto mayor, más urgente. */
    public abstract int getPrioridad();

    @Override
    public int compareTo(Alerta otra) {
        return Integer.compare(otra.getPrioridad(), getPrioridad());
    }

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + proyecto + ": " + getMensaje();
    }
}
