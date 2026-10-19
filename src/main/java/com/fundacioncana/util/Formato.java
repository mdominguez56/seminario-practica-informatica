package com.fundacioncana.util;

import com.fundacioncana.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Conversión entre los valores del modelo y el texto que se muestra o ingresa en pantalla. */
public final class Formato {

    private static final Locale ARGENTINA = Locale.forLanguageTag("es-AR");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formato() {
    }

    public static String moneda(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(ARGENTINA);
        return formato.format(valor == null ? BigDecimal.ZERO : valor);
    }

    public static String porcentaje(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString() + " %";
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "-" : FECHA.format(fecha);
    }

    /**
     * Convierte el texto ingresado en un importe. Acepta coma o punto decimal.
     * Transforma la NumberFormatException en una excepción de validación del sistema.
     */
    public static BigDecimal leerImporte(String texto, String campo) throws ValidacionException {
        if (texto == null || texto.isBlank()) {
            throw new ValidacionException("El campo \"" + campo + "\" es obligatorio.");
        }
        String limpio = texto.trim().replace("$", "").replace(" ", "");
        if (limpio.contains(",")) {
            // Formato local: el punto separa miles y la coma separa decimales.
            limpio = limpio.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser un número válido.");
        }
    }

    public static BigDecimal leerPorcentaje(String texto) throws ValidacionException {
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException("El avance debe ser un número entre 0 y 100.");
        }
    }
}
