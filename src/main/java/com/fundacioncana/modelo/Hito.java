package com.fundacioncana.modelo;

import com.fundacioncana.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Etapa de un proyecto, con fecha límite, responsable, avance y costos asociados. */
public class Hito extends Entidad {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private int idProyecto;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLimite;
    private BigDecimal porcentajeAvance;
    private EstadoHito estado;
    private int idResponsable;
    private String nombreResponsable;
    private final List<Costo> costos = new ArrayList<>();

    /** Constructor para un hito nuevo: comienza pendiente y sin avance. */
    public Hito(int idProyecto, String nombre, String descripcion, LocalDate fechaLimite, int idResponsable) {
        this(0, idProyecto, nombre, descripcion, fechaLimite, BigDecimal.ZERO, EstadoHito.PENDIENTE,
                idResponsable, null);
    }

    public Hito(int id, int idProyecto, String nombre, String descripcion, LocalDate fechaLimite,
                BigDecimal porcentajeAvance, EstadoHito estado, int idResponsable, String nombreResponsable) {
        super(id);
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.porcentajeAvance = porcentajeAvance;
        this.estado = estado;
        this.idResponsable = idResponsable;
        this.nombreResponsable = nombreResponsable;
    }

    @Override
    public void validar() throws ValidacionException {
        if (idProyecto <= 0) {
            throw new ValidacionException("El hito debe pertenecer a un proyecto.");
        }
        requerirTexto(nombre, "Nombre del hito");
        if (fechaLimite == null) {
            throw new ValidacionException("El hito debe tener una fecha límite.");
        }
        if (idResponsable <= 0) {
            throw new ValidacionException("El hito debe tener un responsable asignado.");
        }
        validarPorcentaje(porcentajeAvance);
    }

    /**
     * Actualiza el avance y deriva el estado: 0 % pendiente, 100 % completado,
     * cualquier valor intermedio en progreso.
     */
    public void actualizarAvance(BigDecimal nuevoAvance) throws ValidacionException {
        validarPorcentaje(nuevoAvance);
        this.porcentajeAvance = nuevoAvance;
        if (nuevoAvance.signum() == 0) {
            estado = EstadoHito.PENDIENTE;
        } else if (nuevoAvance.compareTo(CIEN) == 0) {
            estado = EstadoHito.COMPLETADO;
        } else {
            estado = EstadoHito.EN_PROGRESO;
        }
    }

    private static void validarPorcentaje(BigDecimal valor) throws ValidacionException {
        if (valor == null || valor.signum() < 0 || valor.compareTo(CIEN) > 0) {
            throw new ValidacionException("El avance debe estar entre 0 y 100.");
        }
    }

    /** Un hito está atrasado si su fecha límite ya pasó y no fue completado. */
    public boolean estaAtrasado(LocalDate hoy) {
        return estado != EstadoHito.COMPLETADO && fechaLimite.isBefore(hoy);
    }

    public BigDecimal calcularCostoTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Costo costo : costos) {
            total = total.add(costo.getMonto());
        }
        return total;
    }

    public void agregarCosto(Costo costo) {
        costos.add(costo);
    }

    public List<Costo> getCostos() {
        return Collections.unmodifiableList(costos);
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public BigDecimal getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public EstadoHito getEstado() {
        return estado;
    }

    public int getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
