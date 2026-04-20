package com.ulatina.gestion.gui;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FrmPanelAdministrativo extends JPanel {

    /**
     * Se crean las variables que se llamarán durante la creacion del resto de la vista
     */
    private JPanel panelContenido;
    private JPanel cardParroquias;
    private JPanel cardUsuarios;
    private JPanel cardRoles;

    public FrmPanelAdministrativo() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppColors.FONDO);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(crearEncabezado(), BorderLayout.NORTH);
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);
        panelContenido.add(crearGrid(), BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        p.add(UIFactory.crearEncabezado("Mantenimiento Usuarios", "Gestioná los diferentes modulos del sistema."));

        return p;
    }

    /**
     * Se crean los espacios con los botones de acciones (Parroquias / Usuarios).
     */
    private JPanel crearGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 18, 0));
        grid.setOpaque(false);

        cardParroquias = crearTarjeta(
                "Gestión Parroquias",
                "Administrá las parroquias del sistema",
                AppColors.SIDE_BG,
                AppColors.SIDE_TXT,
                this::abrirGestionParroquias
        );

        cardUsuarios = crearTarjeta(
                "Gestión Usuarios",
                "Administrá los usuarios y sus accesos",
                AppColors.SIDE_BG,
                AppColors.SIDE_TXT,
                this::abrirGestionUsuarios
        );

        grid.add(cardParroquias);
        grid.add(cardUsuarios);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(grid, BorderLayout.NORTH);
        return wrapper;
    }

    /**
     * Se crean las tarjetas de los
     */
    private JPanel crearTarjeta(String titulo, String descripcion,
                                Color bg, Color fg, Runnable accion) {

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(0x1E2130));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg, 1, true),
                new EmptyBorder(24, 18, 24, 18)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(fg);

        gbc.gridy = 1;
        JLabel lblDesc = new JLabel("<html><center>" + descripcion + "</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(fg);

        gbc.gridy = 0; card.add(lblTitulo, gbc);
        gbc.gridy = 1; card.add(lblDesc, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0x3B82F6));
            }

            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(0x1E2130));
            }

            @Override public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });

        return card;
    }

    /**
     * Metodos para cambiar de vista dentro del panel principal del panel de administracion.
     */
    private void cambiarVista(JPanel nuevaVista, String titulo) {

        panelContenido.removeAll();
        panelContenido.add(nuevaVista, BorderLayout.CENTER);

        panelContenido.revalidate();
        panelContenido.repaint();
    }
    public void volverAlGrid() {
        panelContenido.removeAll();
        panelContenido.add(crearGrid(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
    private void abrirGestionParroquias() {
        cambiarVista(new FrmMantenimientoParroquias(this), "Mantenimiento Parroquias");
        System.out.println("Entra a la ventana de parroquias");
    }
    private void abrirGestionUsuarios() {
        cambiarVista(new FrmMantenimientoUsuarios(this), "Mantenimiento Usuarios");
        System.out.println("Entra a la ventana de Usuarios");
    }

}