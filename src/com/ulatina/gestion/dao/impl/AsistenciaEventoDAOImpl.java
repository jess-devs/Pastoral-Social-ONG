package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IAsistenciaEventoDAO;
import com.ulatina.gestion.model.AsistenciaEvento;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class AsistenciaEventoDAOImpl extends GenericDAOImpl<AsistenciaEvento, Long> implements IAsistenciaEventoDAO {

    public AsistenciaEventoDAOImpl() {
        super(AsistenciaEvento.class);
    }

    @Override
    public List<AsistenciaEvento> findByEvento(Long eventoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AsistenciaEvento a WHERE a.evento.id = :eventoId", AsistenciaEvento.class)
                     .setParameter("eventoId", eventoId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AsistenciaEvento> findByPersona(Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AsistenciaEvento a WHERE a.persona.id = :personaId", AsistenciaEvento.class)
                     .setParameter("personaId", personaId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public AsistenciaEvento findByEventoAndPersona(Long eventoId, Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM AsistenciaEvento a WHERE a.evento.id = :eventoId AND a.persona.id = :personaId",
                    AsistenciaEvento.class)
                     .setParameter("eventoId", eventoId)
                     .setParameter("personaId", personaId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AsistenciaEvento> findAsistentesConfirmados(Long eventoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM AsistenciaEvento a WHERE a.evento.id = :eventoId AND a.asistio = true",
                    AsistenciaEvento.class)
                     .setParameter("eventoId", eventoId)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
