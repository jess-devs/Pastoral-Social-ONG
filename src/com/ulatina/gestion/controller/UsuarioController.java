package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.IUsuarioDAO;
import com.ulatina.gestion.dao.impl.UsuarioDAOImpl;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Controlador para autenticación
 * Registro y cambio de contraseña de usuarios.
 */
public class UsuarioController {

    private final IUsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    /**
     * Auténtica un usuario por email y contraseña.
     * @param email
     * @param password
     * @return el Usuario si las credenciales son correctas y está activo, null en caso contrario.
     */
    public Usuario login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty())
            return null;
        Usuario u = usuarioDAO.findByEmail(email.trim().toLowerCase());
        if (u == null || !Boolean.TRUE.equals(u.getActivo()))
            return null;
        String hash = hashPassword(email.trim().toLowerCase(), password);
        return hash.equals(u.getPasswordHash()) ? u : null;
    }

    /**
     * Registra un nuevo usuario.
     * Lanza IllegalArgumentException si el email ya existe.
     * @param nombre
     * @param email
     * @param password
     * @param rol
     * @param parroquia
     */
    public void registrar(String nombre, String email, String password, RolUsuario rol, Parroquia parroquia) {
        String emailNorm = email.trim().toLowerCase();
        if (usuarioDAO.findByEmail(emailNorm) != null)
            throw new IllegalArgumentException("El correo ya está registrado.");

        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setEmail(emailNorm);
        u.setPasswordHash(hashPassword(emailNorm, password));
        u.setRol(rol != null ? rol : RolUsuario.VOLUNTARIO);
        u.setActivo(true);
        u.setParroquia(parroquia);
        usuarioDAO.save(u);
    }

    /**
     * Cambia la contraseña de un usuario identificado por email.
     * @param email
     * @param nuevaPassword
     * @return true si el usuario fue encontrado y la contraseña actualizada, false si no existe.
     */
    public boolean cambiarPassword(String email, String nuevaPassword) {
        String emailNorm = email.trim().toLowerCase();
        Usuario u = usuarioDAO.findByEmail(emailNorm);
        if (u == null) return false;
        u.setPasswordHash(hashPassword(emailNorm, nuevaPassword));
        usuarioDAO.update(u);
        return true;
    }

    /**
     * SHA-256
     * @param email
     * @param rawPassword
     * @return
     */
    private String hashPassword(String email, String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = email + ":" + rawPassword;
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}
