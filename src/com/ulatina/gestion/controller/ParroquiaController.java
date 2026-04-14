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

    public List<Parroquia> findAll(){
        try{
            return parroquiaDAO.findAll();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void saveParroquia(String nombre, String sector_filial,String vicaria, String direccion, String telefono, boolean activa){
        Parroquia p = new Parroquia();
        p.setNombre(nombre);
        p.setSectorFilial(sector_filial);
        p.setVicaria(vicaria);
        p.setDireccion(direccion);
        p.setTelefono(telefono);
        p.setActiva(activa);

        parroquiaDAO.save(p);

    }

    public void editParroquias(Parroquia p){
        parroquiaDAO.update(p);
    }
    //desactivar PARROQUIAS

}
