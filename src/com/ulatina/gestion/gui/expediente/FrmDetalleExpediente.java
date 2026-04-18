package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.enums.EtapaExpediente;
import com.ulatina.gestion.model.enums.TipoDocumentoAdjunto;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Diálogo modal para crear o editar un expediente.
 * Orquesta los 8 tabs y gestiona la persistencia.
 * En modo nuevo (expediente == null) las pestañas 1-7 están bloqueadas hasta
 * el primer guardado. En modo edición precarga todos los datos.
 */
public class FrmDetalleExpediente extends JDialog {

  private Expediente expediente;
  private final boolean esNuevo;
  private final Runnable onGuardado;
  private boolean readOnly = false;

  private final ExpedienteController expedienteController =
    new ExpedienteController();
  private final ParroquiaController parroquiaController =
    new ParroquiaController();

  private final ExpedienteContext ctx;

  private JLabel lblTituloPrincipal;
  private JLabel lblMarcadorBadge;
  private JTabbedPane tabbedPane;
  private BarraProgresoPanel panelBarraProgreso;

  private final TabDatos tabDatos;
  private final TabFamilia tabFamilia;
  private final TabVivienda tabVivienda;
  private final TabAdendum tabAdendum;
  private final TabDocs tabDocs;
  private final TabAsistencia tabAsistencia;
  private final TabEntrevistas tabEntrevistas;
  private final TabProlongaciones tabProlongaciones;

  /**
   * @param owner      ventana propietaria del diálogo.
   * @param expediente expediente a editar, o null para crear uno nuevo.
   * @param onGuardado callback ejecutado tras cada guardado exitoso.
   */
  public FrmDetalleExpediente(
    Window owner,
    Expediente expediente,
    Runnable onGuardado
  ) {
    super(owner, ModalityType.APPLICATION_MODAL);
    this.expediente = expediente;
    this.esNuevo = (expediente == null);
    this.onGuardado = onGuardado;

    ctx = new ExpedienteContext(
      expediente,
      esNuevo,
      expedienteController,
      parroquiaController,
      owner
    );

    tabDatos = new TabDatos(ctx, urgente -> {
      if (lblMarcadorBadge != null) lblMarcadorBadge.setVisible(urgente);
    });
    tabFamilia = new TabFamilia(ctx);
    tabVivienda = new TabVivienda(ctx);
    tabAdendum = new TabAdendum(ctx);
    tabDocs = new TabDocs(ctx);
    tabAsistencia = new TabAsistencia(ctx);
    tabEntrevistas = new TabEntrevistas(ctx);
    tabProlongaciones = new TabProlongaciones(ctx);

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
    setTitle(
      esNuevo
        ? "Nuevo Expediente"
        : "Expediente #" + expediente.getNumeroFicha()
    );
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

    lblTituloPrincipal = new JLabel(
      esNuevo
        ? "Nuevo Expediente"
        : "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular()
    );
    lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblTituloPrincipal.setForeground(AppColors.TEXTO);

    lblMarcadorBadge = new JLabel("  URGENTE  ") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(AppColors.ROJO);
        g2.fill(
          new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20)
        );
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

    EtapaExpediente etapaInicial = (expediente != null &&
      expediente.getEtapaActual() != null)
      ? expediente.getEtapaActual()
      : EtapaExpediente.REGISTRO;
    panelBarraProgreso = new BarraProgresoPanel(etapaInicial.ordinal());

    JPanel norte = new JPanel(new BorderLayout());
    norte.setOpaque(false);
    norte.add(lblTituloPrincipal, BorderLayout.WEST);
    norte.add(derecha, BorderLayout.EAST);

