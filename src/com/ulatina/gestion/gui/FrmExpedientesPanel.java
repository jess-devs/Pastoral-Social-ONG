package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.gui.expediente.FrmDetalleExpediente;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.BadgeRenderer;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * FrmExpedientesPanel — versión JPanel embebible dentro del Dashboard.
 * Misma lógica que FrmExpedientes pero sin JFrame.
 */
public class FrmExpedientesPanel extends JPanel {

  // ─── Controller ─────────────────────────────────────────────────────────
  private final ExpedienteController expedienteController =
    new ExpedienteController();
  private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

  // ─── Componentes ─────────────────────────────────────────────────────────
  private JTextField txtBuscar;
  private JTable tabla;
  private DefaultTableModel modeloTabla;
  private TableRowSorter<DefaultTableModel> sorter;
  private JPanel panelFiltros;
  private JPanel panelInfoBar;
  private JLabel lblInfoSeleccion;
  private JComboBox<String> cmbEstado;
  private JComboBox<String> cmbEtapa;
  private Expediente expedienteSeleccionado = null;

  public FrmExpedientesPanel() {
    setLayout(new BorderLayout(0, 8));
    setBackground(AppColors.FONDO);
    setBorder(new EmptyBorder(20, 24, 20, 24));

    JPanel norte = new JPanel();
    norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
    norte.setOpaque(false);

    panelInfoBar = crearPanelInfoBar();
    panelInfoBar.setVisible(false);
    norte.add(panelInfoBar);
    norte.add(Box.createVerticalStrut(10));
    norte.add(crearBarraBusqueda());
    norte.add(Box.createVerticalStrut(12));

    panelFiltros = crearPanelFiltros();
    panelFiltros.setVisible(false);

    add(norte, BorderLayout.NORTH);
    add(crearPanelTabla(), BorderLayout.CENTER);
    add(panelFiltros, BorderLayout.SOUTH);

    cargarTabla();
  }

