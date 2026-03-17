package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.model.enums.EtapaExpediente;

import java.util.List;

public interface IExpedienteDAO extends IGenericDAO<Expediente, Long> {
    Expediente findByNumeroFicha(String numeroFicha);
    List<Expediente> findByParroquia(Long parroquiaId);
    List<Expediente> findByTitular(Long titularId);
    List<Expediente> findByEstado(EstadoExpediente estado);
    List<Expediente> findByEtapa(EtapaExpediente etapa);
    List<Expediente> findByColorMarcador(String colorMarcador);
}
