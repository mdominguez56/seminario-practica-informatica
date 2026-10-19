package com.fundacioncana.vista;

import com.fundacioncana.controlador.TableroController;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.Alerta;
import com.fundacioncana.modelo.IndicadoresTablero;
import com.fundacioncana.modelo.Proyecto;
import com.fundacioncana.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/** Tablero de control para el Consejo Directivo (CU04). */
public class TableroView {

    private final TableroController controller;
    private final VBox raiz = new VBox(16);
    private final HBox tarjetas = new HBox(14);
    private final TableView<Proyecto> tabla = new TableView<>();
    private final ListView<Alerta> alertas = new ListView<>();
    private final BarChart<Number, String> grafico = new BarChart<>(new NumberAxis(), new CategoryAxis());
    private final Label estado = new Label();

    public TableroView(TableroController controller) {
        this.controller = controller;
        construir();
        actualizar();
    }

    private void construir() {
        Label titulo = new Label("Tablero de control");
        titulo.getStyleClass().add("titulo-seccion");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button actualizar = new Button("Actualizar");
        actualizar.setOnAction(e -> actualizar());
        HBox cabecera = new HBox(12, titulo, espacio, estado, actualizar);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        grafico.setTitle("Presupuesto y costo ejecutado por proyecto");
        grafico.setAnimated(false);
        grafico.setPrefHeight(330);
        grafico.setLegendVisible(true);
        grafico.setCategoryGap(4);
        grafico.setBarGap(1);
        HBox.setHgrow(grafico, Priority.ALWAYS);

        Label tituloAlertas = new Label("Alertas de gestión");
        tituloAlertas.getStyleClass().add("subtitulo");
        alertas.setPrefWidth(430);
        alertas.setPlaceholder(new Label("No hay alertas."));
        alertas.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(Alerta alerta, boolean vacia) {
                super.updateItem(alerta, vacia);
                getStyleClass().removeAll("alerta-desvio", "alerta-atraso");
                if (vacia || alerta == null) {
                    setText(null);
                } else {
                    setText(alerta.getTipo() + " · " + alerta.getProyecto() + "\n" + alerta.getMensaje());
                    setWrapText(true);
                    setPrefWidth(0);
                    getStyleClass().add(alerta.getPrioridad() >= 100 ? "alerta-desvio" : "alerta-atraso");
                }
            }
        });
        VBox panelAlertas = new VBox(8, tituloAlertas, alertas);
        VBox.setVgrow(alertas, Priority.ALWAYS);

        HBox centro = new HBox(16, grafico, panelAlertas);
        centro.setPrefHeight(330);

        tabla.getColumns().add(Tablas.columna("Proyecto", 190, Proyecto::getNombre));
        tabla.getColumns().add(Tablas.columna("Líder", 140, Proyecto::getNombreLider));
        tabla.getColumns().add(Tablas.columna("Estado", 110, p -> p.getEstado().toString()));
        tabla.getColumns().add(Tablas.columnaAvance("Avance", 190, Proyecto::calcularAvance));
        tabla.getColumns().add(Tablas.columna("Presupuesto", 130, p -> Formato.moneda(p.getPresupuestoInicial())));
        tabla.getColumns().add(Tablas.columna("Costo ejecutado", 130, p -> Formato.moneda(p.calcularCostoEjecutado())));
        tabla.getColumns().add(Tablas.columna("Ejecución", 90, p -> Formato.porcentaje(p.calcularEjecucionPresupuestaria())));
        Tablas.configurar(tabla, "No hay proyectos registrados.");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        raiz.getChildren().addAll(cabecera, tarjetas, centro, tabla);
        raiz.getStyleClass().add("contenido");
    }

    public void actualizar() {
        try {
            List<Proyecto> proyectos = controller.obtenerProyectos();
            LocalDate hoy = LocalDate.now();
            IndicadoresTablero indicadores = controller.calcularIndicadores(proyectos, hoy);
            mostrarTarjetas(indicadores);
            mostrarGrafico(proyectos);
            alertas.setItems(FXCollections.observableArrayList(controller.generarAlertas(proyectos, hoy)));
            tabla.setItems(FXCollections.observableArrayList(proyectos));
            estado.setText("Datos al " + Formato.fecha(hoy));
            if (proyectos.isEmpty()) {
                estado.setText("No existen proyectos: el tablero se muestra vacío.");
            }
        } catch (PersistenciaException e) {
            estado.setText(e.getMessage());
        }
    }

    private void mostrarTarjetas(IndicadoresTablero ind) {
        tarjetas.getChildren().setAll(
                tarjeta("Proyectos activos", ind.getProyectosActivos() + " de " + ind.getTotalProyectos()),
                tarjeta("Avance promedio", Formato.porcentaje(ind.getAvancePromedio())),
                tarjeta("Presupuesto activo", Formato.moneda(ind.getPresupuestoTotal())),
                tarjeta("Costo ejecutado", Formato.moneda(ind.getCostoTotal())),
                tarjeta("Hitos atrasados", String.valueOf(ind.getHitosAtrasados())));
    }

    private VBox tarjeta(String titulo, String valor) {
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("tarjeta-titulo");
        Label numero = new Label(valor);
        numero.getStyleClass().add("tarjeta-valor");
        VBox tarjeta = new VBox(6, etiqueta, numero);
        tarjeta.getStyleClass().add("tarjeta");
        HBox.setHgrow(tarjeta, Priority.ALWAYS);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        return tarjeta;
    }

    private void mostrarGrafico(List<Proyecto> proyectos) {
        XYChart.Series<Number, String> presupuesto = new XYChart.Series<>();
        presupuesto.setName("Presupuesto");
        XYChart.Series<Number, String> costo = new XYChart.Series<>();
        costo.setName("Costo ejecutado");
        for (Proyecto p : proyectos) {
            presupuesto.getData().add(new XYChart.Data<>(p.getPresupuestoInicial(), p.getNombre()));
            costo.getData().add(new XYChart.Data<>(p.calcularCostoEjecutado(), p.getNombre()));
        }
        grafico.getData().setAll(List.of(presupuesto, costo));
    }

    public Parent getVista() {
        return raiz;
    }
}
