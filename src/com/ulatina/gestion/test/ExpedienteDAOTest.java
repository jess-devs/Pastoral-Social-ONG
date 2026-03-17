package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IExpedienteDAO;
import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.IPersonaDAO;
import com.ulatina.gestion.dao.impl.ExpedienteDAOImpl;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.dao.impl.PersonaDAOImpl;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.model.enums.EtapaExpediente;
import com.ulatina.gestion.util.JPAUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ExpedienteDAOTest {

    private static IExpedienteDAO expedienteDAO;
    private static IParroquiaDAO parroquiaDAO;
    private static IPersonaDAO personaDAO;
    private static Parroquia parroquiaAux;
    private static Persona titularAux;

    @BeforeClass
    public static void setUp() {
        expedienteDAO = new ExpedienteDAOImpl();
        parroquiaDAO  = new ParroquiaDAOImpl();
        personaDAO    = new PersonaDAOImpl();

        parroquiaAux = new Parroquia();
        parroquiaAux.setNombre("Parroquia Aux Expediente Test");
        parroquiaAux.setActiva(true);
        parroquiaDAO.save(parroquiaAux);

        titularAux = new Persona();
        titularAux.setNombres("Juan");
        titularAux.setApellidos("Ramirez Test");
        titularAux.setNumeroDocumento("9-9999-0001");
        personaDAO.save(titularAux);

        Expediente e = new Expediente();
        e.setNumeroFicha("TEST-001");
        e.setFechaInicio(new Date());
        e.setEntrevistador("Trabajador Social Test");
        e.setEstado(EstadoExpediente.EN_PROCESO);
        e.setEtapaActual(EtapaExpediente.REGISTRO);
        e.setColorMarcador("ROJO");
        e.setParroquia(parroquiaAux);
        e.setTitular(titularAux);
        expedienteDAO.save(e);
    }

    @AfterClass
    public static void tearDown() {
        Expediente e = expedienteDAO.findByNumeroFicha("TEST-001");
        if (e != null) expedienteDAO.delete(e.getId());
        personaDAO.delete(titularAux.getId());
        parroquiaDAO.delete(parroquiaAux.getId());
        JPAUtil.close();
    }

    @Test
    public void testFindAll() {
        List<Expediente> lista = expedienteDAO.findAll();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByNumeroFicha() {
        Expediente e = expedienteDAO.findByNumeroFicha("TEST-001");
        assertNotNull(e);
        assertEquals("TEST-001", e.getNumeroFicha());
    }

    @Test
    public void testFindByParroquia() {
        List<Expediente> lista = expedienteDAO.findByParroquia(parroquiaAux.getId());
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByTitular() {
        List<Expediente> lista = expedienteDAO.findByTitular(titularAux.getId());
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByEstado() {
        List<Expediente> lista = expedienteDAO.findByEstado(EstadoExpediente.EN_PROCESO);
        assertNotNull(lista);
        for (Expediente e : lista) {
            assertEquals(EstadoExpediente.EN_PROCESO, e.getEstado());
        }
    }

    @Test
    public void testFindByEtapa() {
        List<Expediente> lista = expedienteDAO.findByEtapa(EtapaExpediente.REGISTRO);
        assertNotNull(lista);
        for (Expediente e : lista) {
            assertEquals(EtapaExpediente.REGISTRO, e.getEtapaActual());
        }
    }

    @Test
    public void testFindByColorMarcador() {
        List<Expediente> lista = expedienteDAO.findByColorMarcador("ROJO");
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testActualizar() {
        Expediente e = expedienteDAO.findByNumeroFicha("TEST-001");
        assertNotNull(e);
        e.setEstado(EstadoExpediente.ACTIVO);
        e.setEtapaActual(EtapaExpediente.FAMILIA);
        expedienteDAO.update(e);

        Expediente actualizado = expedienteDAO.findByNumeroFicha("TEST-001");
        assertEquals(EstadoExpediente.ACTIVO, actualizado.getEstado());
        assertEquals(EtapaExpediente.FAMILIA, actualizado.getEtapaActual());
    }
}
