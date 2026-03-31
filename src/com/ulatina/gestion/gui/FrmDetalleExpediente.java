package com.ulatina.gestion.gui;

import com.ulatina.gestion.dao.impl.*;
import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
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
 * Usa los DAOs existentes del proyecto Pastoral-Social-ONG.
 * Modo nuevo: expediente == null. Modo editar: expediente != null.
 */
public class FrmDetalleExpediente extends JDialog {

    // ─── Colores (igual que FrmExpedientesPanel) ──────────────────────────────
    private static final Color COLOR_FONDO = new Color(245, 246, 250);
    private static final Color COLOR_PANEL = Color.WHITE;
    private static final Color COLOR_PRIMARIO = new Color(34, 197, 94);
    private static final Color COLOR_PRIMARIO_H = new Color(22, 163, 74);
    private static final Color COLOR_GRIS_BTN = new Color(229, 231, 235);
    private static final Color COLOR_GRIS_BTN_H = new Color(209, 213, 219);
    private static final Color COLOR_BORDE = new Color(209, 213, 219);
    private static final Color COLOR_TEXTO = new Color(17, 24, 39);
    private static final Color COLOR_TEXTO_GRIS = new Color(107, 114, 128);
    private static final Color COLOR_AZUL = new Color(59, 130, 246);
    private static final Color COLOR_PURPURA = new Color(109, 40, 217);
    private static final Color COLOR_PURPURA_H = new Color(91, 33, 182);
    private static final Color COLOR_AMARILLO_BG = new Color(254, 243, 199);
    private static final Color COLOR_ROJO = new Color(220, 38, 38);

