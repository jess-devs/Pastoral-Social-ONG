package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IExpedienteDAO;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.model.enums.EtapaExpediente;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class ExpedienteDAOImpl extends GenericDAOImpl<Expediente, Long> implements IExpedienteDAO {

    public ExpedienteDAOImpl() {
        super(Expediente.class);
    }

    @Override
    public Expediente findByNumeroFicha(String numeroFicha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.numeroFicha = :ficha", Expediente.class)
                     .setParameter("ficha", numeroFicha)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expediente> findByParroquia(Long parroquiaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.parroquia.id = :parroquiaId", Expediente.class)
                     .setParameter("parroquiaId", parroquiaId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expediente> findByTitular(Long titularId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.titular.id = :titularId", Expediente.class)
                     .setParameter("titularId", titularId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expediente> findByEstado(EstadoExpediente estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.estado = :estado", Expediente.class)
                     .setParameter("estado", estado)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expediente> findByEtapa(EtapaExpediente etapa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.etapaActual = :etapa", Expediente.class)
                     .setParameter("etapa", etapa)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expediente> findByColorMarcador(String colorMarcador) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Expediente e WHERE e.colorMarcador = :color", Expediente.class)
                     .setParameter("color", colorMarcador)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
