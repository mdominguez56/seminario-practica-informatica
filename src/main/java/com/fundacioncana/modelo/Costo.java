package com.fundacioncana.modelo;

import com.fundacioncana.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Gasto ejecutado y asociado a un hito. */
public class Costo extends Entidad {

    private final int idHito;
    private final LocalDate fecha;
    private final BigDecimal monto;
    private final String descripcion;

    public Costo(int idHito, LocalDate fecha, BigDecimal monto, String descripcion) {
        this(0, idHito, fecha, monto, descripcion);
    }

    public Costo(int id, int idHito, LocalDate fecha, BigDecimal monto, String descripcion) {
        super(id);
        this.idHito = idHito;
        this.fecha = fecha;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    @Override
    public void validar() throws ValidacionException {
        if (idHito <= 0) {
            throw new ValidacionException("El costo debe estar asociado a un hito.");
        }
        if (fecha == null) {
            throw new ValidacionException("La fecha del costo es obligatoria.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new ValidacionException("No se puede registrar un costo con fecha futura.");
        }
        if (monto == null || monto.signum() <= 0) {
            throw new ValidacionException("El monto debe ser mayor que cero.");
        }
        requerirTexto(descripcion, "Descripción");
    }

    public int getIdHito() {
        return idHito;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
