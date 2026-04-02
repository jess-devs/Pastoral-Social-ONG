package com.ulatina.gestion.gui.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;

/**
 * Fábrica de componentes UI reutilizables.
 * Centraliza la creación de botones, campos y etiquetas con el estilo del
 * sistema.
 */
public final class UIFactory {

    private UIFactory() {
    }

    /**
     * Botón redondeado con efecto hover. Sin ActionListener — añadirlo
     * externamente.
     */
    public static JButton crearBoton(String texto, Color bg, Color fg) {
        return crearBoton(texto, bg, fg, null);
    }

    /**
     * Botón redondeado con efecto hover y ActionListener integrado.
     */
    public static JButton crearBoton(String texto, Color bg, Color fg, ActionListener accion) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (accion != null)
            btn.addActionListener(accion);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor(bg));
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });
        return btn;
    }

    /**
     * Versión compacta del botón (padding reducido, fuente 12).
     */
    public static JButton crearBotonSmall(String texto, Color bg, Color fg) {
        return crearBotonSmall(texto, bg, fg, null);
    }

    public static JButton crearBotonSmall(String texto, Color bg, Color fg, ActionListener accion) {
        JButton btn = crearBoton(texto, bg, fg, accion);
        btn.setBorder(new EmptyBorder(4, 12, 4, 12));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    /**
     * Botón con padding de diálogo (7px vertical, 18px horizontal).
     */
    public static JButton crearBotonDialog(String texto, Color bg, Color fg, ActionListener accion) {
        JButton btn = crearBoton(texto, bg, fg, accion);
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        return btn;
    }

    /**
     * Campo de texto con máscara de fecha dd/MM/yyyy.
     */
    public static JFormattedTextField crearCampoFecha() {
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            JFormattedTextField f = new JFormattedTextField(mask);
            f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            f.setColumns(10);
            return f;
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    // ─── Privado ──────────────────────────────────────────────────────────────

    private static Color hoverColor(Color bg) {
        if (bg == AppColors.PRIMARIO)
            return AppColors.PRIMARIO_H;
        if (bg == AppColors.GRIS_BTN)
            return AppColors.GRIS_BTN_H;
        if (bg == AppColors.ROJO)
            return AppColors.ROJO_H;
        if (bg == AppColors.PURPURA)
            return AppColors.PURPURA_H;
        return bg.darker();
    }
}
