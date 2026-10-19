package com.fundacioncana.controlador;

import com.fundacioncana.dao.CostoDao;
import com.fundacioncana.dao.HitoDao;
import com.fundacioncana.dao.UsuarioDao;
import com.fundacioncana.excepciones.PermisoDenegadoException;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.excepciones.ValidacionException;
import com.fundacioncana.modelo.Costo;
import com.fundacioncana.modelo.Hito;
import com.fundacioncana.modelo.Rol;
import com.fundacioncana.modelo.Usuario;

import java.math.BigDecimal;
import java.util.List;

/** CU03 - Gestionar hitos (RF05 a RF08). */
public class HitoController {

    private final Usuario usuarioActual;
    private final HitoDao hitoDao;
    private final CostoDao costoDao;
    private final UsuarioDao usuarioDao;

    public HitoController(Usuario usuarioActual, HitoDao hitoDao, CostoDao costoDao, UsuarioDao usuarioDao) {
        this.usuarioActual = usuarioActual;
        this.hitoDao = hitoDao;
        this.costoDao = costoDao;
        this.usuarioDao = usuarioDao;
    }

    public List<Hito> listarMisHitos() throws PersistenciaException {
        return hitoDao.listarPorResponsable(usuarioActual.getIdUsuario());
    }

    public List<Usuario> listarVoluntarios() throws PersistenciaException {
        return usuarioDao.listarPorRol(Rol.VOLUNTARIO);
    }

    public void guardarHito(Hito hito) throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        verificarGestion();
        hito.validar();
        if (hito.esNueva()) {
            hitoDao.insertar(hito);
        } else {
            hitoDao.actualizar(hito);
        }
    }

    public void eliminarHito(Hito hito) throws PermisoDenegadoException, PersistenciaException {
        verificarGestion();
        hitoDao.eliminar(hito.getId());
    }

    /** RF07: el permiso depende del tipo concreto de usuario (polimorfismo). */
    public void actualizarAvance(Hito hito, BigDecimal nuevoAvance)
            throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        if (!usuarioActual.puedeActualizarAvance(hito)) {
            throw new PermisoDenegadoException("No tiene permiso para actualizar el avance de este hito.");
        }
        hito.actualizarAvance(nuevoAvance);
        hitoDao.actualizarAvance(hito);
    }

    public void registrarCosto(Costo costo) throws PermisoDenegadoException, ValidacionException, PersistenciaException {
        verificarGestion();
        costo.validar();
        costoDao.insertar(costo);
    }

    public void eliminarCosto(Costo costo) throws PermisoDenegadoException, PersistenciaException {
        verificarGestion();
        costoDao.eliminar(costo.getId());
    }

    private void verificarGestion() throws PermisoDenegadoException {
        if (!usuarioActual.puedeGestionarProyectos()) {
            throw new PermisoDenegadoException("Solo un líder de proyecto puede gestionar hitos y costos.");
        }
    }
}
