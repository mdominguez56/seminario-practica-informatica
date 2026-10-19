package com.fundacioncana.vista;

import com.fundacioncana.controlador.HitoController;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.modelo.EstadoHito;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/** Pantalla del voluntario: consulta sus hitos asignados y actualiza el avance (RF07). */
public class MisHitosView {

    private final Usuario usuario;
    private final HitoController controller;
    private final VBox raiz = new VBox(12);
    private final TableView<Hito> tabla = new TableView<>();
    private final Label resumen = new Label();

    public MisHitosView(Usuario usuario, HitoController controller) {
        this.usuario = usuario;
        this.controller = controller;
        construir();
        cargar();
    }

    private void construir() {
        Label titulo = new Label("Mis hitos asignados");
        titulo.getStyleClass().add("titulo-seccion");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button actualizar = new Button("Actualizar avance");
        actualizar.getStyleClass().add("boton-primario");
        actualizar.setOnAction(e -> actualizarAvance());
        HBox cabecera = new HBox(12, titulo, espacio, actualizar);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        LocalDate hoy = LocalDate.now();
        tabla.getColumns().add(Tablas.columna("Hito", 230, Hito::getNombre));
        tabla.getColumns().add(Tablas.columna("Descripción", 300, Hito::getDescripcion));
        tabla.getColumns().add(Tablas.columna("Fecha límite", 110, h -> Formato.fecha(h.getFechaLimite())));
        tabla.getColumns().add(Tablas.columnaAvance("Avance", 190, Hito::getPorcentajeAvance));
        tabla.getColumns().add(Tablas.columna("Estado", 110, h -> h.getEstado().toString()));
        tabla.getColumns().add(Tablas.columna("Alerta", 90, h -> h.estaAtrasado(hoy) ? "Atrasado" : ""));
        Tablas.configurar(tabla, "No tiene hitos asignados.");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        resumen.getStyleClass().add("texto-secundario");
        raiz.getChildren().addAll(cabecera, resumen, tabla);
        raiz.getStyleClass().add("contenido");
    }

    public void cargar() {
        try {
            List<Hito> hitos = controller.listarMisHitos();
            tabla.setItems(FXCollections.observableArrayList(hitos));
            int pendientes = 0;
            int atrasados = 0;
            for (Hito hito : hitos) {
                if (hito.getEstado() != EstadoHito.COMPLETADO) {
                    pendientes++;
                }
                if (hito.estaAtrasado(LocalDate.now())) {
                    atrasados++;
                }
            }
            resumen.setText("Hola, " + usuario.getNombre() + ". Tiene " + hitos.size() + " hitos asignados: "
                    + pendientes + " sin completar y " + atrasados + " atrasados.");
        } catch (PersistenciaException e) {
            resumen.setText(e.getMessage());
        }
    }

    private void actualizarAvance() {
        Hito hito = tabla.getSelectionModel().getSelectedItem();
        if (hito == null) {
            Dialogos.informacion("Seleccione un hito", "Debe seleccionar un hito de la tabla.");
            return;
        }
        Formulario formulario = Formularios.avance(controller, hito);
        formulario.getDialogo().showAndWait();
        cargar();
    }

    public TableView<Hito> getTabla() {
        return tabla;
    }

    public Parent getVista() {
        return raiz;
    }
}
