package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * FrmDetalleExpediente — Dialog de creación/edición de expedientes.
 * Modo nuevo: expediente == null. Modo editar: expediente != null.
 */
public class FrmDetalleExpediente extends JDialog {

    // ─── Estado ───────────────────────────────────────────────────────────────
    private Expediente expediente;
    private final boolean esNuevo;
    private final Runnable onGuardado;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // ─── Controllers ──────────────────────────────────────────────────────────
    private final ExpedienteController expedienteController = new ExpedienteController();
    private final ParroquiaController parroquiaController = new ParroquiaController();

    // ─── Componentes — título ────────────────────────────────────────────────
    private JLabel lblTituloPrincipal;
    private JLabel lblMarcadorBadge;

    // ─── Tab pane ─────────────────────────────────────────────────────────────
    private JTabbedPane tabbedPane;

    // ─── Tab 0: Datos — Persona ───────────────────────────────────────────────
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JComboBox<TipoDocumentoPersona> cmbTipoDoc;
    private JTextField txtNumeroDoc;
    private JFormattedTextField txtFechaNac;
    private JComboBox<Sexo> cmbSexo;
    private JComboBox<EstadoCivil> cmbEstadoCivil;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtNacionalidad;
    private JTextField txtPaisOrigen;
    private JTextField txtProfesion;
    private JTextField txtNivelEducacion;
    private JTextArea txtCondicionSalud;
    private JCheckBox chkTieneSeguro;
    private JTextField txtCondicionMigratoria;
    // Tab 0: Datos — Expediente
    private JTextField txtEntrevistador;
    private JComboBox<EstadoExpediente> cmbEstado;
    private JComboBox<Parroquia> cmbParroquia;
    private JFormattedTextField txtFechaInicio;
    private JFormattedTextField txtFechaPrevista;
    private JTextArea txtObservaciones;
    private JComboBox<String> cmbColorMarcador;

    // ─── Tab 2: Vivienda ──────────────────────────────────────────────────────
    private JTextField txtDirVivienda;
    private JComboBox<TipoVivienda> cmbTipoVivienda;
    private JComboBox<TenenciaVivienda> cmbTenencia;
    private JComboBox<CondicionVivienda> cmbCondicion;
    private Vivienda viviendaActual;

    // ─── Tab 3: Adendum ───────────────────────────────────────────────────────
    private JTextArea txtAdendumObs;
    private DefaultTableModel modeloGastos;
    private JTable tablaGastos;
    private JLabel lblTotalGastos;
    private JComboBox<CategoriaGasto> cmbCatGasto;
    private JTextField txtConceptoGasto;
    private JTextField txtMontoGasto;
    private JFormattedTextField txtFechaGasto;
    private GastoMensual gastoEnEdicion = null;
    private final java.util.List<GastoMensual> gastosActuales = new java.util.ArrayList<>();
    private Adendum adendumActual;

    // ─── Tablas read-only ─────────────────────────────────────────────────────
    private DefaultTableModel modeloFamilia;
    private DefaultTableModel modeloDocs;
    private DefaultTableModel modeloAsistencia;
    private DefaultTableModel modeloEntrevistas;

    // ─── Constructor ──────────────────────────────────────────────────────────
    public FrmDetalleExpediente(Window owner, Expediente expediente, Runnable onGuardado) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.expediente = expediente;
        this.esNuevo = (expediente == null);
        this.onGuardado = onGuardado;

        initComponents();
        cargarDatos();
        aplicarModoAcceso();

        pack();
        setMinimumSize(new Dimension(820, 640));
        setPreferredSize(new Dimension(900, 700));
        setLocationRelativeTo(owner);
        setResizable(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CONSTRUCCIÓN UI
    // ═════════════════════════════════════════════════════════════════════════
    private void initComponents() {
        setTitle(esNuevo ? "Nuevo Expediente" : "Expediente #" + expediente.getNumeroFicha());
        getContentPane().setBackground(AppColors.FONDO);
        setLayout(new BorderLayout());

        tabbedPane = crearTabbedPane();

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    // ─── Panel título ─────────────────────────────────────────────────────────
    private JPanel crearPanelTitulo() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 12, 24));

        lblTituloPrincipal = new JLabel(esNuevo ? "Nuevo Expediente"
                : "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular());
        lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloPrincipal.setForeground(AppColors.TEXTO);

        lblMarcadorBadge = new JLabel("  URGENTE  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.ROJO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblMarcadorBadge.setOpaque(false);
        lblMarcadorBadge.setForeground(Color.WHITE);
        lblMarcadorBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMarcadorBadge.setVisible(false);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(lblMarcadorBadge);

        p.add(lblTituloPrincipal, BorderLayout.WEST);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // ─── TabbedPane ───────────────────────────────────────────────────────────
    private JTabbedPane crearTabbedPane() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tp.setBackground(AppColors.FONDO);

        tp.addTab("Datos", crearTabDatos());
        tp.addTab("Familia", crearTabFamilia());
        tp.addTab("Vivienda", crearTabVivienda());
        tp.addTab("Adendum", crearTabAdendum());
        tp.addTab("Docs", crearTabDocs());
        tp.addTab("Asistencia", crearTabAsistencia());
        tp.addTab("Entrevistas", crearTabEntrevistas());
        return tp;
    }

    // ─── Tab 0: Datos ─────────────────────────────────────────────────────────
    private JScrollPane crearTabDatos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 4, 2, 4);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(2, 4, 5, 12);

