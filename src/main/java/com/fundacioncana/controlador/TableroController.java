package com.fundacioncana.controlador;

import com.fundacioncana.dao.ProyectoDao;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Alerta;
import com.fundacioncana.modelo.AlertaAtraso;
import com.fundacioncana.modelo.AlertaDesvio;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.IndicadoresTablero;
import com.fundacioncana.modelo.Proyecto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/** CU04 - Consultar tablero de control (RF09, RF10 y RF11). */
public class TableroController {

    private final ProyectoDao proyectoDao;

    public TableroController(ProyectoDao proyectoDao) {
        this.proyectoDao = proyectoDao;
    }

    public List<Proyecto> obtenerProyectos() throws PersistenciaException {
        return proyectoDao.listar();
    }

    public IndicadoresTablero calcularIndicadores(List<Proyecto> proyectos, LocalDate hoy) {
        int activos = 0;
        int atrasados = 0;
        BigDecimal sumaAvance = BigDecimal.ZERO;
        BigDecimal presupuesto = BigDecimal.ZERO;
        BigDecimal costo = BigDecimal.ZERO;

        for (Proyecto proyecto : proyectos) {
            if (!proyecto.getEstado().estaActivo()) {
                continue; // Los proyectos finalizados o cancelados no se consideran en los indicadores.
            }
            activos++;
            sumaAvance = sumaAvance.add(proyecto.calcularAvance());
            presupuesto = presupuesto.add(proyecto.getPresupuestoInicial());
            costo = costo.add(proyecto.calcularCostoEjecutado());
            atrasados += proyecto.obtenerHitosAtrasados(hoy).size();
        }

        BigDecimal avancePromedio = activos == 0
                ? BigDecimal.ZERO
                : sumaAvance.divide(BigDecimal.valueOf(activos), 2, RoundingMode.HALF_UP);
        return new IndicadoresTablero(activos, proyectos.size(), avancePromedio, presupuesto, costo, atrasados);
    }

    /**
     * Genera las alertas y las devuelve ordenadas por gravedad usando una cola de prioridad.
     * Cada alerta es un objeto de una subclase distinta, pero se tratan como Alerta (polimorfismo).
     */
    public List<Alerta> generarAlertas(List<Proyecto> proyectos, LocalDate hoy) {
        PriorityQueue<Alerta> cola = new PriorityQueue<>();
        for (Proyecto proyecto : proyectos) {
            if (proyecto.tieneDesvioPresupuestario()) {
                cola.add(new AlertaDesvio(proyecto));
            }
            if (proyecto.getEstado().estaActivo()) {
                for (Hito hito : proyecto.obtenerHitosAtrasados(hoy)) {
                    cola.add(new AlertaAtraso(proyecto.getNombre(), hito, hoy));
                }
            }
        }
        List<Alerta> ordenadas = new ArrayList<>();
        while (!cola.isEmpty()) {
            ordenadas.add(cola.poll());
        }
        return ordenadas;
    }
}
