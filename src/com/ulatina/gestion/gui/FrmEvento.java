package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.EventoController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;
import com.ulatina.gestion.model.enums.TipoEvento;

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
 * Panel principal para la gestión de eventos.
 * Permite listar, buscar, filtrar, crear, editar y eliminar eventos.
 */
public class FrmEvento extends JPanel {

    /** Controlador de eventos (interfaz con la capa lógica y base de datos). */
    private final EventoController eventoController = new EventoController();
    /** Controlador de parroquias. */
    private final ParroquiaController parroquiaController = new ParroquiaController();
    /** Formato de fecha para mostrar valores en la tabla (ej: 15/04/26). */
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    private final Usuario usuario;

    // Componentes
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel panelFiltros;
    private JPanel panelInfoBar;
    private JLabel lblInfoSeleccion;
    private JComboBox<String> cmbFiltroTipo;
    private Evento eventoSeleccionado = null;

    /**
     * Constructor: inicializa el panel y construye la interfaz completa.
     */
    public FrmEvento(Usuario usuario) {
        this.usuario = usuario;
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

    /**
     * Crea la barra de información (verde) que aparece al seleccionar un evento.
     *
     * @return Panel configurado
     */
    private JPanel crearPanelInfoBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(AppColors.VERDE_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.VERDE_BORDE, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        lblInfoSeleccion = new JLabel("Evento seleccionado");
        lblInfoSeleccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoSeleccion.setForeground(AppColors.VERDE_FG);
        p.add(lblInfoSeleccion, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);
        JButton btnEditar = UIFactory.crearBoton("Ver / Editar", AppColors.AZUL, Color.WHITE, e -> abrirFormulario(false));
        JButton btnEliminar = UIFactory.crearBoton("Eliminar", AppColors.ROJO, Color.WHITE, e -> eliminarEvento());

        boolean esReadOnly = usuario.getRol() == RolUsuario.CONSULTA_VICARIAL || usuario.getRol() == RolUsuario.VOLUNTARIO;
        btnEditar.setEnabled(!esReadOnly);
        btnEliminar.setEnabled(!esReadOnly);
        btnEditar.setVisible(!esReadOnly);
        btnEliminar.setVisible(!esReadOnly);

        btns.add(btnEditar);
        btns.add(btnEliminar);
        p.add(btns, BorderLayout.EAST);
        return p;
    }

    /**
     * Crea la barra superior con buscador y botones de acción.
     *
     * @return Panel de búsqueda
     */
    private JPanel crearBarraBusqueda() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtBuscar = UIFactory.crearCampoBusqueda(
                "Buscar por nombre, lugar o parroquia...", this::filtrarTexto);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        derecha.add(UIFactory.crearBoton("Filtros", AppColors.GRIS_BTN, AppColors.TEXTO, e -> {
            panelFiltros.setVisible(!panelFiltros.isVisible());
            revalidate();
            repaint();
        }));
        JButton btnNuevo = UIFactory.crearBoton("+ Nuevo", AppColors.PRIMARIO, Color.WHITE, e -> abrirFormulario(true));
        boolean esReadOnly = usuario.getRol() == RolUsuario.CONSULTA_VICARIAL || usuario.getRol() == RolUsuario.VOLUNTARIO;
        btnNuevo.setEnabled(!esReadOnly);
        btnNuevo.setVisible(!esReadOnly);
        derecha.add(btnNuevo);

        p.add(txtBuscar, BorderLayout.CENTER);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    /**
     * Configura el panel central que contiene la tabla de eventos.
     *
     * @return Panel con tabla configurada
     */
    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        String[] cols = { "ID", "Nombre", "Fecha", "Hora", "Lugar", "Tipo", "Parroquia" };
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(AppColors.GRID_TBL);
        tabla.setSelectionBackground(AppColors.FILA_SEL);
        tabla.setSelectionForeground(AppColors.TEXTO);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFocusable(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getColumnModel().getColumn(0).setResizable(false);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(65);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(160);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centrado);

        tabla.getColumnModel().getColumn(5).setCellRenderer(new TipoBadgeRenderer());

        DefaultTableCellRenderer izqPad = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 4));
                return this;
            }
        };
        tabla.getColumnModel().getColumn(1).setCellRenderer(izqPad);
        tabla.getColumnModel().getColumn(4).setCellRenderer(izqPad);
        tabla.getColumnModel().getColumn(6).setCellRenderer(izqPad);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarSeleccion();
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirFormulario(false);
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.PANEL);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /**
     * Crea el panel inferior con opciones de filtrado.
     *
     * @return Panel de filtros
     */
    private JPanel crearPanelFiltros() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel titulo = new JLabel("Panel de Filtros");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titulo.setForeground(AppColors.TEXTO_GRIS);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(titulo);
        p.add(Box.createVerticalStrut(12));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila1.setOpaque(false);
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbFiltroTipo = new JComboBox<>(new String[] {
                "Todos los tipos",
                TipoEvento.REUNION.name(),
                TipoEvento.CAPACITACION.name(),
                TipoEvento.ENTREGA.name(),
                TipoEvento.CELEBRACION.name(),
                TipoEvento.OTRO.name()
        });
        cmbFiltroTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFiltroTipo.setPreferredSize(new Dimension(180, 32));

        fila1.add(new JLabel("Tipo:"));
        fila1.add(cmbFiltroTipo);
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

    /**
     * Carga todos los eventos desde la base de datos y los muestra en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        eventoSeleccionado = null;
        panelInfoBar.setVisible(false);
        try {
            List<Evento> lista = eventoController.findAll();
            for (Evento ev : lista) {
                String parroquia = ev.getParroquia() != null ? ev.getParroquia().getNombre() : "—";
                String fecha = ev.getFecha() != null ? sdf.format(ev.getFecha()) : "—";
                String hora = ev.getHora() != null
                        ? new SimpleDateFormat("HH:mm").format(ev.getHora()) : "—";
                String tipo = ev.getTipo() != null ? ev.getTipo().name() : "—";
                modeloTabla.addRow(new Object[] {
                        ev.getId(),
                        ev.getNombre() != null ? ev.getNombre() : "—",
                        fecha,
                        hora,
                        ev.getLugar() != null ? ev.getLugar() : "—",
                        tipo,
                        parroquia
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar eventos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filtra las filas de la tabla según el texto en el campo de búsqueda.
     */
    private void filtrarTexto() {
        String texto = txtBuscar.getText().trim();
        if (texto.startsWith("Buscar") || texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1, 4, 6));
    }

    /**
     * Aplica el filtro seleccionado en el combo de tipos de evento.
     */
    private void aplicarFiltros() {
        String tipo = (String) cmbFiltroTipo.getSelectedItem();
        List<RowFilter<DefaultTableModel, Object>> filtros = new java.util.ArrayList<>();
        if (tipo != null && !tipo.startsWith("Todos"))
            filtros.add(RowFilter.regexFilter("^" + tipo + "$", 5));
        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    /**
     * Limpia todos los filtros y restablece el buscador.
     */
    private void limpiarFiltros() {
        cmbFiltroTipo.setSelectedIndex(0);
        sorter.setRowFilter(null);
        txtBuscar.setText("Buscar por nombre, lugar o parroquia...");
        txtBuscar.setForeground(AppColors.TEXTO_GRIS);
    }

    /**
     * Actualiza la barra de información con los datos del evento seleccionado.
     */
    private void actualizarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            eventoSeleccionado = null;
            panelInfoBar.setVisible(false);
            return;
        }
        int fm = tabla.convertRowIndexToModel(fila);
        Long id = (Long) modeloTabla.getValueAt(fm, 0);
        String nombre = modeloTabla.getValueAt(fm, 1).toString();
        String fecha = modeloTabla.getValueAt(fm, 2).toString();
        eventoSeleccionado = eventoController.findById(id);
        lblInfoSeleccion.setText("Seleccionado: " + nombre + "  (" + fecha + ")");
        panelInfoBar.setVisible(true);
        revalidate();
    }

    /**
     * Abre el formulario de creación o edición de evento.
     * @param esNuevo true para crear nuevo, false para editar existente.
     */
    private void abrirFormulario(boolean esNuevo) {
        Evento ev = esNuevo ? null : eventoSeleccionado;
        if (!esNuevo && ev == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        FrmDetalleEvento dlg = new FrmDetalleEvento(owner, ev, usuario, this::cargarTabla);
        dlg.setVisible(true);
    }

    /**
     * Elimina el evento seleccionado tras confirmar con el usuario.
     */
    private void eliminarEvento() {
        if (eventoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = eventoSeleccionado.getNombre();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el evento?\n" + nombre +
                        "\n\nEsta acción eliminará también las asistencias registradas.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            eventoController.eliminarEvento(eventoSeleccionado.getId());
            eventoSeleccionado = null;
            panelInfoBar.setVisible(false);
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Evento eliminado correctamente.",
                    "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el evento:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renderer personalizado que muestra el tipo de evento como una insignia de color.
     */
    private static class TipoBadgeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String tipo = value != null ? value.toString() : "";
            Color bg = tipoColor(tipo, false);
            Color fg = tipoColor(tipo, true);

            JLabel lbl = new JLabel(tipo.replace("_", " "));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));

            JPanel badge = new JPanel(new BorderLayout());
            badge.setBackground(bg);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(fg.brighter(), 1, true),
                    new EmptyBorder(2, 8, 2, 8)));
            badge.add(lbl);

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(isSelected ? AppColors.FILA_SEL : AppColors.PANEL);
            wrapper.add(badge);
            return wrapper;
        }

        /**
         * Devuelve el color correspondiente al tipo de evento.
         * @param tipo Nombre del tipo
         * @param foreground true para el color del texto, false para el fondo
         * @return Color asociado
         */
        private static Color tipoColor(String tipo, boolean foreground) {
            switch (tipo) {
                case "REUNION":
                    return foreground ? AppColors.AZUL_DEEP : AppColors.AZUL_CARD_BG;
                case "CAPACITACION":
                    return foreground ? AppColors.VERDE_FG : AppColors.VERDE_BG;
                case "ENTREGA":
                    return foreground ? AppColors.AMBAR_FG : AppColors.AMBAR_BG;
                case "CELEBRACION":
                    return foreground ? AppColors.PURPURA : AppColors.PURP_BG;
                default:
                    return foreground ? AppColors.BADGE_FG[3] : AppColors.BADGE_BG[3];
            }
        }
    }
}