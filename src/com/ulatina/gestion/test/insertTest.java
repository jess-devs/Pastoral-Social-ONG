package com.ulatina.gestion.test;

import com.ulatina.gestion.dao.IExpedienteDAO;
import com.ulatina.gestion.dao.IParroquiaDAO;
import com.ulatina.gestion.dao.impl.ParroquiaDAOImpl;
import com.ulatina.gestion.model.Parroquia;

public class insertTest
{
    private static IParroquiaDAO dao;

    public static void main(String[] args) {

        dao = new ParroquiaDAOImpl();

        Parroquia p = new Parroquia();
        p.setNombre("Parroquia San Jose Test");
        p.setSectorFilial("Sector Norte");
        p.setVicaria("Vicaria Central");
        p.setDireccion("100m norte del parque");
        p.setTelefono("2222-1111");
        p.setActiva(true);
        dao.save(p);

        System.out.println("funciona  parroquia");
    }

}
