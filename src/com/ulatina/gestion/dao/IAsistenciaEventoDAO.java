package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.AsistenciaEvento;

import java.util.List;

public interface IAsistenciaEventoDAO extends IGenericDAO<AsistenciaEvento, Long> {
    List<AsistenciaEvento> findByEvento(Long eventoId);
    List<AsistenciaEvento> findByPersona(Long personaId);
    AsistenciaEvento findByEventoAndPersona(Long eventoId, Long personaId);
    List<AsistenciaEvento> findAsistentesConfirmados(Long eventoId);
}
