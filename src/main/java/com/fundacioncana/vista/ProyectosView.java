package com.fundacioncana.vista;

import com.fundacioncana.controlador.HitoController;
import com.fundacioncana.controlador.ProyectoController;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.excepciones.SistemaException;
import com.fundacioncana.modelo.Costo;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Proyecto;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.util.CriterioOrden;
import com.fundacioncana.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Gestión de proyectos, hitos y costos (CU02 y CU03). El Consejo la ve en modo de solo lectura. */
public class ProyectosView {

    private final Usuario usuario;
    private final ProyectoController proyectoController;
    private final HitoController hitoController;

    private final VBox raiz = new VBox(12);
    private final TextField buscar = new TextField();
    private final ComboBox<CriterioOrden> orden = new ComboBox<>(FXCollections.observableArrayList(CriterioOrden.values()));
    private final TableView<Proyecto> tablaProyectos = new TableView<>();
    private final TableView<Hito> tablaHitos = new TableView<>();
    private final TableView<Costo> tablaCostos = new TableView<>();
    private final Label detalle = new Label("Seleccione un proyecto para ver sus hitos.");
    private final Label estado = new Label();
    private List<Proyecto> proyectos = new ArrayList<>();

    public ProyectosView(Usuario usuario, ProyectoController proyectoController, HitoController hitoController) {
        this.usuario = usuario;
        this.proyectoController = proyectoController;
        this.hitoController = hitoController;
        construir();
        cargar();
    }

    private void construir() {
        Label titulo = new Label("Proyectos e hitos");
        titulo.getStyleClass().add("titulo-seccion");
        buscar.setPromptText("Buscar por nombre o código");
        buscar.setPrefWidth(240);
        buscar.textProperty().addListener((obs, anterior, actual) -> filtrar());
        orden.setValue(CriterioOrden.ID);
        orden.setOnAction(e -> filtrar());
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox cabecera = new HBox(10, titulo, espacio, estado, new Label("Ordenar por:"), orden, buscar);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        tablaProyectos.getColumns().add(Tablas.columna("Código", 60, p -> String.valueOf(p.getId())));
        tablaProyectos.getColumns().add(Tablas.columna("Proyecto", 200, Proyecto::getNombre));
        tablaProyectos.getColumns().add(Tablas.columna("Líder", 140, Proyecto::getNombreLider));
        tablaProyectos.getColumns().add(Tablas.columna("Estado", 110, p -> p.getEstado().toString()));
        tablaProyectos.getColumns().add(Tablas.columna("Inicio", 90, p -> Formato.fecha(p.getFechaInicio())));
        tablaProyectos.getColumns().add(Tablas.columna("Fin", 90, p -> Formato.fecha(p.getFechaFin())));
        tablaProyectos.getColumns().add(Tablas.columna("Presupuesto", 120, p -> Formato.moneda(p.getPresupuestoInicial())));
        tablaProyectos.getColumns().add(Tablas.columnaAvance("Avance", 170, Proyecto::calcularAvance));
        Tablas.configurar(tablaProyectos, "No hay proyectos que coincidan con la búsqueda.");
        tablaProyectos.setPrefHeight(230);
        tablaProyectos.getSelectionModel().selectedItemProperty().addListener((obs, a, p) -> mostrarHitos(p));

        LocalDate hoy = LocalDate.now();
        tablaHitos.getColumns().add(Tablas.columna("Hito", 180, Hito::getNombre));
        tablaHitos.getColumns().add(Tablas.columna("Responsable", 115, Hito::getNombreResponsable));
        tablaHitos.getColumns().add(Tablas.columna("Fecha límite", 90, h -> Formato.fecha(h.getFechaLimite())));
        tablaHitos.getColumns().add(Tablas.columnaAvance("Avance", 150, Hito::getPorcentajeAvance));
        tablaHitos.getColumns().add(Tablas.columna("Estado", 95,
                h -> h.estaAtrasado(hoy) ? "Atrasado" : h.getEstado().toString()));
        tablaHitos.getColumns().add(Tablas.columna("Costo", 100, h -> Formato.moneda(h.calcularCostoTotal())));
        Tablas.configurar(tablaHitos, "El proyecto no tiene hitos.");
        tablaHitos.getSelectionModel().selectedItemProperty().addListener((obs, a, h) -> mostrarCostos(h));

        tablaCostos.getColumns().add(Tablas.columna("Fecha", 85, c -> Formato.fecha(c.getFecha())));
        tablaCostos.getColumns().add(Tablas.columna("Descripción", 150, Costo::getDescripcion));
        tablaCostos.getColumns().add(Tablas.columna("Monto", 95, c -> Formato.moneda(c.getMonto())));
        Tablas.configurar(tablaCostos, "Sin costos.");
        tablaCostos.setPrefWidth(340);
        tablaCostos.setMinWidth(340);

        detalle.getStyleClass().add("texto-secundario");
        detalle.setWrapText(true);

        HBox inferior = new HBox(12, tablaHitos, tablaCostos);
        HBox.setHgrow(tablaHitos, Priority.ALWAYS);
        VBox.setVgrow(inferior, Priority.ALWAYS);

        raiz.getChildren().addAll(cabecera, tablaProyectos);
        if (usuario.puedeGestionarProyectos()) {
            raiz.getChildren().add(crearBotonesProyecto());
        }
        Label tituloHitos = new Label("Hitos y costos del proyecto seleccionado");
        tituloHitos.getStyleClass().add("subtitulo");
        raiz.getChildren().addAll(tituloHitos, detalle, inferior);
        if (usuario.puedeGestionarProyectos()) {
            raiz.getChildren().add(crearBotonesHito());
        }
        raiz.getStyleClass().add("contenido");
    }

