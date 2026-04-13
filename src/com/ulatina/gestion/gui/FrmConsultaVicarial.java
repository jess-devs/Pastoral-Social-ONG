package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.BadgeRenderer;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.util.JPAUtil;
import com.ulatina.gestion.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * FrmConsultaVicarial — Ventana exclusiva para el rol CONSULTA_VICARIAL.
 * Muestra la seccion de expedientes en modo solo lectura:
 * busqueda y visualizacion, sin opciones de crear, editar ni eliminar.
 */
public class FrmConsultaVicarial extends JFrame {

    private final ExpedienteController expedienteController = new ExpedienteController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    private final Usuario usuario;

    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel panelFiltros;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbEtapa;

    public FrmConsultaVicarial(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Pastoral Social \u2014 Consulta Vicarial");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setPreferredSize(new Dimension(1040, 660));
        setLayout(new BorderLayout());

        add(crearSidebar(), BorderLayout.WEST);
        add(crearAreaPrincipal(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JPAUtil.close();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    // =========================================================================
    // SIDEBAR
    // =========================================================================
    private JPanel crearSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(AppColors.SIDE_BG);
        sb.setPreferredSize(new Dimension(200, 0));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(AppColors.SIDE_BG);
        brand.setBorder(new EmptyBorder(28, 22, 24, 22));
        brand.setMaximumSize(new Dimension(200, 88));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l1 = new JLabel("Pastoral");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l1.setForeground(AppColors.PANEL);
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l2 = new JLabel("Social");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l2.setForeground(AppColors.SIDE_ACTV);
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(l1);
        brand.add(l2);
        sb.add(brand);

        // Unico boton disponible, activo por defecto
        JButton bExp = navBtn("  Expedientes");
        bExp.setBackground(AppColors.SIDE_ACTV);
        bExp.setForeground(AppColors.PANEL);
        sb.add(bExp);

        sb.add(Box.createVerticalGlue());

        JLabel lblRol = new JLabel("  Consulta Vicarial");
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(AppColors.SIDE_TXT);
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRol.setBorder(new EmptyBorder(0, 22, 6, 0));
        sb.add(lblRol);

        JButton bCerrar = navBtn("  Cerrar sesi\u00f3n");
        bCerrar.setForeground(new Color(0xFCA5A5));
        bCerrar.addActionListener(e -> cerrarSesion());
        sb.add(bCerrar);
        sb.add(Box.createVerticalStrut(16));

        return sb;
    }

    private JButton navBtn(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(AppColors.SIDE_TXT);
        btn.setBackground(AppColors.SIDE_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 46));
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setBorder(new EmptyBorder(0, 22, 0, 12));
        return btn;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "\u00bfDesea cerrar la sesi\u00f3n?", "Cerrar sesi\u00f3n",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        SessionContext.cerrarSesion();
        SwingUtilities.invokeLater(() -> {
            new FrmLogin().setVisible(true);
            dispose();
        });
    }

    // =========================================================================
    // AREA PRINCIPAL
    // =========================================================================
    private JPanel crearAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(AppColors.FONDO);
        area.add(crearTopbar(), BorderLayout.NORTH);
        area.add(crearPanelExpedientes(), BorderLayout.CENTER);
        return area;
    }

