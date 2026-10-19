package com.fundacioncana;

import com.fundacioncana.excepciones.ValidacionException;
import com.fundacioncana.modelo.Costo;
import com.fundacioncana.modelo.EstadoHito;
import com.fundacioncana.modelo.EstadoProyecto;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Proyecto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de componente sobre las reglas de negocio del modelo (CPC01 y CPF02). */
class ProyectoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 19);

    private Proyecto proyecto(String presupuesto) {
        return new Proyecto("Comedor", "Brindar almuerzos", new BigDecimal(presupuesto), HOY, null, 2);
    }

    @Test
    void proyectoNuevoComienzaEnPlanificacion() {
        assertEquals(EstadoProyecto.PLANIFICACION, proyecto("1000").getEstado());
    }

    @Test
    void presupuestoNegativoSeRechazaYCeroOPositivoSeAcepta() {
        assertThrows(ValidacionException.class, () -> proyecto("-1").validar());
        assertDoesNotThrow(() -> proyecto("0").validar());
        assertDoesNotThrow(() -> proyecto("150000").validar());
    }

    @Test
    void faltaDeNombreOFechaFinAnteriorSeRechazan() {
        Proyecto sinNombre = new Proyecto(" ", "Objetivo", BigDecimal.TEN, HOY, null, 2);
        assertThrows(ValidacionException.class, sinNombre::validar);
        Proyecto fechasInvertidas = new Proyecto("P", "Objetivo", BigDecimal.TEN, HOY, HOY.minusDays(1), 2);
        assertThrows(ValidacionException.class, fechasInvertidas::validar);
    }

    @Test
    void avanceYCostoSeCalculanDesdeLosHitos() throws ValidacionException {
        Proyecto p = proyecto("1000");
        Hito h1 = new Hito(1, 1, "H1", "", HOY.plusDays(5), BigDecimal.ZERO, EstadoHito.PENDIENTE, 3, "Ana");
        Hito h2 = new Hito(2, 1, "H2", "", HOY.minusDays(3), BigDecimal.ZERO, EstadoHito.PENDIENTE, 3, "Ana");
        h1.actualizarAvance(new BigDecimal("100"));
        h2.actualizarAvance(new BigDecimal("50"));
        h1.agregarCosto(new Costo(1, 1, HOY, new BigDecimal("800"), "Insumos"));
        h2.agregarCosto(new Costo(2, 2, HOY, new BigDecimal("400"), "Traslado"));
        p.agregarHito(h1);
        p.agregarHito(h2);

        assertEquals(new BigDecimal("75.00"), p.calcularAvance());
        assertEquals(new BigDecimal("1200"), p.calcularCostoEjecutado());
        assertTrue(p.tieneDesvioPresupuestario());
        assertEquals(EstadoHito.COMPLETADO, h1.getEstado());
        assertEquals(EstadoHito.EN_PROGRESO, h2.getEstado());
        assertFalse(h1.estaAtrasado(HOY));
        assertTrue(h2.estaAtrasado(HOY));
    }

    @Test
    void avanceFueraDeRangoSeRechaza() {
        Hito hito = new Hito(1, "H", "", HOY, 3);
        assertThrows(ValidacionException.class, () -> hito.actualizarAvance(new BigDecimal("101")));
        assertThrows(ValidacionException.class, () -> hito.actualizarAvance(new BigDecimal("-5")));
    }
}
