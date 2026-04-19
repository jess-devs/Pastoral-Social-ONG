package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.*;
import com.ulatina.gestion.dao.impl.*;
import com.ulatina.gestion.model.*;
import java.util.Collections;
import java.util.List;

public class AdministradorController {
    private final IUsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    /**
     * Consulta al Usuario
     * @return
     */
    public List<Usuario> findAll() {
        try {
            return usuarioDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca el usuario mediante el correo
     * @param email
     * @return
     */
    public Usuario findByCorreo(String email) {
        try {
            return usuarioDAO.findByEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Guarda el usuario
     * @param u
     */
    public void guardarUsuario(Usuario u) {
        if (u.getId() == null)
            usuarioDAO.save(u);
        else
            usuarioDAO.update(u);
    }

}

