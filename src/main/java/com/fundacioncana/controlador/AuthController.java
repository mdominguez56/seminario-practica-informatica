package com.fundacioncana.controlador;

import com.fundacioncana.dao.UsuarioDao;
import com.fundacioncana.excepciones.AutenticacionException;
import com.fundacioncana.excepciones.PersistenciaException;
import com.fundacioncana.excepciones.ValidacionException;
import com.fundacioncana.modelo.Usuario;
import com.fundacioncana.util.Seguridad;

import java.util.regex.Pattern;

/** CU01 - Iniciar sesión. */
public class AuthController {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int MAX_INTENTOS = 3;

    private final UsuarioDao usuarioDao;
    private int intentosFallidos;

    public AuthController(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public Usuario iniciarSesion(String email, String password)
            throws ValidacionException, AutenticacionException, PersistenciaException {
        if (intentosFallidos >= MAX_INTENTOS) {
            throw new AutenticacionException("Se superó la cantidad de intentos permitidos. Reinicie la aplicación.");
        }
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ValidacionException("Ingrese el email y la contraseña.");
        }
        if (!FORMATO_EMAIL.matcher(email.trim()).matches()) {
            throw new ValidacionException("El email no tiene un formato válido.");
        }
        return usuarioDao.autenticar(email.trim().toLowerCase(), Seguridad.hashSha256(password))
                .orElseThrow(() -> {
                    intentosFallidos++;
                    return new AutenticacionException("Email o contraseña incorrectos. Intentos restantes: "
                            + (MAX_INTENTOS - intentosFallidos) + ".");
                });
    }
}
