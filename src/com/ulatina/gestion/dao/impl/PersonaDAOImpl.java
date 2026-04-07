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
            return em.createNamedQuery("Persona.findByNumeroDocumento", Persona.class)
                    .setParameter("num", numeroDocumento)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    // Native Query (SQL) — búsqueda con LOWER/LIKE, específica de MySQL
    @Override
    @SuppressWarnings("unchecked")
    public List<Persona> findByNombre(String nombres, String apellidos) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Persona.findByNombre", Persona.class)
                    .setParameter("nombres", "%" + nombres + "%")
                    .setParameter("apellidos", "%" + apellidos + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Native Query (SQL) — búsqueda parcial de dirección con LOWER/LIKE
    @Override
    @SuppressWarnings("unchecked")
    public List<Persona> findByDireccion(String direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Persona.findByDireccion", Persona.class)
                    .setParameter("direccion", "%" + direccion + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Native Query (SQL) — búsqueda parcial de condición de salud con LOWER/LIKE
    @Override
    @SuppressWarnings("unchecked")
    public List<Persona> findByCondicionSalud(String condicionSalud) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Persona.findByCondicionSalud", Persona.class)
                    .setParameter("condicion", "%" + condicionSalud + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Named Query (JPQL) — búsqueda exacta por país de origen
    @Override
    public List<Persona> findByPaisOrigen(String paisOrigen) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Persona.findByPaisOrigen", Persona.class)
                    .setParameter("pais", paisOrigen)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
