package com.fundacioncana.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/** Algoritmos de ordenación y búsqueda implementados sin recurrir a Collections.sort. */
public final class Algoritmos {

    private Algoritmos() {
    }

    /**
     * Ordenamiento por inserción: recorre la lista y ubica cada elemento en su posición
     * dentro del tramo ya ordenado. Es estable y adecuado para listas pequeñas como las del prototipo.
     */
    public static <T> void ordenarPorInsercion(List<T> lista, Comparator<? super T> comparador) {
        for (int i = 1; i < lista.size(); i++) {
            T actual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && comparador.compare(lista.get(j), actual) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }

    /**
     * Búsqueda binaria sobre una lista ordenada por la clave indicada.
     * Devuelve la posición del elemento o -1 si no existe.
     */
    public static <T, K extends Comparable<K>> int busquedaBinaria(List<T> ordenada, K clave,
                                                                    Function<T, K> extractorClave) {
        int inicio = 0;
        int fin = ordenada.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            int comparacion = extractorClave.apply(ordenada.get(medio)).compareTo(clave);
            if (comparacion == 0) {
                return medio;
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }

    /** Búsqueda secuencial: devuelve todos los elementos que cumplen la condición. */
    public static <T> List<T> busquedaLineal(List<T> lista, Predicate<T> condicion) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : lista) {
            if (condicion.test(elemento)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }
}
