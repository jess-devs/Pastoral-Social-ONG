package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.ProlongacionAyuda;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.util.SessionContext;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

/** Tab 8 — Prolongaciones de ayuda registradas en el expediente. */
public class TabProlongaciones {

  private final ExpedienteContext ctx;

  private JPanel formPanel;
  private DefaultTableModel modeloProlongaciones;
  private JFormattedTextField txtFechaProlongacion;
  private JTextField txtObsProlongacion;

  private final Paginador<ProlongacionAyuda> pagProlongaciones =
    new Paginador<>();
  private final JPanel panelPagProlongaciones = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  public TabProlongaciones(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 12));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));

    formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBackground(AppColors.PANEL);
    formPanel.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(12, 16, 12, 16)
      )
    );

    txtFechaProlongacion = UIFactory.crearCampoFecha();
    txtObsProlongacion = new JTextField();
    txtObsProlongacion.putClientProperty(
      "JTextField.placeholderText",
      "Motivo o nota de la prolongación"
    );

    JPanel filas = new JPanel(new GridLayout(1, 2, 12, 0));
    filas.setBackground(AppColors.PANEL);
    filas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
    filas.add(
      ctx.campoConEtiqueta(
        "Fecha prolongación (dd/mm/aaaa) *",
        txtFechaProlongacion
      )
    );
    filas.add(ctx.campoConEtiqueta("Observaciones", txtObsProlongacion));

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    btnRow.setBackground(AppColors.PANEL);
    JButton btnConfirmar = UIFactory.crearBoton(
      "+ Registrar prolongación",
      AppColors.PURPURA,
      Color.WHITE,
      e -> confirmarProlongacion()
    );
    btnRow.add(btnConfirmar);

    formPanel.add(filas);
    formPanel.add(Box.createVerticalStrut(10));
    formPanel.add(btnRow);

    modeloProlongaciones = new DefaultTableModel(
      new String[] { "Fecha", "Observaciones", "Registrado por" },
      0
    ) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };
    JTable tablaProlongaciones = new JTable(modeloProlongaciones);
    ctx.configurarTabla(tablaProlongaciones);
    JScrollPane scrollTabla = ctx.crearScrollTabla(tablaProlongaciones);

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

  public void setReadOnly(boolean readOnly) {
    if (!readOnly) return;
    if (formPanel != null) formPanel.setVisible(false);
  }

  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (
      modeloProlongaciones == null || exp == null || exp.getId() == null
    ) return;
    pagProlongaciones.cargar(
      ctx.getExpedienteController().findProlongacionesByExpediente(exp.getId())
    );
    refrescarTablaProlongaciones();
  }

  private void refrescarTablaProlongaciones() {
    modeloProlongaciones.setRowCount(0);
    for (ProlongacionAyuda pr : pagProlongaciones.getPagina()) {
      String registradoPor = "—";
      try {
        if (pr.getRegistradoPor() != null) registradoPor = ctx.nvl(
          pr.getRegistradoPor().getNombre()
        );
      } catch (Exception ignored) {}
      modeloProlongaciones.addRow(
        new Object[] {
          pr.getFechaProlongacion() != null
            ? ctx.getSdf().format(pr.getFechaProlongacion())
            : "—",
          ctx.nvl(pr.getObservaciones()),
          registradoPor,
        }
      );
    }
    ctx.actualizarPanelPaginacion(
      panelPagProlongaciones,
      pagProlongaciones,
      this::refrescarTablaProlongaciones
    );
  }

  private void confirmarProlongacion() {
    String fechaStr = txtFechaProlongacion.getText();
    if (
      fechaStr == null || fechaStr.contains("_") || fechaStr.trim().isEmpty()
    ) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "La fecha de prolongación es obligatoria.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Guarde el expediente primero antes de registrar prolongaciones.",
        "Aviso",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    try {
      ProlongacionAyuda prol = new ProlongacionAyuda();
      prol.setFechaProlongacion(ctx.parseFecha(fechaStr));
      prol.setObservaciones(txtObsProlongacion.getText().trim());
      prol.setExpediente(exp);
      Usuario actual = SessionContext.getUsuarioActual();
      if (actual != null) prol.setRegistradoPor(actual);
      ctx.getExpedienteController().guardarProlongacion(prol);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al registrar la prolongación:\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    txtFechaProlongacion.setValue(null);
    txtFechaProlongacion.setText("");
    txtObsProlongacion.setText("");
    cargarDatos();
  }
}
