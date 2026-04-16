package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.EventoController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Evento;
import com.ulatina.gestion.model.Parroquia;
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


public class FrmEvento extends JPanel {

    // Controller
    // Controladores que conectan la interfaz con la lógica del negocio y la base de datos
    private final EventoController eventoController = new EventoController();
    private final ParroquiaController parroquiaController = new ParroquiaController();
    // Formato de fecha que se usará para mostrar las fechas en la tabla (ej: 15/04/26)
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    // Componentes
    // Declaración de todos los componentes visuales que se usarán en el panel
    private JTextField txtBuscar;                          // Campo de texto para buscar eventos
    private JTable tabla;                                  // Tabla donde se listan los eventos
    private DefaultTableModel modeloTabla;                 // Modelo que contiene los datos de la tabla
    private TableRowSorter<DefaultTableModel> sorter;      // Permite ordenar y filtrar filas de la tabla
    private JPanel panelFiltros;                           // Panel que muestra las opciones de filtro
    private JPanel panelInfoBar;                           // Barra que aparece cuando se selecciona un evento
    private JLabel lblInfoSeleccion;                       // Etiqueta que muestra el nombre del evento seleccionado
    private JComboBox<String> cmbFiltroTipo;               // Lista desplegable para filtrar por tipo de evento
    private Evento eventoSeleccionado = null;              // Guarda el evento que el usuario tiene seleccionado