    private JPanel crearTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppColors.PANEL);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE),
                new EmptyBorder(14, 26, 14, 26)));
        top.setPreferredSize(new Dimension(0, 58));

        JLabel lblTitulo = new JLabel("Consulta Vicarial \u2014 Expedientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(AppColors.TEXTO);
        top.add(lblTitulo, BorderLayout.WEST);

        JLabel lblUser = new JLabel(usuario.getNombre() + "  (" + usuario.getRol().name() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(AppColors.TEXTO_GRIS);
        top.add(lblUser, BorderLayout.EAST);

        return top;
    }

    // =========================================================================
    // PANEL EXPEDIENTES (solo lectura)
    // =========================================================================
    private JPanel crearPanelExpedientes() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(AppColors.FONDO);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false);
        norte.add(crearBarraBusqueda());
        norte.add(Box.createVerticalStrut(12));

        panelFiltros = crearPanelFiltros();
        panelFiltros.setVisible(false);

        p.add(norte, BorderLayout.NORTH);
        p.add(crearPanelTabla(), BorderLayout.CENTER);
        p.add(panelFiltros, BorderLayout.SOUTH);

        cargarTabla();
        return p;
    }

    // Barra de busqueda sin boton "+ Nuevo"
    private JPanel crearBarraBusqueda() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setForeground(AppColors.TEXTO_GRIS);
        txtBuscar.setText("Buscar por nombre, c\u00e9dula, ficha...");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        txtBuscar.setPreferredSize(new Dimension(0, 36));
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().startsWith("Buscar")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(AppColors.TEXTO);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().isEmpty()) {
                    txtBuscar.setText("Buscar por nombre, c\u00e9dula, ficha...");
                    txtBuscar.setForeground(AppColors.TEXTO_GRIS);
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
        derecha.add(UIFactory.crearBoton("Filtros", AppColors.GRIS_BTN, AppColors.TEXTO, e -> {
            panelFiltros.setVisible(!panelFiltros.isVisible());
            revalidate();
            repaint();
        }));

        p.add(txtBuscar, BorderLayout.CENTER);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        String[] cols = { "Ficha", "Nombre", "C\u00e9dula", "Estado", "Etapa", "Fecha Inicio" };
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(243, 244, 246));
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

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        // Doble clic abre detalle en modo solo lectura
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirDetalleReadOnly();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.PANEL);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelFiltros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel titulo = new JLabel("Panel de Filtros");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titulo.setForeground(AppColors.TEXTO_GRIS);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titulo);
        p.add(Box.createVerticalStrut(12));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbEstado = new JComboBox<>(new String[]{ "Todos los estados",
                "ACTIVO", "EN_PROCESO", "CERRADO", "SUSPENDIDO" });
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 32));

        cmbEtapa = new JComboBox<>(new String[]{ "Todas las etapas",
                "REGISTRO", "FAMILIA", "VIVIENDA", "GASTOS",
                "DOCUMENTOS", "CONSENTIMIENTO", "EVALUACION", "APROBADO" });
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
        fila2.add(UIFactory.crearBoton("Aplicar", AppColors.AZUL, Color.WHITE, e -> aplicarFiltros()));
        fila2.add(UIFactory.crearBoton("Limpiar", AppColors.GRIS_BTN, AppColors.TEXTO, e -> limpiarFiltros()));
        p.add(fila2);
        return p;
    }

    // =========================================================================
    // LOGICA DE DATOS
    // =========================================================================
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Expediente> lista = expedienteController.findAll();
            for (Expediente exp : lista) {
                String nombre = exp.getTitular() != null
                        ? exp.getTitular().getNombres() + " " + exp.getTitular().getApellidos()
                        : "\u2014";
                String cedula = exp.getTitular() != null && exp.getTitular().getNumeroDocumento() != null
                        ? exp.getTitular().getNumeroDocumento()
                        : "\u2014";
                String estado = exp.getEstado()      != null ? exp.getEstado().name()      : "\u2014";
                String etapa  = exp.getEtapaActual() != null ? exp.getEtapaActual().name() : "\u2014";
                String fecha  = exp.getFechaInicio() != null ? sdf.format(exp.getFechaInicio()) : "\u2014";
                modeloTabla.addRow(new Object[]{
                        exp.getNumeroFicha() != null ? exp.getNumeroFicha() : String.valueOf(exp.getId()),
                        nombre, cedula, estado, etapa, fecha
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar expedientes:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarTexto() {
        String texto = txtBuscar.getText().trim();
        if (texto.startsWith("Buscar") || texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
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
        txtBuscar.setText("Buscar por nombre, c\u00e9dula, ficha...");
        txtBuscar.setForeground(AppColors.TEXTO_GRIS);
    }

    private void abrirDetalleReadOnly() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        int fm = tabla.convertRowIndexToModel(fila);
        String ficha = modeloTabla.getValueAt(fm, 0).toString();
        Expediente exp = expedienteController.findByNumeroFicha(ficha);
        if (exp == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el expediente seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FrmDetalleExpediente dlg = new FrmDetalleExpediente(this, exp, null);
        dlg.setReadOnly(true);
        dlg.setVisible(true);
    }
}
