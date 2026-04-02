package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.model.Parroquia;

import java.util.Collections;
import java.util.List;

/**
 * Controlador para operaciones de parroquia.
 */
public class ParroquiaController {

    private final IParroquiaDAO parroquiaDAO = new ParroquiaDAOImpl();

    /**
     * Devuelve las parroquias disponibles.
     * Intenta primero las activas; si no hay, retorna todas.
     */
    public List<Parroquia> findParroquiasDisponibles() {
        try {
            List<Parroquia> activas = parroquiaDAO.findActivas();
            if (activas != null && !activas.isEmpty())
                return activas;
            return parroquiaDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
