package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Adendum;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.Vivienda;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Objeto de contexto compartido entre FrmDetalleExpediente y sus pestañas.
 * Centraliza el estado mutable del expediente en edición, las referencias a los
 * servicios de persistencia y los métodos de UI reutilizables por todas las pestañas.
 */
public class ExpedienteContext {

  /** Expediente que se está creando o editando en el diálogo actual. */
  private Expediente expediente;
  /** Persona titular del expediente; puede cambiar durante el guardado. */
  private Persona titular;
  /** Vivienda actualmente cargada para el expediente; null si no se ha registrado ninguna. */
  private Vivienda viviendaActual;
  /** Adéndum actualmente cargado para el expediente; null si no existe todavía. */
  private Adendum adendumActual;

  /** Servicio de persistencia para expedientes, personas, viviendas y entidades relacionadas. */
  private final ExpedienteController expedienteController;
  /** Servicio de persistencia para parroquias, usado al poblar el combo de parroquias. */
  private final ParroquiaController parroquiaController;

  /** true si el diálogo fue abierto para crear un nuevo expediente; false si es edición. */
  private final boolean esNuevo;

  /** Formateador de fechas en formato dd/MM/yyyy compartido entre pestañas. */
  private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  /** Ventana propietaria del diálogo, usada como parent en los diálogos modales. */
  private final Window owner;

  /**
   * Crea el contexto con todos los recursos compartidos de la sesión de edición.
   * @param expediente expediente a editar, o null si es nuevo
   * @param esNuevo    true si se está creando un expediente por primera vez
   * @param ec         controlador de expedientes
   * @param pc         controlador de parroquias
   * @param owner      ventana propietaria para los diálogos modales
   */
  public ExpedienteContext(
    Expediente expediente,
    boolean esNuevo,
    ExpedienteController ec,
    ParroquiaController pc,
    Window owner
  ) {
    this.expediente = expediente;
    this.esNuevo = esNuevo;
    this.expedienteController = ec;
    this.parroquiaController = pc;
    this.owner = owner;
  }

  /**
   * Devuelve el expediente en edición.
   * @return expediente actual del contexto
   */
  public Expediente getExpediente() {
    return expediente;
  }

  /**
   * Reemplaza el expediente del contexto; usado al crear el objeto en guardar().
   * @param e nuevo expediente
   */
  public void setExpediente(Expediente e) {
    this.expediente = e;
  }

  /**
   * Devuelve la persona titular del expediente.
   * @return titular actual, o null si todavía no se ha asignado
   */
  public Persona getTitular() {
    return titular;
  }

  /**
   * Asigna la persona titular; llamado por guardar() antes de aplicarAlModelo().
   * @param p persona titular
   */
  public void setTitular(Persona p) {
    this.titular = p;
  }

  /**
   * Devuelve la vivienda cargada para este expediente.
   * @return vivienda actual, o null si no existe
   */
  public Vivienda getViviendaActual() {
    return viviendaActual;
  }

  /**
   * Actualiza la vivienda del contexto.
   * @param v vivienda a asociar al expediente
   */
  public void setViviendaActual(Vivienda v) {
    this.viviendaActual = v;
  }

  /**
   * Devuelve el adéndum cargado para este expediente.
   * @return adéndum actual, o null si no existe
   */
  public Adendum getAdendumActual() {
    return adendumActual;
  }

  /**
   * Actualiza el adéndum del contexto.
   * @param a adéndum a asociar al expediente
   */
  public void setAdendumActual(Adendum a) {
    this.adendumActual = a;
  }

  // ─── Getters de constantes ───────────────────────────────────────────────

  /**
   * Indica si el diálogo fue abierto en modo creación.
   * @return true si el expediente es nuevo en esta sesión
   */
  public boolean isEsNuevo() {
    return esNuevo;
  }

  /**
   * Devuelve el formateador de fechas dd/MM/yyyy compartido.
   * @return instancia de SimpleDateFormat con patrón dd/MM/yyyy
   */
  public SimpleDateFormat getSdf() {
    return sdf;
  }

  /**
   * Devuelve el controlador de expedientes.
   * @return instancia de ExpedienteController
   */
  public ExpedienteController getExpedienteController() {
    return expedienteController;
  }

  /**
   * Devuelve el controlador de parroquias.
   * @return instancia de ParroquiaController
   */
  public ParroquiaController getParroquiaController() {
    return parroquiaController;
  }

  /**
   * Devuelve la ventana propietaria del diálogo.
   * @return ventana propietaria para usar como parent en JOptionPane
   */
  public Window getOwner() {
    return owner;
  }

  // ─── Helpers de datos ────────────────────────────────────────────────────

  /**
   * Convierte null a cadena vacía para evitar NullPointerException al mostrar valores.
   * @param s cadena de entrada, puede ser null
   * @return la misma cadena si no es null, o "" si lo es
   */
  public String nvl(String s) {
    return s != null ? s : "";
  }

  /**
   * Parsea una cadena de texto en formato dd/MM/yyyy a Date.
   * Devuelve null si el texto está vacío, es null o contiene guiones bajos
   * (estado vacío de un MaskFormatter).
   * @param texto cadena con fecha en formato dd/MM/yyyy
   * @return Date parseada, o null si el texto no es una fecha válida
   */
  public Date parseFecha(String texto) {
    if (
      texto == null || texto.trim().isEmpty() || texto.contains("_")
    ) return null;
    try {
      return sdf.parse(texto.trim());
    } catch (ParseException ignored) {
      return null;
    }
  }

