package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IAsistenciaSolicitadaDAO;
import com.ulatina.gestion.model.AsistenciaSolicitada;
import com.ulatina.gestion.model.enums.TipoAsistencia;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class AsistenciaSolicitadaDAOImpl extends GenericDAOImpl<AsistenciaSolicitada, Long> implements IAsistenciaSolicitadaDAO {

    public AsistenciaSolicitadaDAOImpl() {
        super(AsistenciaSolicitada.class);
    }

    @Override
    public List<AsistenciaSolicitada> findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("AsistenciaSolicitada.findByExpediente", AsistenciaSolicitada.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<AsistenciaSolicitada> findByTipoAsistencia(TipoAsistencia tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("AsistenciaSolicitada.findByTipo", AsistenciaSolicitada.class)
                     .setParameter("tipo", tipo)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
