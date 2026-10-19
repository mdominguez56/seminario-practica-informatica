package com.fundacioncana.vista;

import com.fundacioncana.controlador.AuthController;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.excepciones.SistemaException;
import com.fundacioncana.modelo.Usuario;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/** Pantalla de inicio de sesión (CU01). */
public class LoginView {

    private final AuthController authController;
    private final Consumer<Usuario> alIngresar;

    private final TextField email = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label mensaje = new Label();
    private final StackPane raiz = new StackPane();

    public LoginView(AuthController authController, Consumer<Usuario> alIngresar) {
        this.authController = authController;
        this.alIngresar = alIngresar;
        construir();
    }

    private void construir() {
        Label titulo = new Label("Fundación María de Caná");
        titulo.getStyleClass().add("login-titulo");
        Label subtitulo = new Label("Sistema de gestión de proyectos solidarios");
        subtitulo.getStyleClass().add("texto-secundario");

        email.setPromptText("usuario@cana.org");
        password.setPromptText("Contraseña");
        Button ingresar = new Button("Ingresar");
        ingresar.getStyleClass().add("boton-primario");
        ingresar.setMaxWidth(Double.MAX_VALUE);
        ingresar.setDefaultButton(true);
        ingresar.setOnAction(e -> ingresar());

        mensaje.getStyleClass().add("mensaje-error");
        mensaje.setWrapText(true);

        Label ayuda = new Label("Usuarios de prueba: ver README del repositorio.");
        ayuda.getStyleClass().add("texto-ayuda");

        VBox tarjeta = new VBox(12, titulo, subtitulo, new Label("Email"), email,
                new Label("Contraseña"), password, ingresar, mensaje, ayuda);
        tarjeta.getStyleClass().add("login-tarjeta");
        tarjeta.setMaxWidth(380);
        tarjeta.setMaxHeight(VBox.USE_PREF_SIZE);

        raiz.getChildren().add(tarjeta);
        raiz.setAlignment(Pos.CENTER);
        raiz.getStyleClass().add("login-fondo");
    }

    /** Toma los datos, delega la validación al controlador y muestra el error si corresponde. */
    public void ingresar() {
        try {
            Usuario usuario = authController.iniciarSesion(email.getText(), password.getText());
            alIngresar.accept(usuario);
        } catch (PersistenciaException e) {
            mensaje.setText(e.getMessage());
        } catch (SistemaException e) {
            mensaje.setText(e.getMessage());
            password.clear();
        }
    }

    public void completar(String emailIngresado, String passwordIngresado) {
        email.setText(emailIngresado);
        password.setText(passwordIngresado);
    }

    public Parent getVista() {
        return raiz;
    }
}
