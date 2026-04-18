package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Adendum;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.GastoMensual;
import com.ulatina.gestion.model.enums.CategoriaGasto;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

/** Tab 4 — Observaciones de la entrevista y gastos mensuales. */
public class TabAdendum implements TabConDatosGuardables {

  private final ExpedienteContext ctx;

  private JPanel entradaPanel;
  private JTextArea txtAdendumObs;
  private DefaultTableModel modeloGastos;
  private JTable tablaGastos;
  private JLabel lblTotalGastos;
  private JComboBox<CategoriaGasto> cmbCatGasto;
  private JTextField txtConceptoGasto;
  private JTextField txtMontoGasto;
  private JFormattedTextField txtFechaGasto;

  private GastoMensual gastoEnEdicion = null;
  private final List<GastoMensual> gastosActuales = new ArrayList<>();
  private final Paginador<GastoMensual> pagGastos = new Paginador<>();
  private final JPanel panelPagGastos = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  public TabAdendum(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
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
    txtConceptoGasto.putClientProperty(
      "JTextField.placeholderText",
      "Ej: Electricidad"
    );
    txtMontoGasto = new JTextField(8);
    txtMontoGasto.putClientProperty("JTextField.placeholderText", "Ej: 18500");
    txtFechaGasto = UIFactory.crearCampoFecha();

    for (JComponent c : new JComponent[] {
      cmbCatGasto,
      txtConceptoGasto,
      txtMontoGasto,
      txtFechaGasto,
    }) {
      c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      if (
        c instanceof JTextField || c instanceof JFormattedTextField
      ) c.setBorder(
        BorderFactory.createCompoundBorder(
          new LineBorder(AppColors.BORDE, 1, true),
          new EmptyBorder(4, 8, 4, 8)
        )
      );
      c.setPreferredSize(new Dimension(c.getPreferredSize().width, 32));
    }
    cmbCatGasto.setBorder(new LineBorder(AppColors.BORDE, 1, true));

    JButton btnAgregar = UIFactory.crearBotonDialog(
      "+ Agregar gasto",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> confirmarGasto()
    );

    entradaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    entradaPanel.setOpaque(false);
    entradaPanel.add(ctx.campoConEtiqueta("Categoría", cmbCatGasto));
    entradaPanel.add(ctx.campoConEtiqueta("Concepto", txtConceptoGasto));
    entradaPanel.add(ctx.campoConEtiqueta("Monto (₡)", txtMontoGasto));
    entradaPanel.add(ctx.campoConEtiqueta("Fecha (dd/mm/aaaa)", txtFechaGasto));
    JPanel btnWrapper = new JPanel(new BorderLayout());
    btnWrapper.setOpaque(false);
    btnWrapper.setBorder(new EmptyBorder(18, 0, 0, 0));
    btnWrapper.add(btnAgregar, BorderLayout.SOUTH);
    entradaPanel.add(btnWrapper);

    modeloGastos = new DefaultTableModel(
      new String[] { "Categoría", "Concepto", "Monto", "Fecha", "Acciones" },
      0
    ) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };

    tablaGastos = new JTable(modeloGastos);
    ctx.configurarTabla(tablaGastos);