    // Constructor: se ejecuta al crear el panel, arma toda la interfaz
    public FrmEvento() {
        // BorderLayout organiza los componentes en zonas: NORTH, CENTER, SOUTH, etc.
        setLayout(new BorderLayout(0, 8));
        setBackground(AppColors.FONDO);
        // Agrega un margen interno alrededor del panel (arriba, derecha, abajo, izquierda)
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Panel que apila componentes de arriba hacia abajo (Y_AXIS)
        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setOpaque(false); // Sin fondo propio, hereda el color del padre

        // Se crea la barra de info y se oculta hasta que el usuario seleccione algo
        panelInfoBar = crearPanelInfoBar();
        panelInfoBar.setVisible(false);
        norte.add(panelInfoBar);
        norte.add(Box.createVerticalStrut(10)); // Espacio de 10px entre componentes
        norte.add(crearBarraBusqueda());
        norte.add(Box.createVerticalStrut(12));

        // El panel de filtros también se crea oculto; se muestra con el botón "Filtros"
        panelFiltros = crearPanelFiltros();
        panelFiltros.setVisible(false);

        // Se colocan los paneles en sus zonas del BorderLayout
        add(norte, BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(panelFiltros, BorderLayout.SOUTH);

        // Se cargan los datos desde la base de datos al iniciar
        cargarTabla();
    }

    // Info bar
    // Crea la barra verde que aparece en la parte superior cuando se selecciona un evento,
    // mostrando el nombre del evento y botones para editarlo o eliminarlo
    private JPanel crearPanelInfoBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(AppColors.VERDE_BG);
        // Borde redondeado con color verde claro y padding interno
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.VERDE_BORDE, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46)); // Altura fija de 46px

        lblInfoSeleccion = new JLabel("Evento seleccionado");
        lblInfoSeleccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoSeleccion.setForeground(AppColors.VERDE_FG);
        p.add(lblInfoSeleccion, BorderLayout.CENTER);

        // Panel con los botones de acción alineados a la derecha
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);
        // Al hacer clic en "Ver / Editar" se abre el formulario en modo edición (false = no es nuevo)
        btns.add(UIFactory.crearBoton("Ver / Editar", AppColors.AZUL, Color.WHITE, e -> abrirFormulario(false)));
        btns.add(UIFactory.crearBoton("Eliminar", AppColors.ROJO, Color.WHITE, e -> eliminarEvento()));
        p.add(btns, BorderLayout.EAST);
        return p;
    }

    // Barra búsqueda
    // Crea la barra superior con el campo de búsqueda y los botones "Filtros" y "+ Nuevo"
    private JPanel crearBarraBusqueda() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtBuscar = UIFactory.crearCampoBusqueda(
                "Buscar por nombre, lugar o parroquia...", this::filtrarTexto);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        // El botón "Filtros" muestra u oculta el panel de filtros (toggle)
        derecha.add(UIFactory.crearBoton("Filtros", AppColors.GRIS_BTN, AppColors.TEXTO, e -> {
            panelFiltros.setVisible(!panelFiltros.isVisible());
            revalidate(); // Recalcula el layout después del cambio de visibilidad
            repaint();
        }));
        // El botón "+ Nuevo" abre el formulario en modo creación (true = es nuevo)
        derecha.add(UIFactory.crearBoton("+ Nuevo", AppColors.PRIMARIO, Color.WHITE, e -> abrirFormulario(true)));

        p.add(txtBuscar, BorderLayout.CENTER);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // Tabla
    // Construye el panel central con la tabla de eventos y toda su configuración visual
    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.PANEL);
        p.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        // Se definen las columnas de la tabla
        String[] cols = { "ID", "Nombre", "Fecha", "Hora", "Lugar", "Tipo", "Parroquia" };
        // DefaultTableModel maneja los datos de la tabla; se sobreescribe isCellEditable
        // para que el usuario no pueda editar directamente las celdas
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);          // Altura de cada fila en píxeles
        tabla.setShowVerticalLines(false); // Sin líneas verticales para un diseño más limpio
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(AppColors.GRID_TBL);
        tabla.setSelectionBackground(AppColors.FILA_SEL);
        tabla.setSelectionForeground(AppColors.TEXTO);
        tabla.setIntercellSpacing(new Dimension(0, 0)); // Sin espacio extra entre celdas
        tabla.setFocusable(false); // Evita que la tabla reciba foco del teclado
        // Estilo del encabezado de la tabla
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
        tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
        tabla.getTableHeader().setReorderingAllowed(false); // El usuario no puede reordenar columnas

        // La columna ID se oculta visualmente poniendo su ancho en 0,
        // pero sigue existiendo en el modelo para poder recuperar el ID al seleccionar una fila
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getColumnModel().getColumn(0).setResizable(false);
        // Anchos preferidos para cada columna visible
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(65);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(160);

        // Renderer que centra el texto horizontalmente (usado para ID, Fecha y Hora)
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centrado);

        // Renderer personalizado para la columna Tipo: muestra el valor como un badge de color
        tabla.getColumnModel().getColumn(5).setCellRenderer(new TipoBadgeRenderer());

        // Renderer que agrega padding a la izquierda del texto en Nombre, Lugar y Parroquia
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

        // El sorter permite ordenar la tabla al hacer clic en los encabezados
        // y también es el que aplica los filtros de búsqueda
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        // Cuando el usuario selecciona una fila, se actualiza la barra de info
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarSeleccion();
        });
        // Doble clic sobre una fila abre el formulario de detalle en modo edición
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirFormulario(false);
            }
        });

        JScrollPane scroll = new JScrollPane(tabla); // Agrega scroll a la tabla
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.PANEL);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // Filtros
    // Crea el panel de filtros que aparece en la parte inferior al presionar "Filtros"
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

        // ComboBox con todos los tipos de evento disponibles como opciones de filtro
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

    //  Lógica de datos

    // Limpia la tabla y la vuelve a llenar con todos los eventos de la base de datos
    private void cargarTabla() {
        modeloTabla.setRowCount(0); // Elimina todas las filas existentes
        eventoSeleccionado = null;
        panelInfoBar.setVisible(false);
        try {
            List<Evento> lista = eventoController.findAll(); // Trae todos los eventos
            for (Evento ev : lista) {
                // Si algún campo es null se muestra "—" para no dejar celdas vacías
                String parroquia = ev.getParroquia() != null ? ev.getParroquia().getNombre() : "—";
                String fecha = ev.getFecha() != null ? sdf.format(ev.getFecha()) : "—";
                String hora = ev.getHora() != null
                        ? new SimpleDateFormat("HH:mm").format(ev.getHora()) : "—";
                String tipo = ev.getTipo() != null ? ev.getTipo().name() : "—";
                // Se agrega una fila por cada evento con sus datos formateados
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

    // Filtra las filas de la tabla en tiempo real según el texto escrito en el buscador.
    // Busca coincidencias en Nombre (col 1), Lugar (col 4) y Parroquia (col 6)
    private void filtrarTexto() {
        String texto = txtBuscar.getText().trim();
        // Si el campo tiene el placeholder o está vacío, se quita el filtro
        if (texto.startsWith("Buscar") || texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        // (?i) hace que la búsqueda no distinga entre mayúsculas y minúsculas
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1, 4, 6));
    }

    // Aplica el filtro seleccionado en el ComboBox de tipo de evento
    private void aplicarFiltros() {
        String tipo = (String) cmbFiltroTipo.getSelectedItem();
        List<RowFilter<DefaultTableModel, Object>> filtros = new java.util.ArrayList<>();
        // Solo filtra si se eligió un tipo específico (no "Todos los tipos")
        if (tipo != null && !tipo.startsWith("Todos"))
            // ^ y $ aseguran que el texto coincida exactamente con el tipo (no parcialmente)
            filtros.add(RowFilter.regexFilter("^" + tipo + "$", 5));
        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
    }

    // Restablece todos los filtros a su estado inicial y limpia el buscador
    private void limpiarFiltros() {
        cmbFiltroTipo.setSelectedIndex(0);
        sorter.setRowFilter(null);
        txtBuscar.setText("Buscar por nombre, lugar o parroquia...");
        txtBuscar.setForeground(AppColors.TEXTO_GRIS);
    }

    // Se ejecuta cada vez que el usuario selecciona (o deselecciona) una fila en la tabla.
    // Actualiza la barra de info con el nombre y fecha del evento seleccionado
    private void actualizarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            // Si no hay fila seleccionada, se oculta la barra de info
            eventoSeleccionado = null;
            panelInfoBar.setVisible(false);
            return;
        }
        // convertRowIndexToModel convierte el índice visual al índice real del modelo,
        // necesario porque el sorter puede cambiar el orden de las filas
        int fm = tabla.convertRowIndexToModel(fila);
        Long id = (Long) modeloTabla.getValueAt(fm, 0);       // Se obtiene el ID de la columna oculta
        String nombre = modeloTabla.getValueAt(fm, 1).toString();
        String fecha = modeloTabla.getValueAt(fm, 2).toString();
        // Se busca el objeto Evento completo en la base de datos usando el ID
        eventoSeleccionado = eventoController.findById(id);
        lblInfoSeleccion.setText("Seleccionado: " + nombre + "  (" + fecha + ")");
        panelInfoBar.setVisible(true);
        revalidate(); // Refresca el layout para que la barra aparezca correctamente
    }

    // Abre el formulario de detalle/edición de evento.
    // Si esNuevo = true, abre un formulario vacío para crear; si es false, carga el evento seleccionado
    private void abrirFormulario(boolean esNuevo) {
        Evento ev = esNuevo ? null : eventoSeleccionado;
        if (!esNuevo && ev == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this); // Ventana padre del diálogo
        // Se pasa this::cargarTabla como callback para que al guardar se refresque la tabla
        FrmDetalleEvento dlg = new FrmDetalleEvento(owner, ev, this::cargarTabla);
        dlg.setVisible(true);
    }

    // Elimina el evento seleccionado previa confirmación del usuario
    private void eliminarEvento() {
        if (eventoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = eventoSeleccionado.getNombre();
        // Se muestra un diálogo de confirmación antes de eliminar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el evento?\n" + nombre +
                        "\n\nEsta acción eliminará también las asistencias registradas.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return; // Si el usuario cancela, no hace nada
        try {
            eventoController.eliminarEvento(eventoSeleccionado.getId());
            eventoSeleccionado = null;
            panelInfoBar.setVisible(false);
            cargarTabla(); // Se recarga la tabla para reflejar el cambio
            JOptionPane.showMessageDialog(this, "Evento eliminado correctamente.",
                    "Eliminado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el evento:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Badge renderer para TipoEvento
    // Clase interna que personaliza cómo se ve la celda de la columna "Tipo".
    // En lugar de mostrar texto plano, dibuja una etiqueta (badge) con color de fondo
    private static class TipoBadgeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String tipo = value != null ? value.toString() : "";
            // Se obtienen los colores de fondo y texto según el tipo de evento
            Color bg = tipoColor(tipo, false);
            Color fg = tipoColor(tipo, true);

            // Etiqueta con el nombre del tipo (se reemplaza "_" por espacio para mejor lectura)
            JLabel lbl = new JLabel(tipo.replace("_", " "));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));

            // Panel que actúa como el "badge" con borde redondeado
            JPanel badge = new JPanel(new BorderLayout());
            badge.setBackground(bg);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(fg.brighter(), 1, true),
                    new EmptyBorder(2, 8, 2, 8)));
            badge.add(lbl);

            // Wrapper que centra el badge dentro de la celda y maneja el color de selección
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(isSelected ? AppColors.FILA_SEL : AppColors.PANEL);
            wrapper.add(badge);
            return wrapper;
        }

        // Devuelve el color correspondiente según el tipo de evento.
        // Si foreground = true devuelve el color del texto, si false devuelve el color de fondo
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
                default: // OTRO
                    return foreground ? AppColors.BADGE_FG[3] : AppColors.BADGE_BG[3];
            }
        }
    }
}