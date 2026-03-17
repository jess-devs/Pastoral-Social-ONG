package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.MiembroFamiliar;

import java.util.List;

public interface IMiembroFamiliarDAO extends IGenericDAO<MiembroFamiliar, Long> {
    List<MiembroFamiliar> findByExpediente(Long expedienteId);
    List<MiembroFamiliar> findByPersona(Long personaId);
    List<MiembroFamiliar> findJefaturasByExpediente(Long expedienteId);
}
