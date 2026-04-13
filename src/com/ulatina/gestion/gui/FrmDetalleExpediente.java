package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.*;
import com.ulatina.gestion.util.FileStorageUtil;
import com.ulatina.gestion.util.SessionContext;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Diálogo modal para crear o editar un expediente.
 * En modo nuevo (expediente == null) las pestañas 1 a 7 están bloqueadas hasta
 * que el registro se guarda por primera vez.
 * En modo edición precarga todos los datos asociados al expediente recibido.
 */
public class FrmDetalleExpediente extends JDialog {

    private Expediente expediente;

    /** true cuando el diálogo se abrió sin expediente existente.
     *  Afecta el título, las validaciones de guardar() y el estado inicial de las pestañas. */
    private final boolean esNuevo;

    /** Callback ejecutado después de cada guardado exitoso.
     *  Normalmente es FrmExpedientes::cargarTabla para refrescar la lista padre. */
    private final Runnable onGuardado;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private final ExpedienteController expedienteController = new ExpedienteController();
    private final ParroquiaController parroquiaController = new ParroquiaController();

    private JLabel lblTituloPrincipal;
    private JLabel lblMarcadorBadge;

    private JTabbedPane tabbedPane;

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
    private JTextField txtEntrevistador;
    private JComboBox<EstadoExpediente> cmbEstado;
    private JComboBox<Parroquia> cmbParroquia;
    private JFormattedTextField txtFechaInicio;
    private JFormattedTextField txtFechaPrevista;
    private JTextArea txtObservaciones;
    private JComboBox<String> cmbColorMarcador;

    private JTextField txtDirVivienda;
    private JComboBox<TipoVivienda> cmbTipoVivienda;
    private JComboBox<TenenciaVivienda> cmbTenencia;
    private JComboBox<CondicionVivienda> cmbCondicion;

    /** Vivienda vinculada al expediente. Null si aún no se ha registrado. */
    private Vivienda viviendaActual;

    private JTextArea txtAdendumObs;
    private DefaultTableModel modeloGastos;
    private JTable tablaGastos;
    private JLabel lblTotalGastos;
    private JComboBox<CategoriaGasto> cmbCatGasto;
    private JTextField txtConceptoGasto;
    private JTextField txtMontoGasto;
    private JFormattedTextField txtFechaGasto;

    /** Gasto mensual que se está editando en este momento.
     *  Null cuando el formulario de gastos está en modo agregar. */
    private GastoMensual gastoEnEdicion = null;
    private final java.util.List<GastoMensual> gastosActuales = new java.util.ArrayList<>();

    /** Adendum vinculado al expediente; contiene los gastos mensuales.
     *  Puede ser null hasta el primer guardado. */
    private Adendum adendumActual;

    private DefaultTableModel modeloFamilia;
    private DefaultTableModel modeloEntrevistas;

    private JFormattedTextField txtFechaEntrevista;
    private JTextField txtEntrevistadorEntrev;
    private JTextArea txtObsEntrevista;
    private JCheckBox chkRecomiendaAyuda;

    private DefaultTableModel modeloProlongaciones;
    private JFormattedTextField txtFechaProlongacion;
    private JTextField txtObsProlongacion;
    private BarraProgresoPanel panelBarraProgreso;

