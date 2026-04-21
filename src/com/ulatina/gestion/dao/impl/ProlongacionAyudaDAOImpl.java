package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IProlongacionAyudaDAO;
import com.ulatina.gestion.model.ProlongacionAyuda;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class ProlongacionAyudaDAOImpl extends GenericDAOImpl<ProlongacionAyuda, Long> implements IProlongacionAyudaDAO {

    public ProlongacionAyudaDAOImpl() {
        super(ProlongacionAyuda.class);
    }

    @Override
    public List<ProlongacionAyuda> findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM ProlongacionAyuda p LEFT JOIN FETCH p.registradoPor WHERE p.expediente.id = :expedienteId",
                    ProlongacionAyuda.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProlongacionAyuda> findByRegistradoPor(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM ProlongacionAyuda p WHERE p.registradoPor.id = :usuarioId", ProlongacionAyuda.class)
                     .setParameter("usuarioId", usuarioId)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
