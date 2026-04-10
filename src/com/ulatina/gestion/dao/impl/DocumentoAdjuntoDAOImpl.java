package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IDocumentoAdjuntoDAO;
import com.ulatina.gestion.model.DocumentoAdjunto;
import com.ulatina.gestion.model.enums.TipoDocumentoAdjunto;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class DocumentoAdjuntoDAOImpl extends GenericDAOImpl<DocumentoAdjunto, Long> implements IDocumentoAdjuntoDAO {

    public DocumentoAdjuntoDAOImpl() {
        super(DocumentoAdjunto.class);
    }

    @Override
    public List<DocumentoAdjunto> findByExpediente(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("DocumentoAdjunto.findByExpediente", DocumentoAdjunto.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<DocumentoAdjunto> findByTipo(TipoDocumentoAdjunto tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("DocumentoAdjunto.findByTipo", DocumentoAdjunto.class)
                     .setParameter("tipo", tipo)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<DocumentoAdjunto> findDocumentosFirmados(Long expedienteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("DocumentoAdjunto.findFirmadosByExpediente", DocumentoAdjunto.class)
                     .setParameter("expedienteId", expedienteId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<DocumentoAdjunto> findBySubidoPor(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM DocumentoAdjunto d WHERE d.subidoPor.id = :usuarioId", DocumentoAdjunto.class)
                     .setParameter("usuarioId", usuarioId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DocumentoAdjunto> buscarPorDescripcion(Long expedienteId, String texto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("DocumentoAdjunto.buscarPorDescripcion", DocumentoAdjunto.class)
                     .setParameter("expedienteId", expedienteId)
                     .setParameter("texto", texto)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
