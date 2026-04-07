package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class ParroquiaDAOImpl extends GenericDAOImpl<Parroquia, Long> implements IParroquiaDAO {

    public ParroquiaDAOImpl() {
        super(Parroquia.class);
    }

    @Override
    public Parroquia findByNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Parroquia.FindByNombre", Parroquia.class)
                    .setParameter("nombre", nombre)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Parroquia> findActivas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Parroquia.findActivas", Parroquia.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
