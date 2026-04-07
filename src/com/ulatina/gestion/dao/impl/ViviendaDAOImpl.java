package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IViviendaDAO;
import com.ulatina.gestion.model.Vivienda;
import com.ulatina.gestion.model.enums.CondicionVivienda;
import com.ulatina.gestion.model.enums.TipoVivienda;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class ViviendaDAOImpl extends GenericDAOImpl<Vivienda, Long> implements IViviendaDAO {

    public ViviendaDAOImpl() {
        super(Vivienda.class);
    }

    @Override
    public Vivienda findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Vivienda.findByExpediente", Vivienda.class)
                    .setParameter("expedienteId", expedienteId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vivienda> findByTipo(TipoVivienda tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Vivienda.findByTipo", Vivienda.class)
                    .setParameter("tipo", tipo)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vivienda> findByCondicion(CondicionVivienda condicion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Vivienda.findByCondicion", Vivienda.class)
                    .setParameter("condicion", condicion)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}