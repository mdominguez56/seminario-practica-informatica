package com.fundacioncana.vista;

import com.fundacioncana.excepciones.SistemaException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Diálogo genérico de alta o edición. Al presionar "Guardar" ejecuta la acción recibida;
 * si se produce una excepción del sistema, muestra el mensaje y mantiene el formulario abierto.
 */
public class Formulario {

    /** Acción que puede fallar con una excepción propia del sistema. */
    @FunctionalInterface
    public interface Accion {
        void ejecutar() throws SistemaException;
    }

    private final Dialog<ButtonType> dialogo = new Dialog<>();
    private final GridPane grilla = new GridPane();
    private final Label mensajeError = new Label();
    private int fila;

    public Formulario(String titulo) {
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(titulo);
        grilla.setHgap(12);
        grilla.setVgap(10);
        mensajeError.getStyleClass().add("mensaje-error");
        mensajeError.setWrapText(true);
        mensajeError.setMaxWidth(460);
        VBox contenido = new VBox(14, grilla, mensajeError);
        contenido.setPadding(new Insets(10, 4, 0, 4));
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Guardar", ButtonType.OK.getButtonData()), ButtonType.CANCEL);
        dialogo.getDialogPane().getStylesheets().add(
                Formulario.class.getResource("/css/estilos.css").toExternalForm());
    }

    public Formulario campo(String etiqueta, Node control) {
        grilla.addRow(fila++, new Label(etiqueta), control);
        return this;
    }

    public Dialog<ButtonType> getDialogo() {
        return dialogo;
    }

    public void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
    }

    /** Asocia la acción al botón Guardar sin mostrar el diálogo. */
    public void configurar(Accion accion) {
        Button guardar = (Button) dialogo.getDialogPane().lookupButton(dialogo.getDialogPane().getButtonTypes().get(0));
        guardar.addEventFilter(ActionEvent.ACTION, evento -> {
            try {
                accion.ejecutar();
            } catch (SistemaException e) {
                mostrarError(e.getMessage());
                evento.consume(); // Evita que el diálogo se cierre.
            }
        });
    }
}
