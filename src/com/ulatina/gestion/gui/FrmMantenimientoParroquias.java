package com.ulatina.gestion.gui;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.test.ParroquiaDAOTest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmMantenimientoParroquias extends JPanel {

    private JPanel panelContenido;

    public FrmMantenimientoParroquias() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppColors.FONDO);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(UIFactory.crearEncabezado("Mantenimiento Parroquias", "Gestionar las parroquias que utilizan el sistema."), BorderLayout.NORTH);

        // Contenedor donde cargarán las vistas
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);

        add(panelContenido, BorderLayout.CENTER);
    }

    // ─── Acciones ────────────────────────────────────────────────
    private void saveP() {
        ParroquiaDAOTest testP = new ParroquiaDAOTest();
        System.out.println("funciona Parroquia");
    }
}