package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.DocumentoAdjunto;
import com.ulatina.gestion.model.enums.TipoDocumentoAdjunto;

import java.util.List;

public interface IDocumentoAdjuntoDAO extends IGenericDAO<DocumentoAdjunto, Long> {
    List<DocumentoAdjunto> findByExpediente(Long expedienteId);
    List<DocumentoAdjunto> findByTipo(TipoDocumentoAdjunto tipo);
    List<DocumentoAdjunto> findDocumentosFirmados(Long expedienteId);
    List<DocumentoAdjunto> findBySubidoPor(Long usuarioId);
}
