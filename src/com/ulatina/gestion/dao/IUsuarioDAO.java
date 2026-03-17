package com.ulatina.gestion.dao;

import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;

import java.util.List;

public interface IUsuarioDAO extends IGenericDAO<Usuario, Long> {
    Usuario findByEmail(String email);
    List<Usuario> findByParroquia(Long parroquiaId);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findActivos();
}
