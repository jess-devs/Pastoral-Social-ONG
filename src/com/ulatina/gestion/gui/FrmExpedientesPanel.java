package com.ulatina.gestion.gui;

import com.ulatina.gestion.dao.IExpedienteDAO;
import com.ulatina.gestion.dao.impl.ExpedienteDAOImpl;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.model.enums.EtapaExpediente;
import com.ulatina.gestion.util.JPAUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * FrmExpedientesPanel — versión JPanel embebible dentro del Dashboard.
 * Misma lógica que FrmExpedientes pero sin JFrame.
 */
public class FrmExpedientesPanel extends JPanel {

    // ─── Colores ─────────────────────────────────────────────────────────────
    private static final Color COLOR_FONDO       = new Color(245, 246, 250);
    private static final Color COLOR_PANEL       = Color.WHITE;
    private static final Color COLOR_PRIMARIO    = new Color(34, 197, 94);
    private static final Color COLOR_PRIMARIO_H  = new Color(22, 163, 74);
    private static final Color COLOR_GRIS_BTN    = new Color(229, 231, 235);
    private static final Color COLOR_GRIS_BTN_H  = new Color(209, 213, 219);
    private static final Color COLOR_BORDE       = new Color(209, 213, 219);
    private static final Color COLOR_HEADER_TBL  = new Color(249, 250, 251);
    private static final Color COLOR_FILA_SEL    = new Color(239, 246, 255);
    private static final Color COLOR_TEXTO       = new Color(17, 24, 39);
    private static final Color COLOR_TEXTO_GRIS  = new Color(107, 114, 128);
    private static final Color COLOR_AZUL        = new Color(59, 130, 246);
    private static final Color COLOR_AZUL_PANEL  = new Color(239, 246, 255);
    private static final Color COLOR_AZUL_BORDE  = new Color(147, 197, 253);

    private static final Color[] BADGE_BG = {
        new Color(220, 252, 231),
        new Color(254, 249, 195),
        new Color(254, 226, 226),
        new Color(243, 244, 246)
    };
    private static final Color[] BADGE_FG = {
        new Color(22, 101, 52),
        new Color(133, 77, 14),
        new Color(153, 27, 27),
        new Color(55, 65, 81)
    };

    // ─── DAO ─────────────────────────────────────────────────────────────────
    private final IExpedienteDAO expedienteDAO = new ExpedienteDAOImpl();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    // ─── Componentes ─────────────────────────────────────────────────────────
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel panelFiltros;
    private JPanel panelInfoBar;
    private JLabel lblInfoSeleccion;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbEtapa;
    private Expediente expedienteSeleccionado = null;

