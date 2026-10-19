package com.fundacioncana;

import com.fundacioncana.modelo.LiderProyecto;
import com.fundacioncana.modelo.OpcionMenu;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.modelo.UsuarioFactory;
import com.fundacioncana.modelo.Rol;
import com.fundacioncana.modelo.Voluntario;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.util.Algoritmos;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgoritmosTest {

    @Test
    void ordenamientoPorInsercionYBusquedaBinaria() {
        List<Integer> numeros = new ArrayList<>(List.of(42, 7, 19, 3, 25));
        Algoritmos.ordenarPorInsercion(numeros, Comparator.naturalOrder());
        assertEquals(List.of(3, 7, 19, 25, 42), numeros);
        assertEquals(2, Algoritmos.busquedaBinaria(numeros, 19, Function.identity()));
        assertEquals(-1, Algoritmos.busquedaBinaria(numeros, 8, Function.identity()));
    }

    @Test
    void busquedaLinealDevuelveCoincidencias() {
        List<String> nombres = List.of("Comedor Solidario", "Apoyo Escolar", "Campaña de Invierno");
        assertEquals(List.of("Comedor Solidario", "Campaña de Invierno"),
                Algoritmos.busquedaLineal(nombres, n -> n.startsWith("C")));
    }

    @Test
    void permisosYMenuDependenDelTipoDeUsuario() {
        Usuario lider = UsuarioFactory.crear(Rol.LIDER_PROYECTO, 2, "Carlos", "c@cana.org", true);
        Usuario voluntaria = UsuarioFactory.crear(Rol.VOLUNTARIO, 3, "Ana", "a@cana.org", true);
        Hito hitoDeAna = new Hito(1, "Hito", "", LocalDate.now(), 3);
        Hito hitoDeOtro = new Hito(1, "Hito", "", LocalDate.now(), 5);

        assertInstanceOf(LiderProyecto.class, lider);
        assertInstanceOf(Voluntario.class, voluntaria);
        assertTrue(lider.puedeGestionarProyectos());
        assertFalse(voluntaria.puedeGestionarProyectos());
        assertTrue(voluntaria.puedeActualizarAvance(hitoDeAna));
        assertFalse(voluntaria.puedeActualizarAvance(hitoDeOtro));
        assertEquals(List.of(OpcionMenu.MIS_HITOS), voluntaria.getOpcionesMenu());
    }
}