  // ─── Info bar ─────────────────────────────────────────────────────────────
  private JPanel crearPanelInfoBar() {
    JPanel p = new JPanel(new BorderLayout(10, 0));
    p.setBackground(AppColors.AZUL_PANEL);
    p.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.AZUL_BORDE, 1, true),
        new EmptyBorder(8, 14, 8, 14)
      )
    );
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

    lblInfoSeleccion = new JLabel("Expediente seleccionado");
    lblInfoSeleccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblInfoSeleccion.setForeground(AppColors.AZUL_DEEP);
    p.add(lblInfoSeleccion, BorderLayout.CENTER);

    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    btns.setOpaque(false);
    btns.add(
      UIFactory.crearBoton("Ver / Editar", AppColors.AZUL, Color.WHITE, e ->
        abrirFormulario(false)
      )
    );
    btns.add(
      UIFactory.crearBoton("Eliminar", AppColors.ROJO, Color.WHITE, e ->
        eliminarExpediente()
      )
    );
    p.add(btns, BorderLayout.EAST);
    return p;
  }

  // ─── Barra búsqueda ───────────────────────────────────────────────────────
  private JPanel crearBarraBusqueda() {
    JPanel p = new JPanel(new BorderLayout(8, 0));
    p.setOpaque(false);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

    txtBuscar = UIFactory.crearCampoBusqueda(
      "Buscar por nombre, cédula, ficha...",
      this::filtrarTexto
    );

    JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    derecha.setOpaque(false);
    derecha.add(
      UIFactory.crearBoton(
        "Filtros",
        AppColors.GRIS_BTN,
        AppColors.TEXTO,
        e -> {
          panelFiltros.setVisible(!panelFiltros.isVisible());
          revalidate();
          repaint();
        }
      )
    );
    derecha.add(
      UIFactory.crearBoton("+ Nuevo", AppColors.PRIMARIO, Color.WHITE, e ->
        abrirFormulario(true)
      )
    );

    p.add(txtBuscar, BorderLayout.CENTER);
    p.add(derecha, BorderLayout.EAST);
    return p;
  }

  // ─── Tabla ────────────────────────────────────────────────────────────────
  private JPanel crearPanelTabla() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(AppColors.PANEL);
    p.setBorder(new LineBorder(AppColors.BORDE, 1, true));

    String[] cols = {
      "Ficha",
      "Nombre",
      "Cédula",
      "Estado",
      "Etapa",
      "Fecha Inicio",
    };
    modeloTabla = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };

    tabla = new JTable(modeloTabla);
    sorter = new TableRowSorter<>(modeloTabla);
    UIFactory.configurarTablaExpedientes(tabla, sorter);

    tabla
      .getSelectionModel()
      .addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) actualizarSeleccion();
      });
    tabla.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) abrirFormulario(false);
        }
      }
    );

    JScrollPane scroll = new JScrollPane(tabla);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getViewport().setBackground(AppColors.PANEL);

    p.add(scroll, BorderLayout.CENTER);
    return p;
  }

  // ─── Filtros ──────────────────────────────────────────────────────────────
  private JPanel crearPanelFiltros() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBackground(AppColors.PANEL);
    p.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(14, 16, 14, 16)
      )
    );

    JLabel titulo = new JLabel("Panel de Filtros (RF-3)");
    titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    titulo.setForeground(AppColors.TEXTO_GRIS);
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.add(titulo);
    p.add(Box.createVerticalStrut(12));

    JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    fila1.setOpaque(false);
    fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

    cmbEstado = new JComboBox<>(
      new String[] {
        "Todos los estados",
        "ACTIVO",
        "EN_PROCESO",
        "CERRADO",
        "SUSPENDIDO",
      }
    );
    cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cmbEstado.setPreferredSize(new Dimension(160, 32));

    cmbEtapa = new JComboBox<>(
      new String[] {
        "Todas las etapas",
        "REGISTRO",
        "FAMILIA",
        "VIVIENDA",
        "GASTOS",
        "DOCUMENTOS",
        "CONSENTIMIENTO",
        "EVALUACION",
        "APROBADO",
      }
    );
    cmbEtapa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cmbEtapa.setPreferredSize(new Dimension(180, 32));

    fila1.add(new JLabel("Estado:"));
    fila1.add(cmbEstado);
    fila1.add(Box.createHorizontalStrut(8));
    fila1.add(new JLabel("Etapa:"));
    fila1.add(cmbEtapa);
    p.add(fila1);
    p.add(Box.createVerticalStrut(12));

    JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    fila2.setOpaque(false);
    fila2.setAlignmentX(Component.LEFT_ALIGNMENT);
    fila2.add(
      UIFactory.crearBoton("Aplicar", AppColors.AZUL, Color.WHITE, e ->
        aplicarFiltros()
      )
    );
    fila2.add(
      UIFactory.crearBoton("Limpiar", AppColors.GRIS_BTN, AppColors.TEXTO, e ->
        limpiarFiltros()
      )
    );
    p.add(fila2);
    return p;
  }

  // ─── Lógica de datos ──────────────────────────────────────────────────────
  private void cargarTabla() {
    modeloTabla.setRowCount(0);
    expedienteSeleccionado = null;
    panelInfoBar.setVisible(false);
    try {
      List<Expediente> lista = expedienteController.findAll();
      for (Expediente exp : lista) {
        String nombre =
          exp.getTitular() != null
            ? exp.getTitular().getNombres() +
              " " +
              exp.getTitular().getApellidos()
            : "—";
        String cedula =
          exp.getTitular() != null &&
          exp.getTitular().getNumeroDocumento() != null
            ? exp.getTitular().getNumeroDocumento()
            : "—";
        String estado = exp.getEstado() != null ? exp.getEstado().name() : "—";
        String etapa =
          exp.getEtapaActual() != null ? exp.getEtapaActual().name() : "—";
        String fecha =
          exp.getFechaInicio() != null ? sdf.format(exp.getFechaInicio()) : "—";
        modeloTabla.addRow(
          new Object[] {
            exp.getNumeroFicha() != null
              ? exp.getNumeroFicha()
              : String.valueOf(exp.getId()),
            nombre,
            cedula,
            estado,
            etapa,
            fecha,
          }
        );
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        this,
        "Error al cargar expedientes:\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void filtrarTexto() {
    String texto = txtBuscar.getText().trim();
    if (texto.startsWith("Buscar") || texto.isEmpty()) {
      sorter.setRowFilter(null);
      return;
    }
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1, 2));
  }

  private void aplicarFiltros() {
    String estado = (String) cmbEstado.getSelectedItem();
    String etapa = (String) cmbEtapa.getSelectedItem();
    List<RowFilter<DefaultTableModel, Object>> filtros =
      new java.util.ArrayList<>();
    if (estado != null && !estado.startsWith("Todos")) filtros.add(
      RowFilter.regexFilter("^" + estado + "$", 3)
    );
    if (etapa != null && !etapa.startsWith("Todas")) filtros.add(
      RowFilter.regexFilter("^" + etapa + "$", 4)
    );
    sorter.setRowFilter(
      filtros.isEmpty() ? null : RowFilter.andFilter(filtros)
    );
  }

  private void limpiarFiltros() {
    cmbEstado.setSelectedIndex(0);
    cmbEtapa.setSelectedIndex(0);
    sorter.setRowFilter(null);
    txtBuscar.setText("Buscar por nombre, cédula, ficha...");
    txtBuscar.setForeground(AppColors.TEXTO_GRIS);
  }

  private void actualizarSeleccion() {
    int fila = tabla.getSelectedRow();
    if (fila < 0) {
      expedienteSeleccionado = null;
      panelInfoBar.setVisible(false);
      return;
    }
    int fm = tabla.convertRowIndexToModel(fila);
    String ficha = modeloTabla.getValueAt(fm, 0).toString();
    String nombre = modeloTabla.getValueAt(fm, 1).toString();
    expedienteSeleccionado = expedienteController.findByNumeroFicha(ficha);
    lblInfoSeleccion.setText(
      "Seleccionado: " + nombre + "  (Ficha " + ficha + ")"
    );
    panelInfoBar.setVisible(true);
    revalidate();
  }

  private void abrirFormulario(boolean esNuevo) {
    Expediente exp = esNuevo ? null : expedienteSeleccionado;
    if (!esNuevo && exp == null) {
      JOptionPane.showMessageDialog(
        this,
        "Seleccione un expediente primero.",
        "Sin selección",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Window owner = SwingUtilities.getWindowAncestor(this);
    FrmDetalleExpediente dlg = new FrmDetalleExpediente(
      owner,
      exp,
      this::cargarTabla
    );
    dlg.setVisible(true);
  }

  private void eliminarExpediente() {
    if (expedienteSeleccionado == null) {
      JOptionPane.showMessageDialog(
        this,
        "Seleccione un expediente primero.",
        "Sin selección",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    String nombre = lblInfoSeleccion
      .getText()
      .replaceFirst("Seleccionado: ", "");
    int confirm = JOptionPane.showConfirmDialog(
      this,
      "¿Está seguro que desea eliminar el expediente?\n" +
        nombre +
        "\n\nEsta acción eliminará también todos los datos asociados (familia, vivienda, documentos, etc.)",
      "Confirmar eliminación",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE
    );
    if (confirm != JOptionPane.YES_OPTION) return;
    try {
      expedienteController.eliminarExpediente(expedienteSeleccionado.getId());
      expedienteSeleccionado = null;
      panelInfoBar.setVisible(false);
      cargarTabla();
      JOptionPane.showMessageDialog(
        this,
        "Expediente eliminado correctamente.",
        "Eliminado",
        JOptionPane.INFORMATION_MESSAGE
      );
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        this,
        "Error al eliminar el expediente:\n" + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }
}
