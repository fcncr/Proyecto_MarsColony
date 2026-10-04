package com.mycompany.mars_colony.controlador.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AutenticadorAdmin {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SAL = 16;

    private final String usuario;
    private final byte[] hashClave;
    private final byte[] sal;

    public AutenticadorAdmin(String usuario, char[] clave) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("El usuario administrativo no puede estar vacío.");
        }

        if (clave == null || clave.length == 0) {
            throw new IllegalArgumentException("La contraseña administrativa no puede estar vacía.");
        }

        this.usuario = usuario;
        this.sal = generarSal();
        this.hashClave = generarHash(clave, sal);
    }

    public boolean autenticar(String u, char[] clave) {
        if (u == null || clave == null) {
            return false;
        }

        if (!usuario.equals(u)) {
            return false;
        }

        byte[] hashIngresado = generarHash(clave, sal);

        try {
            return MessageDigest.isEqual(hashClave, hashIngresado);
        } finally {
            Arrays.fill(hashIngresado, (byte) 0);
        }
    }

    private byte[] generarSal() {
        byte[] nuevaSal = new byte[LONGITUD_SAL];
        SecureRandom random = new SecureRandom();
        random.nextBytes(nuevaSal);
        return nuevaSal;
    }

    private byte[] generarHash(char[] clave, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(clave, sal, ITERACIONES, LONGITUD_HASH);

        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return fabrica.generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña administrativa.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}