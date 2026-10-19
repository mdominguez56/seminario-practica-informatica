package com.fundacioncana.vista;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.util.function.Function;

/** Métodos auxiliares para construir columnas de TableView sin repetir código. */
public final class Tablas {

    private Tablas() {
    }

    public static <T> TableColumn<T, String> columna(String titulo, double ancho, Function<T, String> valor) {
        TableColumn<T, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(celda -> new SimpleStringProperty(valor.apply(celda.getValue())));
        columna.setPrefWidth(ancho);
        return columna;
    }

    /** Columna que muestra un porcentaje con una barra de progreso. */
    public static <T> TableColumn<T, BigDecimal> columnaAvance(String titulo, double ancho,
                                                               Function<T, BigDecimal> valor) {
        TableColumn<T, BigDecimal> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(celda -> new SimpleObjectProperty<>(valor.apply(celda.getValue())));
        columna.setPrefWidth(ancho);
        columna.setCellFactory(c -> new TableCell<>() {
            private final ProgressBar barra = new ProgressBar();
            private final Label texto = new Label();
            private final HBox contenido = new HBox(8, barra, texto);

            {
                barra.setPrefWidth(90);
                texto.setMinWidth(Label.USE_PREF_SIZE);
            }

            @Override
            protected void updateItem(BigDecimal avance, boolean vacia) {
                super.updateItem(avance, vacia);
                if (vacia || avance == null) {
                    setGraphic(null);
                } else {
                    barra.setProgress(avance.doubleValue() / 100);
                    texto.setText(avance.stripTrailingZeros().toPlainString() + " %");
                    setGraphic(contenido);
                }
            }
        });
        return columna;
    }

    public static <T> void configurar(TableView<T> tabla, String mensajeVacia) {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label(mensajeVacia));
    }
}
