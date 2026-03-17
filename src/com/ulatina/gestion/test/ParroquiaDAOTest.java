package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.util.JPAUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParroquiaDAOTest {

    private static IParroquiaDAO dao;

    @BeforeClass
    public static void setUp() {
        dao = new ParroquiaDAOImpl();

        Parroquia p = new Parroquia();
        p.setNombre("Parroquia San Jose Test");
        p.setSectorFilial("Sector Norte");
        p.setVicaria("Vicaria Central");
        p.setDireccion("100m norte del parque");
        p.setTelefono("2222-1111");
        p.setActiva(true);
        dao.save(p);
    }

    @AfterClass
    public static void tearDown() {
        Parroquia p = dao.findByNombre("Parroquia San Jose Test");
        if (p != null) dao.delete(p.getId());
        JPAUtil.close();
    }

    @Test
    public void testFindAll() {
        List<Parroquia> lista = dao.findAll();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByNombre() {
        Parroquia p = dao.findByNombre("Parroquia San Jose Test");
        assertNotNull(p);
        assertEquals("Parroquia San Jose Test", p.getNombre());
    }

    @Test
    public void testFindActivas() {
        List<Parroquia> activas = dao.findActivas();
        assertNotNull(activas);
        for (Parroquia p : activas) {
            assertTrue(p.getActiva());
        }
    }

    @Test
    public void testFindById() {
        Parroquia p = dao.findByNombre("Parroquia San Jose Test");
        assertNotNull(p);
        Parroquia encontrada = dao.findById(p.getId());
        assertNotNull(encontrada);
        assertEquals(p.getId(), encontrada.getId());
    }

    @Test
    public void testActualizar() {
        Parroquia p = dao.findByNombre("Parroquia San Jose Test");
        assertNotNull(p);
        p.setTelefono("2233-4455");
        dao.update(p);

        Parroquia actualizada = dao.findById(p.getId());
        assertEquals("2233-4455", actualizada.getTelefono());
    }

    @Test
    public void testEliminarYGuardarDeNuevo() {
        Parroquia p = dao.findByNombre("Parroquia San Jose Test");
        assertNotNull(p);
        Long id = p.getId();
        dao.delete(id);
        assertNull(dao.findById(id));

        // Volver a insertar para que tearDown no falle
        Parroquia nueva = new Parroquia();
        nueva.setNombre("Parroquia San Jose Test");
        nueva.setActiva(true);
        dao.save(nueva);
        assertNotNull(nueva.getId());
    }
}
