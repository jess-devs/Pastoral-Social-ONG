package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.controller.UsuarioController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;

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

/**
 * Formulario de Mantenimiento de Usuarios.
 * RF-10: los usuarios NO se eliminan, solo se desactivan.
 */
public class FrmMantenimientoUsuarios extends JPanel {

    /**
     * Se inicializan el controlador de parroquias para ser utilizado e ingresar
     * los metodos necesarios a ser utilizados.
     * Se llama al FrmPanel administrativo para ser utilizado en caso que el usuario
     * requiera devolverse al menu principal a realizar otras gestiones como administrador.
     */
    private final UsuarioController usuarioController = new UsuarioController();
    private final ParroquiaController parroquiaController = new ParroquiaController();
    private final FrmPanelAdministrativo frmMain;

    /**
     * Se crean las variables que se llamarán durante la creacion del resto de la vista
     */
    private static final String VISTA_LISTA = "LISTA";
    private static final String VISTA_FORM = "FORM";

    /**
     * Se crea la carta que se va a utilizar.
     * Se crea el panel principan que contendra el contenido de la vista.
     */
    private CardLayout cardLayout;
    private JPanel panelCards;

    /**
     * Se crea la tabla y el modelo que contendra los datos de las parroquias.
     */
    private JTable tabla;
    private DefaultTableModel modelo;

    /**
     * Se crean los diferentes componentes que se utilizaran para que el usuario
     * digite los datos de la parroquia nueva o que puede editar.
     */
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField pswPassword;
    private JPanel panelPassword;
    private JComboBox<RolUsuario> cmbRol;
    private JComboBox<Parroquia> cmbParroquia;
    private JCheckBox chkEstatus;
    private JLabel lblTituloForm;

    //Usuario seleccionado para interactuar.
    private Usuario usuarioEditando = null;

    /**
     * Se crea el constructor de la vista de Usuarios.
     * Utiliza las "card" que divide las partes de la vista.
     * Se utiliza el formado de encabezado ya establecido.
     * Se utiliza el metodo de carga de tabla para mostrar los datos de las parroquias.
     * Boton de volver implementado para regrear al inicio del panel de adminitracion
     */
    public FrmMantenimientoUsuarios(FrmPanelAdministrativo frmMain) {
        this.frmMain = frmMain;
        setLayout(new BorderLayout(0, 0));
        setBackground(AppColors.FONDO);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel barTop = new JPanel(new BorderLayout());
        barTop.setOpaque(false);
        barTop.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnVolver = crearBoton("Volver", AppColors.GRIS_BTN, AppColors.GRIS_BTN_H, AppColors.TEXTO);
        btnVolver.addActionListener(e -> frmMain.volverAlGrid());

        add(UIFactory.crearEncabezado("Mantenimiendo Usuarios", "Gestioná los usuarios y sus accesos al sistema."), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setOpaque(false);

        panelCards.add(crearVistaLista(), VISTA_LISTA);
        panelCards.add(crearVistaFormulario(), VISTA_FORM);

        add(panelCards, BorderLayout.CENTER);
        add(btnVolver, BorderLayout.SOUTH);

        cargarTabla();
    }

    /**
     * Se crean los espacios con los botones de acciones.
     */
    private JPanel crearVistaLista() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);

        JPanel barTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        barTop.setOpaque(false);

        //Boton desactivar
        JButton btnDesactivar = crearBoton("Desactivar Usuario", AppColors.ROJO, AppColors.ROJO_H, Color.WHITE);
        btnDesactivar.addActionListener(e -> desactivarUsuario());
        barTop.add(btnDesactivar);
        barTop.add(Box.createHorizontalStrut(8));
        //Boton Editar
        JButton btnEditar = crearBoton("Editar Usuario", AppColors.AZUL, AppColors.AZUL_BORDE, Color.WHITE);
        btnEditar.addActionListener(e -> abrirEditar());
        barTop.add(btnEditar);
        barTop.add(Box.createHorizontalStrut(8));
        //Boton Agregar
        JButton btnAgregar = crearBoton("Agregar Usuario", AppColors.PRIMARIO, AppColors.PRIMARIO_H, Color.WHITE);
        btnAgregar.addActionListener(e -> abrirAgregar());
        barTop.add(btnAgregar);

