package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.model.Parroquia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FrmMantenimientoParroquias extends JPanel {

    // ─── Controller ──────────────────────────────────────────────────────────
    private final ParroquiaController parroquiaController = new ParroquiaController();
    private final FrmPanelAdministrativo frmMain;

    // ─── CardLayout vistas ───────────────────────────────────────────────────
    private static final String VISTA_LISTA = "LISTA";
    private static final String VISTA_FORM = "FORM";

    private CardLayout cardLayout;
    private JPanel panelCards;

    // ─── Tabla ───────────────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;

    // ─── Formulario ──────────────────────────────────────────────────────────
    private JTextField txtNombre;
    private JTextField txtVicaria;
    private JTextField txtSector;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JLabel lblTituloForm;
    private JCheckBox chkEstatus;

    private Parroquia parroquiaEditando = null;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public FrmMantenimientoParroquias(FrmPanelAdministrativo frmMain) {
        this.frmMain = frmMain;
        setLayout(new BorderLayout(0, 0));
        setBackground(AppColors.FONDO);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        add(crearEncabezado(), BorderLayout.NORTH);

        cardLayout  = new CardLayout();
        panelCards  = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        panelCards.add(crearVistaLista(), VISTA_LISTA);
        panelCards.add(crearVistaFormulario(), VISTA_FORM);


        add(panelCards, BorderLayout.CENTER);

        cargarTabla();
    }

    // ─── Encabezado ──────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titulo = new JLabel("Mantenimiento Parroquias");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(AppColors.TEXTO);

        JLabel subtitulo = new JLabel("Gestioná las parroquias que utilizan el sistema.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(AppColors.TEXTO_GRIS);

        // Barra superior: ← Volver + título dinámico
        JPanel barTop = new JPanel(new BorderLayout());
        barTop.setOpaque(false);
        barTop.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnVolver = crearBoton("Volver", AppColors.GRIS_BTN, AppColors.GRIS_BTN_H, AppColors.TEXTO);
        btnVolver.addActionListener(e -> frmMain.volverAlGrid());

        p.add(titulo,    BorderLayout.NORTH);
        p.add(subtitulo, BorderLayout.CENTER);
        p.add(btnVolver, BorderLayout.SOUTH);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VISTA LISTA
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel crearVistaLista() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);

        // Barra superior con botón "+ Nueva"
        JPanel barTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        barTop.setOpaque(false);

        JButton btnDesactivar = crearBoton("Desactivar Parroquia", AppColors.ROJO, AppColors.ROJO_H, Color.WHITE);
        btnDesactivar.addActionListener(e -> desactivarParroquia());
        barTop.add(btnDesactivar);
        barTop.add(Box.createHorizontalStrut(8));
        JButton btnEditar = crearBoton("Editar Parroquia", AppColors.AZUL, AppColors.AZUL_BORDE, Color.WHITE);
        btnEditar.addActionListener(e -> abrirEditar());
        barTop.add(btnEditar);
        barTop.add(Box.createHorizontalStrut(8));
        JButton btnAgregar = crearBoton("Agregar Parroquia", AppColors.PRIMARIO, AppColors.PRIMARIO_H, Color.WHITE);
        btnAgregar.addActionListener(e -> abrirAgregar());
        barTop.add(btnAgregar);

        p.add(barTop, BorderLayout.NORTH);
        p.add(crearPanelTabla(), BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(0, 0, 0, 0)));

        modelo = new DefaultTableModel(
                new String[]{"ID","Nombre", "Vicaria", "Sector / Filial", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setBackground(AppColors.PANEL);
        tabla.setSelectionBackground(AppColors.FILA_SEL);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setFocusable(false);

        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(AppColors.HEADER_TBL);
        header.setForeground(AppColors.TEXTO_GRIS);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
        header.setReorderingAllowed(false);

        // Centrar columnas Estado
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoBadgeRenderer());

        // Anchos de columna
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);

        // Doble clic → editar
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirEditar();
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VISTA FORMULARIO
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel crearVistaFormulario() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        // Barra superior: ← Volver + título dinámico
        JPanel barTop = new JPanel(new BorderLayout());
        barTop.setOpaque(false);
        barTop.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnVolver = crearBoton("Volver", AppColors.GRIS_BTN, AppColors.GRIS_BTN_H, AppColors.TEXTO);
        btnVolver.addActionListener(e -> mostrar(VISTA_LISTA));

        lblTituloForm = new JLabel("Nueva Parroquia");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloForm.setForeground(AppColors.TEXTO);
        lblTituloForm.setBorder(new EmptyBorder(0, 14, 0, 0));

        barTop.add(btnVolver,     BorderLayout.WEST);
        barTop.add(lblTituloForm, BorderLayout.CENTER);

        p.add(barTop, BorderLayout.NORTH);
        p.add(crearTarjetaFormulario(), BorderLayout.CENTER);
        return p;
    }

    private JPanel crearTarjetaFormulario() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppColors.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(24, 28, 24, 28)));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 6, 8, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        txtNombre   = crearTextField();
        txtVicaria  = crearTextField();
        txtSector   = crearTextField();
        txtDireccion = crearTextField();
        txtTelefono  = crearTextField();

        chkEstatus = new JCheckBox("Estatus Parroquia");
        chkEstatus.isSelected();
        chkEstatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Fila 0: Nombre | Vicaria | Estatus
        g.gridx = 0; g.gridy = 0; g.weightx = 0.5;
        grid.add(crearCampo("Nombre", txtNombre), g);
        g.gridx = 1;
        grid.add(crearCampo("Vicaria", txtVicaria), g);
        g.gridx = 2;
        grid.add(chkEstatus = new JCheckBox(), g);

        // Fila 1: Sector/Filial | Dirección | Teléfono
        g.gridx = 0; g.gridy = 1; g.weightx = 0.33;
        grid.add(crearCampo("Sector / Filial", txtSector), g);
        g.gridx = 1;
        grid.add(crearCampo("Dirección", txtDireccion), g);
        g.gridx = 2;
        grid.add(crearCampo("Teléfono", txtTelefono), g);

        card.add(grid, BorderLayout.CENTER);
        card.add(crearBarraBotones(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearBarraBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        p.setOpaque(false);

        JButton btnCancelar = crearBoton("Cancelar", AppColors.GRIS_BTN, AppColors.GRIS_BTN_H, AppColors.TEXTO);
        btnCancelar.addActionListener(e -> mostrar(VISTA_LISTA));

        JButton btnGuardar = crearBoton("Guardar", AppColors.PRIMARIO, AppColors.PRIMARIO_H, Color.WHITE);
        btnGuardar.addActionListener(e -> guardar());

        p.add(btnCancelar);
        p.add(btnGuardar);
        return p;
    }

    // ─── Helpers de UI ───────────────────────────────────────────────────────
    private JPanel crearCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        p.add(lbl,   BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        tf.setPreferredSize(new Dimension(0, 36));
        return tf;
    }

    private JButton crearBoton(String texto, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g.dispose();
                super.paintComponent(g2);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg);    }
        });
        return btn;
    }

    // ─── Lógica ──────────────────────────────────────────────────────────────
    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Parroquia> lista = parroquiaController.findAll();
            for (Parroquia p : lista) {
                modelo.addRow(new Object[]{
                        p.getId(),
                        p.getNombre(),
                        p.getSectorFilial(),
                        p.getVicaria(),
                        Boolean.TRUE.equals(p.getActiva()) ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando parroquias: " + ex.getMessage());
        }
    }

    private void desactivarParroquia(){
        System.out.println("Pendiente logica para desactivar Parroquia desde el main");
    }

    private void abrirAgregar() {
        parroquiaEditando = null;
        limpiarFormulario();
        lblTituloForm.setText("Registrar nueva parroquia");
        mostrar(VISTA_FORM);
    }

    private void abrirEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        Long id = (Long) modelo.getValueAt(fila, 0);
        // Buscar la parroquia en el controller
        try {
            List<Parroquia> lista = parroquiaController.findAll();
            parroquiaEditando = lista.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            parroquiaEditando = null;
        }

        if (parroquiaEditando != null) {
            txtNombre.setText(parroquiaEditando.getNombre());
            txtVicaria.setText(parroquiaEditando.getVicaria() != null ? parroquiaEditando.getVicaria() : "");
            txtSector.setText(parroquiaEditando.getSectorFilial() != null ? parroquiaEditando.getSectorFilial() : "");
            txtDireccion.setText(parroquiaEditando.getDireccion() != null ? parroquiaEditando.getDireccion() : "");
            txtTelefono.setText(parroquiaEditando.getTelefono() != null ? parroquiaEditando.getTelefono() : "");
        }

        lblTituloForm.setText("Editar Parroquia");
        mostrar(VISTA_FORM);
    }

    private void guardar() {
        String nombre  = txtNombre.getText().trim();
        String vicaria = txtVicaria.getText().trim();
        String sectorFilial = txtSector.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();
        Boolean estatus = chkEstatus.isSelected();

        if (nombre.isEmpty() || vicaria.isEmpty() || sectorFilial.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son requeridos.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (parroquiaEditando == null) {
                // Nueva parroquia
                Parroquia p = new Parroquia();
                p.setNombre(nombre);
                p.setVicaria(vicaria);
                p.setSectorFilial(txtSector.getText().trim());
                p.setDireccion(txtDireccion.getText().trim());
                p.setTelefono(txtTelefono.getText().trim());
                p.setActiva(chkEstatus.isSelected());
                parroquiaController.saveParroquia(nombre, sectorFilial, vicaria, direccion, telefono, estatus);
            } else {
                // Edición
                parroquiaEditando.setNombre(nombre);
                parroquiaEditando.setVicaria(vicaria);
                parroquiaEditando.setSectorFilial(txtSector.getText().trim());
                parroquiaEditando.setDireccion(txtDireccion.getText().trim());
                parroquiaEditando.setTelefono(txtTelefono.getText().trim());
                parroquiaEditando.setActiva(chkEstatus.isSelected());
                parroquiaController.editParroquias(parroquiaEditando);
            }
            cargarTabla();
            mostrar(VISTA_LISTA);
            JOptionPane.showMessageDialog(this, "Parroquia guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtVicaria.setText("");
        txtSector.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
    }

    private void mostrar(String vista) {
        cardLayout.show(panelCards, vista);
    }

    // ─── Renderers personalizados ─────────────────────────────────────────────
    static class EstadoBadgeRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                                                                 boolean sel, boolean foc, int r, int c) {
            JLabel lbl = new JLabel(v != null ? v.toString() : "");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            boolean activa = "Activa".equals(v);
            lbl.setBackground(activa ? new Color(0xDCFCE7) : new Color(0xFEE2E2));
            lbl.setForeground(activa ? new Color(0x166534) : new Color(0x991B1B));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrapper.setBackground(sel ? AppColors.FILA_SEL : AppColors.PANEL);
            wrapper.add(lbl);
            return wrapper;
        }
    }
}