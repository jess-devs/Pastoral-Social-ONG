package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.enums.TipoEvento;

import java.util.Date;
import java.util.List;

public interface IEventoDAO extends IGenericDAO<Evento, Long> {
    List<Evento> findByParroquia(Long parroquiaId);
    List<Evento> findByTipo(TipoEvento tipo);
    List<Evento> findByFecha(Date fecha);
    List<Evento> findByNombre(String nombre);
}
