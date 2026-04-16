package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

// Implementación concreta del DAO de Evento.
// Aquí es donde realmente se ejecutan las consultas contra la base de datos usando JPA.
// Extiende GenericDAOImpl para heredar las operaciones básicas (save, update, delete)
// e implementa IEventoDAO para agregar las consultas específicas de eventos.
public class EventoDAOImpl extends GenericDAOImpl<Evento, Long> implements IEventoDAO {

    // El constructor le indica a la clase genérica que trabajará con la entidad Evento
    public EventoDAOImpl() {
        super(Evento.class);
    }

    // Obtiene todos los eventos usando una NamedQuery definida en la entidad Evento.
    // El EntityManager es la conexión con la base de datos; siempre se cierra en el bloque finally
    // para liberar recursos aunque ocurra un error.
    @Override
    public List<Evento> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findAll", Evento.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Busca un evento por ID usando getSingleResult(), que espera exactamente un resultado.
    // Si no existe ningún registro con ese ID, JPA lanza NoResultException y se captura
    // para devolver null sin que el programa falle.
    @Override
    public Evento findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findById", Evento.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // Busca todos los eventos asociados a una parroquia.
    // setParameter("parroquiaId", parroquiaId) inyecta el valor en la consulta JPQL de forma segura.
    @Override
    public List<Evento> findByParroquia(Long parroquiaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findByParroquia", Evento.class)
                    .setParameter("parroquiaId", parroquiaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Filtra eventos por tipo (el enum TipoEvento se pasa directamente como parámetro).
    @Override
    public List<Evento> findByTipo(TipoEvento tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findByTipo", Evento.class)
                    .setParameter("tipo", tipo)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Busca eventos que coincidan con la fecha indicada.
    @Override
    public List<Evento> findByFecha(Date fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findByFecha", Evento.class)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Busca eventos cuyo nombre coincida con el parámetro recibido.
    @Override
    public List<Evento> findByNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Evento.findByNombre", Evento.class)
                    .setParameter("nombre", nombre)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}