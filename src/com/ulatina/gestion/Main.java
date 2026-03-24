package com.ulatina.gestion;

import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.IUsuarioDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.dao.impl.UsuarioDAOImpl;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.PersistenceException;
import javax.swing.*;

public class Main {

    private static IUsuarioDAO usuarioDAO;
    private static IParroquiaDAO parroquiaDAO;
    private static Parroquia parroquiaAux;

    public static void main(String[] args) {
        try {
            usuarioDAO = new UsuarioDAOImpl();
            parroquiaDAO = new ParroquiaDAOImpl();

            parroquiaAux = new Parroquia();
            parroquiaAux.setNombre("Parroquia Aux-2");
            parroquiaAux.setActiva(true);
            parroquiaDAO.save(parroquiaAux);

            Usuario u = new Usuario();
            u.setNombre("Admin Test-2");
            u.setEmail("admin2@test.com");
            u.setPasswordHash("admin2_seguro_123");
            u.setRol(RolUsuario.ADMIN);
            u.setActivo(true);
            u.setParroquia(parroquiaAux);
            usuarioDAO.save(u);

            System.out.println("Datos insertados correctamente.");
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar usuario: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error inesperado: " + ex.getMessage());
        } finally {
            JPAUtil.close();
        }
    }
}
