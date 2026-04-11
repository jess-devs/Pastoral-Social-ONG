package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.dao.impl.EventoDAOImpl;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;

import java.util.Collections;
import java.util.Date;
import java.util.List;

//Controlador para las operaciones de eventos
public class EventoController {

    private final IEventoDAO eventoDAO = new EventoDAOImpl();

    //Hace las consultas

    public List<Evento> findAll() {
        try {
            return eventoDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Evento findById(Long id) {
        try {
            return eventoDAO.findById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Evento> findByParroquia(Long parroquiaId) {
        try {
            return eventoDAO.findByParroquia(parroquiaId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Evento> findByTipo(TipoEvento tipo) {
        try {
            return eventoDAO.findByTipo(tipo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Evento> findByFecha(Date fecha) {
        try {
            return eventoDAO.findByFecha(fecha);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Evento> findByNombre(String nombre) {
        try {
            return eventoDAO.findByNombre(nombre);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    //escritura

    public void guardarEvento(Evento evento) {
        if (evento.getId() == null)
            eventoDAO.save(evento);
        else
            eventoDAO.update(evento);
    }

    public void eliminarEvento(Long id) {
        eventoDAO.delete(id);
    }
}
