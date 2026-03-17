package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class EventoDAOImpl extends GenericDAOImpl<Evento, Long> implements IEventoDAO {

    public EventoDAOImpl() {
        super(Evento.class);
    }

    @Override
    public List<Evento> findByParroquia(Long parroquiaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Evento e WHERE e.parroquia.id = :parroquiaId", Evento.class)
                     .setParameter("parroquiaId", parroquiaId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Evento> findByTipo(TipoEvento tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Evento e WHERE e.tipo = :tipo", Evento.class)
                     .setParameter("tipo", tipo)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