  /**
   * Crea un JScrollPane estilizado para envolver tablas del formulario.
   * Aplica borde de color BORDE y fondo PANEL al viewport.
   * @param tabla tabla a envolver en el scroll
   * @return JScrollPane configurado con el estilo visual de la aplicación
   */
  public JScrollPane crearScrollTabla(JTable tabla) {
    JScrollPane s = new JScrollPane(tabla);
    s.setBorder(new LineBorder(AppColors.BORDE, 1, true));
    s.getViewport().setBackground(AppColors.PANEL);
    return s;
  }

  /**
   * Envuelve un componente con una etiqueta gris encima usando BorderLayout.
   * @param texto  texto de la etiqueta que aparece sobre el campo
   * @param campo  componente de entrada a envolver
   * @return panel transparente con la etiqueta al norte y el campo al centro
   */
  public JPanel campoConEtiqueta(String texto, JComponent campo) {
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
   * Crea un botón que alterna entre "Sí" (fondo PRIMARIO) y "No" (fondo GRIS_BTN) en cada clic.
   * @param estadoInicial "Sí" para que empiece activo, cualquier otro valor para inactivo
   * @return botón toggle de 52x30px con lógica de alternancia incorporada
   */
  public JButton crearBtnToggle(String estadoInicial) {
    boolean[] estado = { estadoInicial.equals("Sí") };
    JButton btn = UIFactory.crearBotonSmall(
      estado[0] ? "Sí" : "No",
      estado[0] ? AppColors.PRIMARIO : AppColors.GRIS_BTN,
      estado[0] ? AppColors.PANEL : AppColors.TEXTO
    );
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

  /**
   * Sincroniza el texto y los colores de un botón toggle con un valor booleano.
   * Útil al cargar datos existentes para que el toggle refleje el estado guardado.
   * @param btn      botón toggle creado con crearBtnToggle
   * @param estadoSi true para poner el botón en estado "Sí", false para "No"
   */
  public void resetearToggle(JButton btn, boolean estadoSi) {
    btn.setText(estadoSi ? "Sí" : "No");
    btn.setBackground(estadoSi ? AppColors.PRIMARIO : AppColors.GRIS_BTN);
    btn.setForeground(estadoSi ? AppColors.PANEL : AppColors.TEXTO);
    btn.repaint();
  }

  /**
   * Aplica el estilo visual estándar a una tabla: fuente Segoe UI, sin líneas verticales,
   * altura de fila configurable y encabezado con fondo HEADER_TBL.
   * @param tabla     tabla a estilizar
   * @param rowHeight altura en píxeles para cada fila
   */
  public void configurarTabla(JTable tabla, int rowHeight) {
    tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tabla.setRowHeight(rowHeight);
    tabla.setShowVerticalLines(false);
    tabla.setGridColor(AppColors.GRID_TBL);
    tabla.setFocusable(false);
    tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
    tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
    tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
    tabla
      .getTableHeader()
      .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
  }

  /**
   * Aplica el estilo visual estándar a una tabla con altura de fila de 34px.
   * @param tabla tabla a estilizar
   * @see #configurarTabla(JTable, int)
   */
  public void configurarTabla(JTable tabla) {
    configurarTabla(tabla, 34);
  }

  /**
   * Reconstruye los controles de paginación dentro del panel dado.
   * Si el paginador no necesita paginación, deja el panel vacío.
   * Los botones llaman a refresh.run() y luego actualizan su estado habilitado.
   * @param panel   panel de destino donde se colocan los controles
   * @param pag     paginador que proporciona el estado y la navegación
   * @param refresh acción que actualiza la lista o tabla visible
   */
  public void actualizarPanelPaginacion(
    JPanel panel,
    Paginador<?> pag,
    Runnable refresh
  ) {
    panel.removeAll();
    if (pag.necesitaPaginacion()) {
      JButton btnAnt = new JButton("< Anterior");
      JButton btnSig = new JButton("Siguiente >");
      JLabel lblPag = new JLabel(pag.etiqueta());
      Font f = new Font("Segoe UI", Font.PLAIN, 11);
      btnAnt.setFont(f);
      btnSig.setFont(f);
      lblPag.setFont(f);
      lblPag.setForeground(AppColors.TEXTO_GRIS);
      btnAnt.setEnabled(pag.hayAnterior());
      btnSig.setEnabled(pag.haySiguiente());
      btnAnt.addActionListener(e -> {
        pag.anterior();
        refresh.run();
      });
      btnSig.addActionListener(e -> {
        pag.siguiente();
        refresh.run();
      });
      panel.add(btnAnt);
      panel.add(lblPag);
      panel.add(btnSig);
    }
    panel.revalidate();
    panel.repaint();
  }

  /**
   * Crea una etiqueta con fondo redondeado para mostrar categorías o estados en listas y tablas.
   * @param texto texto que aparece dentro del badge
   * @param bg    color de fondo del badge
   * @param fg    color del texto del badge
   * @return JLabel con paintComponent sobreescrito para dibujar el fondo redondeado
   */
  public JLabel crearBadge(String texto, Color bg, Color fg) {
    JLabel badge = new JLabel(" " + texto + " ") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(getBackground());
        g2.fill(
          new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12)
        );
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
}
