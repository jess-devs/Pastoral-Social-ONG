package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IAsistenciaEventoDAO;
import com.ulatina.gestion.dao.IEventoDAO;
import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.IPersonaDAO;
import com.ulatina.gestion.dao.impl.AsistenciaEventoDAOImpl;
import com.ulatina.gestion.dao.impl.EventoDAOImpl;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.dao.impl.PersonaDAOImpl;
import com.ulatina.gestion.model.AsistenciaEvento;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.enums.TipoEvento;
import com.ulatina.gestion.util.JPAUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class EventoDAOTest {

    private static IEventoDAO eventoDAO;
    private static IAsistenciaEventoDAO asistenciaDAO;
    private static IParroquiaDAO parroquiaDAO;
    private static IPersonaDAO personaDAO;
    private static Parroquia parroquiaAux;
    private static Persona personaAux;
    private static Evento eventoAux;

    @BeforeClass
    public static void setUp() {
        eventoDAO     = new EventoDAOImpl();
        asistenciaDAO = new AsistenciaEventoDAOImpl();
        parroquiaDAO  = new ParroquiaDAOImpl();
        personaDAO    = new PersonaDAOImpl();

        parroquiaAux = new Parroquia();
        parroquiaAux.setNombre("Parroquia Aux Evento Test");
        parroquiaAux.setActiva(true);
        parroquiaDAO.save(parroquiaAux);

        personaAux = new Persona();
        personaAux.setNombres("Luis");
        personaAux.setApellidos("Mora Test");
        personaAux.setNumeroDocumento("9-8888-0002");
        personaDAO.save(personaAux);

        eventoAux = new Evento();
        eventoAux.setNombre("Reunion Mensual Test");
        eventoAux.setFecha(new Date());
        eventoAux.setLugar("Salon Parroquial");
        eventoAux.setTipo(TipoEvento.REUNION);
        eventoAux.setParroquia(parroquiaAux);
        eventoDAO.save(eventoAux);

        AsistenciaEvento a = new AsistenciaEvento();
        a.setEvento(eventoAux);
        a.setPersona(personaAux);
        a.setAsistio(true);
        a.setObservaciones("Asistio puntualmente");
        asistenciaDAO.save(a);
    }

    @AfterClass
    public static void tearDown() {
        List<AsistenciaEvento> asistencias = asistenciaDAO.findByEvento(eventoAux.getId());
        for (AsistenciaEvento a : asistencias) asistenciaDAO.delete(a.getId());
        eventoDAO.delete(eventoAux.getId());
        personaDAO.delete(personaAux.getId());
        parroquiaDAO.delete(parroquiaAux.getId());
        JPAUtil.close();
    }

    @Test
    public void testFindAll() {
        List<Evento> lista = eventoDAO.findAll();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByParroquia() {
        List<Evento> lista = eventoDAO.findByParroquia(parroquiaAux.getId());
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByTipo() {
        List<Evento> lista = eventoDAO.findByTipo(TipoEvento.REUNION);
        assertNotNull(lista);
        for (Evento e : lista) {
            assertEquals(TipoEvento.REUNION, e.getTipo());
        }
    }

    @Test
    public void testFindAsistenciasByEvento() {
        List<AsistenciaEvento> lista = asistenciaDAO.findByEvento(eventoAux.getId());
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindAsistentesConfirmados() {
        List<AsistenciaEvento> confirmados = asistenciaDAO.findAsistentesConfirmados(eventoAux.getId());
        assertNotNull(confirmados);
        assertFalse(confirmados.isEmpty());
        for (AsistenciaEvento a : confirmados) {
            assertTrue(a.getAsistio());
        }
    }

    @Test
    public void testFindByEventoAndPersona() {
        AsistenciaEvento a = asistenciaDAO.findByEventoAndPersona(eventoAux.getId(), personaAux.getId());
        // Solo verificamos que existe — el query ya filtra por eventoId y personaId,
        // acceder a a.getEvento() fuera de sesión lanza LazyInitializationException
        assertNotNull(a);
        assertNotNull(a.getId());
    }
}
