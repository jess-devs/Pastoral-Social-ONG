package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

public class EventoDAOImpl extends GenericDAOImpl<Evento, Long> implements IEventoDAO {

    public EventoDAOImpl() {
        super(Evento.class);
    }

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