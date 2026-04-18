package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Entrevista;
import com.ulatina.gestion.model.Expediente;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

/** Tab 7 — Entrevistas realizadas en el marco del expediente. */
public class TabEntrevistas {

  private final ExpedienteContext ctx;

  private DefaultTableModel modeloEntrevistas;
  private JFormattedTextField txtFechaEntrevista;
  private JTextField txtEntrevistadorEntrev;
  private JTextArea txtObsEntrevista;
  private JCheckBox chkRecomiendaAyuda;

  private final Paginador<Entrevista> pagEntrevistas = new Paginador<>();
  private final JPanel panelPagEntrevistas = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  public TabEntrevistas(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 12));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));

    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBackground(AppColors.PANEL);
    formPanel.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(12, 16, 12, 16)
      )
    );

    txtFechaEntrevista = UIFactory.crearCampoFecha();
    txtEntrevistadorEntrev = new JTextField();
    txtEntrevistadorEntrev.putClientProperty(
      "JTextField.placeholderText",
      "Nombre del entrevistador"
    );
    chkRecomiendaAyuda = new JCheckBox("Recomienda ayuda");
    chkRecomiendaAyuda.setBackground(AppColors.PANEL);
    chkRecomiendaAyuda.setForeground(AppColors.TEXTO);
    chkRecomiendaAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    JPanel filasCampos = new JPanel(new GridLayout(1, 2, 12, 0));
    filasCampos.setBackground(AppColors.PANEL);
    filasCampos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
    filasCampos.add(
      ctx.campoConEtiqueta("Fecha (dd/mm/aaaa) *", txtFechaEntrevista)
    );
    filasCampos.add(
      ctx.campoConEtiqueta("Entrevistador *", txtEntrevistadorEntrev)
    );

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
    JButton btnAgregar = UIFactory.crearBoton(
      "+ Agregar entrevista",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> confirmarEntrevista()
    );
    JButton btnLimpiar = UIFactory.crearBoton(
      "Limpiar",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> limpiarFormularioEntrevista()
    );
    btnRow.add(btnAgregar);
    btnRow.add(btnLimpiar);

    formPanel.add(filasCampos);
    formPanel.add(Box.createVerticalStrut(8));
    formPanel.add(
      new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
        {
          setOpaque(false);
          add(chkRecomiendaAyuda);
        }
      }
    );
    formPanel.add(Box.createVerticalStrut(6));
    formPanel.add(obsPanel);
    formPanel.add(Box.createVerticalStrut(10));
    formPanel.add(btnRow);

    modeloEntrevistas = new DefaultTableModel(
      new String[] {
        "Fecha",
        "Entrevistador",
        "Recomienda Ayuda",
        "Observaciones",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };
    JTable tablaEntrevistas = new JTable(modeloEntrevistas);
    ctx.configurarTabla(tablaEntrevistas);
    JScrollPane scrollTabla = ctx.crearScrollTabla(tablaEntrevistas);

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

  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    pagEntrevistas.cargar(
      ctx.getExpedienteController().findEntrevistasByExpediente(exp.getId())
    );
    refrescarTablaEntrevistas();

    com.ulatina.gestion.model.Usuario actual =
      com.ulatina.gestion.util.SessionContext.getUsuarioActual();
    if (
      actual != null && txtEntrevistadorEntrev != null
    ) txtEntrevistadorEntrev.setText(actual.getNombre());
  }

  private void refrescarTablaEntrevistas() {
    modeloEntrevistas.setRowCount(0);
    for (Entrevista e : pagEntrevistas.getPagina()) {
      String obs = ctx.nvl(e.getObservaciones());
      if (obs.length() > 60) obs = obs.substring(0, 57) + "...";
      modeloEntrevistas.addRow(
        new Object[] {
          e.getFecha() != null ? ctx.getSdf().format(e.getFecha()) : "—",
          ctx.nvl(e.getEntrevistador()),
          Boolean.TRUE.equals(e.getRecomiendaAyuda()) ? "Sí" : "No",
          obs,
        }
      );
    }
    ctx.actualizarPanelPaginacion(
      panelPagEntrevistas,
      pagEntrevistas,
      this::refrescarTablaEntrevistas
    );
  }

  private void confirmarEntrevista() {
    String fechaStr = txtFechaEntrevista.getText();
    String entrevistador = txtEntrevistadorEntrev.getText().trim();
    if (
      fechaStr == null || fechaStr.contains("_") || fechaStr.trim().isEmpty()
    ) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "La fecha de la entrevista es obligatoria.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    if (entrevistador.isEmpty()) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "El nombre del entrevistador es obligatorio.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Guarde el expediente primero antes de agregar entrevistas.",
        "Aviso",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    try {
      Entrevista entrevista = new Entrevista();
      entrevista.setFecha(ctx.parseFecha(fechaStr));
      entrevista.setEntrevistador(entrevistador);
      entrevista.setObservaciones(txtObsEntrevista.getText().trim());
      entrevista.setRecomiendaAyuda(chkRecomiendaAyuda.isSelected());
      entrevista.setExpediente(exp);
      ctx.getExpedienteController().guardarEntrevista(entrevista);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al guardar la entrevista:" +
                "\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }
    limpiarFormularioEntrevista();
    cargarDatos();
  }

  private void limpiarFormularioEntrevista() {
    txtFechaEntrevista.setValue(null);
    txtFechaEntrevista.setText("");
    txtEntrevistadorEntrev.setText("");
    txtObsEntrevista.setText("");
    chkRecomiendaAyuda.setSelected(false);
  }
}
