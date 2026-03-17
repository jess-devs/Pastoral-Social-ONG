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
            return em.createQuery("SELECT d FROM DocumentoAdjunto d WHERE d.expediente.id = :expedienteId", DocumentoAdjunto.class)
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
            return em.createQuery("SELECT d FROM DocumentoAdjunto d WHERE d.tipo = :tipo", DocumentoAdjunto.class)
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
            return em.createQuery(
                    "SELECT d FROM DocumentoAdjunto d WHERE d.expediente.id = :expedienteId AND d.esDocFirmado = true",
                    DocumentoAdjunto.class)
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
}
