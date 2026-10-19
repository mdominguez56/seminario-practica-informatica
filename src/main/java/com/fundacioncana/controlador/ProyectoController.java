package com.fundacioncana.controlador;

import com.fundacioncana.dao.ProyectoDao;
import com.fundacioncana.dao.UsuarioDao;
import com.fundacioncana.excepciones.PermisoDenegadoException;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.excepciones.ValidacionException;
import com.fundacioncana.modelo.EstadoProyecto;
import com.fundacioncana.modelo.Proyecto;
import com.fundacioncana.modelo.Rol;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.util.Algoritmos;
import com.fundacioncana.util.CriterioOrden;

import java.util.ArrayList;
import java.util.List;

/** CU02 - Registrar proyecto y RF03/RF04: consulta y modificación de proyectos. */
public class ProyectoController {

    private final Usuario usuarioActual;
    private final ProyectoDao proyectoDao;
    private final UsuarioDao usuarioDao;

    public ProyectoController(Usuario usuarioActual, ProyectoDao proyectoDao, UsuarioDao usuarioDao) {
        this.usuarioActual = usuarioActual;
        this.proyectoDao = proyectoDao;
        this.usuarioDao = usuarioDao;
    }

    public List<Proyecto> listarProyectos() throws PersistenciaException {
        return proyectoDao.listar();
    }

    public List<Usuario> listarLideres() throws PersistenciaException {
        return usuarioDao.listarPorRol(Rol.LIDER_PROYECTO);
    }

    public void registrarProyecto(Proyecto proyecto)
            throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        verificarPermiso();
        proyecto.validar();
        proyectoDao.insertar(proyecto);
    }

    public void actualizarProyecto(Proyecto proyecto)
            throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        verificarPermiso();
        if (proyecto.esNueva()) {
            throw new ValidacionException("Debe seleccionar un proyecto existente.");
        }
        proyecto.validar();
        proyectoDao.actualizar(proyecto);
    }

    /** Baja lógica: el proyecto se conserva con estado CANCELADO. */
    public void cancelarProyecto(Proyecto proyecto)
            throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        verificarPermiso();
        if (proyecto.getEstado() == EstadoProyecto.FINALIZADO) {
            throw new ValidacionException("Un proyecto finalizado no puede cancelarse.");
        }
        proyectoDao.eliminar(proyecto.getId());
        proyecto.setEstado(EstadoProyecto.CANCELADO);
    }

    /**
     * Filtra y ordena la lista recibida. Si el texto es numérico, busca el código con
     * búsqueda binaria; si no, busca por coincidencia parcial del nombre con búsqueda lineal.
     */
    public List<Proyecto> buscarYOrdenar(List<Proyecto> proyectos, String texto, CriterioOrden criterio) {
        List<Proyecto> resultado;
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        if (filtro.isEmpty()) {
            resultado = new ArrayList<>(proyectos);
        } else if (filtro.matches("\\d+")) {
            List<Proyecto> porCodigo = new ArrayList<>(proyectos);
            Algoritmos.ordenarPorInsercion(porCodigo, CriterioOrden.ID.getComparador());
            int posicion = Algoritmos.busquedaBinaria(porCodigo, Integer.parseInt(filtro), Proyecto::getId);
            resultado = new ArrayList<>();
            if (posicion >= 0) {
                resultado.add(porCodigo.get(posicion));
            }
        } else {
            resultado = Algoritmos.busquedaLineal(proyectos, p -> p.getNombre().toLowerCase().contains(filtro));
        }
        Algoritmos.ordenarPorInsercion(resultado, criterio.getComparador());
        return resultado;
    }

    private void verificarPermiso() throws PermisoDenegadoException {
        if (!usuarioActual.puedeGestionarProyectos()) {
            throw new PermisoDenegadoException("El rol " + usuarioActual.getRol().getDescripcion()
                    + " no puede modificar proyectos.");
        }
    }
}
