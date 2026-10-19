package com.fundacioncana.modelo;

import com.fundacioncana.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Iniciativa solidaria de la Fundación. Contiene sus hitos y calcula los indicadores. */
public class Proyecto extends Entidad {

    private int idLider;
    private String nombreLider;
    private String nombre;
    private String objetivo;
    private BigDecimal presupuestoInicial;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoProyecto estado;
    private final List<Hito> hitos = new ArrayList<>();

    /** Constructor para un proyecto nuevo: comienza en estado de planificación (CU02). */
    public Proyecto(String nombre, String objetivo, BigDecimal presupuestoInicial,
                    LocalDate fechaInicio, LocalDate fechaFin, int idLider) {
        this(0, idLider, null, nombre, objetivo, presupuestoInicial, fechaInicio, fechaFin,
                EstadoProyecto.PLANIFICACION);
    }

    /** Constructor completo, utilizado al reconstruir el objeto desde la base de datos. */
    public Proyecto(int id, int idLider, String nombreLider, String nombre, String objetivo,
                    BigDecimal presupuestoInicial, LocalDate fechaInicio, LocalDate fechaFin,
                    EstadoProyecto estado) {
        super(id);
        this.idLider = idLider;
        this.nombreLider = nombreLider;
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.presupuestoInicial = presupuestoInicial;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    @Override
    public void validar() throws ValidacionException {
        requerirTexto(nombre, "Nombre");
        requerirTexto(objetivo, "Objetivo");
        if (presupuestoInicial == null) {
            throw new ValidacionException("El presupuesto inicial es obligatorio.");
        }
        if (presupuestoInicial.signum() < 0) {
            throw new ValidacionException("El presupuesto inicial no puede ser negativo.");
        }
        if (fechaInicio == null) {
            throw new ValidacionException("La fecha de inicio es obligatoria.");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new ValidacionException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        if (idLider <= 0) {
            throw new ValidacionException("El proyecto debe tener un líder responsable.");
        }
    }

    /** Avance global: promedio del avance de los hitos (regla de negocio del TP1). */
    public BigDecimal calcularAvance() {
        if (hitos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal suma = BigDecimal.ZERO;
        for (Hito hito : hitos) {
            suma = suma.add(hito.getPorcentajeAvance());
        }
        return suma.divide(BigDecimal.valueOf(hitos.size()), 2, RoundingMode.HALF_UP);
    }

    /** Costo ejecutado: suma de los costos registrados en los hitos del proyecto. */
    public BigDecimal calcularCostoEjecutado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Hito hito : hitos) {
            total = total.add(hito.calcularCostoTotal());
        }
        return total;
    }

    /** Porcentaje del presupuesto consumido. */
    public BigDecimal calcularEjecucionPresupuestaria() {
        if (presupuestoInicial == null || presupuestoInicial.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return calcularCostoEjecutado()
                .multiply(BigDecimal.valueOf(100))
                .divide(presupuestoInicial, 2, RoundingMode.HALF_UP);
    }

    public boolean tieneDesvioPresupuestario() {
        return calcularCostoEjecutado().compareTo(presupuestoInicial) > 0;
    }

    public List<Hito> obtenerHitosAtrasados(LocalDate hoy) {
        List<Hito> atrasados = new ArrayList<>();
        for (Hito hito : hitos) {
            if (hito.estaAtrasado(hoy)) {
                atrasados.add(hito);
            }
        }
        return atrasados;
    }

    public void agregarHito(Hito hito) {
        hitos.add(hito);
    }

    public void limpiarHitos() {
        hitos.clear();
    }

    /** Se devuelve una vista no modificable para preservar el encapsulamiento. */
    public List<Hito> getHitos() {
        return Collections.unmodifiableList(hitos);
    }

    public int getIdLider() {
        return idLider;
    }

    public void setIdLider(int idLider) {
        this.idLider = idLider;
    }

    public String getNombreLider() {
        return nombreLider;
    }

    public void setNombreLider(String nombreLider) {
        this.nombreLider = nombreLider;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public BigDecimal getPresupuestoInicial() {
        return presupuestoInicial;
    }

    public void setPresupuestoInicial(BigDecimal presupuestoInicial) {
        this.presupuestoInicial = presupuestoInicial;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