        //Posiciones de los botones dentro del panel.
        p.add(barTop, BorderLayout.NORTH);
        p.add(crearPanelTabla(), BorderLayout.CENTER);
        return p;
    }

    /**
     * Se crea el panel que contiene la tabla de parroquias
     */
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.PANEL);
        panel.setBorder(new LineBorder(AppColors.BORDE, 1, true));

        //Encabezados de la tabla.
        modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Email", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setBackground(AppColors.PANEL);
        tabla.setSelectionBackground(AppColors.FILA_SEL);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setFocusable(false);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(AppColors.HEADER_TBL);
        header.setForeground(AppColors.TEXTO_GRIS);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
        header.setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        tabla.getColumnModel().getColumn(3).setCellRenderer(new RolBadgeRenderer());
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoBadgeRenderer());

        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) abrirEditar();
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    /**
     * //     * Panel utilizado para los botones de acciones.
     */
    private JPanel crearVistaFormulario() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        JPanel barTop = new JPanel(new BorderLayout());
        barTop.setOpaque(false);
        barTop.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnVolver = crearBoton("Volver", AppColors.GRIS_BTN, AppColors.GRIS_BTN_H, AppColors.TEXTO);
        btnVolver.addActionListener(e -> mostrar(VISTA_LISTA));

        lblTituloForm = new JLabel("Agregar Usuario");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloForm.setForeground(AppColors.TEXTO);
        lblTituloForm.setBorder(new EmptyBorder(0, 14, 0, 0));

        barTop.add(btnVolver, BorderLayout.WEST);
        barTop.add(lblTituloForm, BorderLayout.CENTER);

        p.add(barTop, BorderLayout.NORTH);
        p.add(crearTarjetaFormulario(), BorderLayout.CENTER);
        return p;
    }

    /**
     * Se genera el panel con los campos del formulario
     * para agregar los datos de la parroquia.
     */
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
        g.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = crearTextField();
        txtEmail = crearTextField();
        pswPassword = new JPasswordField();
        pswPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pswPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        pswPassword.setPreferredSize(new Dimension(0, 36));

        cmbRol = new JComboBox<>(RolUsuario.values());
        cmbParroquia = new JComboBox<>();
        estilizarCombo(cmbRol);
        estilizarCombo(cmbParroquia);
        chkEstatus = new JCheckBox("Estatus Parroquia");
        chkEstatus.isSelected();

        cargarParroquias();

        chkEstatus = new JCheckBox("Usuario activo");
        chkEstatus.setSelected(true);
        chkEstatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkEstatus.setOpaque(false);

        // Fila 0: Nombre | Email
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0.5;
        grid.add(crearCampo("Nombre Completo", txtNombre), g);
        g.gridx = 1;
        grid.add(crearCampo("Email", txtEmail), g);

        // Fila 1: Rol | Parroquia
        g.gridx = 0;
        g.gridy = 1;
        grid.add(crearCampo("Rol", cmbRol), g);
        g.gridx = 1;
        grid.add(crearCampo("Parroquia", cmbParroquia), g);

        // Fila 2: Password | Estatus
        panelPassword = crearCampo("Contraseña", pswPassword);
        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0.5;
        g.gridwidth = 1;
        grid.add(panelPassword, g);
        g.gridx = 1;
        grid.add(chkEstatus, g);

        card.add(grid, BorderLayout.CENTER);
        card.add(crearBarraBotones(), BorderLayout.SOUTH);
        return card;
    }

    /**
     * Genera la barra con los botones de acciones para agregar/editar parroquias.
     */
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

    private JPanel crearCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        p.add(lbl, BorderLayout.NORTH);
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

    private void estilizarCombo(JComboBox<?> cmb) {
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setBackground(AppColors.PANEL);
        cmb.setPreferredSize(new Dimension(0, 36));
    }

    private JButton crearBoton(String texto, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g2) {
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
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    /**
     * Logica utliada dentro de la vista de parroquias
     */
    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Usuario> lista = usuarioController.findAll();
            for (Usuario u : lista) {
                modelo.addRow(new Object[]{
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getRol() != null ? u.getRol().name() : "",
                        Boolean.TRUE.equals(u.getActivo()) ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando usuarios: " + ex.getMessage());
        }
    }

    private void cargarParroquias() {
        cmbParroquia.removeAllItems();
        cmbParroquia.addItem(null);
        try {
            for (Parroquia p : parroquiaController.findAll()) {
                cmbParroquia.addItem(p);
            }
        } catch (Exception ex) {
            System.out.println("No hay parroquias registradas");
        }
    }

    private void abrirAgregar() {
        usuarioEditando = null;
        limpiarFormulario();
        txtEmail.setEditable(true);
        panelPassword.setVisible(true);
        chkEstatus.setSelected(true);
        lblTituloForm.setText("Agregar Usuario");
        mostrar(VISTA_FORM);
    }

    private void abrirEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un usuario de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) modelo.getValueAt(fila, 0);

        try {
            List<Usuario> lista = usuarioController.findAll();
            usuarioEditando = lista.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            usuarioEditando = null;
        }

        if (usuarioEditando == null) return;

        txtNombre.setText(usuarioEditando.getNombre());
        txtEmail.setText(usuarioEditando.getEmail());
        txtEmail.setEditable(false);
        cmbRol.setSelectedItem(usuarioEditando.getRol());
        chkEstatus.setSelected(Boolean.TRUE.equals(usuarioEditando.getActivo()));

        if (usuarioEditando.getParroquia() != null) {
            for (int i = 0; i < cmbParroquia.getItemCount(); i++) {
                Parroquia p = cmbParroquia.getItemAt(i);
                if (p != null && p.getId().equals(usuarioEditando.getParroquia().getId())) {
                    cmbParroquia.setSelectedIndex(i);
                    break;
                }
            }
        }

        lblTituloForm.setText("Editar Usuario");
        mostrar(VISTA_FORM);
    }

    /**
     * Llamado de campos para guardar los datos de la parroquia ingresada.
     */
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        RolUsuario rol = (RolUsuario) cmbRol.getSelectedItem();
        Parroquia parroquia = (Parroquia) cmbParroquia.getSelectedItem();
        boolean estatus = chkEstatus.isSelected();

        if (nombre.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioEditando == null) {
                String password = new String(pswPassword.getPassword()).trim();
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "La contraseña es obligatoria.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                usuarioController.saveUsuario(nombre, email, password, rol, parroquia, estatus);
            } else {
                usuarioEditando.setNombre(nombre);
                usuarioEditando.setRol(rol);
                usuarioEditando.setParroquia(parroquia);
                usuarioEditando.setActivo(estatus);
                usuarioController.editUsuario(usuarioEditando);
            }
            cargarTabla();
            mostrar(VISTA_LISTA);
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivarUsuario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccioná un usuario de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = modelo.getValueAt(fila, 1).toString();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desactivar al usuario \"" + nombre,
                "Confirmar desactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        Long id = (Long) modelo.getValueAt(fila, 0);
        try {
            List<Usuario> lista = usuarioController.findAll();
            Usuario u = lista.stream()
                    .filter(us -> us.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (u != null) {
                usuarioController.desactivatedUsuario(u);
                cargarTabla();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        pswPassword.setText("");
        if (cmbRol.getItemCount() > 0) cmbRol.setSelectedIndex(0);
        cmbParroquia.setSelectedIndex(0);
    }

    private void mostrar(String vista) {
        cardLayout.show(panelCards, vista);
    }

    static class RolBadgeRenderer extends DefaultTableCellRenderer {
        private static final java.util.Map<String, Color[]> COLORES = new java.util.HashMap<>();

        static {
            COLORES.put("ADMIN", new Color[]{AppColors.PURP_BG, AppColors.PURPURA_H});
            COLORES.put("COORDINADOR", new Color[]{AppColors.VERDE_BG, AppColors.VERDE_FG});
            COLORES.put("VOLUNTARIO", new Color[]{AppColors.ALQUILER_BG, AppColors.AMBAR_FG});
            COLORES.put("CONSULTA_VICARIAL", new Color[]{AppColors.AZUL_CARD_BG, AppColors.AZUL_DEEP});
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                                                       boolean sel, boolean foc, int r, int c) {
            String texto = v != null ? v.toString() : "";
            Color[] cols = COLORES.getOrDefault(texto, new Color[]{AppColors.GRID_TBL, AppColors.BADGE_FG[3]});
            JLabel lbl = new JLabel(texto);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(cols[0]);
            lbl.setForeground(cols[1]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrapper.setBackground(sel ? AppColors.FILA_SEL : AppColors.PANEL);
            wrapper.add(lbl);
            return wrapper;
        }
    }

    static class EstadoBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                                                       boolean sel, boolean foc, int r, int c) {
            boolean activo = "Activo".equals(v);
            JLabel lbl = new JLabel(v != null ? v.toString() : "");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(activo ? AppColors.VERDE_BG : AppColors.ROJO_CARD_BG);
            lbl.setForeground(activo ? AppColors.VERDE_FG : AppColors.ROJO_CARD_FG);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrapper.setBackground(sel ? AppColors.FILA_SEL : AppColors.PANEL);
            wrapper.add(lbl);
            return wrapper;
        }
    }
}