package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IGastoMensualDAO;
import com.ulatina.gestion.model.GastoMensual;
import com.ulatina.gestion.model.enums.CategoriaGasto;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class GastoMensualDAOImpl extends GenericDAOImpl<GastoMensual, Long> implements IGastoMensualDAO {

    public GastoMensualDAOImpl() {
        super(GastoMensual.class);
    }

    @Override
    public List<GastoMensual> findByAdendum(Long adendumId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM GastoMensual g WHERE g.adendum.id = :adendumId", GastoMensual.class)
                     .setParameter("adendumId", adendumId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<GastoMensual> findByCategoria(CategoriaGasto categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT g FROM GastoMensual g WHERE g.categoria = :categoria", GastoMensual.class)
                     .setParameter("categoria", categoria)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
