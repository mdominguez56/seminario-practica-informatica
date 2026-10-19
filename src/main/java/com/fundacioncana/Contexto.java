package com.fundacioncana;

import com.fundacioncana.controlador.AuthController;
import com.fundacioncana.controlador.HitoController;
import com.fundacioncana.controlador.ProyectoController;
import com.fundacioncana.controlador.TableroController;
import com.fundacioncana.dao.CostoDao;
import com.fundacioncana.dao.HitoDao;
import com.fundacioncana.dao.ProyectoDao;
import com.fundacioncana.dao.UsuarioDao;
import com.fundacioncana.modelo.Usuario;

/** Crea una única vez los DAO y entrega controladores configurados para el usuario de la sesión. */
public class Contexto {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final CostoDao costoDao = new CostoDao();
    private final HitoDao hitoDao = new HitoDao(costoDao);
    private final ProyectoDao proyectoDao = new ProyectoDao(hitoDao);

    public AuthController crearAuthController() {
        return new AuthController(usuarioDao);
    }

    public ProyectoController crearProyectoController(Usuario usuario) {
        return new ProyectoController(usuario, proyectoDao, usuarioDao);
    }

    public HitoController crearHitoController(Usuario usuario) {
        return new HitoController(usuario, hitoDao, costoDao, usuarioDao);
    }

    public TableroController crearTableroController() {
        return new TableroController(proyectoDao);
    }
}
