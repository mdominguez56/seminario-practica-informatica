package com.fundacioncana.util;

import com.fundacioncana.modelo.Proyecto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Exporta e importa datos de proyectos a/desde archivos de texto plano.
 * Utiliza BufferedWriter/FileWriter para escritura y BufferedReader/FileReader para lectura.
 *
 * <p>El array {@code ENCABEZADOS} (arreglo nativo de String) define las columnas del archivo exportado.</p>
 */
public final class ExportadorArchivo {

    /** Arreglo nativo con los encabezados de columna del archivo exportado. */
    private static final String[] ENCABEZADOS = {
        "Código", "Proyecto", "Líder", "Estado", "Inicio", "Fin", "Presupuesto", "Avance %"
    };

    private static final String SEPARADOR = " | ";
    private static final int ANCHO_LINEA = 100;

    private ExportadorArchivo() {
    }

    /**
     * Escribe la lista de proyectos en el archivo indicado.
     *
     * @param proyectos lista a exportar (puede estar vacía)
     * @param destino   archivo de destino elegido por el usuario
     * @throws IOException si no se puede escribir el archivo
     */
    public static void exportarProyectos(List<Proyecto> proyectos, File destino) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destino))) {

            // Título y fecha de exportación
            writer.write("FUNDACIÓN MARÍA DE CANÁ - Reporte de Proyectos");
            writer.newLine();
            writer.write("Generado el: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            writer.newLine();
            writer.write("=".repeat(ANCHO_LINEA));
            writer.newLine();

            // Encabezados usando el array nativo ENCABEZADOS
            writer.write(String.join(SEPARADOR, ENCABEZADOS));
            writer.newLine();
            writer.write("-".repeat(ANCHO_LINEA));
            writer.newLine();

            // Una línea por proyecto
            for (Proyecto p : proyectos) {
                String[] fila = {
                    String.valueOf(p.getId()),
                    p.getNombre(),
                    p.getNombreLider() != null ? p.getNombreLider() : "-",
                    p.getEstado().toString(),
                    Formato.fecha(p.getFechaInicio()),
                    Formato.fecha(p.getFechaFin()),
                    Formato.moneda(p.getPresupuestoInicial()),
                    Formato.porcentaje(p.calcularAvance())
                };
                writer.write(String.join(SEPARADOR, fila));
                writer.newLine();
            }

            writer.write("=".repeat(ANCHO_LINEA));
            writer.newLine();
            writer.write("Total: " + proyectos.size() + " proyecto(s)");
            writer.newLine();
        }
    }

    /**
     * Lee el archivo exportado y devuelve sus líneas de contenido (sin encabezados ni separadores).
     *
     * @param origen archivo a leer
     * @return lista de líneas con datos de proyectos
     * @throws IOException si no se puede leer el archivo
     */
    public static List<String> leerLineas(File origen) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(origen))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }
}
