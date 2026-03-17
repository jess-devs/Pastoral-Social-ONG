package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IAdendumDAO;
import com.ulatina.gestion.model.Adendum;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class AdendumDAOImpl extends GenericDAOImpl<Adendum, Long> implements IAdendumDAO {

    public AdendumDAOImpl() {
        super(Adendum.class);
    }

    @Override
    public Adendum findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Adendum a WHERE a.expediente.id = :expedienteId", Adendum.class)
                     .setParameter("expedienteId", expedienteId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
