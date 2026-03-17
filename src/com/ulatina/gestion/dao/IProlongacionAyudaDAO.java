package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.ProlongacionAyuda;

import java.util.List;

public interface IProlongacionAyudaDAO extends IGenericDAO<ProlongacionAyuda, Long> {
    List<ProlongacionAyuda> findByExpediente(Long expedienteId);
    List<ProlongacionAyuda> findByRegistradoPor(Long usuarioId);
}