    private HBox crearBotonesProyecto() {
        Button nuevo = new Button("Nuevo proyecto");
        nuevo.getStyleClass().add("boton-primario");
        nuevo.setOnAction(e -> abrirFormularioProyecto(null));
        Button editar = new Button("Modificar");
        editar.setOnAction(e -> conProyecto(this::abrirFormularioProyecto));
        Button cancelar = new Button("Cancelar proyecto");
        cancelar.getStyleClass().add("boton-peligro");
        cancelar.setOnAction(e -> conProyecto(this::cancelarProyecto));
        return new HBox(8, nuevo, editar, cancelar);
    }

    private HBox crearBotonesHito() {
        Button nuevo = new Button("Nuevo hito");
        nuevo.getStyleClass().add("boton-primario");
        nuevo.setOnAction(e -> conProyecto(p -> abrirFormularioHito(p, null)));
        Button editar = new Button("Modificar hito");
        editar.setOnAction(e -> conHito(h -> abrirFormularioHito(proyectoSeleccionado(), h)));
        Button avance = new Button("Actualizar avance");
        avance.setOnAction(e -> conHito(h -> ejecutarFormulario(Formularios.avance(hitoController, h),
                "Avance actualizado.")));
        Button costo = new Button("Registrar costo");
        costo.setOnAction(e -> conHito(h -> ejecutarFormulario(Formularios.costo(hitoController, h),
                "Costo registrado.")));
        Button eliminar = new Button("Eliminar hito");
        eliminar.getStyleClass().add("boton-peligro");
        eliminar.setOnAction(e -> conHito(this::eliminarHito));
        Button eliminarCosto = new Button("Eliminar costo");
        eliminarCosto.setOnAction(e -> eliminarCosto());
        return new HBox(8, nuevo, editar, avance, costo, eliminar, eliminarCosto);
    }

    /** Carga los proyectos desde la base y conserva la selección actual. */
    public void cargar() {
        int seleccionado = proyectoSeleccionado() == null ? -1 : proyectoSeleccionado().getId();
        try {
            proyectos = proyectoController.listarProyectos();
            estado.setText(proyectos.size() + " proyectos");
            filtrar();
            for (Proyecto p : tablaProyectos.getItems()) {
                if (p.getId() == seleccionado) {
                    tablaProyectos.getSelectionModel().select(p);
                }
            }
        } catch (PersistenciaException e) {
            estado.setText(e.getMessage());
        }
    }

    private void filtrar() {
        tablaProyectos.setItems(FXCollections.observableArrayList(
                proyectoController.buscarYOrdenar(proyectos, buscar.getText(), orden.getValue())));
    }

    private void mostrarHitos(Proyecto proyecto) {
        if (proyecto == null) {
            tablaHitos.getItems().clear();
            detalle.setText("Seleccione un proyecto para ver sus hitos.");
            return;
        }
        detalle.setText(proyecto.getNombre() + ": " + proyecto.getObjetivo() + "  ·  Costo ejecutado "
                + Formato.moneda(proyecto.calcularCostoEjecutado()) + " ("
                + Formato.porcentaje(proyecto.calcularEjecucionPresupuestaria()) + " del presupuesto)");
        tablaHitos.setItems(FXCollections.observableArrayList(proyecto.getHitos()));
    }

