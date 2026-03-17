package com.ulatina.gestion.dao.impl;

import com.ulatina.gestion.dao.IPersonaDAO;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class PersonaDAOImpl extends GenericDAOImpl<Persona, Long> implements IPersonaDAO {

    public PersonaDAOImpl() {
        super(Persona.class);
    }

    @Override
    public Persona findByNumeroDocumento(String numeroDocumento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Persona p WHERE p.numeroDocumento = :num", Persona.class)
                     .setParameter("num", numeroDocumento)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Persona> findByNombre(String nombres, String apellidos) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Persona p WHERE LOWER(p.nombres) LIKE LOWER(:nombres) AND LOWER(p.apellidos) LIKE LOWER(:apellidos)",
                    Persona.class)
                     .setParameter("nombres", "%" + nombres + "%")
                     .setParameter("apellidos", "%" + apellidos + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Persona> findByDireccion(String direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Persona p WHERE LOWER(p.direccion) LIKE LOWER(:direccion)",
                    Persona.class)
                     .setParameter("direccion", "%" + direccion + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Persona> findByCondicionSalud(String condicionSalud) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Persona p WHERE LOWER(p.condicionSalud) LIKE LOWER(:condicion)",
                    Persona.class)
                     .setParameter("condicion", "%" + condicionSalud + "%")
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Persona> findByPaisOrigen(String paisOrigen) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Persona p WHERE p.paisOrigen = :pais", Persona.class)
                     .setParameter("pais", paisOrigen)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
