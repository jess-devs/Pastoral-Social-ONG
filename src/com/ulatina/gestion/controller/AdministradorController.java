package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.*;
import com.ulatina.gestion.dao.impl.*;
import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.EstadoExpediente;

import java.util.Collections;
import java.util.List;

public class AdministradorController {
    private final IUsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    // ═════════════════════════════════════════════════════════════════════════
    // CONSULTAS — Usuario
    // ═════════════════════════════════════════════════════════════════════════

    public List<Usuario> findAll() {
        try {
            return usuarioDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Usuario findByCorreo(String email) {
        try {
            return usuarioDAO.findByEmail(email);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ESCRITURA
    // ═════════════════════════════════════════════════════════════════════════

    public void guardarUsuario(Usuario u) {
        if (u.getId() == null)
            usuarioDAO.save(u);
        else
            usuarioDAO.update(u);
    }

}

