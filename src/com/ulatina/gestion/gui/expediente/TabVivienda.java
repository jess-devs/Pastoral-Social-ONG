package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Vivienda;
import com.ulatina.gestion.model.enums.CondicionVivienda;
import com.ulatina.gestion.model.enums.TenenciaVivienda;
import com.ulatina.gestion.model.enums.TipoVivienda;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Tab 3 — Datos de la vivienda del titular. */
public class TabVivienda implements TabConDatosGuardables {

  private final ExpedienteContext ctx;

  private JTextField txtDirVivienda;
  private JComboBox<TipoVivienda> cmbTipoVivienda;
  private JComboBox<TenenciaVivienda> cmbTenencia;
  private JComboBox<CondicionVivienda> cmbCondicion;

  public TabVivienda(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
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

    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Tipo de Vivienda",
      cmbTipoVivienda,
      "Tenencia",
      cmbTenencia
    );

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
    p.add(
      new JPanel() {
        {
          setOpaque(false);
        }
      },
      sc
    );

    return p;
  }

  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    Vivienda v = ctx
      .getExpedienteController()
      .findViviendaByExpediente(exp.getId());
    ctx.setViviendaActual(v);
    if (v != null) {
      txtDirVivienda.setText(ctx.nvl(v.getDireccion()));
      if (v.getTipo() != null) cmbTipoVivienda.setSelectedItem(v.getTipo());
      if (v.getTenencia() != null) cmbTenencia.setSelectedItem(v.getTenencia());
      if (v.getCondicion() != null) cmbCondicion.setSelectedItem(
        v.getCondicion()
      );
    }
  }

  // ─── TabConDatosGuardables ────────────────────────────────────────────────

  @Override
  public void validar() throws IllegalStateException {
    /* vivienda es opcional */
  }

  @Override
  public void aplicarAlModelo() {
    String dir = txtDirVivienda.getText().trim();
    if (dir.isEmpty() && cmbTipoVivienda.getSelectedIndex() == 0) return;
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    Vivienda v = ctx.getViviendaActual();
    if (v == null) {
      v = new Vivienda();
      v.setExpediente(exp);
      ctx.setViviendaActual(v);
    }
    v.setDireccion(dir);
    v.setTipo((TipoVivienda) cmbTipoVivienda.getSelectedItem());
    v.setTenencia((TenenciaVivienda) cmbTenencia.getSelectedItem());
    v.setCondicion((CondicionVivienda) cmbCondicion.getSelectedItem());
  }

  // ─── Helpers privados ────────────────────────────────────────────────────

  private void agregarFila(
    JPanel p,
    int row,
    GridBagConstraints lc,
    GridBagConstraints fc,
    String lbl1,
    Component c1,
    String lbl2,
    Component c2
  ) {
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
}
