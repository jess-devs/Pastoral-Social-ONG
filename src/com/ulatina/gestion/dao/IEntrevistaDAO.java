package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Entrevista;

import java.util.List;

public interface IEntrevistaDAO extends IGenericDAO<Entrevista, Long> {
    List<Entrevista> findByExpediente(Long expedienteId);
    List<Entrevista> findRecomendadas(Long expedienteId);
}
