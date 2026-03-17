package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.IUsuarioDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.dao.impl.UsuarioDAOImpl;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;
import com.ulatina.gestion.util.JPAUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UsuarioDAOTest {

    private static IUsuarioDAO usuarioDAO;
    private static IParroquiaDAO parroquiaDAO;
    private static Parroquia parroquiaAux;

    @BeforeClass
    public static void setUp() {
        usuarioDAO   = new UsuarioDAOImpl();
        parroquiaDAO = new ParroquiaDAOImpl();

        parroquiaAux = new Parroquia();
        parroquiaAux.setNombre("Parroquia Aux Usuario Test");
        parroquiaAux.setActiva(true);
        parroquiaDAO.save(parroquiaAux);

        Usuario u = new Usuario();
        u.setNombre("Admin Test");
        u.setEmail("admin@test.com");
        u.setPasswordHash("hash_seguro_123");
        u.setRol(RolUsuario.ADMIN);
        u.setActivo(true);
        u.setParroquia(parroquiaAux);
        usuarioDAO.save(u);
    }

    @AfterClass
    public static void tearDown() {
        Usuario u = usuarioDAO.findByEmail("admin@test.com");
        if (u != null) usuarioDAO.delete(u.getId());
        parroquiaDAO.delete(parroquiaAux.getId());
        JPAUtil.close();
    }

    @Test
    public void testFindAll() {
        List<Usuario> lista = usuarioDAO.findAll();
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testFindByEmail() {
        Usuario u = usuarioDAO.findByEmail("admin@test.com");
        assertNotNull(u);
        assertEquals("Admin Test", u.getNombre());
    }

    @Test
    public void testFindByRol() {
        List<Usuario> admins = usuarioDAO.findByRol(RolUsuario.ADMIN);
        assertNotNull(admins);
        assertFalse(admins.isEmpty());
        for (Usuario u : admins) {
            assertEquals(RolUsuario.ADMIN, u.getRol());
        }
    }

    @Test
    public void testFindActivos() {
        List<Usuario> activos = usuarioDAO.findActivos();
        assertNotNull(activos);
        for (Usuario u : activos) {
            assertTrue(u.getActivo());
        }
    }

    @Test
    public void testFindByParroquia() {
        List<Usuario> lista = usuarioDAO.findByParroquia(parroquiaAux.getId());
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
    }

    @Test
    public void testActualizar() {
        Usuario u = usuarioDAO.findByEmail("admin@test.com");
        assertNotNull(u);
        u.setNombre("Admin Actualizado");
        usuarioDAO.update(u);

        Usuario actualizado = usuarioDAO.findByEmail("admin@test.com");
        assertEquals("Admin Actualizado", actualizado.getNombre());
    }
}
