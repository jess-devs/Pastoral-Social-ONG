package com.ulatina.gestion.gui;

import com.ulatina.gestion.gui.util.AppColors;
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

        add(crearEncabezado(), BorderLayout.NORTH);

        // Contenedor donde cargarán las vistas
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);

        add(panelContenido, BorderLayout.CENTER);
    }

    // ─── Encabezado ───────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel titulo = new JLabel("Mantenimiento Roles");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(AppColors.TEXTO);

        JLabel subtitulo = new JLabel("Gestionar los Roles existentes de los usuarios.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(AppColors.TEXTO);

        p.add(titulo, BorderLayout.CENTER);
        p.add(subtitulo, BorderLayout.SOUTH);

        return p;
    }

    // ─── Acciones ────────────────────────────────────────────────
    private void saveP() {
        UsuarioDAOTest testU = new UsuarioDAOTest();
        System.out.println("funciona Rol");
    }
}