        GridBagConstraints wc = new GridBagConstraints();
        wc.gridwidth = GridBagConstraints.REMAINDER;
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.weightx = 1.0;
        wc.insets = new Insets(2, 4, 5, 12);

        int row = 0;

        // Subtítulo sección Titular
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 4;
        JLabel secTitular = new JLabel("DATOS DEL TITULAR");
        secTitular.setFont(new Font("Segoe UI", Font.BOLD, 11));
        secTitular.setForeground(AppColors.TEXTO_GRIS);
        secTitular.setBorder(new EmptyBorder(0, 0, 4, 0));
        p.add(secTitular, lc);
        lc.gridwidth = 1;
        row++;

        txtNombres = new JTextField(15);
        txtApellidos = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Nombres *", txtNombres, "Apellidos *", txtApellidos);

        cmbTipoDoc = new JComboBox<>(TipoDocumentoPersona.values());
        txtNumeroDoc = new JTextField(12);
        txtTelefono = new JTextField(12);
        JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        docPanel.setOpaque(false);
        docPanel.add(cmbTipoDoc);
        docPanel.add(txtNumeroDoc);
        agregarFila(p, row++, lc, fc, "Documento *", docPanel, "Teléfono", txtTelefono);

        txtFechaNac = UIFactory.crearCampoFecha();
        cmbSexo = new JComboBox<>(Sexo.values());
        agregarFila(p, row++, lc, fc, "Fecha Nacimiento", txtFechaNac, "Sexo", cmbSexo);

        cmbEstadoCivil = new JComboBox<>(EstadoCivil.values());
        txtProfesion = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Estado Civil", cmbEstadoCivil, "Profesión/Oficio", txtProfesion);

        txtNivelEducacion = new JTextField(15);
        txtPaisOrigen = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Nivel Educación", txtNivelEducacion, "País Origen", txtPaisOrigen);

        txtNacionalidad = new JTextField(15);
        txtCondicionMigratoria = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Nacionalidad", txtNacionalidad, "Cond. Migratoria", txtCondicionMigratoria);

        // Dirección (full width)
        txtDireccion = new JTextField();
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 1;
        p.add(etiqueta("Dirección"), lc);
        wc.gridx = 1;
        wc.gridy = row;
        wc.gridwidth = GridBagConstraints.REMAINDER;
        p.add(txtDireccion, wc);
        wc.gridwidth = 4;
        row++;

        // Tiene Seguro
        chkTieneSeguro = new JCheckBox("Tiene seguro de salud");
        chkTieneSeguro.setOpaque(false);
        chkTieneSeguro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 4;
        p.add(chkTieneSeguro, lc);
        lc.gridwidth = 1;
        row++;

