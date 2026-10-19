package com.fundacioncana.vista;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Mensajes estándar para informar el resultado de cada operación (RNF07). */
public final class Dialogos {

    private Dialogos() {
    }

    public static void error(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void informacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        return alerta.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.showAndWait();
    }
}
