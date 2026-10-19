package com.fundacioncana.modelo;

/** Crea la subclase de Usuario que corresponde al rol leído desde la base de datos. */
public final class UsuarioFactory {

    private UsuarioFactory() {
    }

    public static Usuario crear(Rol rol, int id, String nombre, String email, boolean activo) {
        return switch (rol) {
            case DIRECTIVO -> new MiembroConsejo(id, nombre, email, activo);
            case LIDER_PROYECTO -> new LiderProyecto(id, nombre, email, activo);
            case VOLUNTARIO -> new Voluntario(id, nombre, email, activo);
        };
    }
}
