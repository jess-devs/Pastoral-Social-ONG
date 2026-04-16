package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.dao.impl.EventoDAOImpl;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;

import java.util.Collections;
import java.util.Date;
import java.util.List;

// Controlador que actúa como intermediario entre la interfaz gráfica y la base de datos.
// Toda operación sobre eventos pasa por aquí antes de llegar al DAO.
public class EventoController {

    // Se usa la interfaz IEventoDAO para no depender directamente de la implementación concreta.
    // Esto facilita cambiar la implementación en el futuro sin tocar el resto del código.
    private final IEventoDAO eventoDAO = new EventoDAOImpl();

    // Retorna la lista completa de eventos registrados en la base de datos.
    // Si ocurre un error, imprime el detalle y devuelve una lista vacía para no romper la interfaz.
    public List<Evento> findAll() {
        try {
            return eventoDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Busca y devuelve un evento por su ID único.
    // Si no lo encuentra o falla, devuelve null.
    public Evento findById(Long id) {
        try {
            return eventoDAO.findById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Devuelve todos los eventos que pertenecen a una parroquia específica,
    // identificada por su ID.
    public List<Evento> findByParroquia(Long parroquiaId) {
        try {
            return eventoDAO.findByParroquia(parroquiaId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Filtra los eventos según su tipo (REUNION, CAPACITACION, ENTREGA, etc.).
    public List<Evento> findByTipo(TipoEvento tipo) {
        try {
            return eventoDAO.findByTipo(tipo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Devuelve los eventos que coinciden con una fecha específica.
    public List<Evento> findByFecha(Date fecha) {
        try {
            return eventoDAO.findByFecha(fecha);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Busca eventos cuyo nombre coincida con el texto recibido.
    public List<Evento> findByNombre(String nombre) {
        try {
            return eventoDAO.findByNombre(nombre);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Decide si el evento se debe insertar o actualizar según si ya tiene ID asignado.
    // Si el ID es null significa que es nuevo, entonces se guarda; si ya tiene ID, se actualiza.
    public void guardarEvento(Evento evento) {
        if (evento.getId() == null)
            eventoDAO.save(evento);
        else
            eventoDAO.update(evento);
    }

    // Elimina un evento de la base de datos usando su ID.
    public void eliminarEvento(Long id) {
        eventoDAO.delete(id);
    }
}