    /**
     * Paginadores en memoria para cada lista del formulario.
     * Cada paginador guarda la lista completa y un índice de página; devuelve 20 ítems
     * por página. Deben recargarse con pag.cargar(lista) antes de redibujar el panel.
     */
    private final Paginador<MiembroFamiliar>      pagFamilia       = new Paginador<>();
    private final Paginador<GastoMensual>         pagGastos        = new Paginador<>();
    private final Paginador<DocumentoAdjunto>     pagDocs          = new Paginador<>();
    private final Paginador<AsistenciaSolicitada> pagAsistencias   = new Paginador<>();
    private final Paginador<Entrevista>           pagEntrevistas   = new Paginador<>();
    private final Paginador<ProlongacionAyuda>    pagProlongaciones = new Paginador<>();
    private final JPanel panelPagFamilia       = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel panelPagGastos        = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel panelPagDocs          = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel panelPagAsistencias   = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel panelPagEntrevistas   = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel panelPagProlongaciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));

    private JPanel panelListaAsistencias;
    private JComboBox<TipoAsistencia> cmbTipoAsistencia;
    private JTextField txtModalidadAsist;
    private JTextField txtFrecuenciaAsist;
    private JTextField txtDuracionAsist;
    private JTextField txtValorAsist;

    /** Asistencia que se está editando. Null en modo agregar. */
    private AsistenciaSolicitada asistenciaEnEdicion = null;
    private JButton btnConfirmarAsist;

    private File archivoSeleccionado;
    private JLabel lblArchivoNombre;
    private JComboBox<TipoDocumentoAdjunto> cmbTipoDocAdjunto;
    private JTextField txtDescripcionDoc;
    private JCheckBox chkEsFirmado;

    /** Panel de datos del firmante; solo visible cuando chkEsFirmado está marcado. */
    private JPanel panelFirmante;
    private JTextField txtNombreFirmante;
    private JFormattedTextField txtFechaFirmaDoc;
    private JPanel panelListaDocs;

    private JPanel panelFormFamilia;
    private JLabel lblHeaderFormFamilia;
    private JLabel lblPersonaSeleccionada;
    private JTextField txtBuscarCedula;
    private JComboBox<String> cmbRelacion;
    private JButton btnJefatura;
    private JTextField txtOcupacion;
    private JButton btnTrabaja;
    private JTextField txtIngreso;

    /** Miembro familiar en edición. Null en modo agregar. */
    private MiembroFamiliar miembroEnEdicion;

    /** Persona encontrada por cédula; se asigna al miembro en confirmarMiembro().
     *  Null si la persona aún no existe en el sistema. */
    private Persona personaSeleccionada;

    /** Lista local de miembros cuando el expediente aún no está persistido.
     *  En modo edición los miembros se leen directo de la BD. */
    private final java.util.List<MiembroFamiliar> listaMiembros = new java.util.ArrayList<>();
    private JTable tablaFamilia;

    /**
     * @param owner      ventana propietaria del diálogo.
     * @param expediente expediente a editar, o null para crear uno nuevo.
     * @param onGuardado callback ejecutado tras cada guardado exitoso.
     */
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

    private void initComponents() {
        setTitle(esNuevo ? "Nuevo Expediente" : "Expediente #" + expediente.getNumeroFicha());
        getContentPane().setBackground(AppColors.FONDO);
        setLayout(new BorderLayout());

        tabbedPane = crearTabbedPane();
        actualizarEtapaActual();

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel p = new JPanel(new BorderLayout(12, 6));
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
        lblMarcadorBadge.setForeground(AppColors.PANEL);
        lblMarcadorBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMarcadorBadge.setVisible(false);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(lblMarcadorBadge);

        EtapaExpediente etapaInicial = (expediente != null && expediente.getEtapaActual() != null)
                ? expediente.getEtapaActual()
                : EtapaExpediente.REGISTRO;
        panelBarraProgreso = new BarraProgresoPanel(etapaInicial.ordinal());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(lblTituloPrincipal, BorderLayout.WEST);
        norte.add(derecha, BorderLayout.EAST);

        p.add(norte, BorderLayout.NORTH);
        if (!esNuevo)
            p.add(panelBarraProgreso, BorderLayout.SOUTH);
        return p;
    }

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
        tp.addTab("Prolongaciones", crearTabProlongaciones());
        return tp;
    }

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

        chkTieneSeguro = new JCheckBox("Tiene seguro de salud");
        chkTieneSeguro.setOpaque(false);
        chkTieneSeguro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lc.gridx = 0;
        lc.gridy = row;
        lc.gridwidth = 4;
        p.add(chkTieneSeguro, lc);
        lc.gridwidth = 1;
        row++;

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

    private JPanel crearTabFamilia() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setOpaque(false);
        barraTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitGrupo = new JLabel("Grupo Familiar");
        lblTitGrupo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitGrupo.setForeground(AppColors.TEXTO);

        JButton btnAgregarMiembro = UIFactory.crearBotonSmall("Agregar", AppColors.PRIMARIO, Color.WHITE,
                e -> mostrarFormFamilia(true));

        barraTop.add(lblTitGrupo, BorderLayout.WEST);
        barraTop.add(btnAgregarMiembro, BorderLayout.EAST);

        modeloFamilia = new DefaultTableModel(
                new String[] { "Nombre", "Cédula", "Relación", "Jefatura", "Ocupación", "Ingreso", "Acciones" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaFamilia = new JTable(modeloFamilia);
        configurarTabla(tablaFamilia, 38);
        tablaFamilia.setShowHorizontalLines(true);
        tablaFamilia.setSelectionBackground(AppColors.FILA_SEL);

        tablaFamilia.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaFamilia.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaFamilia.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaFamilia.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaFamilia.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaFamilia.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaFamilia.getColumnModel().getColumn(6).setPreferredWidth(90);
        tablaFamilia.getColumnModel().getColumn(6).setMaxWidth(100);
        tablaFamilia.getColumnModel().getColumn(6).setMinWidth(80);

        // Renderer badge Jefatura (col 3)
        tablaFamilia.getColumnModel().getColumn(3).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            boolean esJef = "Sí".equals(val);
            Color bg = esJef ? AppColors.BADGE_BG[0] : AppColors.BADGE_BG[3];
            Color fg = esJef ? AppColors.BADGE_FG[0] : AppColors.BADGE_FG[3];
            JLabel badge = new JLabel(String.valueOf(val), SwingConstants.CENTER);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setOpaque(true);
            badge.setBackground(bg);
            badge.setForeground(fg);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            cell.setBackground(sel ? tbl.getSelectionBackground() : AppColors.PANEL);
            cell.add(badge);
            return cell;
        });

        // Renderer Acciones (col 6)
        tablaFamilia.getColumnModel().getColumn(6).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            cell.setOpaque(true);
            cell.setBackground(sel ? tbl.getSelectionBackground() : AppColors.PANEL);
            JLabel editar = new JLabel("Editar");
            editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            editar.setForeground(AppColors.AZUL);
            JLabel sep = new JLabel("|");
            sep.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sep.setForeground(AppColors.TEXTO_GRIS);
            JLabel eliminar = new JLabel("X");
            eliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            eliminar.setForeground(AppColors.ROJO);
            cell.add(editar);
            cell.add(sep);
            cell.add(eliminar);
            return cell;
        });

        tablaFamilia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablaFamilia.columnAtPoint(e.getPoint());
                int row = tablaFamilia.rowAtPoint(e.getPoint());
                if (col != 6 || row < 0 || row >= listaMiembros.size())
                    return;
                Rectangle rect = tablaFamilia.getCellRect(row, col, false);
                int relX = e.getX() - rect.x;
                if (relX < 50)
                    cargarMiembroEnFormulario(row);
                else
                    eliminarMiembro(row);
            }
        });

        JScrollPane scrollTabla = crearScrollTabla(tablaFamilia);

        panelPagFamilia.setOpaque(false);
        JPanel tablaConPag = new JPanel(new BorderLayout(0, 0));
        tablaConPag.setOpaque(false);
        tablaConPag.add(scrollTabla, BorderLayout.CENTER);
        tablaConPag.add(panelPagFamilia, BorderLayout.SOUTH);

        panelFormFamilia = crearPanelFormFamilia();
        panelFormFamilia.setVisible(false);

        p.add(barraTop, BorderLayout.NORTH);
        p.add(tablaConPag, BorderLayout.CENTER);
        p.add(panelFormFamilia, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelFormFamilia() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 0));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppColors.AZUL);
        header.setBorder(new EmptyBorder(8, 14, 8, 14));
        lblHeaderFormFamilia = new JLabel("Agregar miembro familiar");
        lblHeaderFormFamilia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHeaderFormFamilia.setForeground(AppColors.PANEL);
        header.add(lblHeaderFormFamilia, BorderLayout.WEST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(AppColors.PANEL);
        body.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1),
                new EmptyBorder(12, 14, 12, 14)));

        txtBuscarCedula = new JTextField(14);
        txtBuscarCedula.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscarCedula.putClientProperty("JTextField.placeholderText", "Buscar por cédula...");
        txtBuscarCedula.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true), new EmptyBorder(4, 8, 4, 8)));
        txtBuscarCedula.setPreferredSize(new Dimension(160, 30));
        txtBuscarCedula.addActionListener(e -> buscarPersonaPorCedula());

        JButton btnBuscar = UIFactory.crearBotonSmall("Buscar", AppColors.AZUL, Color.WHITE,
                e -> buscarPersonaPorCedula());
        JButton btnCrearNueva = UIFactory.crearBotonSmall("Crear nueva", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> abrirFormNuevaPersona());

        lblPersonaSeleccionada = new JLabel("Ninguna persona seleccionada");
        lblPersonaSeleccionada.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblPersonaSeleccionada.setForeground(AppColors.TEXTO_GRIS);

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila1.setOpaque(false);
        fila1.add(campoConEtiqueta("Persona (cédula):", txtBuscarCedula));
        JPanel btnsBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        btnsBusqueda.setOpaque(false);
        btnsBusqueda.setBorder(new EmptyBorder(18, 0, 0, 0));
        btnsBusqueda.add(btnBuscar);
        btnsBusqueda.add(btnCrearNueva);
        fila1.add(btnsBusqueda);
        JPanel wrapLblPersona = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        wrapLblPersona.setOpaque(false);
        wrapLblPersona.setBorder(new EmptyBorder(14, 0, 0, 0));
        wrapLblPersona.add(lblPersonaSeleccionada);
        fila1.add(wrapLblPersona);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbRelacion = new JComboBox<>(new String[] {
                "Esposo/a", "Hijo/a", "Padre", "Madre", "Hermano/a", "Abuelo/a", "Tío/a", "Sobrino/a", "Otro"
        });
        cmbRelacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRelacion.setPreferredSize(new Dimension(130, 30));

        btnJefatura = crearBtnToggle("No");
        txtOcupacion = new JTextField(12);
        txtOcupacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtOcupacion.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true), new EmptyBorder(4, 8, 4, 8)));
        txtOcupacion.setPreferredSize(new Dimension(130, 30));

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        fila2.setOpaque(false);
        fila2.add(campoConEtiqueta("Relación con titular", cmbRelacion));
        fila2.add(campoConEtiqueta("Jefatura?", btnJefatura));
        fila2.add(campoConEtiqueta("Ocupación", txtOcupacion));
        fila2.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnTrabaja = crearBtnToggle("Sí");
        txtIngreso = new JTextField(10);
        txtIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtIngreso.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true), new EmptyBorder(4, 8, 4, 8)));
        txtIngreso.setPreferredSize(new Dimension(120, 30));
        txtIngreso.putClientProperty("JTextField.placeholderText", "0");

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        fila3.setOpaque(false);
        fila3.add(campoConEtiqueta("Trabaja?", btnTrabaja));
        fila3.add(campoConEtiqueta("Ingreso mensual (₡)", txtIngreso));
        fila3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnGuardarMiembro = UIFactory.crearBotonDialog("Guardar", AppColors.PRIMARIO, Color.WHITE,
                e -> confirmarMiembro());
        JButton btnCancelarMiembro = UIFactory.crearBotonDialog("Cancelar", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> mostrarFormFamilia(false));

        JPanel filaBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        filaBtns.setOpaque(false);
        filaBtns.add(btnGuardarMiembro);
        filaBtns.add(btnCancelarMiembro);
        filaBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(fila1);
        body.add(Box.createVerticalStrut(4));
        body.add(fila2);
        body.add(fila3);
        body.add(filaBtns);

        contenedor.add(header, BorderLayout.NORTH);
        contenedor.add(body, BorderLayout.CENTER);
        return contenedor;
    }

    /**
     * Crea un botón que alterna entre "Sí" (verde) y "No" (gris) al hacer clic.
     * @param estadoInicial "Sí" o "No".
     * @return el botón configurado.
     */
    private JButton crearBtnToggle(String estadoInicial) {
        boolean[] estado = { estadoInicial.equals("Sí") };
        JButton btn = UIFactory.crearBotonSmall(
                estado[0] ? "Sí" : "No",
                estado[0] ? AppColors.PRIMARIO : AppColors.GRIS_BTN,
                estado[0] ? AppColors.PANEL : AppColors.TEXTO);
        btn.setPreferredSize(new Dimension(52, 30));
        btn.addActionListener(e -> {
            estado[0] = !estado[0];
            btn.setText(estado[0] ? "Sí" : "No");
            btn.setBackground(estado[0] ? AppColors.PRIMARIO : AppColors.GRIS_BTN);
            btn.setForeground(estado[0] ? AppColors.PANEL : AppColors.TEXTO);
            btn.repaint();
        });
        return btn;
    }

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
        configurarTabla(tablaGastos);

        tablaGastos.getColumnModel().getColumn(4).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            cell.setOpaque(true);
            cell.setBackground(sel ? tbl.getSelectionBackground() : AppColors.PANEL);
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

        JScrollPane scrollGastos = crearScrollTabla(tablaGastos);

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

        panelPagGastos.setOpaque(false);
        JPanel gastosBottom = new JPanel(new BorderLayout(0, 0));
        gastosBottom.setOpaque(false);
        gastosBottom.add(totalPanel, BorderLayout.NORTH);
        gastosBottom.add(panelPagGastos, BorderLayout.SOUTH);

        JPanel gastosConTotal = new JPanel(new BorderLayout());
        gastosConTotal.setOpaque(false);
        gastosConTotal.add(scrollGastos, BorderLayout.CENTER);
        gastosConTotal.add(gastosBottom, BorderLayout.SOUTH);

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


    private void mostrarFormFamilia(boolean visible) {
        if (!visible) {
            miembroEnEdicion = null;
            personaSeleccionada = null;
            txtBuscarCedula.setText("");
            lblPersonaSeleccionada.setText("Ninguna persona seleccionada");
            lblPersonaSeleccionada.setForeground(AppColors.TEXTO_GRIS);
            cmbRelacion.setSelectedIndex(0);
            resetearToggle(btnJefatura, false);
            txtOcupacion.setText("");
            resetearToggle(btnTrabaja, true);
            txtIngreso.setText("");
            lblHeaderFormFamilia.setText("Agregar miembro familiar");
        }
        panelFormFamilia.setVisible(visible);
        panelFormFamilia.getParent().revalidate();
        panelFormFamilia.getParent().repaint();
    }

    private void resetearToggle(JButton btn, boolean estadoSi) {
        btn.setText(estadoSi ? "Sí" : "No");
        btn.setBackground(estadoSi ? AppColors.PRIMARIO : AppColors.GRIS_BTN);
        btn.setForeground(estadoSi ? AppColors.PANEL : AppColors.TEXTO);
        btn.repaint();
    }

    /**
     * Busca una Persona por el número de cédula ingresado.
     * Si la encuentra, rellena el formulario de familia con sus datos.
     * Si no existe, muestra la opción de crear una persona nueva.
     */
    private void buscarPersonaPorCedula() {
        String cedula = txtBuscarCedula.getText().trim();
        if (cedula.isEmpty())
            return;
        Persona p = expedienteController.findPersonaByNumeroDocumento(cedula);
        if (p != null) {
            personaSeleccionada = p;
            lblPersonaSeleccionada.setText(nvl(p.getNombres()) + " " + nvl(p.getApellidos()));
            lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
        } else {
            personaSeleccionada = null;
            lblPersonaSeleccionada.setText("Persona no encontrada.");
            lblPersonaSeleccionada.setForeground(AppColors.ROJO);
            JOptionPane.showMessageDialog(this,
                    "No se encontró persona con cédula \"" + cedula + "\".\nUse 'Crear nueva' para registrarla.",
                    "Persona no encontrada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void abrirFormNuevaPersona() {
        JDialog dlg = new JDialog(this, "Nueva Persona", true);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(AppColors.PANEL);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppColors.PANEL);
        form.setBorder(new EmptyBorder(16, 20, 8, 20));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 4, 2, 4);
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(2, 4, 4, 4);

        JTextField txtNom = new JTextField(14);
        JTextField txtApe = new JTextField(14);
        JComboBox<TipoDocumentoPersona> cmbTipo = new JComboBox<>(TipoDocumentoPersona.values());
        JTextField txtDoc = new JTextField(12);
        JTextField txtTel = new JTextField(12);

        agregarFila(form, 0, lc, fc, "Nombres *", txtNom, "Apellidos *", txtApe);
        agregarFila(form, 1, lc, fc, "Tipo Doc *", cmbTipo, "Número Doc *", txtDoc);
        lc.gridx = 0;
        lc.gridy = 2;
        form.add(etiqueta("Teléfono"), lc);
        fc.gridx = 1;
        fc.gridy = 2;
        form.add(txtTel, fc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        btns.setOpaque(false);
        btns.add(UIFactory.crearBotonDialog("Guardar", AppColors.PRIMARIO, Color.WHITE, e -> {
            String nom = txtNom.getText().trim();
            String ape = txtApe.getText().trim();
            String doc = txtDoc.getText().trim();
            if (nom.isEmpty() || ape.isEmpty() || doc.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nombres, Apellidos y Número de Documento son obligatorios.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (expedienteController.findPersonaByNumeroDocumento(doc) != null) {
                JOptionPane.showMessageDialog(dlg, "Ya existe una persona con ese número de documento.",
                        "Documento duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Persona nueva = new Persona();
            nueva.setNombres(nom);
            nueva.setApellidos(ape);
            nueva.setTipoDocumento((TipoDocumentoPersona) cmbTipo.getSelectedItem());
            nueva.setNumeroDocumento(doc);
            nueva.setTelefono(txtTel.getText().trim());
            expedienteController.guardarPersona(nueva);
            personaSeleccionada = nueva;
            txtBuscarCedula.setText(doc);
            lblPersonaSeleccionada.setText(nom + " " + ape);
            lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
            dlg.dispose();
        }));
        btns.add(UIFactory.crearBotonDialog("Cancelar", AppColors.GRIS_BTN, AppColors.TEXTO, e -> dlg.dispose()));

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(480, 200));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    /**
     * Agrega o actualiza un MiembroFamiliar.
     * En modo nuevo comprueba duplicados en listaMiembros (la BD aún no tiene el registro).
     * En modo edición persiste directamente y recarga la tabla.
     */
    private void confirmarMiembro() {
        if (expediente == null || expediente.getId() == null) {
            JOptionPane.showMessageDialog(this, "Guarde el expediente antes de agregar miembros familiares.",
                    "Expediente no guardado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (personaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione o cree una persona primero.",
                    "Persona requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verificar duplicado (solo en modo nuevo)
        if (miembroEnEdicion == null) {
            for (MiembroFamiliar m : listaMiembros) {
                if (m.getPersona() != null && m.getPersona().getId() != null
                        && m.getPersona().getId().equals(personaSeleccionada.getId())) {
                    JOptionPane.showMessageDialog(this, "Esta persona ya es miembro de este expediente.",
                            "Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        MiembroFamiliar miembro = (miembroEnEdicion != null) ? miembroEnEdicion : new MiembroFamiliar();
        miembro.setPersona(personaSeleccionada);
        miembro.setExpediente(expediente);
        miembro.setRelacionTitular((String) cmbRelacion.getSelectedItem());
        miembro.setEsJefatura("Sí".equals(btnJefatura.getText()));
        miembro.setOcupacion(txtOcupacion.getText().trim());
        miembro.setTrabaja("Sí".equals(btnTrabaja.getText()));
        String ingresoTxt = txtIngreso.getText().trim().replace(",", "");
        if (!ingresoTxt.isEmpty()) {
            try {
                miembro.setIngresoMensual(new BigDecimal(ingresoTxt));
            } catch (NumberFormatException ex) {
                miembro.setIngresoMensual(BigDecimal.ZERO);
            }
        } else {
            miembro.setIngresoMensual(BigDecimal.ZERO);
        }

        expedienteController.guardarMiembro(miembro);
        cargarTablaFamilia();
        mostrarFormFamilia(false);
    }

    private void cargarMiembroEnFormulario(int row) {
        if (row < 0 || row >= listaMiembros.size())
            return;
        MiembroFamiliar m = listaMiembros.get(row);
        miembroEnEdicion = m;
        try {
            personaSeleccionada = m.getPersona();
        } catch (Exception ignored) {
            personaSeleccionada = null;
        }

        if (personaSeleccionada != null) {
            txtBuscarCedula.setText(nvl(personaSeleccionada.getNumeroDocumento()));
            lblPersonaSeleccionada
                    .setText(nvl(personaSeleccionada.getNombres()) + " " + nvl(personaSeleccionada.getApellidos()));
            lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
        }
        if (m.getRelacionTitular() != null) {
            cmbRelacion.setSelectedItem(m.getRelacionTitular());
        }
        resetearToggle(btnJefatura, Boolean.TRUE.equals(m.getEsJefatura()));
        txtOcupacion.setText(nvl(m.getOcupacion()));
        resetearToggle(btnTrabaja, Boolean.TRUE.equals(m.getTrabaja()));
        txtIngreso.setText(m.getIngresoMensual() != null ? m.getIngresoMensual().toPlainString() : "");
        lblHeaderFormFamilia.setText("Editar miembro familiar");
        mostrarFormFamilia(true);
    }

    private void eliminarMiembro(int row) {
        if (row < 0 || row >= listaMiembros.size())
            return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este miembro del grupo familiar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        MiembroFamiliar m = listaMiembros.get(row);
        if (m.getId() != null)
            expedienteController.eliminarMiembro(m.getId());
        cargarTablaFamilia();
    }

    /**
     * Envuelve un componente con su etiqueta encima, usando fuente pequeña en gris.
     * @param texto texto de la etiqueta.
     * @param campo componente a envolver.
     * @return panel con etiqueta arriba y campo abajo.
     */
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

    /**
     * Carga los datos de un gasto en el formulario y establece gastoEnEdicion
     * para que confirmarGasto() actualice en lugar de crear uno nuevo.
     * @param row índice en gastosActuales.
     */
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

    /**
     * Agrega o actualiza un GastoMensual.
     * Valida que concepto y monto no estén vacíos. Si adendumActual es null, lo crea primero.
     */
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

    /**
     * Elimina el gasto en la posición indicada de gastosActuales, previa confirmación.
     * @param row índice en gastosActuales.
     */
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

    /** Resetea los campos del formulario de gasto al estado vacío. */
    private void limpiarFormularioGasto() {
        cmbCatGasto.setSelectedIndex(0);
        txtConceptoGasto.setText("");
        txtMontoGasto.setText("");
        try {
            txtFechaGasto.setText("");
        } catch (Exception ignored) {
        }
    }

    /** Recarga los gastos desde la BD, actualiza el paginador y refresca la tabla. */
    private void recargarTablaGastos() {
        if (adendumActual == null || adendumActual.getId() == null) return;
        pagGastos.cargar(expedienteController.findGastosByAdendum(adendumActual.getId()));
        refrescarTablaGastos();
    }

    private void refrescarTablaGastos() {
        modeloGastos.setRowCount(0);
        gastosActuales.clear();
        // Total sobre TODOS los gastos, no solo la página actual
        BigDecimal total = pagGastos.getTodos().stream()
                .map(g -> g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotalGastos.setText(String.format("%,.0f colones", total));
        for (GastoMensual g : pagGastos.getPagina()) {
            gastosActuales.add(g);
            BigDecimal m = g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO;
            String catDisplay = g.getCategoria() != null
                    ? g.getCategoria().name().replace("_", " ").substring(0, 1).toUpperCase()
                            + g.getCategoria().name().replace("_", " ").substring(1).toLowerCase()
                    : "—";
            modeloGastos.addRow(new Object[] {
                    catDisplay, nvl(g.getConcepto()),
                    String.format("%,.0f", m),
                    g.getFecha() != null ? sdf.format(g.getFecha()) : "—",
                    ""
            });
        }
        actualizarPanelPaginacion(panelPagGastos, pagGastos, this::refrescarTablaGastos);
    }


    /**
     * Agrega o actualiza una AsistenciaSolicitada.
     * Requiere que el campo modalidad no esté vacío.
     * Distingue modo agregar vs. editar según asistenciaEnEdicion.
     */
    private void confirmarAsistencia() {
        String modalidad = txtModalidadAsist.getText().trim();
        if (modalidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La modalidad es obligatoria.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expediente == null || expediente.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Guarde el expediente primero antes de agregar asistencias.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal valor = BigDecimal.ZERO;
        String valorStr = txtValorAsist.getText().trim();
        if (!valorStr.isEmpty()) {
            try {
                valor = new BigDecimal(valorStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El valor debe ser un número válido.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            AsistenciaSolicitada a = (asistenciaEnEdicion != null) ? asistenciaEnEdicion : new AsistenciaSolicitada();
            a.setTipoAsistencia((TipoAsistencia) cmbTipoAsistencia.getSelectedItem());
            a.setModalidad(modalidad);
            a.setFrecuencia(txtFrecuenciaAsist.getText().trim());
            a.setDuracion(txtDuracionAsist.getText().trim());
            a.setValor(valor);
            a.setExpediente(expediente);
            expedienteController.guardarAsistencia(a);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la asistencia:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        limpiarFormularioAsistencia();
        recargarListaAsistencias();
    }

    private void limpiarFormularioAsistencia() {
        asistenciaEnEdicion = null;
        cmbTipoAsistencia.setSelectedIndex(0);
        txtModalidadAsist.setText("");
        txtFrecuenciaAsist.setText("");
        txtDuracionAsist.setText("");
        txtValorAsist.setText("");
        btnConfirmarAsist.setText("+ Agregar asistencia");
        btnConfirmarAsist.setBackground(AppColors.PRIMARIO);
    }

    private void recargarListaAsistencias() {
        if (expediente == null || expediente.getId() == null) {
            panelListaAsistencias.removeAll();
            panelListaAsistencias.revalidate();
            panelListaAsistencias.repaint();
            return;
        }
        pagAsistencias.cargar(expedienteController.findAsistenciasByExpediente(expediente.getId()));
        refrescarListaAsistencias();
    }

    private void refrescarListaAsistencias() {
        panelListaAsistencias.removeAll();
        for (AsistenciaSolicitada a : pagAsistencias.getPagina())
            panelListaAsistencias.add(crearFilaAsistencia(a));
        panelListaAsistencias.revalidate();
        panelListaAsistencias.repaint();
        actualizarPanelPaginacion(panelPagAsistencias, pagAsistencias, this::refrescarListaAsistencias);
    }

    private JPanel crearTabDocs() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        p.add(crearPanelSubidaDoc(), BorderLayout.NORTH);

        panelListaDocs = new JPanel();
        panelListaDocs.setLayout(new BoxLayout(panelListaDocs, BoxLayout.Y_AXIS));
        panelListaDocs.setBackground(AppColors.PANEL);

        JScrollPane scroll = new JScrollPane(panelListaDocs);
        scroll.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scroll.getViewport().setBackground(AppColors.PANEL);

        JLabel lblDocs = new JLabel("Documentos adjuntos");
        lblDocs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDocs.setForeground(AppColors.TEXTO_GRIS);

        panelPagDocs.setOpaque(false);
        JPanel scrollConPag = new JPanel(new BorderLayout(0, 0));
        scrollConPag.setOpaque(false);
        scrollConPag.add(scroll, BorderLayout.CENTER);
        scrollConPag.add(panelPagDocs, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setBackground(AppColors.PANEL);
        centro.add(lblDocs, BorderLayout.NORTH);
        centro.add(scrollConPag, BorderLayout.CENTER);

        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelSubidaDoc() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        JPanel filePicker = new JPanel(new BorderLayout(8, 0));
        filePicker.setBackground(AppColors.FONDO);
        filePicker.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        filePicker.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        lblArchivoNombre = new JLabel("Ningún archivo seleccionado");
        lblArchivoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblArchivoNombre.setForeground(AppColors.TEXTO_GRIS);

        JButton btnElegir = UIFactory.crearBotonSmall("Seleccionar archivo...", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> elegirArchivo());

        filePicker.add(lblArchivoNombre, BorderLayout.CENTER);
        filePicker.add(btnElegir, BorderLayout.EAST);

        JPanel fila1 = new JPanel(new GridLayout(1, 2, 12, 0));
        fila1.setBackground(AppColors.PANEL);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel grpTipo = new JPanel(new BorderLayout(0, 4));
        grpTipo.setBackground(AppColors.PANEL);
        JLabel lblTipoDoc = new JLabel("Tipo de documento");
        lblTipoDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTipoDoc.setForeground(AppColors.TEXTO_GRIS);
        cmbTipoDocAdjunto = new JComboBox<>(TipoDocumentoAdjunto.values());
        cmbTipoDocAdjunto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        grpTipo.add(lblTipoDoc, BorderLayout.NORTH);
        grpTipo.add(cmbTipoDocAdjunto, BorderLayout.CENTER);

        JPanel grpDesc = new JPanel(new BorderLayout(0, 4));
        grpDesc.setBackground(AppColors.PANEL);
        JLabel lblDescDoc = new JLabel("Descripción (opcional)");
        lblDescDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescDoc.setForeground(AppColors.TEXTO_GRIS);
        txtDescripcionDoc = new JTextField();
        txtDescripcionDoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcionDoc.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE), new EmptyBorder(4, 8, 4, 8)));
        grpDesc.add(lblDescDoc, BorderLayout.NORTH);
        grpDesc.add(txtDescripcionDoc, BorderLayout.CENTER);

        fila1.add(grpTipo);
        fila1.add(grpDesc);

        chkEsFirmado = new JCheckBox("Es consentimiento firmado");
        chkEsFirmado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkEsFirmado.setBackground(AppColors.PANEL);
        chkEsFirmado.setForeground(AppColors.TEXTO);

        panelFirmante = new JPanel(new GridLayout(1, 2, 12, 0));
        panelFirmante.setBackground(AppColors.PANEL);
        panelFirmante.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panelFirmante.setVisible(false);

        JPanel grpFirmante = new JPanel(new BorderLayout(0, 4));
        grpFirmante.setBackground(AppColors.PANEL);
        JLabel lblFirmante = new JLabel("Nombre firmante");
        lblFirmante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFirmante.setForeground(AppColors.TEXTO_GRIS);
        txtNombreFirmante = new JTextField();
        txtNombreFirmante.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNombreFirmante.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE), new EmptyBorder(4, 8, 4, 8)));
        grpFirmante.add(lblFirmante, BorderLayout.NORTH);
        grpFirmante.add(txtNombreFirmante, BorderLayout.CENTER);

        JPanel grpFechaFirma = new JPanel(new BorderLayout(0, 4));
        grpFechaFirma.setBackground(AppColors.PANEL);
        JLabel lblFechaFirma = new JLabel("Fecha firma");
        lblFechaFirma.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFechaFirma.setForeground(AppColors.TEXTO_GRIS);
        txtFechaFirmaDoc = UIFactory.crearCampoFecha();
        txtFechaFirmaDoc.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE), new EmptyBorder(4, 8, 4, 8)));
        grpFechaFirma.add(lblFechaFirma, BorderLayout.NORTH);
        grpFechaFirma.add(txtFechaFirmaDoc, BorderLayout.CENTER);

        panelFirmante.add(grpFirmante);
        panelFirmante.add(grpFechaFirma);

        chkEsFirmado.addActionListener(e -> panelFirmante.setVisible(chkEsFirmado.isSelected()));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(AppColors.PANEL);
        JButton btnSubir = UIFactory.crearBoton("Subir documento", AppColors.PRIMARIO, Color.WHITE,
                e -> subirDocumento());
        btnRow.add(btnSubir);

        p.add(filePicker);
        p.add(Box.createVerticalStrut(10));
        p.add(fila1);
        p.add(Box.createVerticalStrut(8));
        p.add(chkEsFirmado);
        p.add(panelFirmante);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRow);

        return p;
    }

    private void elegirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar documento");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fc.getSelectedFile();
            lblArchivoNombre.setText(archivoSeleccionado.getName());
            lblArchivoNombre.setForeground(AppColors.TEXTO);
        }
    }

    /**
     * Valida que el archivo seleccionado no pese más de 5 MB y que no exista ya
     * un documento con el mismo nombre en el expediente.
     * Si pasa la validación, copia el archivo y persiste el DocumentoAdjunto.
     */
    private void subirDocumento() {
        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un archivo primero.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (archivoSeleccionado.length() > FileStorageUtil.MAX_BYTES) {
            JOptionPane.showMessageDialog(this,
                    "El archivo supera el límite de 5 MB.",
                    "Archivo muy grande", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verificar que no exista ya un documento con el mismo nombre en este expediente
        String nombreNuevo = archivoSeleccionado.getName();
        boolean yaExiste = expedienteController.findDocsByExpediente(expediente.getId()).stream()
                .anyMatch(d -> d.getArchivoUrl() != null && d.getArchivoUrl().endsWith(nombreNuevo));
        if (yaExiste) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un documento con el nombre \"" + nombreNuevo + "\" en este expediente.",
                    "Documento duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            File copiado = FileStorageUtil.copiarArchivo(archivoSeleccionado, expediente.getNumeroFicha());
            String rutaRel = FileStorageUtil.rutaRelativa(expediente.getNumeroFicha(), copiado.getName());

            DocumentoAdjunto doc = new DocumentoAdjunto();
            doc.setExpediente(expediente);
            doc.setTipo((TipoDocumentoAdjunto) cmbTipoDocAdjunto.getSelectedItem());
            String desc = txtDescripcionDoc.getText().trim();
            doc.setDescripcion(desc.isEmpty() ? null : desc);
            doc.setArchivoUrl(rutaRel);
            doc.setFechaSubida(new Date());
            doc.setEsDocFirmado(chkEsFirmado.isSelected());
            if (chkEsFirmado.isSelected()) {
                doc.setNombreFirmante(txtNombreFirmante.getText().trim());
                String fechaStr = txtFechaFirmaDoc.getText().replace("_", "").trim();
                if (fechaStr.length() == 10) {
                    try {
                        doc.setFechaFirma(new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr));
                    } catch (ParseException ignored) {
                    }
                }
            }
            doc.setSubidoPor(SessionContext.getUsuarioActual());

            expedienteController.guardarDocumento(doc);

            archivoSeleccionado = null;
            lblArchivoNombre.setText("Ningún archivo seleccionado");
            lblArchivoNombre.setForeground(AppColors.TEXTO_GRIS);
            txtDescripcionDoc.setText("");
            chkEsFirmado.setSelected(false);
            panelFirmante.setVisible(false);
            txtNombreFirmante.setText("");
            txtFechaFirmaDoc.setValue(null);

            cargarTablaDocs();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al copiar el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el documento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearFilaDoc(DocumentoAdjunto doc) {
        String nombreArchivo = doc.getArchivoUrl() != null
                ? new File(doc.getArchivoUrl()).getName()
                : "—";

        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setBackground(AppColors.PANEL);
        fila.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppColors.BORDE),
                new EmptyBorder(10, 12, 10, 12)));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izq.setBackground(AppColors.PANEL);

        JLabel lblNombreDoc = new JLabel(nombreArchivo);
        lblNombreDoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreDoc.setForeground(AppColors.TEXTO);
        izq.add(lblNombreDoc);
        izq.add(crearBadgeTipo(doc.getTipo()));
        if (Boolean.TRUE.equals(doc.getEsDocFirmado())) {
            izq.add(crearBadge("Firmado", AppColors.VERDE_BG, AppColors.VERDE_FG));
        }

        String fecha = doc.getFechaSubida() != null ? sdf.format(doc.getFechaSubida()) : "—";
        String subidoPor = doc.getSubidoPor() != null ? doc.getSubidoPor().getNombre() : "—";
        String firmaInfo = Boolean.TRUE.equals(doc.getEsDocFirmado()) && doc.getNombreFirmante() != null
                ? "  ·  Firma: " + doc.getNombreFirmante()
                : "";
        JLabel lblMeta = new JLabel(fecha + "  ·  Subido por: " + subidoPor + firmaInfo);
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMeta.setForeground(AppColors.TEXTO_GRIS);

        JPanel centro = new JPanel(new BorderLayout(0, 2));
        centro.setBackground(AppColors.PANEL);
        centro.add(izq, BorderLayout.NORTH);
        centro.add(lblMeta, BorderLayout.SOUTH);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setBackground(AppColors.PANEL);

        JButton btnVer = UIFactory.crearBotonSmall("Ver", AppColors.AZUL_PANEL, AppColors.AZUL, e -> {
            try {
                FileStorageUtil.abrirArchivo(doc.getArchivoUrl());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo abrir el archivo: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = UIFactory.crearBotonSmall("X", AppColors.ROJO_CARD_BG, AppColors.ROJO, e -> {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el documento \"" + nombreArchivo + "\"?\nEsta acción no se puede deshacer.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf == JOptionPane.YES_OPTION) {
                FileStorageUtil.eliminarArchivo(doc.getArchivoUrl());
                expedienteController.eliminarDocumento(doc.getId());
                cargarTablaDocs();
            }
        });

        der.add(btnVer);
        der.add(btnEliminar);

        fila.add(centro, BorderLayout.CENTER);
        fila.add(der, BorderLayout.EAST);
        return fila;
    }

    private JLabel crearBadgeTipo(TipoDocumentoAdjunto tipo) {
        Color bg, fg;
        switch (tipo != null ? tipo : TipoDocumentoAdjunto.OTRO) {
            case CEDULA:
            case PASAPORTE:
                bg = AppColors.AZUL_CARD_BG;
                fg = AppColors.AZUL_CARD_FG;
                break;
            case CONSENTIMIENTO:
                bg = AppColors.VERDE_BG;
                fg = AppColors.VERDE_FG;
                break;
            case INFO_MEDICA:
            case DICTAMEN:
            case RECETA:
                bg = AppColors.ROJO_CARD_BG;
                fg = AppColors.ROJO_CARD_FG;
                break;
            case FACTURA:
                bg = AppColors.PURP_BG;
                fg = AppColors.PURPURA;
                break;
            default:
                bg = AppColors.AMBAR_BG;
                fg = AppColors.AMBAR_FG;
                break;
        }
        String texto = tipo != null ? tipo.name().replace("_", " ") : "OTRO";
        return crearBadge(texto, bg, fg);
    }

    private JLabel crearBadge(String texto, Color bg, Color fg) {
        JLabel badge = new JLabel(" " + texto + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBackground(bg);
        badge.setForeground(fg);
        return badge;
    }

    private JPanel crearFilaAsistencia(AsistenciaSolicitada a) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setBackground(AppColors.PANEL);
        fila.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, AppColors.BORDE),
                new EmptyBorder(10, 12, 10, 12)));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izq.setBackground(AppColors.PANEL);
        izq.add(crearBadgeAsistencia(a.getTipoAsistencia()));

        JLabel lblModalidad = new JLabel(nvl(a.getModalidad()));
        lblModalidad.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblModalidad.setForeground(AppColors.TEXTO);
        izq.add(lblModalidad);

        String valorStr = a.getValor() != null ? String.format("₡ %,.0f", a.getValor()) : "";
        String frecStr = nvl(a.getFrecuencia());
        String durStr = nvl(a.getDuracion());
        StringBuilder meta = new StringBuilder();
        if (!frecStr.equals("—"))
            meta.append(frecStr);
        if (!durStr.equals("—")) {
            if (meta.length() > 0)
                meta.append("  ·  ");
            meta.append(durStr);
        }
        if (!valorStr.isEmpty()) {
            if (meta.length() > 0)
                meta.append("  ·  ");
            meta.append(valorStr);
        }

        JLabel lblMeta = new JLabel(meta.toString());
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMeta.setForeground(AppColors.TEXTO_GRIS);

        JPanel centro = new JPanel(new BorderLayout(0, 2));
        centro.setBackground(AppColors.PANEL);
        centro.add(izq, BorderLayout.NORTH);
        centro.add(lblMeta, BorderLayout.SOUTH);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        der.setBackground(AppColors.PANEL);

        JButton btnEditar = UIFactory.crearBotonSmall("Editar", AppColors.AZUL_PANEL, AppColors.AZUL, e -> {
            asistenciaEnEdicion = a;
            cmbTipoAsistencia.setSelectedItem(a.getTipoAsistencia());
            txtModalidadAsist.setText(nvl(a.getModalidad()).equals("—") ? "" : nvl(a.getModalidad()));
            txtFrecuenciaAsist.setText(nvl(a.getFrecuencia()).equals("—") ? "" : nvl(a.getFrecuencia()));
            txtDuracionAsist.setText(nvl(a.getDuracion()).equals("—") ? "" : nvl(a.getDuracion()));
            txtValorAsist.setText(a.getValor() != null ? a.getValor().toPlainString() : "");
            btnConfirmarAsist.setText("Guardar cambios");
            btnConfirmarAsist.setBackground(AppColors.AZUL);
        });

        JButton btnEliminar = UIFactory.crearBotonSmall("X", AppColors.ROJO_CARD_BG, AppColors.ROJO, e -> {
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar esta asistencia?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    if (a.getId() != null)
                        expedienteController.eliminarAsistencia(a.getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                recargarListaAsistencias();
            }
        });

        der.add(btnEditar);
        der.add(btnEliminar);

        fila.add(centro, BorderLayout.CENTER);
        fila.add(der, BorderLayout.EAST);
        return fila;
    }

    private JLabel crearBadgeAsistencia(TipoAsistencia tipo) {
        Color bg, fg;
        switch (tipo != null ? tipo : TipoAsistencia.OTRO) {
            case ALIMENTOS:
                bg = AppColors.VERDE_BG;
                fg = AppColors.VERDE_FG;
                break;
            case MEDICAMENTOS:
                bg = AppColors.PURP_BG;
                fg = AppColors.PURPURA;
                break;
            case HIGIENE_LIMPIEZA:
                bg = AppColors.AZUL_CARD_BG;
                fg = AppColors.AZUL_CARD_FG;
                break;
            case INDUMENTARIA:
                bg = AppColors.AMBAR_BG;
                fg = AppColors.AMBAR_FG;
                break;
            case ALQUILER:
                bg = AppColors.ALQUILER_BG;
                fg = AppColors.AMBAR_FG;
                break;
            case SERVICIOS:
                bg = AppColors.SERVICIOS_BG;
                fg = AppColors.SERVICIOS_FG;
                break;
            case APARATOS_ORTOPEDICOS:
                bg = AppColors.ROJO_CARD_BG;
                fg = AppColors.ROJO_CARD_FG;
                break;
            default:
                bg = AppColors.GRIS_BTN;
                fg = AppColors.TEXTO_GRIS;
                break;
        }
        String texto = tipo != null ? tipo.name().replace("_", " ") : "OTRO";
        return crearBadge(texto, bg, fg);
    }

    private JPanel crearTabAsistencia() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        p.add(crearPanelFormAsistencia(), BorderLayout.NORTH);

        panelListaAsistencias = new JPanel();
        panelListaAsistencias.setLayout(new BoxLayout(panelListaAsistencias, BoxLayout.Y_AXIS));
        panelListaAsistencias.setBackground(AppColors.PANEL);

        JScrollPane scroll = new JScrollPane(panelListaAsistencias);
        scroll.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scroll.getViewport().setBackground(AppColors.PANEL);

        JLabel lblLista = new JLabel("Asistencias registradas");
        lblLista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLista.setForeground(AppColors.TEXTO_GRIS);

        panelPagAsistencias.setOpaque(false);
        JPanel scrollConPag = new JPanel(new BorderLayout(0, 0));
        scrollConPag.setOpaque(false);
        scrollConPag.add(scroll, BorderLayout.CENTER);
        scrollConPag.add(panelPagAsistencias, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setBackground(AppColors.PANEL);
        centro.add(lblLista, BorderLayout.NORTH);
        centro.add(scrollConPag, BorderLayout.CENTER);

        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelFormAsistencia() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        cmbTipoAsistencia = new JComboBox<>(TipoAsistencia.values());
        txtModalidadAsist = new JTextField();
        txtModalidadAsist.putClientProperty("JTextField.placeholderText", "Ej: Entrega directa");
        txtFrecuenciaAsist = new JTextField();
        txtFrecuenciaAsist.putClientProperty("JTextField.placeholderText", "Ej: Quincenal");
        txtDuracionAsist = new JTextField();
        txtDuracionAsist.putClientProperty("JTextField.placeholderText", "Ej: 6 meses");
        txtValorAsist = new JTextField();
        txtValorAsist.putClientProperty("JTextField.placeholderText", "Ej: 25000");

        JPanel fila = new JPanel(new GridLayout(1, 5, 12, 0));
        fila.setBackground(AppColors.PANEL);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        fila.add(campoConEtiqueta("Tipo", cmbTipoAsistencia));
        fila.add(campoConEtiqueta("Modalidad *", txtModalidadAsist));
        fila.add(campoConEtiqueta("Frecuencia", txtFrecuenciaAsist));
        fila.add(campoConEtiqueta("Duración", txtDuracionAsist));
        fila.add(campoConEtiqueta("Valor (₡)", txtValorAsist));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(AppColors.PANEL);
        btnConfirmarAsist = UIFactory.crearBoton("+ Agregar asistencia", AppColors.PRIMARIO, Color.WHITE,
                e -> confirmarAsistencia());
        JButton btnLimpiar = UIFactory.crearBoton("Limpiar", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> limpiarFormularioAsistencia());
        btnRow.add(btnConfirmarAsist);
        btnRow.add(btnLimpiar);

        p.add(fila);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRow);
        return p;
    }

    private JPanel crearTabEntrevistas() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(AppColors.PANEL);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        txtFechaEntrevista = UIFactory.crearCampoFecha();
        txtEntrevistadorEntrev = new JTextField();
        txtEntrevistadorEntrev.putClientProperty("JTextField.placeholderText", "Nombre del entrevistador");
        chkRecomiendaAyuda = new JCheckBox("Recomienda ayuda");
        chkRecomiendaAyuda.setBackground(AppColors.PANEL);
        chkRecomiendaAyuda.setForeground(AppColors.TEXTO);
        chkRecomiendaAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel filasCampos = new JPanel(new GridLayout(1, 2, 12, 0));
        filasCampos.setBackground(AppColors.PANEL);
        filasCampos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        filasCampos.add(campoConEtiqueta("Fecha (dd/mm/aaaa) *", txtFechaEntrevista));
        filasCampos.add(campoConEtiqueta("Entrevistador *", txtEntrevistadorEntrev));

        txtObsEntrevista = new JTextArea(3, 20);
        txtObsEntrevista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObsEntrevista.setLineWrap(true);
        txtObsEntrevista.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObsEntrevista);
        scrollObs.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        scrollObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lblObs = new JLabel("Observaciones");
        lblObs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblObs.setForeground(AppColors.TEXTO_GRIS);

        JPanel obsPanel = new JPanel(new BorderLayout(0, 4));
        obsPanel.setOpaque(false);
        obsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        obsPanel.add(lblObs, BorderLayout.NORTH);
        obsPanel.add(scrollObs, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(AppColors.PANEL);
        JButton btnAgregar = UIFactory.crearBoton("+ Agregar entrevista", AppColors.PRIMARIO, Color.WHITE,
                e -> confirmarEntrevista());
        JButton btnLimpiar = UIFactory.crearBoton("Limpiar", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> limpiarFormularioEntrevista());
        btnRow.add(btnAgregar);
        btnRow.add(btnLimpiar);

        formPanel.add(filasCampos);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            {
                setOpaque(false);
                add(chkRecomiendaAyuda);
            }
        });
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(obsPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnRow);

        modeloEntrevistas = new DefaultTableModel(
                new String[] { "Fecha", "Entrevistador", "Recomienda Ayuda", "Observaciones" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaEntrevistas = new JTable(modeloEntrevistas);
        configurarTabla(tablaEntrevistas);
        JScrollPane scrollTabla = crearScrollTabla(tablaEntrevistas);

        JLabel lblLista = new JLabel("Entrevistas realizadas");
        lblLista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLista.setForeground(AppColors.TEXTO_GRIS);

        panelPagEntrevistas.setOpaque(false);
        JPanel centroPanel = new JPanel(new BorderLayout(0, 6));
        centroPanel.setBackground(AppColors.PANEL);
        centroPanel.add(lblLista, BorderLayout.NORTH);
        centroPanel.add(scrollTabla, BorderLayout.CENTER);
        centroPanel.add(panelPagEntrevistas, BorderLayout.SOUTH);

        p.add(formPanel, BorderLayout.NORTH);
        p.add(centroPanel, BorderLayout.CENTER);
        return p;
    }

    private void confirmarEntrevista() {
        String fechaStr = txtFechaEntrevista.getText();
        String entrevistador = txtEntrevistadorEntrev.getText().trim();
        if (fechaStr == null || fechaStr.contains("_") || fechaStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de la entrevista es obligatoria.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (entrevistador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del entrevistador es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expediente == null || expediente.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Guarde el expediente primero antes de agregar entrevistas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Entrevista entrevista = new Entrevista();
            entrevista.setFecha(parseFecha(fechaStr));
            entrevista.setEntrevistador(entrevistador);
            entrevista.setObservaciones(txtObsEntrevista.getText().trim());
            entrevista.setRecomiendaAyuda(chkRecomiendaAyuda.isSelected());
            entrevista.setExpediente(expediente);
            expedienteController.guardarEntrevista(entrevista);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la entrevista:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        limpiarFormularioEntrevista();
        cargarTablaEntrevistas();
    }

    private void limpiarFormularioEntrevista() {
        txtFechaEntrevista.setValue(null);
        txtFechaEntrevista.setText("");
        txtEntrevistadorEntrev.setText("");
        txtObsEntrevista.setText("");
        chkRecomiendaAyuda.setSelected(false);
    }

    private JPanel crearTabProlongaciones() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(AppColors.PANEL);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(12, 16, 12, 16)));

        txtFechaProlongacion = UIFactory.crearCampoFecha();
        txtObsProlongacion = new JTextField();
        txtObsProlongacion.putClientProperty("JTextField.placeholderText", "Motivo o nota de la prolongación");

        JPanel filas = new JPanel(new GridLayout(1, 2, 12, 0));
        filas.setBackground(AppColors.PANEL);
        filas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        filas.add(campoConEtiqueta("Fecha prolongación (dd/mm/aaaa) *", txtFechaProlongacion));
        filas.add(campoConEtiqueta("Observaciones", txtObsProlongacion));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(AppColors.PANEL);
        JButton btnConfirmar = UIFactory.crearBoton("+ Registrar prolongación", AppColors.PURPURA, Color.WHITE,
                e -> confirmarProlongacion());
        btnRow.add(btnConfirmar);

        formPanel.add(filas);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnRow);

        modeloProlongaciones = new DefaultTableModel(
                new String[] { "Fecha", "Observaciones", "Registrado por" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tablaProlongaciones = new JTable(modeloProlongaciones);
        configurarTabla(tablaProlongaciones);
        JScrollPane scrollTabla = crearScrollTabla(tablaProlongaciones);

        JLabel lblLista = new JLabel("Prolongaciones registradas");
        lblLista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLista.setForeground(AppColors.TEXTO_GRIS);

        panelPagProlongaciones.setOpaque(false);
        JPanel centroPanel = new JPanel(new BorderLayout(0, 6));
        centroPanel.setBackground(AppColors.PANEL);
        centroPanel.add(lblLista, BorderLayout.NORTH);
        centroPanel.add(scrollTabla, BorderLayout.CENTER);
        centroPanel.add(panelPagProlongaciones, BorderLayout.SOUTH);

        p.add(formPanel, BorderLayout.NORTH);
        p.add(centroPanel, BorderLayout.CENTER);
        return p;
    }

    private void confirmarProlongacion() {
        String fechaStr = txtFechaProlongacion.getText();
        if (fechaStr == null || fechaStr.contains("_") || fechaStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de prolongación es obligatoria.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expediente == null || expediente.getId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Guarde el expediente primero antes de registrar prolongaciones.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ProlongacionAyuda prol = new ProlongacionAyuda();
            prol.setFechaProlongacion(parseFecha(fechaStr));
            prol.setObservaciones(txtObsProlongacion.getText().trim());
            prol.setExpediente(expediente);
            Usuario usuarioActual = SessionContext.getUsuarioActual();
            if (usuarioActual != null)
                prol.setRegistradoPor(usuarioActual);
            expedienteController.guardarProlongacion(prol);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar la prolongación:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtFechaProlongacion.setValue(null);
        txtFechaProlongacion.setText("");
        txtObsProlongacion.setText("");
        cargarTablaProlongaciones();
    }

    private void cargarTablaProlongaciones() {
        if (modeloProlongaciones == null || expediente == null || expediente.getId() == null) return;
        pagProlongaciones.cargar(expedienteController.findProlongacionesByExpediente(expediente.getId()));
        refrescarTablaProlongaciones();
    }

    private void refrescarTablaProlongaciones() {
        modeloProlongaciones.setRowCount(0);
        for (ProlongacionAyuda pr : pagProlongaciones.getPagina()) {
            String registradoPor = "—";
            try {
                if (pr.getRegistradoPor() != null)
                    registradoPor = nvl(pr.getRegistradoPor().getNombre());
            } catch (Exception ignored) {}
            modeloProlongaciones.addRow(new Object[] {
                    pr.getFechaProlongacion() != null ? sdf.format(pr.getFechaProlongacion()) : "—",
                    nvl(pr.getObservaciones()),
                    registradoPor
            });
        }
        actualizarPanelPaginacion(panelPagProlongaciones, pagProlongaciones, this::refrescarTablaProlongaciones);
    }

    /**
     * Crea un panel con etiqueta superior y tabla de solo lectura.
     * @param modelo      modelo de la tabla.
     * @param descripcion texto de la etiqueta de encabezado.
     * @return panel listo para usar en una pestaña.
     */
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
        tabla.setGridColor(AppColors.GRID_TBL);
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

    /**
     * Aplica el estilo visual estándar (fuente, altura de fila, colores) a una tabla.
     * @param rowHeight altura de cada fila en píxeles.
     */
    private void configurarTabla(JTable tabla, int rowHeight) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(rowHeight);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(AppColors.GRID_TBL);
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
    }

    private void configurarTabla(JTable tabla) { configurarTabla(tabla, 34); }

    private JScrollPane crearScrollTabla(JTable tabla) {
        JScrollPane s = new JScrollPane(tabla);
        s.setBorder(new LineBorder(AppColors.BORDE, 1, true));
        s.getViewport().setBackground(AppColors.PANEL);
        return s;
    }

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
                e -> tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1));
        btnProlong.setVisible(!esNuevo);

        derecha.add(btnGuardar);
        derecha.add(btnProlong);

        p.add(btnCancelar, BorderLayout.WEST);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    /**
     * Precarga combos (parroquias, enums) y, si el expediente no es null,
     * llama a todos los métodos cargarTabla*() para llenar cada pestaña.
     * Es seguro llamarlo con expediente null (modo nuevo).
     */
    private void cargarDatos() {
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

        // En modo nuevo, precarga el entrevistador con el usuario en sesión
        if (esNuevo) {
            Usuario actual = SessionContext.getUsuarioActual();
            if (actual != null) {
                txtEntrevistador.setText(actual.getNombre());
            }
        } else {
            txtEntrevistador.setText(nvl(expediente.getEntrevistador()));
        }

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

        // Pre-llenar entrevistador con el usuario actual
        Usuario usuarioActual = SessionContext.getUsuarioActual();
        if (usuarioActual != null && txtEntrevistadorEntrev != null)
            txtEntrevistadorEntrev.setText(usuarioActual.getNombre());

        cargarVivienda();
        cargarAdendum();
        cargarTablaFamilia();
        cargarTablaDocs();
        cargarTablaAsistencia();
        cargarTablaEntrevistas();
        cargarTablaProlongaciones();
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
        if (expediente == null || expediente.getId() == null) return;
        pagFamilia.cargar(expedienteController.findMiembrosByExpediente(expediente.getId()));
        refrescarTablaFamilia();
    }

    private void refrescarTablaFamilia() {
        modeloFamilia.setRowCount(0);
        listaMiembros.clear();
        for (MiembroFamiliar m : pagFamilia.getPagina()) {
            listaMiembros.add(m);
            String nombre = "—", cedula = "—";
            try {
                if (m.getPersona() != null) {
                    nombre = nvl(m.getPersona().getNombres()) + " " + nvl(m.getPersona().getApellidos());
                    cedula = nvl(m.getPersona().getNumeroDocumento());
                }
            } catch (Exception ignored) { /* LazyInitializationException */ }
            modeloFamilia.addRow(new Object[] {
                    nombre, cedula, nvl(m.getRelacionTitular()),
                    Boolean.TRUE.equals(m.getEsJefatura()) ? "Sí" : "No",
                    nvl(m.getOcupacion()),
                    m.getIngresoMensual() != null ? m.getIngresoMensual().toPlainString() : "0.00",
                    ""
            });
        }
        actualizarPanelPaginacion(panelPagFamilia, pagFamilia, this::refrescarTablaFamilia);
    }

    private void cargarTablaDocs() {
        if (panelListaDocs == null || expediente == null || expediente.getId() == null) return;
        pagDocs.cargar(expedienteController.findDocsByExpediente(expediente.getId()));
        refrescarListaDocs();
    }

    private void refrescarListaDocs() {
        panelListaDocs.removeAll();
        List<DocumentoAdjunto> pagina = pagDocs.getPagina();
        if (pagina.isEmpty() && pagDocs.getTodos().isEmpty()) {
            JLabel lblVacio = new JLabel("No hay documentos adjuntos.");
            lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblVacio.setForeground(AppColors.TEXTO_GRIS);
            lblVacio.setBorder(new EmptyBorder(16, 12, 16, 12));
            panelListaDocs.add(lblVacio);
        } else {
            for (DocumentoAdjunto d : pagina)
                panelListaDocs.add(crearFilaDoc(d));
        }
        panelListaDocs.revalidate();
        panelListaDocs.repaint();
        actualizarPanelPaginacion(panelPagDocs, pagDocs, this::refrescarListaDocs);
    }

    private void cargarTablaAsistencia() {
        recargarListaAsistencias();
    }

    private void cargarTablaEntrevistas() {
        if (expediente == null || expediente.getId() == null) return;
        pagEntrevistas.cargar(expedienteController.findEntrevistasByExpediente(expediente.getId()));
        refrescarTablaEntrevistas();
    }

    private void refrescarTablaEntrevistas() {
        modeloEntrevistas.setRowCount(0);
        for (Entrevista e : pagEntrevistas.getPagina()) {
            String obs = nvl(e.getObservaciones());
            if (obs.length() > 60) obs = obs.substring(0, 57) + "...";
            modeloEntrevistas.addRow(new Object[] {
                    e.getFecha() != null ? sdf.format(e.getFecha()) : "—",
                    nvl(e.getEntrevistador()),
                    Boolean.TRUE.equals(e.getRecomiendaAyuda()) ? "Sí" : "No",
                    obs
            });
        }
        actualizarPanelPaginacion(panelPagEntrevistas, pagEntrevistas, this::refrescarTablaEntrevistas);
    }

    /**
     * Guarda el expediente.
     * Pasos: valida campos obligatorios, verifica en modo nuevo que no exista otro
     * expediente con el mismo número de documento, crea o actualiza Persona y Expediente
     * en una sola transacción, recalcula la etapa, desbloquea las pestañas y llama onGuardado.
     */
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

            // En modo nuevo, verifica que no exista otro expediente con el mismo número de documento
            if (esNuevo) {
                Expediente existente = expedienteController.findByNumeroFicha(
                        expedienteController.generarNumeroFicha(numDoc));
                if (existente != null) {
                    JOptionPane.showMessageDialog(this,
                            "Ya existe un expediente registrado para este número de documento.\nFicha: " + existente.getNumeroFicha(),
                            "Duplicado detectado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

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
            // 3. Crear o actualizar Expediente
            boolean wasNuevo = esNuevo || (expediente == null);
            if (wasNuevo) {
                expediente = new Expediente();
                expediente.setNumeroFicha(expedienteController.generarNumeroFicha(numDoc));
                expediente.setEtapaActual(EtapaExpediente.REGISTRO);
            }

            expediente.setParroquia((Parroquia) cmbParroquia.getSelectedItem());
            expediente.setEstado((EstadoExpediente) cmbEstado.getSelectedItem());
            expediente.setEntrevistador(txtEntrevistador.getText().trim());
            expediente.setFechaInicio(parseFecha(txtFechaInicio.getText()));
            expediente.setFechaPrevistaConclusion(parseFecha(txtFechaPrevista.getText()));
            expediente.setObservaciones(txtObservaciones.getText().trim());
            String marcador = (String) cmbColorMarcador.getSelectedItem();
            expediente.setColorMarcador("NINGUNO".equals(marcador) ? null : marcador);

            // Persona + Expediente en una sola transacción atómica
            expedienteController.guardarTitularYExpediente(titular, expediente);
            EtapaExpediente etapaAntes = expediente.getEtapaActual();
            actualizarEtapaActual();
            if (expediente.getEtapaActual() != etapaAntes)
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
            actualizarBarraProgreso();
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

    private void actualizarTitulo() {
        if (expediente == null)
            return;
        String titulo = "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular();
        setTitle("Expediente " + titulo);
        if (lblTituloPrincipal != null)
            lblTituloPrincipal.setText(titulo);
    }

    /**
     * Guarda la etapa calculada en expediente.etapaActual y refresca la barra de progreso.
     * No hace nada si el expediente es null.
     */
    private void actualizarEtapaActual() {
        if (expediente == null)
            return;
        EtapaExpediente etapaActual = expediente.getEtapaActual();
        if (etapaActual == null)
            etapaActual = EtapaExpediente.REGISTRO;

        // Solo avanza, nunca retrocede
        EtapaExpediente calculada = calcularEtapa();
        if (calculada.ordinal() > etapaActual.ordinal())
            expediente.setEtapaActual(calculada);
    }

    /**
     * Determina la etapa más avanzada alcanzada por el expediente inspeccionando
     * qué secciones tienen datos, de mayor a menor:
     * entrevistas > doc CONSENTIMIENTO > cualquier doc > gastos > vivienda > familia > REGISTRO.
     * @return la etapa calculada; nunca null.
     */
    private EtapaExpediente calcularEtapa() {
        if (expediente == null || expediente.getId() == null)
            return EtapaExpediente.REGISTRO;
        Long id = expediente.getId();

        boolean tieneEntrevistas = !expedienteController.findEntrevistasByExpediente(id).isEmpty();
        if (tieneEntrevistas)
            return EtapaExpediente.EVALUACION;

        List<DocumentoAdjunto> docs = expedienteController.findDocsByExpediente(id);
        boolean tieneConsentimiento = docs.stream().anyMatch(
                d -> d.getTipo() != null && TipoDocumentoAdjunto.CONSENTIMIENTO.equals(d.getTipo()));
        if (tieneConsentimiento)
            return EtapaExpediente.CONSENTIMIENTO;
        if (!docs.isEmpty())
            return EtapaExpediente.DOCUMENTOS;

        boolean tieneGastos = adendumActual != null &&
                !expedienteController.findGastosByAdendum(adendumActual.getId()).isEmpty();
        if (tieneGastos)
            return EtapaExpediente.GASTOS;

        if (viviendaActual != null)
            return EtapaExpediente.VIVIENDA;

        if (!expedienteController.findMiembrosByExpediente(id).isEmpty())
            return EtapaExpediente.FAMILIA;

        return EtapaExpediente.REGISTRO;
    }

    private void actualizarBarraProgreso() {
        if (panelBarraProgreso == null || expediente == null)
            return;
        EtapaExpediente etapaActual = expediente.getEtapaActual() != null
                ? expediente.getEtapaActual()
                : EtapaExpediente.REGISTRO;
        panelBarraProgreso.setIdx(etapaActual.ordinal());
    }

    /**
     * Bloquea las pestañas 1 a 7 mientras el expediente no tenga ID (aún no persistido).
     * Pone un tooltip en cada pestaña bloqueada explicando por qué no está disponible.
     */
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

    /**
     * Intenta parsear el texto como fecha con formato dd/MM/yyyy.
     * Devuelve null si el texto está vacío, contiene guiones bajos del enmascaramiento
     * o no es una fecha válida. No lanza excepción.
     * @param texto texto a parsear.
     * @return la fecha parseada o null.
     */
    private Date parseFecha(String texto) {
        if (texto == null || texto.trim().isEmpty() || texto.contains("_"))
            return null;
        try {
            return sdf.parse(texto.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Paginador genérico en memoria.
     * Mantiene la lista completa y un índice de página.
     * getPagina() devuelve los 20 ítems de la página actual.
     * Los datos se cargan con cargar(List) y no se consulta la BD directamente.
     */
    private static class Paginador<T> {
        private static final int TAMANO = 20;
        private List<T> datos = Collections.emptyList();
        private int pagina = 0;

        void cargar(List<T> todos) { datos = todos != null ? todos : Collections.emptyList(); pagina = 0; }
        List<T> getPagina() {
            int desde = pagina * TAMANO;
            int hasta = Math.min(desde + TAMANO, datos.size());
            return desde < datos.size() ? datos.subList(desde, hasta) : Collections.emptyList();
        }
        List<T> getTodos() { return Collections.unmodifiableList(datos); }
        boolean hayAnterior()   { return pagina > 0; }
        boolean haySiguiente()  { return (pagina + 1) * TAMANO < datos.size(); }
        void anterior()         { if (hayAnterior()) pagina--; }
        void siguiente()        { if (haySiguiente()) pagina++; }
        boolean necesitaPaginacion() { return datos.size() > TAMANO; }
        String etiqueta() {
            int total = Math.max(1, (int) Math.ceil(datos.size() / (double) TAMANO));
            return "Página " + (pagina + 1) + " de " + total;
        }
    }

    private void actualizarPanelPaginacion(JPanel panel, Paginador<?> pag, Runnable refresh) {
        panel.removeAll();
        if (pag.necesitaPaginacion()) {
            JButton btnAnt = new JButton("< Anterior");
            JButton btnSig = new JButton("Siguiente >");
            JLabel lblPag  = new JLabel(pag.etiqueta());
            Font f = new Font("Segoe UI", Font.PLAIN, 11);
            btnAnt.setFont(f); btnSig.setFont(f);
            lblPag.setFont(f); lblPag.setForeground(AppColors.TEXTO_GRIS);
            btnAnt.setEnabled(pag.hayAnterior());
            btnSig.setEnabled(pag.haySiguiente());
            btnAnt.addActionListener(e -> { pag.anterior(); refresh.run(); });
            btnSig.addActionListener(e -> { pag.siguiente(); refresh.run(); });
            panel.add(btnAnt); panel.add(lblPag); panel.add(btnSig);
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Panel que dibuja el progreso de etapas del expediente.
     * Un círculo por etapa: verde = completada, azul (más grande) = actual, gris = pendiente.
     * La etapa actual muestra su nombre debajo del círculo.
     */
    private static class BarraProgresoPanel extends JPanel {
        private int idx;

        BarraProgresoPanel(int idx) {
            this.idx = idx;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 38));
        }

        void setIdx(int idx) {
            this.idx = idx;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            EtapaExpediente[] etapas = EtapaExpediente.values();
            int n = etapas.length;
            int w = getWidth();
            int cy = 12; // centro Y del rail y círculos
            int r = 5; // radio etapas normales
            int rAct = 7; // radio etapa actual
            int step = w / (n + 1);

            // Rail completo (gris)
            g2.setColor(AppColors.HEADER_TBL);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(step, cy, w - step, cy);

            // Segmento completado (verde)
            if (idx > 0) {
                g2.setColor(AppColors.VERDE_BG);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(step, cy, step * (idx + 1), cy);
            }

            // Círculos y etiqueta de la etapa actual
            for (int i = 0; i < n; i++) {
                int cx = step * (i + 1);
                if (i < idx) {
                    g2.setColor(AppColors.VERDE_BG);
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setColor(AppColors.VERDE_FG);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                } else if (i == idx) {
                    g2.setColor(AppColors.PRIMARIO);
                    g2.fillOval(cx - rAct, cy - rAct, rAct * 2, rAct * 2);
                    // Nombre de la etapa actual debajo del círculo
                    String nombre = etapas[i].getDisplay();
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(AppColors.TEXTO);
                    g2.drawString(nombre, cx - fm.stringWidth(nombre) / 2, cy + rAct + fm.getAscent() + 1);
                } else {
                    g2.setColor(AppColors.HEADER_TBL);
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setColor(AppColors.TEXTO_GRIS);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                }
            }
            g2.dispose();
        }
    }
}