    p.add(norte, BorderLayout.NORTH);
    if (!esNuevo) p.add(panelBarraProgreso, BorderLayout.SOUTH);
    return p;
  }

  private JTabbedPane crearTabbedPane() {
    JTabbedPane tp = new JTabbedPane(
      JTabbedPane.TOP,
      JTabbedPane.SCROLL_TAB_LAYOUT
    );
    tp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tp.setBackground(AppColors.FONDO);

    tp.addTab("Datos", tabDatos.construir());
    tp.addTab("Familia", tabFamilia.construir());
    tp.addTab("Vivienda", tabVivienda.construir());
    tp.addTab("Adendum", tabAdendum.construir());
    tp.addTab("Docs", tabDocs.construir());
    tp.addTab("Asistencia", tabAsistencia.construir());
    tp.addTab("Entrevistas", tabEntrevistas.construir());
    tp.addTab("Prolongaciones", tabProlongaciones.construir());
    return tp;
  }

  private JPanel crearBarraInferior() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(AppColors.PANEL);
    p.setBorder(
      javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createMatteBorder(
          1,
          0,
          0,
          0,
          AppColors.BORDE
        ),
        new EmptyBorder(12, 24, 12, 24)
      )
    );

    JButton btnCancelar = UIFactory.crearBotonDialog(
      "Cancelar",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> dispose()
    );

    JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    derecha.setOpaque(false);
    JButton btnGuardar = UIFactory.crearBotonDialog(
      "Guardar",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> guardar()
    );
    JButton btnProlong = UIFactory.crearBotonDialog(
      "+ Prolongar ayuda",
      AppColors.PURPURA,
      Color.WHITE,
      e -> tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1)
    );
    btnProlong.setVisible(!esNuevo);
    derecha.add(btnGuardar);
    derecha.add(btnProlong);

    p.add(btnCancelar, BorderLayout.WEST);
    p.add(derecha, BorderLayout.EAST);
    return p;
  }

  // ─── Carga de datos ───────────────────────────────────────────────────────

  private void cargarDatos() {
    tabDatos.cargarDatos();
    if (esNuevo) return;

    tabVivienda.cargarDatos();
    tabAdendum.cargarDatos();
    tabFamilia.cargarDatos();
    tabDocs.cargarDatos();
    tabAsistencia.cargarDatos();
    tabEntrevistas.cargarDatos();
    tabProlongaciones.cargarDatos();
  }

  // ─── Guardado ─────────────────────────────────────────────────────────────

  private void guardar() {
    try {
      tabDatos.validar();
    } catch (IllegalStateException e) {
      JOptionPane.showMessageDialog(
        this,
        e.getMessage(),
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    try {
      String numDoc = tabDatos.getNumeroDocumento();

      if (esNuevo) {
        Expediente existente = expedienteController.findByNumeroFicha(
          expedienteController.generarNumeroFicha(numDoc)
        );
        if (existente != null) {
          JOptionPane.showMessageDialog(
            this,
            "Ya existe un expediente registrado para este número de documento." +
                    "\nFicha: " +
              existente.getNumeroFicha(),
            "Duplicado detectado",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }
      }

      Persona titular = expedienteController.findPersonaByNumeroDocumento(
        numDoc
      );
      if (titular == null) titular = new Persona();
      ctx.setTitular(titular);

      boolean wasNuevo = esNuevo || (expediente == null);
      if (wasNuevo) {
        expediente = new Expediente();
        expediente.setNumeroFicha(
          expedienteController.generarNumeroFicha(numDoc)
        );
        expediente.setEtapaActual(EtapaExpediente.REGISTRO);
        ctx.setExpediente(expediente);
      }

      tabDatos.aplicarAlModelo();

      EtapaExpediente etapaAntes = expediente.getEtapaActual();
      expedienteController.guardarTitularYExpediente(titular, expediente);
      actualizarEtapaActual();
      if (
        expediente.getEtapaActual() != etapaAntes
      ) expedienteController.guardarExpediente(expediente);

      tabVivienda.aplicarAlModelo();
      if (ctx.getViviendaActual() != null) expedienteController.guardarVivienda(
        ctx.getViviendaActual()
      );

      tabAdendum.aplicarAlModelo();
      if (ctx.getAdendumActual() != null) expedienteController.guardarAdendum(
        ctx.getAdendumActual()
      );

      actualizarTitulo();
      actualizarBarraProgreso();
      aplicarModoAcceso();
      if (onGuardado != null) onGuardado.run();

      JOptionPane.showMessageDialog(
        this,
        "Expediente guardado correctamente.",
        "Guardado",
        JOptionPane.INFORMATION_MESSAGE
      );
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
        this,
        "Error al guardar el expediente:" +
                "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  // ─── Etapas y barra de progreso ──────────────────────────────────────────

  private void actualizarEtapaActual() {
    if (expediente == null) return;
    EtapaExpediente etapaActual = expediente.getEtapaActual();
    if (etapaActual == null) etapaActual = EtapaExpediente.REGISTRO;
    EtapaExpediente calculada = calcularEtapa();
    if (calculada.ordinal() > etapaActual.ordinal()) expediente.setEtapaActual(
      calculada
    );
  }

  private EtapaExpediente calcularEtapa() {
    if (
      expediente == null || expediente.getId() == null
    ) return EtapaExpediente.REGISTRO;
    Long id = expediente.getId();

    if (
      !expedienteController.findEntrevistasByExpediente(id).isEmpty()
    ) return EtapaExpediente.EVALUACION;

    List<com.ulatina.gestion.model.DocumentoAdjunto> docs =
      expedienteController.findDocsByExpediente(id);
    boolean tieneConsentimiento = docs
      .stream()
      .anyMatch(
        d ->
          d.getTipo() != null &&
          TipoDocumentoAdjunto.CONSENTIMIENTO.equals(d.getTipo())
      );
    if (tieneConsentimiento) return EtapaExpediente.CONSENTIMIENTO;
    if (!docs.isEmpty()) return EtapaExpediente.DOCUMENTOS;

    com.ulatina.gestion.model.Adendum ad = ctx.getAdendumActual();
    if (
      ad != null &&
      !expedienteController.findGastosByAdendum(ad.getId()).isEmpty()
    ) return EtapaExpediente.GASTOS;

    if (ctx.getViviendaActual() != null) return EtapaExpediente.VIVIENDA;

    if (
      !expedienteController.findMiembrosByExpediente(id).isEmpty()
    ) return EtapaExpediente.FAMILIA;

    return EtapaExpediente.REGISTRO;
  }

  private void actualizarBarraProgreso() {
    if (panelBarraProgreso == null || expediente == null) return;
    EtapaExpediente etapa =
      expediente.getEtapaActual() != null
        ? expediente.getEtapaActual()
        : EtapaExpediente.REGISTRO;
    panelBarraProgreso.setIdx(etapa.ordinal());
  }

  // ─── Título y acceso ─────────────────────────────────────────────────────

  private void actualizarTitulo() {
    if (expediente == null) return;
    String titulo =
      "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular();
    setTitle("Expediente " + titulo);
    if (lblTituloPrincipal != null) lblTituloPrincipal.setText(titulo);
  }

  private void aplicarModoAcceso() {
    if (tabbedPane == null) return;
    boolean tieneId = (expediente != null && expediente.getId() != null);
    String tooltip = tieneId
      ? null
      : "Disponible después de guardar el expediente";
    for (int i = 1; i < tabbedPane.getTabCount(); i++) {
      tabbedPane.setEnabledAt(i, tieneId);
      tabbedPane.setToolTipTextAt(i, tooltip);
    }
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    if (!readOnly) return;
    setTitle(
      "Consulta Expediente #" +
        (expediente != null ? expediente.getNumeroFicha() : "")
    );
    aplicarReadOnly(getContentPane());
  }

  private void aplicarReadOnly(Container container) {
    for (Component c : container.getComponents()) {
      if (c instanceof JTextField) ((JTextField) c).setEditable(false);
      else if (c instanceof JFormattedTextField) (
        (JFormattedTextField) c
      ).setEditable(false);
      else if (c instanceof JTextArea) ((JTextArea) c).setEditable(false);
      else if (c instanceof JComboBox) ((JComboBox<?>) c).setEnabled(false);
      else if (c instanceof JCheckBox) ((JCheckBox) c).setEnabled(false);
      else if (c instanceof JButton) {
        JButton btn = (JButton) c;
        String txt = btn.getText() != null ? btn.getText() : "";
        if (
          txt.equals("Guardar") ||
          txt.startsWith("+ Prolong") ||
          txt.startsWith("Agregar") ||
          txt.startsWith("Eliminar") ||
          txt.startsWith("Subir") ||
          txt.startsWith("Buscar")
        ) {
          btn.setVisible(false);
        } else if (txt.equals("Cancelar")) {
          btn.setText("Cerrar");
        }
      }
      if (c instanceof Container) aplicarReadOnly((Container) c);
    }
  }

  private String nombreTitular() {
    if (expediente == null || expediente.getTitular() == null) return "";
    return (
      ctx.nvl(expediente.getTitular().getNombres()) +
      " " +
      ctx.nvl(expediente.getTitular().getApellidos())
    );
  }
}