    // ─── Estado ───────────────────────────────────────────────────────────────
    private Expediente expediente;
    private final boolean esNuevo;
    private final Runnable onGuardado;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // ─── DAOs ─────────────────────────────────────────────────────────────────
    private final ExpedienteDAOImpl expedienteDAO = new ExpedienteDAOImpl();
    private final PersonaDAOImpl personaDAO = new PersonaDAOImpl();
    private final ViviendaDAOImpl viviendaDAO = new ViviendaDAOImpl();
    private final AdendumDAOImpl adendumDAO = new AdendumDAOImpl();
    private final GastoMensualDAOImpl gastoDAO = new GastoMensualDAOImpl();
    private final MiembroFamiliarDAOImpl miembroDAO = new MiembroFamiliarDAOImpl();
    private final DocumentoAdjuntoDAOImpl documentoDAO = new DocumentoAdjuntoDAOImpl();
    private final AsistenciaSolicitadaDAOImpl asistenciaDAO = new AsistenciaSolicitadaDAOImpl();
    private final EntrevistaDAOImpl entrevistaDAO = new EntrevistaDAOImpl();
    private final ParroquiaDAOImpl parroquiaDAO = new ParroquiaDAOImpl();

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
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        tabbedPane = crearTabbedPane();

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    // ─── Panel título ─────────────────────────────────────────────────────────
    private JPanel crearPanelTitulo() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(16, 24, 12, 24));

        lblTituloPrincipal = new JLabel(esNuevo ? "Nuevo Expediente"
                : "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular());
        lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloPrincipal.setForeground(COLOR_TEXTO);

        lblMarcadorBadge = new JLabel("  URGENTE  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ROJO);
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
        tp.setBackground(COLOR_FONDO);

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
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 4, 2, 4);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(2, 4, 5, 12);

        GridBagConstraints wc = new GridBagConstraints(); // full-width
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
        secTitular.setForeground(COLOR_TEXTO_GRIS);
        secTitular.setBorder(new EmptyBorder(0, 0, 4, 0));
        p.add(secTitular, lc);
        lc.gridwidth = 1;
        row++;

        // Nombres | Apellidos
        txtNombres = new JTextField(15);
        txtApellidos = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Nombres *", txtNombres, "Apellidos *", txtApellidos);

        // Tipo Doc + Número Doc | Teléfono
        cmbTipoDoc = new JComboBox<>(TipoDocumentoPersona.values());
        txtNumeroDoc = new JTextField(12);
        txtTelefono = new JTextField(12);
        JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        docPanel.setOpaque(false);
        docPanel.add(cmbTipoDoc);
        docPanel.add(txtNumeroDoc);
        agregarFila(p, row++, lc, fc, "Documento *", docPanel, "Teléfono", txtTelefono);

        // Fecha Nacimiento | Sexo
        txtFechaNac = crearCampoFecha();
        cmbSexo = new JComboBox<>(Sexo.values());
        agregarFila(p, row++, lc, fc, "Fecha Nacimiento", txtFechaNac, "Sexo", cmbSexo);

        // Estado Civil | Profesión/Oficio
        cmbEstadoCivil = new JComboBox<>(EstadoCivil.values());
        txtProfesion = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Estado Civil", cmbEstadoCivil, "Profesión/Oficio", txtProfesion);

        // Nivel Educación | País Origen
        txtNivelEducacion = new JTextField(15);
        txtPaisOrigen = new JTextField(15);
        agregarFila(p, row++, lc, fc, "Nivel Educación", txtNivelEducacion, "País Origen", txtPaisOrigen);

        // Nacionalidad | Cond. Migratoria
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

        // Condición de Salud (con borde amarillo)
        txtCondicionSalud = new JTextArea(2, 20);
        txtCondicionSalud.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCondicionSalud.setLineWrap(true);
        txtCondicionSalud.setWrapStyleWord(true);
        JScrollPane scrollSalud = new JScrollPane(txtCondicionSalud);
        scrollSalud.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_AMARILLO_BG, 2, true),
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
        sep.setForeground(COLOR_BORDE);
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
        secExp.setForeground(COLOR_TEXTO_GRIS);
        secExp.setBorder(new EmptyBorder(0, 0, 4, 0));
        p.add(secExp, lc);
        lc.gridwidth = 1;
        row++;

        // Entrevistador | Estado
        txtEntrevistador = new JTextField(15);
        cmbEstado = new JComboBox<>(EstadoExpediente.values());
        agregarFila(p, row++, lc, fc, "Entrevistador", txtEntrevistador, "Estado", cmbEstado);

        // Parroquia | Color Marcador
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

        // Fecha Inicio | Fecha Prevista
        txtFechaInicio = crearCampoFecha();
        txtFechaPrevista = crearCampoFecha();
        agregarFila(p, row++, lc, fc, "Fecha Inicio *", txtFechaInicio, "Fecha Prevista Concl.", txtFechaPrevista);

        // Observaciones (full width)
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBorder(new LineBorder(COLOR_BORDE, 1, true));
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
        scroll.getViewport().setBackground(COLOR_PANEL);
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
        l.setForeground(COLOR_TEXTO_GRIS);
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
        p.setBackground(COLOR_PANEL);
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

        // Dirección (full width)
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
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lObs = new JLabel("Observaciones generales");
        lObs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lObs.setForeground(COLOR_TEXTO);

        txtAdendumObs = new JTextArea(4, 20);
        txtAdendumObs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAdendumObs.setLineWrap(true);
        txtAdendumObs.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtAdendumObs);
        scrollObs.setBorder(new LineBorder(COLOR_BORDE, 1, true));
        scrollObs.setPreferredSize(new Dimension(0, 100));

        JPanel obsPanel = new JPanel(new BorderLayout(0, 6));
        obsPanel.setOpaque(false);
        obsPanel.add(lObs, BorderLayout.NORTH);
        obsPanel.add(scrollObs, BorderLayout.CENTER);

        JLabel lGastos = new JLabel("Gastos Mensuales");
        lGastos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lGastos.setForeground(COLOR_TEXTO);
        lGastos.setBorder(new EmptyBorder(8, 0, 4, 0));

        modeloGastos = new DefaultTableModel(
                new String[] { "Categoría", "Concepto", "Monto", "Fecha" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaGastos = new JTable(modeloGastos);
        tablaGastos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaGastos.setRowHeight(32);
        tablaGastos.setShowVerticalLines(false);
        tablaGastos.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaGastos.getTableHeader().setBackground(new Color(249, 250, 251));
        tablaGastos.getTableHeader().setForeground(COLOR_TEXTO_GRIS);
        tablaGastos.setFocusable(false);
        JScrollPane scrollGastos = new JScrollPane(tablaGastos);
        scrollGastos.setBorder(new LineBorder(COLOR_BORDE, 1, true));

        JPanel gastosPanel = new JPanel(new BorderLayout(0, 4));
        gastosPanel.setOpaque(false);
        gastosPanel.add(lGastos, BorderLayout.NORTH);
        gastosPanel.add(scrollGastos, BorderLayout.CENTER);

        p.add(obsPanel, BorderLayout.NORTH);
        p.add(gastosPanel, BorderLayout.CENTER);
        return p;
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
        p.setBackground(COLOR_PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lbl = new JLabel(descripcion);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXTO_GRIS);

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(243, 244, 246));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(new Color(249, 250, 251));
        tabla.getTableHeader().setForeground(COLOR_TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(COLOR_BORDE, 1, true));
        scroll.getViewport().setBackground(COLOR_PANEL);

        p.add(lbl, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── Barra inferior ───────────────────────────────────────────────────────
    private JPanel crearBarraInferior() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(12, 24, 12, 24)));

        JButton btnCancelar = crearBoton("Cancelar", COLOR_GRIS_BTN, COLOR_TEXTO, e -> dispose());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        JButton btnGuardar = crearBoton("Guardar", COLOR_PRIMARIO, Color.WHITE, e -> guardar());

        JButton btnProlong = crearBoton("+ Prolongar ayuda", COLOR_PURPURA, Color.WHITE,
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
        // Poblar parroquias — intenta activas primero, si no hay usa findAll
        try {
            List<Parroquia> parroquias = parroquiaDAO.findActivas();
            if (parroquias == null || parroquias.isEmpty()) {
                parroquias = parroquiaDAO.findAll();
            }
            for (Parroquia par : parroquias)
                cmbParroquia.addItem(par);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (esNuevo) {
            cmbEstado.setSelectedItem(EstadoExpediente.EN_PROCESO);
            cmbColorMarcador.setSelectedIndex(0);
            txtFechaInicio.setValue(null);
            try {
                txtFechaInicio.setText(sdf.format(new Date()));
            } catch (Exception ignored) {
            }
            return;
        }

        // ── Modo editar: poblar campos de Persona ──
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

        // ── Campos de Expediente ──
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

        // Seleccionar parroquia en combo
        if (expediente.getParroquia() != null) {
            for (int i = 0; i < cmbParroquia.getItemCount(); i++) {
                if (cmbParroquia.getItemAt(i).getId().equals(expediente.getParroquia().getId())) {
                    cmbParroquia.setSelectedIndex(i);
                    break;
                }
            }
        }

        // ── Cargar subtabs ──
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
        try {
            viviendaActual = viviendaDAO.findByExpediente(expediente.getId());
            if (viviendaActual != null) {
                txtDirVivienda.setText(nvl(viviendaActual.getDireccion()));
                if (viviendaActual.getTipo() != null)
                    cmbTipoVivienda.setSelectedItem(viviendaActual.getTipo());
                if (viviendaActual.getTenencia() != null)
                    cmbTenencia.setSelectedItem(viviendaActual.getTenencia());
                if (viviendaActual.getCondicion() != null)
                    cmbCondicion.setSelectedItem(viviendaActual.getCondicion());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarAdendum() {
        if (expediente == null || expediente.getId() == null)
            return;
        try {
            adendumActual = adendumDAO.findByExpediente(expediente.getId());
            if (adendumActual != null) {
                txtAdendumObs.setText(nvl(adendumActual.getObservaciones()));
                // NO acceder adendumActual.getGastosMensuales() → lazy en entidad detached
                if (adendumActual.getId() != null) {
                    List<GastoMensual> gastos = gastoDAO.findByAdendum(adendumActual.getId());
                    modeloGastos.setRowCount(0);
                    for (GastoMensual g : gastos) {
                        modeloGastos.addRow(new Object[] {
                                g.getCategoria() != null ? g.getCategoria().name() : "—",
                                nvl(g.getConcepto()),
                                g.getMonto() != null ? g.getMonto().toPlainString() : "0.00",
                                g.getFecha() != null ? sdf.format(g.getFecha()) : "—"
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTablaFamilia() {
        if (expediente == null || expediente.getId() == null)
            return;
        try {
            modeloFamilia.setRowCount(0);
            List<MiembroFamiliar> miembros = miembroDAO.findByExpediente(expediente.getId());
            for (MiembroFamiliar m : miembros) {
                String nombre = "—";
                try {
                    if (m.getPersona() != null) {
                        nombre = nvl(m.getPersona().getNombres()) + " " + nvl(m.getPersona().getApellidos());
                    }
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTablaDocs() {
        if (expediente == null || expediente.getId() == null)
            return;
        try {
            modeloDocs.setRowCount(0);
            List<DocumentoAdjunto> docs = documentoDAO.findByExpediente(expediente.getId());
            for (DocumentoAdjunto d : docs) {
                // NO acceder d.getSubidoPor() → lazy en entidad detached
                modeloDocs.addRow(new Object[] {
                        d.getTipo() != null ? d.getTipo().name() : "—",
                        nvl(d.getDescripcion()),
                        d.getFechaSubida() != null ? sdf.format(d.getFechaSubida()) : "—",
                        Boolean.TRUE.equals(d.getEsDocFirmado()) ? "Sí" : "No"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTablaAsistencia() {
        if (expediente == null || expediente.getId() == null)
            return;
        try {
            modeloAsistencia.setRowCount(0);
            List<AsistenciaSolicitada> lista = asistenciaDAO.findByExpediente(expediente.getId());
            for (AsistenciaSolicitada a : lista) {
                modeloAsistencia.addRow(new Object[] {
                        a.getTipoAsistencia() != null ? a.getTipoAsistencia().name().replace("_", " ") : "—",
                        nvl(a.getModalidad()),
                        nvl(a.getFrecuencia()),
                        nvl(a.getDuracion()),
                        a.getValor() != null ? a.getValor().toPlainString() : "—"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarTablaEntrevistas() {
        if (expediente == null || expediente.getId() == null)
            return;
        try {
            modeloEntrevistas.setRowCount(0);
            List<Entrevista> lista = entrevistaDAO.findByExpediente(expediente.getId());
            for (Entrevista e : lista) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
            Persona titular = null;
            try {
                titular = personaDAO.findByNumeroDocumento(numDoc);
            } catch (Exception ignored) {
            }

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

            if (titular.getId() == null) {
                personaDAO.save(titular);
            } else {
                personaDAO.update(titular);
            }

            // 3. Crear o actualizar Expediente
            boolean wasNuevo = esNuevo || (expediente == null);
            if (wasNuevo) {
                expediente = new Expediente();
                expediente.setNumeroFicha(generarNumeroFicha());
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

            if (wasNuevo) {
                expedienteDAO.save(expediente);
            } else {
                expedienteDAO.update(expediente);
            }

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
                    if (viviendaActual.getId() == null) {
                        viviendaDAO.save(viviendaActual);
                    } else {
                        viviendaDAO.update(viviendaActual);
                    }
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
                    if (adendumActual.getId() == null) {
                        adendumDAO.save(adendumActual);
                    } else {
                        adendumDAO.update(adendumActual);
                    }
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
    // HELPERS UI
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

    private JFormattedTextField crearCampoFecha() {
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

    private Date parseFecha(String texto) {
        if (texto == null || texto.trim().isEmpty() || texto.contains("_"))
            return null;
        try {
            return sdf.parse(texto.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    private String generarNumeroFicha() {
        String fecha = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        int sufijo = (int) (Math.random() * 9000) + 1000;
        return "EXP-" + fecha + "-" + sufijo;
    }

    private JButton crearBoton(String texto, Color bg, Color fg, ActionListener accion) {
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
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(accion);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg == COLOR_PRIMARIO ? COLOR_PRIMARIO_H
                        : bg == COLOR_GRIS_BTN ? COLOR_GRIS_BTN_H
                                : bg == COLOR_PURPURA ? COLOR_PURPURA_H
                                        : bg.darker());
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
}
