package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.BadgeRenderer;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.util.JPAUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * FrmExpedientes - Pantalla principal de gestión de expedientes.
 */
public class FrmExpedientes extends JFrame {

    // ─── Requerido por el .form de IntelliJ (binding) ───────────────────────
    private JPanel mainPanel;

    // ─── Controller ─────────────────────────────────────────────────────────
    private final ExpedienteController expedienteController = new ExpedienteController();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    // ─── Componentes UI ─────────────────────────────────────────────────────
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel panelFiltros;
    private JPanel panelInfoBar;
    private JLabel lblInfoSeleccion;
    private JButton btnVerEditar;
    private JButton btnNuevoInfo;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbEtapa;

    // Expediente actualmente seleccionado
    private Expediente expedienteSeleccionado = null;

    // ─── Constructor ────────────────────────────────────────────────────────
    public FrmExpedientes() {
        setTitle("Expedientes — Pastoral Social");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(820, 600));
        setPreferredSize(new Dimension(900, 660));
        getContentPane().setBackground(AppColors.FONDO);
        setLayout(new BorderLayout());

        JPanel contenedor = new JPanel(new BorderLayout(0, 12));
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(20, 24, 20, 24));

        contenedor.add(crearEncabezado(), BorderLayout.NORTH);
        contenedor.add(crearPanelCentral(), BorderLayout.CENTER);

        add(contenedor, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        cargarTabla();
    }

    // ─── Encabezado con título ───────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel titulo = new JLabel("Expedientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(AppColors.TEXTO);
        p.add(titulo);
        return p;
    }

    // ─── Panel central ───────────────────────────────────────────────────────
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panelInfoBar = crearPanelInfoBar();
        panelInfoBar.setVisible(false);
        panel.add(panelInfoBar);
        panel.add(Box.createVerticalStrut(10));

        panel.add(crearBarraBusqueda());
        panel.add(Box.createVerticalStrut(12));

        panel.add(crearPanelTabla());
        panel.add(Box.createVerticalStrut(14));

        panelFiltros = crearPanelFiltros();
        panelFiltros.setVisible(false);
        panel.add(panelFiltros);

        return panel;
    }

    // ─── Barra de información al seleccionar fila ────────────────────────────
    private JPanel crearPanelInfoBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(AppColors.AZUL_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.AZUL_BORDE, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        lblInfoSeleccion = new JLabel("Expediente seleccionado");
        lblInfoSeleccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoSeleccion.setForeground(AppColors.AZUL_DEEP);
        p.add(lblInfoSeleccion, BorderLayout.CENTER);

        JPanel botonesInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botonesInfo.setOpaque(false);

        btnVerEditar = UIFactory.crearBotonSmall("Ver / Editar", AppColors.AZUL, Color.WHITE);
        btnVerEditar.addActionListener(e -> abrirFormulario(false));

        btnNuevoInfo = UIFactory.crearBotonSmall("Eliminar", AppColors.ROJO, Color.WHITE);
        btnNuevoInfo.addActionListener(e -> eliminarExpediente());

        botonesInfo.add(btnVerEditar);
        botonesInfo.add(btnNuevoInfo);
        p.add(botonesInfo, BorderLayout.EAST);

        return p;
    }

    // Barra búsqueda + Filtros + Nuevo
    private JPanel crearBarraBusqueda() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtBuscar = UIFactory.crearCampoBusqueda("Buscar por nombre, cédula, ficha...", this::filtrarTexto);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);

        JButton btnFiltros = UIFactory.crearBoton("Filtros", AppColors.GRIS_BTN, AppColors.TEXTO);
        btnFiltros.addActionListener(e -> {
            panelFiltros.setVisible(!panelFiltros.isVisible());
            revalidate();
            repaint();
        });

        JButton btnNuevo = UIFactory.crearBoton("+ Nuevo", AppColors.PRIMARIO, Color.WHITE);
        btnNuevo.addActionListener(e -> abrirFormulario(true));

        derecha.add(btnFiltros);
        derecha.add(btnNuevo);

        p.add(txtBuscar, BorderLayout.CENTER);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // Panel con la tabla
    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        String[] columnas = { "Ficha", "Nombre", "Cédula", "Estado", "Etapa", "Fecha Inicio" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        UIFactory.configurarTablaExpedientes(tabla, sorter);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                actualizarSeleccion();
        });

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    abrirFormulario(false);
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.PANEL);
        scroll.setPreferredSize(new Dimension(0, 300));

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // Panel de Filtros
    private JPanel crearPanelFiltros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel titulo = new JLabel("Panel de Filtros (RF-3)");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titulo.setForeground(AppColors.TEXTO_GRIS);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titulo);
        p.add(Box.createVerticalStrut(12));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbEstado = new JComboBox<>(new String[] { "Todos los estados",
                "ACTIVO", "EN_PROCESO", "CERRADO", "SUSPENDIDO" });
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEstado.setPreferredSize(new Dimension(160, 32));

        cmbEtapa = new JComboBox<>(new String[] { "Todas las etapas",
                "REGISTRO", "FAMILIA", "VIVIENDA", "GASTOS",
                "DOCUMENTOS", "CONSENTIMIENTO", "EVALUACION", "APROBADO" });
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

        fila2.add(UIFactory.crearBoton("Aplicar", AppColors.AZUL, Color.WHITE, e -> aplicarFiltros()));
        fila2.add(UIFactory.crearBoton("Limpiar", AppColors.GRIS_BTN, AppColors.TEXTO, e -> limpiarFiltros()));
        p.add(fila2);

        return p;
    }

    // Carga de datos
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        expedienteSeleccionado = null;
        panelInfoBar.setVisible(false);

        try {
            List<Expediente> lista = expedienteController.findAll();
            for (Expediente exp : lista) {
                String nombre = exp.getTitular() != null
                        ? exp.getTitular().getNombres() + " " + exp.getTitular().getApellidos()
                        : "—";
                String cedula = exp.getTitular() != null && exp.getTitular().getNumeroDocumento() != null
                        ? exp.getTitular().getNumeroDocumento()
                        : "—";
                String estado = exp.getEstado() != null ? exp.getEstado().name() : "—";
                String etapa = exp.getEtapaActual() != null ? exp.getEtapaActual().name() : "—";
                String fecha = exp.getFechaInicio() != null ? sdf.format(exp.getFechaInicio()) : "—";

                modeloTabla.addRow(new Object[] {
                        exp.getNumeroFicha() != null ? exp.getNumeroFicha() : String.valueOf(exp.getId()),
                        nombre, cedula, estado, etapa, fecha
                });
            }
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron expedientes en la base de datos.",
                        "Sin registros", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar expedientes:\n" + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Filtrar por texto
    private void filtrarTexto() {
        String texto = txtBuscar.getText().trim();
        if (texto.startsWith("Buscar") || texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1, 2));
    }

    // Aplicar filtros combo
    private void aplicarFiltros() {
        String estado = (String) cmbEstado.getSelectedItem();
        String etapa = (String) cmbEtapa.getSelectedItem();
        List<RowFilter<DefaultTableModel, Object>> filtros = new java.util.ArrayList<>();
        if (estado != null && !estado.startsWith("Todos"))
            filtros.add(RowFilter.regexFilter("^" + estado + "₡", 3));
        if (etapa != null && !etapa.startsWith("Todas"))
            filtros.add(RowFilter.regexFilter("^" + etapa + "₡", 4));
        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    // Limpiar filtros
    private void limpiarFiltros() {
        cmbEstado.setSelectedIndex(0);
        cmbEtapa.setSelectedIndex(0);
        sorter.setRowFilter(null);
        txtBuscar.setText("Buscar por nombre, cédula, ficha...");
        txtBuscar.setForeground(AppColors.TEXTO_GRIS);
    }

    // Actualiza barra info al seleccionar fila
    private void actualizarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            expedienteSeleccionado = null;
            panelInfoBar.setVisible(false);
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        String ficha = modeloTabla.getValueAt(filaModelo, 0).toString();
        String nombre = modeloTabla.getValueAt(filaModelo, 1).toString();

        expedienteSeleccionado = expedienteController.findByNumeroFicha(ficha);

        lblInfoSeleccion.setText("Seleccionado: " + nombre + "  (Ficha " + ficha + ")");
        panelInfoBar.setVisible(true);
        revalidate();
    }

    // ─── Abre FrmDetalleExpediente ────────────────────────────────────────────
    private void abrirFormulario(boolean esNuevo) {
        Expediente exp = esNuevo ? null : expedienteSeleccionado;
        if (!esNuevo && exp == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un expediente de la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FrmDetalleExpediente dlg = new FrmDetalleExpediente(this, exp, this::cargarTabla);
        dlg.setVisible(true);
    }

    // ─── Eliminar expediente seleccionado ────────────────────────────────────
    private void eliminarExpediente() {
        if (expedienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un expediente primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = lblInfoSeleccion.getText().replaceFirst("Seleccionado: ", "");
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el expediente?\n" + nombre +
                        "\n\nEsta acción eliminará también todos los datos asociados (familia, vivienda, documentos, etc.)",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION)
            return;
        try {
            expedienteController.eliminarExpediente(expedienteSeleccionado.getId());
            expedienteSeleccionado = null;
            panelInfoBar.setVisible(false);
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Expediente eliminado correctamente.",
                    "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el expediente:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── main para prueba standalone ────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            FrmExpedientes frm = new FrmExpedientes();
            frm.setVisible(true);
            frm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    JPAUtil.close();
                }
            });
        });
    }
}
