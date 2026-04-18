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
 * Orquesta las 8 pestañas del formulario, gestiona el flujo de guardado y
 * mantiene la barra de progreso de etapas sincronizada.
 * En modo nuevo las pestañas 1-7 están deshabilitadas hasta el primer guardado.
 * En modo edición precarga datos en todas las pestañas.
 * En modo consulta (setReadOnly) deshabilita toda edición recursivamente.
 */
public class FrmDetalleExpediente extends JDialog {

  /** Expediente que se está editando; puede ser reemplazado al guardar en modo nuevo. */
  private Expediente expediente;

  /** true si el diálogo fue abierto sin expediente existente (creación). */
  private final boolean esNuevo;

  /** Callback invocado tras cada guardado exitoso para actualizar la lista padre. */
  private final Runnable onGuardado;

  /** true cuando el diálogo está en modo consulta; impide edición. */
  private boolean readOnly = false;

  /** Servicio de persistencia para expedientes y entidades relacionadas. */
  private final ExpedienteController expedienteController =
    new ExpedienteController();

  /** Servicio de persistencia para parroquias. */
  private final ParroquiaController parroquiaController =
    new ParroquiaController();

  /** Contexto compartido que se pasa a todas las pestañas. */
  private final ExpedienteContext ctx;

  /** Etiqueta del encabezado con el número de ficha y nombre del titular. */
  private JLabel lblTituloPrincipal;

  /** Badge "URGENTE" visible cuando el expediente tiene ese marcador de prioridad. */
  private JLabel lblMarcadorBadge;

  /** Contenedor de las 8 pestañas. */
  private JTabbedPane tabbedPane;

  /** Barra visual de progreso de etapas; oculta en modo nuevo. */
  private BarraProgresoPanel panelBarraProgreso;

  /** Pestaña 1: datos del titular y del expediente. */
  private final TabDatos tabDatos;

  /** Pestaña 2: grupo familiar del titular. */
  private final TabFamilia tabFamilia;

  /** Pestaña 3: datos de la vivienda del titular. */
  private final TabVivienda tabVivienda;

  /** Pestaña 4: adéndum con observaciones y gastos mensuales. */
  private final TabAdendum tabAdendum;

  /** Pestaña 5: documentos adjuntos del expediente. */
  private final TabDocs tabDocs;

  /** Pestaña 6: asistencias solicitadas. */
  private final TabAsistencia tabAsistencia;

  /** Pestaña 7: entrevistas realizadas. */
  private final TabEntrevistas tabEntrevistas;

  /** Pestaña 8: prolongaciones de ayuda. */
  private final TabProlongaciones tabProlongaciones;

  /**
   * Crea el diálogo, inicializa las pestañas y carga los datos existentes si los hay.
   *
   * @param owner      ventana propietaria del diálogo
   * @param expediente expediente a editar, o null para crear uno nuevo
   * @param onGuardado callback ejecutado tras cada guardado exitoso
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

  /**
   * Inicializa el contenido del diálogo: título, fondo, layout, tabbed pane,
   * panel de encabezado y barra inferior.
   */
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

  /**
   * Construye el panel de encabezado con el título del expediente, el badge de urgencia
   * y la barra de progreso de etapas (solo visible en modo edición).
   *
   * @return JPanel con el encabezado del diálogo
   */
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

  /**
   * Crea el JTabbedPane con las 8 pestañas del expediente.
   *
   * @return JTabbedPane configurado con todas las pestañas
   */
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

  /**
   * Construye la barra inferior con el botón Cancelar a la izquierda
   * y los botones Guardar y Prolongar ayuda a la derecha.
   * El botón Prolongar ayuda solo es visible en modo edición.
   *
   * @return JPanel con la barra inferior del diálogo
   */
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

  /**
   * Carga datos en todas las pestañas.
   * En modo nuevo solo carga tabDatos (las demás pestañas están bloqueadas).
   */
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

