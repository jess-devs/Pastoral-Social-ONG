package com.ulatina.gestion.gui;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.test.UsuarioDAOTest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmMantenimientoRoles extends JPanel {
    private JPanel panelContenido;


    public FrmMantenimientoRoles() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppColors.FONDO);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(UIFactory.crearEncabezado("Mantenimiento Roles", "Gestionar los Roles existentes de los usuarios."), BorderLayout.NORTH);

        // Contenedor donde cargarán las vistas
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);

        add(panelContenido, BorderLayout.CENTER);
    }

    // ─── Acciones ────────────────────────────────────────────────
    private void saveP() {
        UsuarioDAOTest testU = new UsuarioDAOTest();
        System.out.println("funciona Rol");
    }
}

