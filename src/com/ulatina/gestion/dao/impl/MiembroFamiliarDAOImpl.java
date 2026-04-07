package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IMiembroFamiliarDAO;
import com.ulatina.gestion.model.MiembroFamiliar;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class MiembroFamiliarDAOImpl extends GenericDAOImpl<MiembroFamiliar, Long> implements IMiembroFamiliarDAO {

    public MiembroFamiliarDAOImpl() {
        super(MiembroFamiliar.class);
    }

    @Override
    public List<MiembroFamiliar> findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("MiembroFamiliar.findByExpediente", MiembroFamiliar.class)
                    .setParameter("expedienteId", expedienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MiembroFamiliar> findByPersona(Long personaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("MiembroFamiliar.findByPersona", MiembroFamiliar.class)
                    .setParameter("personaId", personaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MiembroFamiliar> findJefaturasByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("MiembroFamiliar.findJefaturasByExpediente", MiembroFamiliar.class)
                    .setParameter("expedienteId", expedienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}