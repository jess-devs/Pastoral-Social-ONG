package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.AsistenciaSolicitada;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.enums.TipoAsistencia;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Pestaña 6 — Asistencias solicitadas para el expediente.
 * Muestra la lista de asistencias como paneles personalizados con badges de color
 * por tipo. Cada fila tiene botones de editar y eliminar inline.
 */
public class TabAsistencia {

  /** Contexto compartido con el resto de pestañas. */
  private final ExpedienteContext ctx;

  /** Panel del formulario de entrada de asistencias; se oculta en modo solo lectura. */
  private JPanel panelFormAsistencia;

  /** Panel vertical que contiene los paneles de cada asistencia registrada. */
  private JPanel panelListaAsistencias;

  /** Tipo de asistencia solicitada (alimentos, medicamentos, etc.). */
  private JComboBox<TipoAsistencia> cmbTipoAsistencia;

  /** Campo requerido: modalidad o forma de entrega de la asistencia (ej: "Entrega directa"). */
  private JTextField txtModalidadAsist;

  /** Frecuencia con la que se entrega la asistencia (ej: "Quincenal"). */
  private JTextField txtFrecuenciaAsist;

  /** Duración estimada de la asistencia (ej: "6 meses"). */
  private JTextField txtDuracionAsist;

  /** Valor monetario de la asistencia en colones. */
  private JTextField txtValorAsist;

  /** Botón principal del formulario; cambia de texto entre "Agregar" y "Guardar cambios". */
  private JButton btnConfirmarAsist;

  /** Asistencia que se está editando; null cuando se está creando una nueva. */
  private AsistenciaSolicitada asistenciaEnEdicion = null;

  /** Paginador de las asistencias del expediente. */
  private final Paginador<AsistenciaSolicitada> pagAsistencias =
    new Paginador<>();

