package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IEntrevistaDAO;
import com.ulatina.gestion.model.Entrevista;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class EntrevistaDAOImpl extends GenericDAOImpl<Entrevista, Long> implements IEntrevistaDAO {

    public EntrevistaDAOImpl() {
        super(Entrevista.class);
    }

    @Override
    public List<Entrevista> findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Entrevista e WHERE e.expediente.id = :expedienteId", Entrevista.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Entrevista> findRecomendadas(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Entrevista e WHERE e.expediente.id = :expedienteId AND e.recomiendaAyuda = true",
                    Entrevista.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
