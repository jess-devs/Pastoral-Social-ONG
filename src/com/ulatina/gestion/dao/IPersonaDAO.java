package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Persona;

import java.util.List;

public interface IPersonaDAO extends IGenericDAO<Persona, Long> {
    Persona findByNumeroDocumento(String numeroDocumento);
    List<Persona> findByNombre(String nombres, String apellidos);
    List<Persona> findByDireccion(String direccion);
    List<Persona> findByCondicionSalud(String condicionSalud);
    List<Persona> findByPaisOrigen(String paisOrigen);
}
