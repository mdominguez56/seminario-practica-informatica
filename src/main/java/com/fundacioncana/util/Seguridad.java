package com.fundacioncana.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Las contraseñas nunca se guardan en texto plano: se compara su hash SHA-256. */
public final class Seguridad {

    private Seguridad() {
    }

    public static String hashSha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 está disponible en toda JVM estándar.
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
