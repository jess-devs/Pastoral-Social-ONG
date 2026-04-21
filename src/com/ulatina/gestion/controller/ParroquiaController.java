package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;

import java.util.Collections;
import java.util.List;

/**
 * Controlador para operaciones de parroquia.
 * Este es utilizado en las vistas para el usuario.
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
    /**
     * Devuelve el listado completo de las parroquias ingresadas.
     */
    public List<Parroquia> findAll(){
        try{
            return parroquiaDAO.findAll();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }
    /**
     * Este metodo se utiliza para guardar en el panel de administrador
     * una parroquia nueva para ser utilizada en el sistema.
     * Bajo los parrametros:
     * @param nombre
     * @param sector_filial
     * @param vicaria
     * @param direccion
     * @param telefono
     * @param activa
     * Se llama a "parroquiaDao", para que almacene los datos y se ejecute.
     */
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
    /**
     * El siguiente metodo es utilizado para editar los datos de la
     * parroquia ingresada en caso de ser necesaria para el usuario.
     * Utiliza el siguiente parametro que extrae los actuales ingresados
     * en la base para hacer la edición.
     * @param p
     */
    public void editParroquias(Parroquia p){
        parroquiaDAO.update(p);
    }
    /**
     * El metodo desactivar parroquias es utilizado para que desde la visualizacion
     * principal el usuario pueda desactivar o poner como inactiva alguna parroquia
     * en caso de que sea neesario.
     * Esto es así para tener una trazabilidad de todas las gestiones que han ocurrido
     * por el sistema.
     */
    public void desactivarParroquia(Parroquia p) {
        p.setActiva(false);
        parroquiaDAO.update(p);
    }
}
