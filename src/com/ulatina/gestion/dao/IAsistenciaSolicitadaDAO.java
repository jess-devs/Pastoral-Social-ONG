package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.AsistenciaSolicitada;
import com.ulatina.gestion.model.enums.TipoAsistencia;

import java.util.List;

public interface IAsistenciaSolicitadaDAO extends IGenericDAO<AsistenciaSolicitada, Long> {
    List<AsistenciaSolicitada> findByExpediente(Long expedienteId);
    List<AsistenciaSolicitada> findByTipoAsistencia(TipoAsistencia tipo);
}
