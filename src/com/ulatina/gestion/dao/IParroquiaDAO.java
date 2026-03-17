package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Parroquia;

import java.util.List;

public interface IParroquiaDAO extends IGenericDAO<Parroquia, Long> {
    Parroquia findByNombre(String nombre);
    List<Parroquia> findActivas();
}