    tablaGastos
      .getColumnModel()
      .getColumn(4)
      .setCellRenderer((tbl, val, sel, foc, row, col) -> {
        JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        cell.setOpaque(true);
        cell.setBackground(
          sel ? tbl.getSelectionBackground() : AppColors.PANEL
        );
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
    tablaGastos.getColumnModel().getColumn(4).setMaxWidth(100);
    tablaGastos.getColumnModel().getColumn(4).setMinWidth(80);

    tablaGastos.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int col = tablaGastos.columnAtPoint(e.getPoint());
          int row = tablaGastos.rowAtPoint(e.getPoint());
          if (col != 4 || row < 0 || row >= gastosActuales.size()) return;
          Rectangle rect = tablaGastos.getCellRect(row, col, false);
          int relX = e.getX() - rect.x;
          if (relX < 50) cargarGastoEnFormulario(row);
          else eliminarGasto(row);
        }
      }
    );

    JScrollPane scrollGastos = ctx.crearScrollTabla(tablaGastos);

    JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
    totalPanel.setBackground(AppColors.HEADER_TBL);
    totalPanel.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDE),
        new EmptyBorder(0, 0, 0, 8)
      )
    );
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

  public void setReadOnly(boolean readOnly) {
    if (!readOnly) return;
    if (entradaPanel != null) entradaPanel.setVisible(false);
    if (tablaGastos != null)
      tablaGastos.removeColumn(tablaGastos.getColumnModel().getColumn(4));
  }

  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    Adendum ad = ctx
      .getExpedienteController()
      .findAdendumByExpediente(exp.getId());
    ctx.setAdendumActual(ad);
    if (ad != null) {
      txtAdendumObs.setText(ctx.nvl(ad.getObservaciones()));
      recargarTablaGastos();
    }
  }

  // ─── TabConDatosGuardables ────────────────────────────────────────────────

  @Override
  public void validar() throws IllegalStateException {
    /* adendum es opcional */
  }

  @Override
  public void aplicarAlModelo() {
    String obsAd = txtAdendumObs.getText().trim();
    if (obsAd.isEmpty()) return;
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    Adendum ad = ctx.getAdendumActual();
    if (ad == null) {
      ad = new Adendum();
      ad.setExpediente(exp);
      ctx.setAdendumActual(ad);
    }
    ad.setObservaciones(obsAd);
  }

  // ─── CRUD gastos ──────────────────────────────────────────────────────────

  private void confirmarGasto() {
    String concepto = txtConceptoGasto.getText().trim();
    String montoStr = txtMontoGasto.getText().trim();
    String fechaStr = txtFechaGasto.getText().trim();

    if (concepto.isEmpty() || montoStr.isEmpty()) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Concepto y monto son obligatorios.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    BigDecimal monto;
    try {
      monto = new BigDecimal(montoStr.replace(",", "."));
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "El monto debe ser un número válido.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }

    try {
      Expediente exp = ctx.getExpediente();
      if (ctx.getAdendumActual() == null) {
        if (exp == null || exp.getId() == null) {
          JOptionPane.showMessageDialog(
            ctx.getOwner(),
            "Guarde el expediente primero antes de agregar gastos.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }
        Adendum ad = ctx
          .getExpedienteController()
          .findAdendumByExpediente(exp.getId());
        if (ad == null) {
          ad = new Adendum();
          ad.setExpediente(exp);
          ad.setObservaciones(txtAdendumObs.getText().trim());
          ctx.getExpedienteController().guardarAdendum(ad);
        }
        ctx.setAdendumActual(ad);
      }

      GastoMensual gasto = (gastoEnEdicion != null)
        ? gastoEnEdicion
        : new GastoMensual();
      gasto.setCategoria((CategoriaGasto) cmbCatGasto.getSelectedItem());
      gasto.setConcepto(concepto);
      gasto.setMonto(monto);
      gasto.setFecha(ctx.parseFecha(fechaStr));
      gasto.setAdendum(ctx.getAdendumActual());
      ctx.getExpedienteController().guardarGasto(gasto);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al guardar el gasto:" +
                "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    gastoEnEdicion = null;
    limpiarFormularioGasto();
    recargarTablaGastos();
  }

  private void eliminarGasto(int row) {
    if (row < 0 || row >= gastosActuales.size()) return;
    int confirm = JOptionPane.showConfirmDialog(
      ctx.getOwner(),
      "¿Eliminar este gasto?",
      "Confirmar",
      JOptionPane.YES_NO_OPTION
    );
    if (confirm != JOptionPane.YES_OPTION) return;
    GastoMensual g = gastosActuales.get(row);
    try {
      if (g.getId() != null) ctx
        .getExpedienteController()
        .eliminarGasto(g.getId());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    recargarTablaGastos();
  }

  private void cargarGastoEnFormulario(int row) {
    if (row < 0 || row >= gastosActuales.size()) return;
    gastoEnEdicion = gastosActuales.get(row);
    cmbCatGasto.setSelectedItem(gastoEnEdicion.getCategoria());
    txtConceptoGasto.setText(ctx.nvl(gastoEnEdicion.getConcepto()));
    txtMontoGasto.setText(
      gastoEnEdicion.getMonto() != null
        ? gastoEnEdicion.getMonto().toPlainString()
        : ""
    );
    if (gastoEnEdicion.getFecha() != null) txtFechaGasto.setText(
      ctx.getSdf().format(gastoEnEdicion.getFecha())
    );
  }

  private void limpiarFormularioGasto() {
    cmbCatGasto.setSelectedIndex(0);
    txtConceptoGasto.setText("");
    txtMontoGasto.setText("");
    try {
      txtFechaGasto.setText("");
    } catch (Exception ignored) {}
  }

  private void recargarTablaGastos() {
    Adendum ad = ctx.getAdendumActual();
    if (ad == null || ad.getId() == null) return;
    pagGastos.cargar(
      ctx.getExpedienteController().findGastosByAdendum(ad.getId())
    );
    refrescarTablaGastos();
  }

  private void refrescarTablaGastos() {
    modeloGastos.setRowCount(0);
    gastosActuales.clear();
    BigDecimal total = pagGastos
      .getTodos()
      .stream()
      .map(g -> g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    lblTotalGastos.setText(String.format("%,.0f colones", total));
    for (GastoMensual g : pagGastos.getPagina()) {
      gastosActuales.add(g);
      BigDecimal m = g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO;
      String catDisplay =
        g.getCategoria() != null
          ? g
              .getCategoria()
              .name()
              .replace("_", " ")
              .substring(0, 1)
              .toUpperCase() +
            g.getCategoria().name().replace("_", " ").substring(1).toLowerCase()
          : "—";
      modeloGastos.addRow(
        new Object[] {
          catDisplay,
          ctx.nvl(g.getConcepto()),
          String.format("%,.0f", m),
          g.getFecha() != null ? ctx.getSdf().format(g.getFecha()) : "—",
          "",
        }
      );
    }
    ctx.actualizarPanelPaginacion(
      panelPagGastos,
      pagGastos,
      this::refrescarTablaGastos
    );
  }
}