  /** Panel de controles de paginación para la lista de asistencias. */
  private final JPanel panelPagAsistencias = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  /**
   * Crea la pestaña con el contexto compartido.
   *
   * @param ctx contexto compartido del diálogo
   */
  public TabAsistencia(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Construye y devuelve el panel principal de la pestaña.
   * La zona norte contiene el formulario de entrada.
   * La zona central contiene el scroll con la lista de asistencias y los controles de paginación.
   *
   * @return JPanel con la estructura completa de la pestaña
   */
  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 12));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));
    panelFormAsistencia = crearPanelFormAsistencia();
    p.add(panelFormAsistencia, BorderLayout.NORTH);

    panelListaAsistencias = new JPanel();
    panelListaAsistencias.setLayout(
      new BoxLayout(panelListaAsistencias, BoxLayout.Y_AXIS)
    );
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

  /**
   * Construye el formulario de entrada de asistencias con los campos tipo, modalidad,
   * frecuencia, duración y valor en una fila de grid, y los botones Agregar y Limpiar.
   *
   * @return JPanel con el formulario de entrada
   */
  private JPanel crearPanelFormAsistencia() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBackground(AppColors.PANEL);
    p.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(12, 16, 12, 16)
      )
    );

    cmbTipoAsistencia = new JComboBox<>(TipoAsistencia.values());
    txtModalidadAsist = new JTextField();
    txtModalidadAsist.putClientProperty(
      "JTextField.placeholderText",
      "Ej: Entrega directa"
    );
    txtFrecuenciaAsist = new JTextField();
    txtFrecuenciaAsist.putClientProperty(
      "JTextField.placeholderText",
      "Ej: Quincenal"
    );
    txtDuracionAsist = new JTextField();
    txtDuracionAsist.putClientProperty(
      "JTextField.placeholderText",
      "Ej: 6 meses"
    );
    txtValorAsist = new JTextField();
    txtValorAsist.putClientProperty("JTextField.placeholderText", "Ej: 25000");

    JPanel fila = new JPanel(new GridLayout(1, 5, 12, 0));
    fila.setBackground(AppColors.PANEL);
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
    fila.add(ctx.campoConEtiqueta("Tipo", cmbTipoAsistencia));
    fila.add(ctx.campoConEtiqueta("Modalidad *", txtModalidadAsist));
    fila.add(ctx.campoConEtiqueta("Frecuencia", txtFrecuenciaAsist));
    fila.add(ctx.campoConEtiqueta("Duración", txtDuracionAsist));
    fila.add(ctx.campoConEtiqueta("Valor (₡)", txtValorAsist));

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    btnRow.setBackground(AppColors.PANEL);
    btnConfirmarAsist = UIFactory.crearBoton(
      "+ Agregar asistencia",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> confirmarAsistencia()
    );
    JButton btnLimpiar = UIFactory.crearBoton(
      "Limpiar",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> limpiarFormularioAsistencia()
    );
    btnRow.add(btnConfirmarAsist);
    btnRow.add(btnLimpiar);

    p.add(fila);
    p.add(Box.createVerticalStrut(10));
    p.add(btnRow);
    return p;
  }

  /**
   * Activa el modo de solo lectura ocultando el formulario de entrada.
   *
   * @param readOnly true para activar el modo consulta; false no hace nada
   */
  public void setReadOnly(boolean readOnly) {
    if (!readOnly) return;
    if (panelFormAsistencia != null) panelFormAsistencia.setVisible(false);
  }

  /**
   * Carga las asistencias del expediente y refresca la lista.
   * Limpia el panel si el expediente no tiene ID todavía.
   */
  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) {
      if (panelListaAsistencias != null) {
        panelListaAsistencias.removeAll();
        panelListaAsistencias.revalidate();
        panelListaAsistencias.repaint();
      }
      return;
    }
    pagAsistencias.cargar(
      ctx.getExpedienteController().findAsistenciasByExpediente(exp.getId())
    );
    refrescarListaAsistencias();
  }

  /**
   * Repopula el panel de lista con la página actual del paginador y actualiza los controles.
   */
  private void refrescarListaAsistencias() {
    panelListaAsistencias.removeAll();
    for (AsistenciaSolicitada a : pagAsistencias.getPagina())
      panelListaAsistencias.add(crearFilaAsistencia(a));
    panelListaAsistencias.revalidate();
    panelListaAsistencias.repaint();
    ctx.actualizarPanelPaginacion(
      panelPagAsistencias,
      pagAsistencias,
      this::refrescarListaAsistencias
    );
  }

  /**
   * Valida que la modalidad no esté vacía y persiste la asistencia.
   * Requiere que el expediente esté guardado.
   * Al finalizar limpia el formulario y recarga la lista.
   */
  private void confirmarAsistencia() {
    String modalidad = txtModalidadAsist.getText().trim();
    if (modalidad.isEmpty()) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "La modalidad es obligatoria.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Guarde el expediente primero antes de agregar asistencias.",
        "Aviso",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    BigDecimal valor = BigDecimal.ZERO;
    String valorStr = txtValorAsist.getText().trim();
    if (!valorStr.isEmpty()) {
      try {
        valor = new BigDecimal(valorStr.replace(",", "."));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
          ctx.getOwner(),
          "El valor debe ser un número válido.",
          "Validación",
          JOptionPane.WARNING_MESSAGE
        );
        return;
      }
    }

    try {
      AsistenciaSolicitada a = (asistenciaEnEdicion != null)
        ? asistenciaEnEdicion
        : new AsistenciaSolicitada();
      a.setTipoAsistencia((TipoAsistencia) cmbTipoAsistencia.getSelectedItem());
      a.setModalidad(modalidad);
      a.setFrecuencia(txtFrecuenciaAsist.getText().trim());
      a.setDuracion(txtDuracionAsist.getText().trim());
      a.setValor(valor);
      a.setExpediente(exp);
      ctx.getExpedienteController().guardarAsistencia(a);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al guardar la asistencia:" + "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    limpiarFormularioAsistencia();
    cargarDatos();
  }

  /**
   * Limpia los campos del formulario y restaura el botón al texto "Agregar asistencia".
   */
  private void limpiarFormularioAsistencia() {
    asistenciaEnEdicion = null;
    cmbTipoAsistencia.setSelectedIndex(0);
    txtModalidadAsist.setText("");
    txtFrecuenciaAsist.setText("");
    txtDuracionAsist.setText("");
    txtValorAsist.setText("");
    btnConfirmarAsist.setText("Agregar asistencia");
    btnConfirmarAsist.setBackground(AppColors.PRIMARIO);
  }

  /**
   * Construye el panel visual de una asistencia con badge de tipo, modalidad,
   * metadatos (frecuencia, duración, valor) y botones de editar y eliminar.
   *
   * @param a asistencia a representar
   * @return JPanel con el diseño de tarjeta de la asistencia
   */
  private JPanel crearFilaAsistencia(AsistenciaSolicitada a) {
    JPanel fila = new JPanel(new BorderLayout(8, 0));
    fila.setBackground(AppColors.PANEL);
    fila.setBorder(
      BorderFactory.createCompoundBorder(
        new MatteBorder(0, 0, 1, 0, AppColors.BORDE),
        new EmptyBorder(10, 12, 10, 12)
      )
    );
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

    JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    izq.setBackground(AppColors.PANEL);
    izq.add(crearBadgeAsistencia(a.getTipoAsistencia()));
    JLabel lblModalidad = new JLabel(ctx.nvl(a.getModalidad()));
    lblModalidad.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblModalidad.setForeground(AppColors.TEXTO);
    izq.add(lblModalidad);

    String valorStr =
      a.getValor() != null ? String.format("₡ %,.0f", a.getValor()) : "";
    String frecStr = ctx.nvl(a.getFrecuencia());
    String durStr = ctx.nvl(a.getDuracion());
    StringBuilder meta = new StringBuilder();
    if (!frecStr.equals("—")) meta.append(frecStr);
    if (!durStr.equals("—")) {
      if (meta.length() > 0) meta.append("  ·  ");
      meta.append(durStr);
    }
    if (!valorStr.isEmpty()) {
      if (meta.length() > 0) meta.append("  ·  ");
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

    JButton btnEditar = UIFactory.crearBotonSmall(
      "Editar",
      AppColors.AZUL_PANEL,
      AppColors.AZUL,
      e -> {
        asistenciaEnEdicion = a;
        cmbTipoAsistencia.setSelectedItem(a.getTipoAsistencia());
        txtModalidadAsist.setText(
          ctx.nvl(a.getModalidad()).equals("—") ? "" : ctx.nvl(a.getModalidad())
        );
        txtFrecuenciaAsist.setText(
          ctx.nvl(a.getFrecuencia()).equals("—")
            ? ""
            : ctx.nvl(a.getFrecuencia())
        );
        txtDuracionAsist.setText(
          ctx.nvl(a.getDuracion()).equals("—") ? "" : ctx.nvl(a.getDuracion())
        );
        txtValorAsist.setText(
          a.getValor() != null ? a.getValor().toPlainString() : ""
        );
        btnConfirmarAsist.setText("Guardar cambios");
        btnConfirmarAsist.setBackground(AppColors.AZUL);
      }
    );
    JButton btnEliminar = UIFactory.crearBotonSmall(
      "X",
      AppColors.ROJO_CARD_BG,
      AppColors.ROJO,
      e -> {
        int conf = JOptionPane.showConfirmDialog(
          ctx.getOwner(),
          "¿Eliminar esta asistencia?",
          "Confirmar eliminación",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE
        );
        if (conf == JOptionPane.YES_OPTION) {
          try {
            if (a.getId() != null) ctx
              .getExpedienteController()
              .eliminarAsistencia(a.getId());
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          cargarDatos();
        }
      }
    );

    der.add(btnEditar);
    der.add(btnEliminar);
    fila.add(centro, BorderLayout.CENTER);
    fila.add(der, BorderLayout.EAST);
    return fila;
  }

  /**
   * Crea un badge de color específico según el tipo de asistencia.
   * Cada tipo tiene un par de colores de fondo y texto predefinidos.
   *
   * @param tipo tipo de asistencia; si es null se usa OTRO
   * @return JLabel badge con el nombre del tipo y los colores correspondientes
   */
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
    return ctx.crearBadge(
      tipo != null ? tipo.name().replace("_", " ") : "OTRO",
      bg,
      fg
    );
  }
}