    public FrmExpedientesPanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Panel superior: infobar + barra busqueda
        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false);

        panelInfoBar = crearPanelInfoBar();
        panelInfoBar.setVisible(false);
        norte.add(panelInfoBar);
        norte.add(Box.createVerticalStrut(10));
        norte.add(crearBarraBusqueda());
        norte.add(Box.createVerticalStrut(12));

        // Panel sur: filtros (colapsable)
        panelFiltros = crearPanelFiltros();
        panelFiltros.setVisible(false);

        add(norte,             BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(panelFiltros,      BorderLayout.SOUTH);

        cargarTabla();
    }

    // ─── Info bar ─────────────────────────────────────────────────────────────
    private JPanel crearPanelInfoBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(COLOR_AZUL_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_AZUL_BORDE, 1, true),
            new EmptyBorder(8, 14, 8, 14)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        lblInfoSeleccion = new JLabel("Expediente seleccionado");
        lblInfoSeleccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoSeleccion.setForeground(new Color(30, 64, 175));
        p.add(lblInfoSeleccion, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);
        btns.add(crearBoton("Ver / Editar", COLOR_AZUL, Color.WHITE, e -> abrirFormulario(false)));
        btns.add(crearBoton("+ Nuevo", COLOR_PRIMARIO, Color.WHITE, e -> abrirFormulario(true)));
        p.add(btns, BorderLayout.EAST);
        return p;
    }

    // ─── Barra búsqueda ───────────────────────────────────────────────────────
    private JPanel crearBarraBusqueda() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setForeground(COLOR_TEXTO_GRIS);
        txtBuscar.setText("Buscar por nombre, cédula, ficha...");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(0, 12, 0, 12)
        ));
        txtBuscar.setPreferredSize(new Dimension(0, 36));
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().startsWith("Buscar")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(COLOR_TEXTO);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().isEmpty()) {
                    txtBuscar.setText("Buscar por nombre, cédula, ficha...");
                    txtBuscar.setForeground(COLOR_TEXTO_GRIS);
                }
            }
        });
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarTexto(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarTexto(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTexto(); }
        });

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        derecha.add(crearBoton("Filtros", COLOR_GRIS_BTN, COLOR_TEXTO, e -> {
            panelFiltros.setVisible(!panelFiltros.isVisible());
            revalidate(); repaint();
        }));
        derecha.add(crearBoton("+ Nuevo", COLOR_PRIMARIO, Color.WHITE, e -> abrirFormulario(true)));

        p.add(txtBuscar, BorderLayout.CENTER);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // ─── Tabla ────────────────────────────────────────────────────────────────
    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PANEL);
        p.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        String[] cols = {"Ficha", "Nombre", "Cédula", "Estado", "Etapa", "Fecha Inicio"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(243, 244, 246));
        tabla.setSelectionBackground(COLOR_FILA_SEL);
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(COLOR_HEADER_TBL);
        tabla.getTableHeader().setForeground(COLOR_TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
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
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 4));
                return this;
            }
        };
        tabla.getColumnModel().getColumn(1).setCellRenderer(izqPad);
        tabla.getColumnModel().getColumn(2).setCellRenderer(izqPad);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarSeleccion();
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirFormulario(false);
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_PANEL);
        // sin tamaño fijo — el BorderLayout.CENTER lo estira al espacio disponible

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── Filtros ──────────────────────────────────────────────────────────────
    private JPanel crearPanelFiltros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titulo = new JLabel("Panel de Filtros (RF-3)");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titulo.setForeground(COLOR_TEXTO_GRIS);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titulo);
        p.add(Box.createVerticalStrut(12));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbEstado = new JComboBox<>(new String[]{"Todos los estados",
            "ACTIVO", "EN_PROCESO", "CERRADO", "SUSPENDIDO"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 32));

        cmbEtapa = new JComboBox<>(new String[]{"Todas las etapas",
            "REGISTRO", "FAMILIA", "VIVIENDA", "GASTOS",
            "DOCUMENTOS", "CONSENTIMIENTO", "EVALUACION", "APROBADO"});
        cmbEtapa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEtapa.setPreferredSize(new Dimension(180, 32));

        fila1.add(new JLabel("Estado:"));
        fila1.add(cmbEstado);
        fila1.add(Box.createHorizontalStrut(8));
        fila1.add(new JLabel("Etapa:"));
        fila1.add(cmbEtapa);
        p.add(fila1);
        p.add(Box.createVerticalStrut(12));

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila2.setOpaque(false);
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila2.add(crearBoton("Aplicar", COLOR_AZUL, Color.WHITE, e -> aplicarFiltros()));
        fila2.add(crearBoton("Limpiar", COLOR_GRIS_BTN, COLOR_TEXTO, e -> limpiarFiltros()));
        p.add(fila2);
        return p;
    }

    // ─── Carga datos ──────────────────────────────────────────────────────────
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        expedienteSeleccionado = null;
        panelInfoBar.setVisible(false);
        try {
            List<Expediente> lista = expedienteDAO.findAll();
            for (Expediente exp : lista) {
                String nombre = exp.getTitular() != null
                    ? exp.getTitular().getNombres() + " " + exp.getTitular().getApellidos() : "—";
                String cedula = exp.getTitular() != null && exp.getTitular().getNumeroDocumento() != null
                    ? exp.getTitular().getNumeroDocumento() : "—";
                String estado = exp.getEstado() != null ? exp.getEstado().name() : "—";
                String etapa  = exp.getEtapaActual() != null ? exp.getEtapaActual().name() : "—";
                String fecha  = exp.getFechaInicio() != null ? sdf.format(exp.getFechaInicio()) : "—";
                modeloTabla.addRow(new Object[]{
                    exp.getNumeroFicha() != null ? exp.getNumeroFicha() : String.valueOf(exp.getId()),
                    nombre, cedula, estado, etapa, fecha
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar expedientes:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarTexto() {
        String texto = txtBuscar.getText().trim();
        if (texto.startsWith("Buscar") || texto.isEmpty()) { sorter.setRowFilter(null); return; }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1, 2));
    }

    private void aplicarFiltros() {
        String estado = (String) cmbEstado.getSelectedItem();
        String etapa  = (String) cmbEtapa.getSelectedItem();
        java.util.List<RowFilter<DefaultTableModel, Object>> filtros = new java.util.ArrayList<>();
        if (estado != null && !estado.startsWith("Todos"))
            filtros.add(RowFilter.regexFilter("^" + estado + "$", 3));
        if (etapa != null && !etapa.startsWith("Todas"))
            filtros.add(RowFilter.regexFilter("^" + etapa + "$", 4));
        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    private void limpiarFiltros() {
        cmbEstado.setSelectedIndex(0);
        cmbEtapa.setSelectedIndex(0);
        sorter.setRowFilter(null);
        txtBuscar.setText("Buscar por nombre, cédula, ficha...");
        txtBuscar.setForeground(COLOR_TEXTO_GRIS);
    }

    private void actualizarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { expedienteSeleccionado = null; panelInfoBar.setVisible(false); return; }
        int fm = tabla.convertRowIndexToModel(fila);
        String ficha  = modeloTabla.getValueAt(fm, 0).toString();
        String nombre = modeloTabla.getValueAt(fm, 1).toString();
        try { expedienteSeleccionado = expedienteDAO.findByNumeroFicha(ficha); }
        catch (Exception ex) { expedienteSeleccionado = null; }
        lblInfoSeleccion.setText("Seleccionado: " + nombre + "  (Ficha " + ficha + ")");
        panelInfoBar.setVisible(true);
        revalidate();
    }

    private void abrirFormulario(boolean esNuevo) {
        if (esNuevo) {
            JOptionPane.showMessageDialog(this, "Aquí se abrirá frmCrearExpediente.", "Nuevo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (expedienteSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un expediente primero.", "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Editar ficha: " + expedienteSeleccionado.getNumeroFicha(), "Editar", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Helper botón ─────────────────────────────────────────────────────────
    private JButton crearBoton(String texto, Color bg, Color fg, ActionListener accion) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
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
        btn.addActionListener(accion);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg == COLOR_PRIMARIO ? COLOR_PRIMARIO_H
                    : bg == COLOR_GRIS_BTN ? COLOR_GRIS_BTN_H : bg.darker());
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); btn.repaint(); }
        });
        return btn;
    }

    // ─── Badge renderer ───────────────────────────────────────────────────────
    private class BadgeRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String estado = value != null ? value.toString() : "";
            int idx;
            switch (estado) {
                case "ACTIVO":     idx = 0; break;
                case "EN_PROCESO": idx = 1; break;
                case "CERRADO":    idx = 2; break;
                default:           idx = 3; break;
            }
            JLabel lbl = new JLabel(estado.replace("_", " "));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBackground(BADGE_BG[idx]);
            lbl.setForeground(BADGE_FG[idx]);
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(isSelected ? COLOR_FILA_SEL : COLOR_PANEL);
            JPanel badge = new JPanel(new BorderLayout());
            badge.setBackground(BADGE_BG[idx]);
            badge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BADGE_FG[idx].brighter(), 1, true),
                new EmptyBorder(2, 8, 2, 8)
            ));
            badge.add(lbl);
            wrapper.add(badge);
            return wrapper;
        }
    }
}