  /**
   * Ejecuta el flujo completo de guardado:
   * 1. valida tabDatos y muestra errores si no es válido,
   * 2. comprueba duplicado de número de ficha en modo nuevo,
   * 3. carga o crea la Persona titular,
   * 4. crea el Expediente si es nuevo,
   * 5. aplica modelos de tabDatos, tabVivienda y tabAdendum,
   * 6. actualiza la etapa y persiste cambios,
   * 7. actualiza la UI y llama a onGuardado.
   */
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
        "Error al guardar el expediente:" + "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**
   * Calcula la etapa actual del expediente y la actualiza solo si avanza.
   * Nunca retrocede la etapa; la lógica es progresiva.
   */
  private void actualizarEtapaActual() {
    if (expediente == null) return;
    EtapaExpediente etapaActual = expediente.getEtapaActual();
    if (etapaActual == null) etapaActual = EtapaExpediente.REGISTRO;
    EtapaExpediente calculada = calcularEtapa();
    if (calculada.ordinal() > etapaActual.ordinal()) expediente.setEtapaActual(
      calculada
    );
  }

  /**
   * Determina la etapa del expediente consultando los datos ya persistidos.
   * La prioridad es de mayor a menor: EVALUACION (tiene entrevistas),
   * CONSENTIMIENTO (tiene doc de consentimiento), DOCUMENTOS (tiene cualquier doc),
   * GASTOS (tiene gastos en adéndum), VIVIENDA (vivienda registrada),
   * FAMILIA (tiene miembros), REGISTRO (sin datos adicionales).
   *
   * @return etapa calculada según el estado de datos del expediente
   */
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

  /**
   * Sincroniza el índice de la barra de progreso con la etapa actual del expediente.
   */
  private void actualizarBarraProgreso() {
    if (panelBarraProgreso == null || expediente == null) return;
    EtapaExpediente etapa =
      expediente.getEtapaActual() != null
        ? expediente.getEtapaActual()
        : EtapaExpediente.REGISTRO;
    panelBarraProgreso.setIdx(etapa.ordinal());
  }

  /**
   * Actualiza el título del diálogo y la etiqueta del encabezado con el número
   * de ficha y el nombre del titular actual.
   */
  private void actualizarTitulo() {
    if (expediente == null) return;
    String titulo =
      "#" + expediente.getNumeroFicha() + "  —  " + nombreTitular();
    setTitle("Expediente " + titulo);
    if (lblTituloPrincipal != null) lblTituloPrincipal.setText(titulo);
  }

  /**
   * Habilita o deshabilita las pestañas 1-7 según si el expediente ya fue persistido.
   * Las pestañas se desbloquean solo después del primer guardado exitoso.
   */
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

  /**
   * Activa el modo de solo lectura: cambia el título a "Consulta", deshabilita
   * la edición en todas las pestañas y llama a aplicarReadOnly recursivamente
   * sobre el contenido del diálogo.
   *
   * @param readOnly true para activar el modo consulta
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    if (!readOnly) return;
    setTitle(
      "Consulta Expediente #" +
        (expediente != null ? expediente.getNumeroFicha() : "")
    );
    aplicarReadOnly(getContentPane());
    tabFamilia.setReadOnly(true);
    tabAdendum.setReadOnly(true);
    tabDocs.setReadOnly(true);
    tabAsistencia.setReadOnly(true);
    tabEntrevistas.setReadOnly(true);
    tabProlongaciones.setReadOnly(true);
  }

  /**
   * Recorre recursivamente los componentes del contenedor y aplica las restricciones
   * de solo lectura: campos de texto no editables, combos y checkboxes deshabilitados,
   * botones de acción ocultos, y el botón Cancelar renombrado a Cerrar.
   *
   * @param container contenedor raíz desde el que comenzar el recorrido
   */
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
          txt.startsWith("Buscar") ||
          txt.startsWith("+") ||
          txt.equals("Limpiar") ||
          txt.equals("X") ||
          txt.equals("Editar") ||
          txt.equals("Seleccionar archivo...") ||
          txt.equals("Crear nueva")
        ) {
          btn.setVisible(false);
        } else if (txt.equals("Cancelar")) {
          btn.setText("Cerrar");
        } else if (txt.equals("Sí") || txt.equals("No")) {
          btn.setEnabled(false);
        }
      }
      if (c instanceof Container) aplicarReadOnly((Container) c);
    }
  }

  /**
   * Devuelve el nombre completo del titular del expediente actual.
   *
   * @return nombre y apellidos del titular, o cadena vacía si no están disponibles
   */
  private String nombreTitular() {
    if (expediente == null || expediente.getTitular() == null) return "";
    return (
      ctx.nvl(expediente.getTitular().getNombres()) +
      " " +
      ctx.nvl(expediente.getTitular().getApellidos())
    );
  }
}
