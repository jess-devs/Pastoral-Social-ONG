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

/** Tab 6 — Asistencias solicitadas por el expediente. */
public class TabAsistencia {

  private final ExpedienteContext ctx;

  private JPanel panelListaAsistencias;
  private JComboBox<TipoAsistencia> cmbTipoAsistencia;
  private JTextField txtModalidadAsist;
  private JTextField txtFrecuenciaAsist;
  private JTextField txtDuracionAsist;
  private JTextField txtValorAsist;
  private JButton btnConfirmarAsist;
  private AsistenciaSolicitada asistenciaEnEdicion = null;

  private final Paginador<AsistenciaSolicitada> pagAsistencias =
    new Paginador<>();
  private final JPanel panelPagAsistencias = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  public TabAsistencia(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 12));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));
    p.add(crearPanelFormAsistencia(), BorderLayout.NORTH);

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
        "Error al guardar la asistencia:" +
                "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    limpiarFormularioAsistencia();
    cargarDatos();
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
