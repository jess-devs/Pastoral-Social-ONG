package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.GastoMensual;
import com.ulatina.gestion.model.enums.CategoriaGasto;

import java.util.List;

public interface IGastoMensualDAO extends IGenericDAO<GastoMensual, Long> {
    List<GastoMensual> findByAdendum(Long adendumId);
    List<GastoMensual> findByCategoria(CategoriaGasto categoria);
}