        // Condición de Salud
        txtCondicionSalud = new JTextArea(2, 20);
        txtCondicionSalud.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCondicionSalud.setLineWrap(true);
        txtCondicionSalud.setWrapStyleWord(true);
        JScrollPane scrollSalud = new JScrollPane(txtCondicionSalud);
        scrollSalud.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.AMBAR_BG, 2, true),
                new EmptyBorder(2, 4, 2, 4)));
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 1;
        p.add(etiqueta("Condición de Salud"), lc);
        wc.gridx = 1;
        wc.gridy = row;
        wc.gridwidth = GridBagConstraints.REMAINDER;
        p.add(scrollSalud, wc);
        wc.gridwidth = 4;
        row++;

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.BORDE);
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 4;
        lc.fill = GridBagConstraints.HORIZONTAL;
        lc.insets = new Insets(10, 4, 10, 4);
        p.add(sep, lc);
        lc.fill = GridBagConstraints.NONE;
        lc.insets = new Insets(5, 4, 2, 4);
        lc.gridwidth = 1;
        row++;

        // Subtítulo sección Expediente
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 4;
        JLabel secExp = new JLabel("DATOS DEL EXPEDIENTE");
        secExp.setFont(new Font("Segoe UI", Font.BOLD, 11));
        secExp.setForeground(AppColors.TEXTO_GRIS);
        secExp.setBorder(new EmptyBorder(0, 0, 4, 0));
        p.add(secExp, lc);
        lc.gridwidth = 1;
        row++;

        txtEntrevistador = new JTextField(15);
        cmbEstado = new JComboBox<>(EstadoExpediente.values());
        agregarFila(p, row++, lc, fc, "Entrevistador", txtEntrevistador, "Estado", cmbEstado);

        cmbParroquia = new JComboBox<>();
        cmbParroquia.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx2, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx2, sel, focus);
                if (value instanceof Parroquia)
                    setText(((Parroquia) value).getNombre());
                return this;
            }
        });
        cmbColorMarcador = new JComboBox<>(new String[] { "NINGUNO", "URGENTE", "PRIORITARIO", "NORMAL" });
        cmbColorMarcador.addActionListener(e -> {
            boolean urgente = "URGENTE".equals(cmbColorMarcador.getSelectedItem());
            lblMarcadorBadge.setVisible(urgente);
        });
        agregarFila(p, row++, lc, fc, "Parroquia", cmbParroquia, "Color Marcador", cmbColorMarcador);

        txtFechaInicio = UIFactory.crearCampoFecha();
        txtFechaPrevista = UIFactory.crearCampoFecha();
        agregarFila(p, row++, lc, fc, "Fecha Inicio *", txtFechaInicio, "Fecha Prevista Concl.", txtFechaPrevista);

        // Observaciones (full width)
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 1;
        p.add(etiqueta("Observaciones"), lc);
        wc.gridx = 1;
        wc.gridy = row;
        wc.gridwidth = GridBagConstraints.REMAINDER;
        p.add(scrollObs, wc);
        row++;

        // Spacer
        GridBagConstraints sc = new GridBagConstraints();
        sc.gridy = row;
        sc.gridwidth = 4;
        sc.weighty = 1.0;
        sc.fill = GridBagConstraints.VERTICAL;
        p.add(Box.createVerticalGlue(), sc);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.PANEL);
        return scroll;
    }

    private void agregarFila(JPanel p, int row,
            GridBagConstraints lc, GridBagConstraints fc,
            String lbl1, Component c1, String lbl2, Component c2) {
        lc.gridx = 0;
        lc.gridy = row;
        p.add(etiqueta(lbl1), lc);
        fc.gridx = 1;
        fc.gridy = row;
        p.add(c1, fc);
        lc.gridx = 2;
        lc.gridy = row;
        p.add(etiqueta(lbl2), lc);
        fc.gridx = 3;
        fc.gridy = row;
        p.add(c2, fc);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(AppColors.TEXTO_GRIS);
        return l;
    }

    // ─── Tab 1: Familia ───────────────────────────────────────────────────────
    private JPanel crearTabFamilia() {
        modeloFamilia = new DefaultTableModel(
                new String[] { "Nombre", "Relación", "Ocupación", "Trabaja", "Ingreso Mensual", "Jefatura" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        return crearTabTabla(modeloFamilia, "Miembros familiares del expediente");
    }

    // ─── Tab 2: Vivienda ──────────────────────────────────────────────────────
    private JPanel crearTabVivienda() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(20, 28, 20, 28));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 4, 2, 4);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(2, 4, 6, 12);

        txtDirVivienda = new JTextField();
        cmbTipoVivienda = new JComboBox<>(TipoVivienda.values());
        cmbTenencia = new JComboBox<>(TenenciaVivienda.values());
        cmbCondicion = new JComboBox<>(CondicionVivienda.values());

        int row = 0;

        lc.gridx = 0;
        lc.gridy = row;
        p.add(etiqueta("Dirección de Vivienda"), lc);
        GridBagConstraints wc = new GridBagConstraints();
        wc.gridx = 1;
        wc.gridy = row;
        wc.gridwidth = 3;
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.weightx = 1.0;
        wc.insets = new Insets(2, 4, 6, 12);
        p.add(txtDirVivienda, wc);
        row++;

        agregarFila(p, row++, lc, fc, "Tipo de Vivienda", cmbTipoVivienda, "Tenencia", cmbTenencia);
        lc.gridx = 0;
        lc.gridy = row;
        p.add(etiqueta("Condición"), lc);
        fc.gridx = 1;
        fc.gridy = row;
        fc.gridwidth = 1;
        p.add(cmbCondicion, fc);
        row++;

        GridBagConstraints sc = new GridBagConstraints();
        sc.gridy = row;
        sc.gridwidth = 4;
        sc.weighty = 1.0;
        sc.fill = GridBagConstraints.BOTH;
        p.add(new JPanel() {
            {
                setOpaque(false);
            }
        }, sc);

        return p;
    }

    // ─── Tab 3: Adendum ───────────────────────────────────────────────────────
    private JPanel crearTabAdendum() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lObs = new JLabel("Observaciones de la entrevista");
        lObs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lObs.setForeground(AppColors.TEXTO);

        txtAdendumObs = new JTextArea(4, 20);
        txtAdendumObs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAdendumObs.setLineWrap(true);
        txtAdendumObs.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtAdendumObs);
        scrollObs.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scrollObs.setPreferredSize(new Dimension(0, 90));

        JPanel obsPanel = new JPanel(new BorderLayout(0, 6));
        obsPanel.setOpaque(false);
        obsPanel.add(lObs, BorderLayout.NORTH);
        obsPanel.add(scrollObs, BorderLayout.CENTER);

        JLabel lGastos = new JLabel("Gastos mensuales");
        lGastos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lGastos.setForeground(AppColors.TEXTO);
        lGastos.setBorder(new EmptyBorder(4, 0, 6, 0));

        cmbCatGasto = new JComboBox<>(CategoriaGasto.values());
        txtConceptoGasto = new JTextField(12);
        txtConceptoGasto.putClientProperty("JTextField.placeholderText", "Ej: Electricidad");
        txtMontoGasto = new JTextField(8);
        txtMontoGasto.putClientProperty("JTextField.placeholderText", "Ej: 18500");
        txtFechaGasto = UIFactory.crearCampoFecha();

        for (JComponent c : new JComponent[] { cmbCatGasto, txtConceptoGasto, txtMontoGasto, txtFechaGasto }) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            if (c instanceof JTextField || c instanceof JFormattedTextField) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(AppColors.BORDE, 1, true),
                        new EmptyBorder(4, 8, 4, 8)));
            }
            c.setPreferredSize(new Dimension(c.getPreferredSize().width, 32));
        }
        cmbCatGasto.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        JButton btnAgregar = UIFactory.crearBotonDialog("+ Agregar gasto", AppColors.PRIMARIO, Color.WHITE,
                e -> confirmarGasto());

        JPanel entradaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        entradaPanel.setOpaque(false);
        entradaPanel.add(campoConEtiqueta("Categoría", cmbCatGasto));
        entradaPanel.add(campoConEtiqueta("Concepto", txtConceptoGasto));
        entradaPanel.add(campoConEtiqueta("Monto (₡)", txtMontoGasto));
        entradaPanel.add(campoConEtiqueta("Fecha (dd/mm/aaaa)", txtFechaGasto));
        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(18, 0, 0, 0));
        btnWrapper.add(btnAgregar, BorderLayout.SOUTH);
        entradaPanel.add(btnWrapper);

        modeloGastos = new DefaultTableModel(
                new String[] { "Categoría", "Concepto", "Monto", "Fecha", "Acciones" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaGastos = new JTable(modeloGastos);
        tablaGastos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaGastos.setRowHeight(34);
        tablaGastos.setShowVerticalLines(false);
        tablaGastos.setGridColor(new Color(243, 244, 246));
        tablaGastos.setFocusable(false);
        tablaGastos.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaGastos.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tablaGastos.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tablaGastos.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));

        tablaGastos.getColumnModel().getColumn(4).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            cell.setOpaque(true);
            cell.setBackground(sel ? tbl.getSelectionBackground() : Color.WHITE);
            JLabel editar = new JLabel("Editar");
            editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            editar.setForeground(AppColors.AZUL);
            JLabel sep2 = new JLabel("|");
            sep2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sep2.setForeground(AppColors.TEXTO_GRIS);
            JLabel eliminar = new JLabel("X");
            eliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            eliminar.setForeground(AppColors.ROJO);
            cell.add(editar);
            cell.add(sep2);
            cell.add(eliminar);
            return cell;
        });
        tablaGastos.getColumnModel().getColumn(4).setMaxWidth(100);
        tablaGastos.getColumnModel().getColumn(4).setMinWidth(80);

        tablaGastos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaGastos.columnAtPoint(e.getPoint());
                int row = tablaGastos.rowAtPoint(e.getPoint());
                if (col != 4 || row < 0 || row >= gastosActuales.size())
                    return;
                Rectangle rect = tablaGastos.getCellRect(row, col, false);
                int relX = e.getX() - rect.x;
                if (relX < 50)
                    cargarGastoEnFormulario(row);
                else
                    eliminarGasto(row);
            }
        });

        JScrollPane scrollGastos = new JScrollPane(tablaGastos);
        scrollGastos.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scrollGastos.getViewport().setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        totalPanel.setBackground(AppColors.HEADER_TBL);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDE),
                new EmptyBorder(0, 0, 0, 8)));
        JLabel lTotal = new JLabel("Total gastos mensuales:");
        lTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lTotal.setForeground(AppColors.TEXTO_GRIS);
        lblTotalGastos = new JLabel("0.00 colones");
        lblTotalGastos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalGastos.setForeground(AppColors.TEXTO);
        totalPanel.add(lTotal);
        totalPanel.add(lblTotalGastos);

        JPanel gastosConTotal = new JPanel(new BorderLayout());
        gastosConTotal.setOpaque(false);
        gastosConTotal.add(scrollGastos, BorderLayout.CENTER);
        gastosConTotal.add(totalPanel, BorderLayout.SOUTH);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setOpaque(false);
        norte.add(obsPanel, BorderLayout.NORTH);
        JPanel gastosTop = new JPanel(new BorderLayout(0, 4));
        gastosTop.setOpaque(false);
        gastosTop.add(lGastos, BorderLayout.NORTH);
        gastosTop.add(entradaPanel, BorderLayout.CENTER);
        norte.add(gastosTop, BorderLayout.CENTER);

        p.add(norte, BorderLayout.NORTH);
        p.add(gastosConTotal, BorderLayout.CENTER);
        return p;
    }

    // ─── Helper: campo con etiqueta encima ───────────────────────────────────
    private JPanel campoConEtiqueta(String texto, JComponent campo) {
        JPanel wrap = new JPanel(new BorderLayout(0, 3));
        wrap.setOpaque(false);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        wrap.add(lbl, BorderLayout.NORTH);
        wrap.add(campo, BorderLayout.CENTER);
        return wrap;
    }

    // ─── Cargar gasto en formulario para edición ──────────────────────────────
    private void cargarGastoEnFormulario(int row) {
        if (row < 0 || row >= gastosActuales.size())
            return;
        gastoEnEdicion = gastosActuales.get(row);
        cmbCatGasto.setSelectedItem(gastoEnEdicion.getCategoria());
        txtConceptoGasto.setText(nvl(gastoEnEdicion.getConcepto()));
        txtMontoGasto.setText(gastoEnEdicion.getMonto() != null ? gastoEnEdicion.getMonto().toPlainString() : "");
        if (gastoEnEdicion.getFecha() != null)
            txtFechaGasto.setText(sdf.format(gastoEnEdicion.getFecha()));
    }

    // ─── Confirmar (agregar o actualizar) gasto ───────────────────────────────
    private void confirmarGasto() {
        String concepto = txtConceptoGasto.getText().trim();
        String montoStr = txtMontoGasto.getText().trim();
        String fechaStr = txtFechaGasto.getText().trim();

        if (concepto.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Concepto y monto son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr.replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número válido.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (adendumActual == null) {
                if (expediente == null || expediente.getId() == null) {
                    JOptionPane.showMessageDialog(this,
                            "Guarde el expediente primero antes de agregar gastos.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                adendumActual = expedienteController.findAdendumByExpediente(expediente.getId());
                if (adendumActual == null) {
                    adendumActual = new Adendum();
                    adendumActual.setExpediente(expediente);
                    adendumActual.setObservaciones(txtAdendumObs.getText().trim());
                    expedienteController.guardarAdendum(adendumActual);
                }
            }

            GastoMensual gasto = (gastoEnEdicion != null) ? gastoEnEdicion : new GastoMensual();
            gasto.setCategoria((CategoriaGasto) cmbCatGasto.getSelectedItem());
            gasto.setConcepto(concepto);
            gasto.setMonto(monto);
            gasto.setFecha(parseFecha(fechaStr));
            gasto.setAdendum(adendumActual);
            expedienteController.guardarGasto(gasto);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el gasto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        gastoEnEdicion = null;
        limpiarFormularioGasto();
        recargarTablaGastos();
    }

    // ─── Eliminar gasto ───────────────────────────────────────────────────────
    private void eliminarGasto(int row) {
        if (row < 0 || row >= gastosActuales.size())
            return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este gasto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        GastoMensual g = gastosActuales.get(row);
        try {
            if (g.getId() != null)
                expedienteController.eliminarGasto(g.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        recargarTablaGastos();
    }

    // ─── Limpiar formulario de gasto ──────────────────────────────────────────
    private void limpiarFormularioGasto() {
        cmbCatGasto.setSelectedIndex(0);
        txtConceptoGasto.setText("");
        txtMontoGasto.setText("");
        try {
            txtFechaGasto.setText("");
        } catch (Exception ignored) {
        }
    }

    // ─── Recargar tabla de gastos ─────────────────────────────────────────────
    private void recargarTablaGastos() {
        modeloGastos.setRowCount(0);
        gastosActuales.clear();
        if (adendumActual == null || adendumActual.getId() == null)
            return;
        List<GastoMensual> gastos = expedienteController.findGastosByAdendum(adendumActual.getId());
        BigDecimal total = BigDecimal.ZERO;
        for (GastoMensual g : gastos) {
            gastosActuales.add(g);
            BigDecimal m = g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO;
            total = total.add(m);
            String catDisplay = g.getCategoria() != null
                    ? g.getCategoria().name().replace("_", " ").substring(0, 1).toUpperCase()
                            + g.getCategoria().name().replace("_", " ").substring(1).toLowerCase()
                    : "—";
            modeloGastos.addRow(new Object[] {
                    catDisplay,
                    nvl(g.getConcepto()),
                    String.format("%,.0f", m),
                    g.getFecha() != null ? sdf.format(g.getFecha()) : "—",
                    ""
            });
        }
        lblTotalGastos.setText(String.format("%,.0f colones", total));
    }

    // ─── Tab 4: Docs ──────────────────────────────────────────────────────────
    private JPanel crearTabDocs() {
        modeloDocs = new DefaultTableModel(
                new String[] { "Tipo", "Descripción", "Fecha Subida", "Firmado" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        return crearTabTabla(modeloDocs, "Documentos adjuntos al expediente");
    }

    // ─── Tab 5: Asistencia ────────────────────────────────────────────────────
    private JPanel crearTabAsistencia() {
        modeloAsistencia = new DefaultTableModel(
                new String[] { "Tipo Asistencia", "Modalidad", "Frecuencia", "Duración", "Valor" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        return crearTabTabla(modeloAsistencia, "Asistencias solicitadas");
    }

    // ─── Tab 6: Entrevistas ───────────────────────────────────────────────────
    private JPanel crearTabEntrevistas() {
        modeloEntrevistas = new DefaultTableModel(
                new String[] { "Fecha", "Entrevistador", "Recomienda Ayuda", "Observaciones" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        return crearTabTabla(modeloEntrevistas, "Entrevistas realizadas");
    }

    // ─── Helper: panel tabla genérico (read-only) ─────────────────────────────
    private JPanel crearTabTabla(DefaultTableModel modelo, String descripcion) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lbl = new JLabel(descripcion);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColors.TEXTO_GRIS);

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(243, 244, 246));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scroll.getViewport().setBackground(AppColors.PANEL);

        p.add(lbl, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── Barra inferior ───────────────────────────────────────────────────────
    private JPanel crearBarraInferior() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDE),
                new EmptyBorder(12, 24, 12, 24)));

        JButton btnCancelar = UIFactory.crearBotonDialog("Cancelar", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> dispose());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        JButton btnGuardar = UIFactory.crearBotonDialog("Guardar", AppColors.PRIMARIO, Color.WHITE, e -> guardar());

        JButton btnProlong = UIFactory.crearBotonDialog("+ Prolongar ayuda", AppColors.PURPURA, Color.WHITE,
                e -> JOptionPane.showMessageDialog(this,
                        "La función de prolongación de ayuda estará disponible en la próxima versión.",
                        "Próximamente", JOptionPane.INFORMATION_MESSAGE));
        btnProlong.setVisible(!esNuevo);

        derecha.add(btnGuardar);
        derecha.add(btnProlong);

        p.add(btnCancelar, BorderLayout.WEST);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CARGA DE DATOS
    // ═════════════════════════════════════════════════════════════════════════
    private void cargarDatos() {
        // Poblar parroquias via controller
        for (Parroquia par : parroquiaController.findParroquiasDisponibles())
            cmbParroquia.addItem(par);

        if (esNuevo) {
            cmbEstado.setSelectedItem(EstadoExpediente.EN_PROCESO);
            cmbColorMarcador.setSelectedIndex(0);
            txtFechaInicio.setValue(null);
            try {
                txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            } catch (Exception ignored) {
            }
            return;
        }

        // Modo editar: poblar campos de Persona
        Persona p = expediente.getTitular();
        if (p != null) {
            txtNombres.setText(nvl(p.getNombres()));
            txtApellidos.setText(nvl(p.getApellidos()));
            if (p.getTipoDocumento() != null)
                cmbTipoDoc.setSelectedItem(p.getTipoDocumento());
            txtNumeroDoc.setText(nvl(p.getNumeroDocumento()));
            if (p.getFechaNacimiento() != null)
                txtFechaNac.setText(sdf.format(p.getFechaNacimiento()));
            if (p.getSexo() != null)
                cmbSexo.setSelectedItem(p.getSexo());
            if (p.getEstadoCivil() != null)
                cmbEstadoCivil.setSelectedItem(p.getEstadoCivil());
            txtTelefono.setText(nvl(p.getTelefono()));
            txtDireccion.setText(nvl(p.getDireccion()));
            txtNacionalidad.setText(nvl(p.getNacionalidad()));
            txtPaisOrigen.setText(nvl(p.getPaisOrigen()));
            txtProfesion.setText(nvl(p.getProfesionOficio()));
            txtNivelEducacion.setText(nvl(p.getNivelEducacion()));
            txtCondicionSalud.setText(nvl(p.getCondicionSalud()));
            if (Boolean.TRUE.equals(p.getTieneSeguro()))
                chkTieneSeguro.setSelected(true);
            txtCondicionMigratoria.setText(nvl(p.getCondicionMigratoria()));
        }

        // Campos de Expediente
        txtEntrevistador.setText(nvl(expediente.getEntrevistador()));
        if (expediente.getEstado() != null)
            cmbEstado.setSelectedItem(expediente.getEstado());
        if (expediente.getFechaInicio() != null)
            txtFechaInicio.setText(sdf.format(expediente.getFechaInicio()));
        if (expediente.getFechaPrevistaConclusion() != null)
            txtFechaPrevista.setText(sdf.format(expediente.getFechaPrevistaConclusion()));
        txtObservaciones.setText(nvl(expediente.getObservaciones()));

        String marcador = expediente.getColorMarcador();
        if (marcador != null && !marcador.isEmpty()) {
            cmbColorMarcador.setSelectedItem(marcador);
            lblMarcadorBadge.setVisible("URGENTE".equals(marcador));
        }

        if (expediente.getParroquia() != null) {
            for (int i = 0; i < cmbParroquia.getItemCount(); i++) {
                if (cmbParroquia.getItemAt(i).getId().equals(expediente.getParroquia().getId())) {
                    cmbParroquia.setSelectedIndex(i);
                    break;
                }
            }
        }

        cargarVivienda();
        cargarAdendum();
        cargarTablaFamilia();
        cargarTablaDocs();
        cargarTablaAsistencia();
        cargarTablaEntrevistas();
    }

    private void cargarVivienda() {
        if (expediente == null || expediente.getId() == null)
            return;
        viviendaActual = expedienteController.findViviendaByExpediente(expediente.getId());
        if (viviendaActual != null) {
            txtDirVivienda.setText(nvl(viviendaActual.getDireccion()));
            if (viviendaActual.getTipo() != null)
                cmbTipoVivienda.setSelectedItem(viviendaActual.getTipo());
            if (viviendaActual.getTenencia() != null)
                cmbTenencia.setSelectedItem(viviendaActual.getTenencia());
            if (viviendaActual.getCondicion() != null)
                cmbCondicion.setSelectedItem(viviendaActual.getCondicion());
        }
    }

    private void cargarAdendum() {
        if (expediente == null || expediente.getId() == null)
            return;
        adendumActual = expedienteController.findAdendumByExpediente(expediente.getId());
        if (adendumActual != null) {
            txtAdendumObs.setText(nvl(adendumActual.getObservaciones()));
            recargarTablaGastos();
        }
    }

    private void cargarTablaFamilia() {
        if (expediente == null || expediente.getId() == null)
            return;
        modeloFamilia.setRowCount(0);
        for (MiembroFamiliar m : expedienteController.findMiembrosByExpediente(expediente.getId())) {
            String nombre = "—";
            try {
                if (m.getPersona() != null)
                    nombre = nvl(m.getPersona().getNombres()) + " " + nvl(m.getPersona().getApellidos());
            } catch (Exception ignored) {
                /* LazyInitializationException en entidad detached */ }
            modeloFamilia.addRow(new Object[] {
                    nombre,
                    nvl(m.getRelacionTitular()),
                    nvl(m.getOcupacion()),
                    Boolean.TRUE.equals(m.getTrabaja()) ? "Sí" : "No",
                    m.getIngresoMensual() != null ? m.getIngresoMensual().toPlainString() : "0.00",
                    Boolean.TRUE.equals(m.getEsJefatura()) ? "Sí" : "No"
            });
        }
    }

    private void cargarTablaDocs() {
        if (expediente == null || expediente.getId() == null)
            return;
        modeloDocs.setRowCount(0);
        for (DocumentoAdjunto d : expedienteController.findDocsByExpediente(expediente.getId())) {
            modeloDocs.addRow(new Object[] {
                    d.getTipo() != null ? d.getTipo().name() : "—",
                    nvl(d.getDescripcion()),
                    d.getFechaSubida() != null ? sdf.format(d.getFechaSubida()) : "—",
                    Boolean.TRUE.equals(d.getEsDocFirmado()) ? "Sí" : "No"
            });
        }
    }

    private void cargarTablaAsistencia() {
        if (expediente == null || expediente.getId() == null)
            return;
        modeloAsistencia.setRowCount(0);
        for (AsistenciaSolicitada a : expedienteController.findAsistenciasByExpediente(expediente.getId())) {
            modeloAsistencia.addRow(new Object[] {
                    a.getTipoAsistencia() != null ? a.getTipoAsistencia().name().replace("_", " ") : "—",
                    nvl(a.getModalidad()),
                    nvl(a.getFrecuencia()),
                    nvl(a.getDuracion()),
                    a.getValor() != null ? a.getValor().toPlainString() : "—"
            });
        }
    }

    private void cargarTablaEntrevistas() {
        if (expediente == null || expediente.getId() == null)
            return;
        modeloEntrevistas.setRowCount(0);
        for (Entrevista e : expedienteController.findEntrevistasByExpediente(expediente.getId())) {
            String obs = nvl(e.getObservaciones());
            if (obs.length() > 60)
                obs = obs.substring(0, 57) + "...";
            modeloEntrevistas.addRow(new Object[] {
                    e.getFecha() != null ? sdf.format(e.getFecha()) : "—",
                    nvl(e.getEntrevistador()),
                    Boolean.TRUE.equals(e.getRecomiendaAyuda()) ? "Sí" : "No",
                    obs
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // GUARDAR
    // ═════════════════════════════════════════════════════════════════════════
    private void guardar() {
        // 1. Validación básica
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombres y apellidos son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtNumeroDoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El número de documento es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbParroquia.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay parroquias disponibles. Registre una parroquia primero.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Buscar o crear Persona
            String numDoc = txtNumeroDoc.getText().trim();
            Persona titular = expedienteController.findPersonaByNumeroDocumento(numDoc);
            if (titular == null)
                titular = new Persona();

            titular.setNombres(txtNombres.getText().trim());
            titular.setApellidos(txtApellidos.getText().trim());
            titular.setTipoDocumento((TipoDocumentoPersona) cmbTipoDoc.getSelectedItem());
            titular.setNumeroDocumento(numDoc);
            titular.setSexo((Sexo) cmbSexo.getSelectedItem());
            titular.setEstadoCivil((EstadoCivil) cmbEstadoCivil.getSelectedItem());
            titular.setTelefono(txtTelefono.getText().trim());
            titular.setDireccion(txtDireccion.getText().trim());
            titular.setNacionalidad(txtNacionalidad.getText().trim());
            titular.setPaisOrigen(txtPaisOrigen.getText().trim());
            titular.setProfesionOficio(txtProfesion.getText().trim());
            titular.setNivelEducacion(txtNivelEducacion.getText().trim());
            titular.setCondicionSalud(txtCondicionSalud.getText().trim());
            titular.setTieneSeguro(chkTieneSeguro.isSelected());
            titular.setCondicionMigratoria(txtCondicionMigratoria.getText().trim());
            titular.setFechaNacimiento(parseFecha(txtFechaNac.getText()));
            expedienteController.guardarPersona(titular);

            // 3. Crear o actualizar Expediente
            boolean wasNuevo = esNuevo || (expediente == null);
            if (wasNuevo) {
                expediente = new Expediente();
                expediente.setNumeroFicha(expedienteController.generarNumeroFicha(numDoc));
                expediente.setEtapaActual(EtapaExpediente.REGISTRO);
            }

            expediente.setTitular(titular);
            expediente.setParroquia((Parroquia) cmbParroquia.getSelectedItem());
            expediente.setEstado((EstadoExpediente) cmbEstado.getSelectedItem());
            expediente.setEntrevistador(txtEntrevistador.getText().trim());
            expediente.setFechaInicio(parseFecha(txtFechaInicio.getText()));
            expediente.setFechaPrevistaConclusion(parseFecha(txtFechaPrevista.getText()));
            expediente.setObservaciones(txtObservaciones.getText().trim());
            String marcador = (String) cmbColorMarcador.getSelectedItem();
            expediente.setColorMarcador("NINGUNO".equals(marcador) ? null : marcador);
            expedienteController.guardarExpediente(expediente);

            // 4. Guardar Vivienda si hay datos
            if (expediente.getId() != null) {
                String dirViv = txtDirVivienda.getText().trim();
                if (!dirViv.isEmpty() || cmbTipoVivienda.getSelectedIndex() > 0) {
                    if (viviendaActual == null) {
                        viviendaActual = new Vivienda();
                        viviendaActual.setExpediente(expediente);
                    }
                    viviendaActual.setDireccion(dirViv);
                    viviendaActual.setTipo((TipoVivienda) cmbTipoVivienda.getSelectedItem());
                    viviendaActual.setTenencia((TenenciaVivienda) cmbTenencia.getSelectedItem());
                    viviendaActual.setCondicion((CondicionVivienda) cmbCondicion.getSelectedItem());
                    expedienteController.guardarVivienda(viviendaActual);
                }
            }

            // 5. Guardar Adendum si hay observaciones
            if (expediente.getId() != null) {
                String obsAd = txtAdendumObs.getText().trim();
                if (!obsAd.isEmpty()) {
                    if (adendumActual == null) {
                        adendumActual = new Adendum();
                        adendumActual.setExpediente(expediente);
                    }
                    adendumActual.setObservaciones(obsAd);
                    expedienteController.guardarAdendum(adendumActual);
                }
            }

            // 6. Post-guardado
            actualizarTitulo();
            aplicarModoAcceso();
            if (onGuardado != null)
                onGuardado.run();

            JOptionPane.showMessageDialog(this, "Expediente guardado correctamente.",
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el expediente:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════════════════
    private void actualizarTitulo() {
        if (expediente == null)
            return;
        String titulo = "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular();
        setTitle("Expediente " + titulo);
        if (lblTituloPrincipal != null)
            lblTituloPrincipal.setText(titulo);
    }

    private void aplicarModoAcceso() {
        if (tabbedPane == null)
            return;
        boolean tieneId = (expediente != null && expediente.getId() != null);
        String tooltip = tieneId ? null : "Disponible después de guardar el expediente";
        for (int i = 1; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setEnabledAt(i, tieneId);
            tabbedPane.setToolTipTextAt(i, tooltip);
        }
    }

    private String nombreTitular() {
        if (expediente == null || expediente.getTitular() == null)
            return "";
        return nvl(expediente.getTitular().getNombres()) + " " + nvl(expediente.getTitular().getApellidos());
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }

    private Date parseFecha(String texto) {
        if (texto == null || texto.trim().isEmpty() || texto.contains("_"))
            return null;
        try {
            return sdf.parse(texto.trim());
        } catch (ParseException e) {
            return null;
        }
    }
}
