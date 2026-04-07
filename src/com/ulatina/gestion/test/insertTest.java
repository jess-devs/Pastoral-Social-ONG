package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IExpedienteDAO;
import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.model.Parroquia;

import javax.swing.*;

public class insertTest
{
    private static IParroquiaDAO dao;

    public static void main(String[] args) {

        try {
            dao = new ParroquiaDAOImpl();

            Parroquia p = new Parroquia();
            p.setNombre("Parroquia San Jose Test 3");
            p.setSectorFilial("Sector Norte 3");
            p.setVicaria("Vicaria Central 3");
            p.setDireccion("100m norte del parque 3");
            p.setTelefono("2222-1333");
            p.setActiva(true);
            dao.save(p);

            JOptionPane.showMessageDialog(null, "Parroquia Insertada correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

}
