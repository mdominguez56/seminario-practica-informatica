package com.fundacioncana.vista;

import com.fundacioncana.controlador.HitoController;
import com.fundacioncana.controlador.ProyectoController;
import com.fundacioncana.excepciones.ValidacionException;
import com.fundacioncana.modelo.Costo;
import com.fundacioncana.modelo.EstadoProyecto;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Proyecto;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.util.Formato;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Construye los formularios de alta y edición. Cada método devuelve un Formulario listo para mostrar. */
public final class Formularios {

    private Formularios() {
    }

    /** Alta (proyecto == null) o modificación de un proyecto. */
    public static Formulario proyecto(ProyectoController controller, Proyecto proyecto,
                                      List<Usuario> lideres) {
        boolean esNuevo = proyecto == null;
        TextField nombre = new TextField(esNuevo ? "" : proyecto.getNombre());
        TextArea objetivo = new TextArea(esNuevo ? "" : proyecto.getObjetivo());
        objetivo.setPrefRowCount(3);
        objetivo.setWrapText(true);
        TextField presupuesto = new TextField(esNuevo ? "" : proyecto.getPresupuestoInicial().toPlainString());
        presupuesto.setPromptText("Ej.: 150000,00");
        DatePicker inicio = new DatePicker(esNuevo ? LocalDate.now() : proyecto.getFechaInicio());
        DatePicker fin = new DatePicker(esNuevo ? null : proyecto.getFechaFin());
        ComboBox<Usuario> lider = new ComboBox<>(FXCollections.observableArrayList(lideres));
        ComboBox<EstadoProyecto> estado = new ComboBox<>(FXCollections.observableArrayList(EstadoProyecto.values()));
        if (!esNuevo) {
            lideres.stream().filter(u -> u.getIdUsuario() == proyecto.getIdLider()).findFirst()
                    .ifPresent(lider::setValue);
            estado.setValue(proyecto.getEstado());
        }

        Formulario formulario = new Formulario(esNuevo ? "Registrar proyecto" : "Modificar proyecto")
                .campo("Nombre *", nombre)
                .campo("Objetivo *", objetivo)
                .campo("Presupuesto inicial *", presupuesto)
                .campo("Fecha de inicio *", inicio)
                .campo("Fecha de fin", fin)
                .campo("Líder responsable *", lider);
        if (!esNuevo) {
            formulario.campo("Estado", estado);
        }

        formulario.configurar(() -> {
            BigDecimal importe = Formato.leerImporte(presupuesto.getText(), "Presupuesto inicial");
            int idLider = lider.getValue() == null ? 0 : lider.getValue().getIdUsuario();
            if (esNuevo) {
                controller.registrarProyecto(new Proyecto(nombre.getText(), objetivo.getText(), importe,
                        inicio.getValue(), fin.getValue(), idLider));
            } else {
                proyecto.setNombre(nombre.getText());
                proyecto.setObjetivo(objetivo.getText());
                proyecto.setPresupuestoInicial(importe);
                proyecto.setFechaInicio(inicio.getValue());
                proyecto.setFechaFin(fin.getValue());
                proyecto.setIdLider(idLider);
                proyecto.setEstado(estado.getValue());
                controller.actualizarProyecto(proyecto);
            }
        });
        return formulario;
    }

    /** Alta (hito == null) o modificación de un hito del proyecto indicado. */
    public static Formulario hito(HitoController controller, Proyecto proyecto, Hito hito,
                                  List<Usuario> voluntarios) {
        boolean esNuevo = hito == null;
        TextField nombre = new TextField(esNuevo ? "" : hito.getNombre());
        TextArea descripcion = new TextArea(esNuevo ? "" : hito.getDescripcion());
        descripcion.setPrefRowCount(3);
        descripcion.setWrapText(true);
        DatePicker fechaLimite = new DatePicker(esNuevo ? null : hito.getFechaLimite());
        ComboBox<Usuario> responsable = new ComboBox<>(FXCollections.observableArrayList(voluntarios));
        if (!esNuevo) {
            voluntarios.stream().filter(u -> u.getIdUsuario() == hito.getIdResponsable()).findFirst()
                    .ifPresent(responsable::setValue);
        }

        Formulario formulario = new Formulario((esNuevo ? "Nuevo hito - " : "Modificar hito - ") + proyecto.getNombre())
                .campo("Nombre *", nombre)
                .campo("Descripción", descripcion)
                .campo("Fecha límite *", fechaLimite)
                .campo("Responsable *", responsable);

        formulario.configurar(() -> {
            int idResponsable = responsable.getValue() == null ? 0 : responsable.getValue().getIdUsuario();
            if (esNuevo) {
                controller.guardarHito(new Hito(proyecto.getId(), nombre.getText(), descripcion.getText(),
                        fechaLimite.getValue(), idResponsable));
            } else {
                hito.setNombre(nombre.getText());
                hito.setDescripcion(descripcion.getText());
                hito.setFechaLimite(fechaLimite.getValue());
                hito.setIdResponsable(idResponsable);
                controller.guardarHito(hito);
            }
        });
        return formulario;
    }

    public static Formulario avance(HitoController controller, Hito hito) {
        TextField avance = new TextField(hito.getPorcentajeAvance().stripTrailingZeros().toPlainString());
        Formulario formulario = new Formulario("Actualizar avance - " + hito.getNombre())
                .campo("Avance actual", new javafx.scene.control.Label(Formato.porcentaje(hito.getPorcentajeAvance())))
                .campo("Nuevo avance (0 a 100) *", avance);
        formulario.configurar(() -> controller.actualizarAvance(hito, Formato.leerPorcentaje(avance.getText())));
        return formulario;
    }

    public static Formulario costo(HitoController controller, Hito hito) {
        DatePicker fecha = new DatePicker(LocalDate.now());
        TextField monto = new TextField();
        monto.setPromptText("Ej.: 3500,00");
        TextField descripcion = new TextField();
        Formulario formulario = new Formulario("Registrar costo - " + hito.getNombre())
                .campo("Fecha *", fecha)
                .campo("Monto *", monto)
                .campo("Descripción *", descripcion);
        formulario.configurar(() -> {
            if (fecha.getValue() == null) {
                throw new ValidacionException("La fecha del costo es obligatoria.");
            }
            controller.registrarCosto(new Costo(hito.getId(), fecha.getValue(),
                    Formato.leerImporte(monto.getText(), "Monto"), descripcion.getText()));
        });
        return formulario;
    }
}
