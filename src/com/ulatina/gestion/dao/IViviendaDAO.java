package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Vivienda;
import com.ulatina.gestion.model.enums.CondicionVivienda;
import com.ulatina.gestion.model.enums.TipoVivienda;

import java.util.List;

public interface IViviendaDAO extends IGenericDAO<Vivienda, Long> {
    Vivienda findByExpediente(Long expedienteId);
    List<Vivienda> findByTipo(TipoVivienda tipo);
    List<Vivienda> findByCondicion(CondicionVivienda condicion);
}
