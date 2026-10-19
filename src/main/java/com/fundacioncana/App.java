package com.fundacioncana;

import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.vista.Dialogos;
import com.fundacioncana.vista.LoginView;
import com.fundacioncana.vista.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

/** Aplicación JavaFX: administra la ventana principal y la navegación entre login y menú. */
public class App extends Application {

    public static final String HOJA_ESTILOS = "/css/estilos.css";

    static {
        // Fechas, importes y botones de los diálogos en español (Argentina).
        Locale.setDefault(Locale.forLanguageTag("es-AR"));
    }

    private final Contexto contexto = new Contexto();
    private Stage ventana;

    @Override
    public void start(Stage ventana) {
        this.ventana = ventana;
        // Cualquier error no previsto se informa al usuario en lugar de cerrar la aplicación.
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) ->
                Platform.runLater(() -> Dialogos.error("Error inesperado", String.valueOf(error.getMessage()))));
        ventana.setTitle("Fundación María de Caná - Gestión de proyectos");
        mostrarLogin();
        ventana.show();
    }

    private void mostrarLogin() {
        LoginView login = new LoginView(contexto.crearAuthController(), this::mostrarPrincipal);
        cambiarEscena(login.getVista(), 520, 600);
    }

    private void mostrarPrincipal(Usuario usuario) {
        MainView principal = new MainView(usuario, contexto, this::mostrarLogin);
        cambiarEscena(principal.getVista(), 1366, 800);
    }

    private void cambiarEscena(Parent raiz, double ancho, double alto) {
        Scene escena = new Scene(raiz, ancho, alto);
        escena.getStylesheets().add(getClass().getResource(HOJA_ESTILOS).toExternalForm());
        ventana.setScene(escena);
        ventana.centerOnScreen();
    }
}
