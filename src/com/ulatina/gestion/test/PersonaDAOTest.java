package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IPersonaDAO;
import com.ulatina.gestion.dao.impl.PersonaDAOImpl;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.enums.EstadoCivil;
import com.ulatina.gestion.model.enums.Sexo;
import com.ulatina.gestion.model.enums.TipoDocumentoPersona;
import com.ulatina.gestion.util.JPAUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class PersonaDAOTest {

    private static IPersonaDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new PersonaDAOImpl();

        Persona p = new Persona();
        p.setNombres("Maria");
        p.setApellidos("Gonzalez Perez");
        p.setTipoDocumento(TipoDocumentoPersona.CEDULA);
        p.setNumeroDocumento("1-2345-6789");
        p.setSexo(Sexo.F);
        p.setEstadoCivil(EstadoCivil.CASADO);
        p.setNacionalidad("Costarricense");
        p.setDireccion("San Jose, Barrio Escalante");
        p.setTelefono("8888-9999");
        p.setCondicionSalud("Diabetes tipo 2");
        p.setPaisOrigen("Costa Rica");
        p.setTieneSeguro(true);

        Calendar cal = Calendar.getInstance();
        cal.set(1985, Calendar.MARCH, 15);
        p.setFechaNacimiento(cal.getTime());

        dao.save(p);
    }

    @AfterClass
    public static void tearDown() {
        Persona p = dao.findByNumeroDocumento("1-2345-6789");
        if (p != null) dao.delete(p.getId());
        JPAUtil.close();
    }

    @Test
    public void testFindAll() {
        List<Persona> lista = dao.findAll();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByNumeroDocumento() {
        Persona p = dao.findByNumeroDocumento("1-2345-6789");
        assertNotNull(p);
        assertEquals("Maria", p.getNombres());
        assertEquals("Gonzalez Perez", p.getApellidos());
    }

    @Test
    public void testFindByNombre() {
        List<Persona> resultado = dao.findByNombre("maria", "gonzalez");
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testFindByDireccion() {
        List<Persona> resultado = dao.findByDireccion("Escalante");
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testFindByCondicionSalud() {
        List<Persona> resultado = dao.findByCondicionSalud("Diabetes");
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testFindByPaisOrigen() {
        List<Persona> resultado = dao.findByPaisOrigen("Costa Rica");
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testActualizar() {
        Persona p = dao.findByNumeroDocumento("1-2345-6789");
        assertNotNull(p);
        p.setTelefono("7777-0000");
        dao.update(p);

        Persona actualizada = dao.findByNumeroDocumento("1-2345-6789");
        assertEquals("7777-0000", actualizada.getTelefono());
    }
}
