package com.ulatina.gestion.gui.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

    /**
     * Configura el estilo visual de una JTable de expedientes (fuente, colores,
     * renderers, anchos de columna y sorter). Los listeners de selección/doble
     * clic deben agregarse externamente.
     */
    public static void configurarTablaExpedientes(JTable tabla, TableRowSorter<DefaultTableModel> sorter) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(AppColors.GRID_TBL);
        tabla.setSelectionBackground(AppColors.FILA_SEL);
        tabla.setSelectionForeground(AppColors.TEXTO);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(90);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(5).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer());

        DefaultTableCellRenderer etapaRender = new DefaultTableCellRenderer();
        etapaRender.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(4).setCellRenderer(etapaRender);

        DefaultTableCellRenderer izqPad = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 4));
                return this;
            }
        };
        tabla.getColumnModel().getColumn(1).setCellRenderer(izqPad);
        tabla.getColumnModel().getColumn(2).setCellRenderer(izqPad);

        tabla.setRowSorter(sorter);
    }

    /**
     * Campo de búsqueda con texto placeholder y comportamiento focus/filter
     * integrado. {@code onFilter} se invoca en cada cambio de texto.
     */
    public static JTextField crearCampoBusqueda(String placeholder, Runnable onFilter) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(AppColors.TEXTO_GRIS);
        tf.setText(placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        tf.setPreferredSize(new Dimension(0, 36));
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().startsWith("Buscar")) {
                    tf.setText("");
                    tf.setForeground(AppColors.TEXTO);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(AppColors.TEXTO_GRIS);
                }
            }
        });
        tf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onFilter.run(); }
            public void removeUpdate(DocumentEvent e) { onFilter.run(); }
            public void changedUpdate(DocumentEvent e) { onFilter.run(); }
        });
        return tf;
    }

    /**
     * Panel de encabezado estándar con título grande y subtítulo descriptivo.
     * Usado en los formularios de mantenimiento.
     */
    public static JPanel crearEncabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AppColors.TEXTO);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(AppColors.TEXTO);

        p.add(lblTitulo, BorderLayout.CENTER);
        p.add(lblSubtitulo, BorderLayout.SOUTH);
        return p;
    }

    /**
     * Botón de texto plano sin borde ni fondo — estilo enlace.
     * Color {@link AppColors#AZUL}, fuente 12pt. Sin ActionListener — añadirlo externamente.
     */
    public static JButton crearBotonLink(String texto) {
        return crearBotonLink(texto, null);
    }

    /**
     * Botón de texto plano sin borde ni fondo — estilo enlace.
     * Color {@link AppColors#AZUL}, fuente 12pt, con ActionListener integrado.
     */
    public static JButton crearBotonLink(String texto, ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(AppColors.AZUL);
        btn.setBackground(null);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (accion != null) btn.addActionListener(accion);
        return btn;
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