    private void mostrarCostos(Hito hito) {
        tablaCostos.setItems(hito == null
                ? FXCollections.observableArrayList()
                : FXCollections.observableArrayList(hito.getCostos()));
    }

    private void abrirFormularioProyecto(Proyecto proyecto) {
        try {
            Formulario formulario = Formularios.proyecto(proyectoController, proyecto, proyectoController.listarLideres());
            ejecutarFormulario(formulario, proyecto == null ? "Proyecto registrado en estado Planificación."
                    : "Proyecto actualizado.");
        } catch (PersistenciaException e) {
            Dialogos.error("Error", e.getMessage());
        }
    }

    private void abrirFormularioHito(Proyecto proyecto, Hito hito) {
        try {
            Formulario formulario = Formularios.hito(hitoController, proyecto, hito, hitoController.listarVoluntarios());
            ejecutarFormulario(formulario, hito == null ? "Hito registrado." : "Hito actualizado.");
        } catch (PersistenciaException e) {
            Dialogos.error("Error", e.getMessage());
        }
    }

    private void ejecutarFormulario(Formulario formulario, String mensajeExito) {
        boolean guardado = formulario.getDialogo().showAndWait()
                .filter(b -> b.getButtonData().isDefaultButton()).isPresent();
        cargar();
        if (guardado) {
            estado.setText(mensajeExito);
        }
    }

    private void cancelarProyecto(Proyecto proyecto) {
        if (Dialogos.confirmar("Cancelar proyecto", "El proyecto \"" + proyecto.getNombre()
                + "\" pasará a estado Cancelado y se conservará su historial. ¿Desea continuar?")) {
            ejecutar(() -> proyectoController.cancelarProyecto(proyecto), "Proyecto cancelado.");
        }
    }

    private void eliminarHito(Hito hito) {
        if (Dialogos.confirmar("Eliminar hito", "Se eliminará el hito \"" + hito.getNombre()
                + "\" junto con sus asignaciones y costos. ¿Desea continuar?")) {
            ejecutar(() -> hitoController.eliminarHito(hito), "Hito eliminado.");
        }
    }

    private void eliminarCosto() {
        Costo costo = tablaCostos.getSelectionModel().getSelectedItem();
        if (costo == null) {
            Dialogos.informacion("Seleccione un costo", "Debe seleccionar un costo de la tabla.");
        } else if (Dialogos.confirmar("Eliminar costo", "¿Eliminar el costo \"" + costo.getDescripcion() + "\"?")) {
            ejecutar(() -> hitoController.eliminarCosto(costo), "Costo eliminado.");
        }
    }

    private void ejecutar(Formulario.Accion accion, String mensajeExito) {
        try {
            accion.ejecutar();
            cargar();
            estado.setText(mensajeExito);
        } catch (SistemaException e) {
            Dialogos.error("No se pudo completar la operación", e.getMessage());
        }
    }

    private Proyecto proyectoSeleccionado() {
        return tablaProyectos.getSelectionModel().getSelectedItem();
    }

    private void conProyecto(java.util.function.Consumer<Proyecto> accion) {
        Proyecto proyecto = proyectoSeleccionado();
        if (proyecto == null) {
            Dialogos.informacion("Seleccione un proyecto", "Debe seleccionar un proyecto de la tabla.");
        } else {
            accion.accept(proyecto);
        }
    }

    private void conHito(java.util.function.Consumer<Hito> accion) {
        Hito hito = tablaHitos.getSelectionModel().getSelectedItem();
        if (hito == null) {
            Dialogos.informacion("Seleccione un hito", "Debe seleccionar un hito de la tabla.");
        } else {
            accion.accept(hito);
        }
    }

    public TableView<Proyecto> getTablaProyectos() {
        return tablaProyectos;
    }

    public TableView<Hito> getTablaHitos() {
        return tablaHitos;
    }

    public TextField getBuscar() {
        return buscar;
    }

    public ComboBox<CriterioOrden> getOrden() {
        return orden;
    }

    public Parent getVista() {
        return raiz;
    }
}
