package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IUsuarioDAO;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Long> implements IUsuarioDAO {

    public UsuarioDAOImpl() {
        super(Usuario.class);
    }

    @Override
    public Usuario findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findByParroquia(Long parroquiaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.parroquia.id = :parroquiaId", Usuario.class)
                     .setParameter("parroquiaId", parroquiaId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findByRol(RolUsuario rol) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.rol = :rol", Usuario.class)
                     .setParameter("rol", rol)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findActivos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.activo = true", Usuario.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
