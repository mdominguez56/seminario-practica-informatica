package com.fundacioncana.vista;

import com.fundacioncana.Contexto;
import com.fundacioncana.modelo.OpcionMenu;
import com.fundacioncana.modelo.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Ventana principal con el menú de selección. Las opciones se generan a partir del rol
 * del usuario que inició sesión: cada subclase de Usuario devuelve su propia lista.
 */
public class MainView {

    private final Usuario usuario;
    private final Contexto contexto;
    private final Runnable alCerrarSesion;
    private final BorderPane raiz = new BorderPane();
    private final ToggleGroup grupoMenu = new ToggleGroup();

    public MainView(Usuario usuario, Contexto contexto, Runnable alCerrarSesion) {
        this.usuario = usuario;
        this.contexto = contexto;
        this.alCerrarSesion = alCerrarSesion;
        construir();
    }

    private void construir() {
        raiz.setTop(crearEncabezado());
        raiz.setLeft(crearMenu());
        seleccionar(usuario.getOpcionesMenu().get(0));
    }

    private Node crearEncabezado() {
        Label marca = new Label("Fundación María de Caná");
        marca.getStyleClass().add("encabezado-titulo");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label datosUsuario = new Label(usuario.getNombre() + "  ·  " + usuario.getRol().getDescripcion());
        datosUsuario.getStyleClass().add("encabezado-usuario");
        Button salir = new Button("Cerrar sesión");
        salir.getStyleClass().add("boton-encabezado");
        salir.setOnAction(e -> alCerrarSesion.run());
        HBox encabezado = new HBox(16, marca, espacio, datosUsuario, salir);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.getStyleClass().add("encabezado");
        return encabezado;
    }

    private Node crearMenu() {
        VBox menu = new VBox(6);
        menu.getStyleClass().add("menu-lateral");
        Label titulo = new Label("MENÚ");
        titulo.getStyleClass().add("menu-titulo");
        menu.getChildren().add(titulo);
        for (OpcionMenu opcion : usuario.getOpcionesMenu()) {
            ToggleButton boton = new ToggleButton(opcion.getTitulo());
            boton.setToggleGroup(grupoMenu);
            boton.setUserData(opcion);
            boton.setMaxWidth(Double.MAX_VALUE);
            boton.getStyleClass().add("menu-opcion");
            boton.setOnAction(e -> seleccionar(opcion));
            menu.getChildren().add(boton);
        }
        menu.setPadding(new Insets(18, 12, 18, 12));
        return menu;
    }

    /** Muestra en el centro la pantalla que corresponde a la opción elegida. */
    public void seleccionar(OpcionMenu opcion) {
        grupoMenu.getToggles().forEach(t -> t.setSelected(t.getUserData() == opcion));
        Node contenido = switch (opcion) {
            case TABLERO -> new TableroView(contexto.crearTableroController()).getVista();
            case PROYECTOS -> new ProyectosView(usuario, contexto.crearProyectoController(usuario),
                    contexto.crearHitoController(usuario)).getVista();
            case MIS_HITOS -> new MisHitosView(usuario, contexto.crearHitoController(usuario)).getVista();
        };
        raiz.setCenter(contenido);
    }

    public Parent getVista() {
        return raiz;
    }
}
