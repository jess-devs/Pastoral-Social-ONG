package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Adendum;

public interface IAdendumDAO extends IGenericDAO<Adendum, Long> {
    Adendum findByExpediente(Long expedienteId);
}
