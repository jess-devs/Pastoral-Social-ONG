package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.controller.UsuarioController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.RolUsuario;
import com.ulatina.gestion.util.JPAUtil;
import com.ulatina.gestion.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * FrmLogin — Login, Registro y Recuperar contraseña.
 */
public class FrmLogin extends JFrame {

    private static final int CARD_W = 400;

    private final UsuarioController   usuarioCtrl   = new UsuarioController();
    private final ParroquiaController parroquiaCtrl = new ParroquiaController();

    private static final String VISTA_LOGIN     = "LOGIN";
    private static final String VISTA_REGISTRO  = "REGISTRO";
    private static final String VISTA_RECUPERAR = "RECUPERAR";

    private CardLayout cardLayout;
    private JPanel     panelCards;

    // Campos login
    private JTextField     txtLoginEmail;
    private JPasswordField txtLoginPassword;

    // Campos registro
    private JTextField     txtRegNombre;
    private JTextField     txtRegEmail;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegConfirm;
    private JComboBox<RolUsuario> cmbRegRol;
    private JComboBox<Parroquia>  cmbRegParroquia;

    // Campos recuperar
    private JTextField     txtRecEmail;
    private JPasswordField txtRecPassword;
    private JPasswordField txtRecConfirm;

    public FrmLogin() {
        setTitle("Pastoral Social");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                JPAUtil.close();
                dispose();
                System.exit(0);
            }
        });

        add(crearCard(), BorderLayout.CENTER);
        setSize(440, 480);
        setLocationRelativeTo(null);
    }

    // ─── Card ────────────────────────────────────────────────────────────────
    private JPanel crearCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppColors.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDE, 1),
                new EmptyBorder(32, 36, 32, 36)));
        // ancho fijo; alto lo controla setSize() por vista

        card.add(crearEncabezado(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelCards = new JPanel(cardLayout);
        panelCards.setBackground(AppColors.PANEL);
        panelCards.add(crearPanelLogin(),     VISTA_LOGIN);
        panelCards.add(crearPanelRegistro(),  VISTA_REGISTRO);
        panelCards.add(crearPanelRecuperar(), VISTA_RECUPERAR);
        card.add(panelCards, BorderLayout.CENTER);

        return card;
    }

    // ─── Encabezado ──────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel barra = new JPanel();
        barra.setPreferredSize(new Dimension(40, 4));
        barra.setMaximumSize(new Dimension(40, 4));
        barra.setBackground(AppColors.PRIMARIO);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(barra);

        p.add(Box.createVerticalStrut(12));

        JLabel lblTitulo = new JLabel("Pastoral Social");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(AppColors.TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblTitulo);

        return p;
    }

    // ─── Panel Login ─────────────────────────────────────────────────────────
    private JPanel crearPanelLogin() {
        JPanel p = panelBase();

        p.add(subtitulo("Iniciar sesión"));
        p.add(vspace(20));

        txtLoginEmail    = campo();
        txtLoginPassword = campoPass();

        p.add(etiqueta("Correo electrónico"));
        p.add(vspace(4));
        p.add(txtLoginEmail);
        p.add(vspace(14));
        p.add(etiqueta("Contraseña"));
        p.add(vspace(4));
        p.add(txtLoginPassword);
        p.add(vspace(22));

        JButton btnIngresar = UIFactory.crearBoton("Ingresar", AppColors.PRIMARIO, Color.WHITE,
                e -> accionLogin());
        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(btnIngresar);

        p.add(vspace(16));
        p.add(separador());
        p.add(vspace(14));

        p.add(linkBtn("Crear una cuenta nueva", e -> mostrar(VISTA_REGISTRO)));
        p.add(vspace(6));
        p.add(linkBtn("Olvidé mi contraseña", e -> mostrar(VISTA_RECUPERAR)));

        return p;
    }

    // ─── Panel Registro ──────────────────────────────────────────────────────
    private JPanel crearPanelRegistro() {
        JPanel p = panelBase();

        p.add(subtitulo("Crear cuenta"));
        p.add(vspace(20));

        txtRegNombre   = campo();
        txtRegEmail    = campo();
        txtRegPassword = campoPass();
        txtRegConfirm  = campoPass();
        cmbRegRol       = new JComboBox<>(RolUsuario.values());
        cmbRegParroquia = new JComboBox<>();
        estilizarCombo(cmbRegRol);
        estilizarCombo(cmbRegParroquia);
        cmbRegParroquia.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof Parroquia ? ((Parroquia) value).getNombre() : "— Sin parroquia —");
                return this;
            }
        });
        cargarParroquias();

        p.add(etiqueta("Nombre completo"));
        p.add(vspace(4));
        p.add(txtRegNombre);
        p.add(vspace(12));

        p.add(etiqueta("Correo electrónico"));
        p.add(vspace(4));
        p.add(txtRegEmail);
        p.add(vspace(12));

        p.add(etiqueta("Contraseña"));
        p.add(vspace(4));
        p.add(txtRegPassword);
        p.add(vspace(12));

        p.add(etiqueta("Confirmar contraseña"));
        p.add(vspace(4));
        p.add(txtRegConfirm);
        p.add(vspace(12));

        p.add(etiqueta("Rol"));
        p.add(vspace(4));
        p.add(cmbRegRol);
        p.add(vspace(12));

        p.add(etiqueta("Parroquia"));
        p.add(vspace(4));
        p.add(cmbRegParroquia);
        p.add(vspace(22));

        JButton btnCrear = UIFactory.crearBoton("Crear cuenta", AppColors.PRIMARIO, Color.WHITE,
                e -> accionRegistrar());
        btnCrear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(btnCrear);

        p.add(vspace(14));
        p.add(separador());
        p.add(vspace(12));
        p.add(linkBtn("Volver al inicio de sesion", e -> mostrar(VISTA_LOGIN)));

        return p;
    }

    // ─── Panel Recuperar ─────────────────────────────────────────────────────
    private JPanel crearPanelRecuperar() {
        JPanel p = panelBase();

        p.add(subtitulo("Recuperar contrasena"));
        p.add(vspace(8));

        JLabel info = new JLabel("Ingrese su correo y defina una nueva contrasena.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(AppColors.TEXTO_GRIS);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(info);
        p.add(vspace(18));

        txtRecEmail    = campo();
        txtRecPassword = campoPass();
        txtRecConfirm  = campoPass();

        p.add(etiqueta("Correo registrado"));
        p.add(vspace(4));
        p.add(txtRecEmail);
        p.add(vspace(12));

        p.add(etiqueta("Nueva contrasena"));
        p.add(vspace(4));
        p.add(txtRecPassword);
        p.add(vspace(12));

        p.add(etiqueta("Confirmar contrasena"));
        p.add(vspace(4));
        p.add(txtRecConfirm);
        p.add(vspace(22));

        JButton btnReset = UIFactory.crearBoton("Restablecer contrasena", AppColors.PRIMARIO, Color.WHITE,
                e -> accionRecuperar());
        btnReset.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(btnReset);

        p.add(vspace(14));
        p.add(separador());
        p.add(vspace(12));
        p.add(linkBtn("Volver al inicio de sesion", e -> mostrar(VISTA_LOGIN)));

        return p;
    }

    // ─── Acciones ────────────────────────────────────────────────────────────
    private void accionLogin() {
        String email = txtLoginEmail.getText().trim();
        String pass  = new String(txtLoginPassword.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            error("Complete todos los campos.");
            return;
        }

        Usuario u = usuarioCtrl.login(email, pass);
        if (u == null) {
            error("Correo o contrasena incorrectos, o usuario inactivo.");
            txtLoginPassword.setText("");
            return;
        }


        SwingUtilities.invokeLater(() -> {
            if (u.getRol() == RolUsuario.CONSULTA_VICARIAL) {
                new FrmConsultaVicarial(u).setVisible(true);
            } else {
                new FrmDashboard(u).setVisible(true);
            }
            dispose();
        });
    }

    private void accionRegistrar() {
        String nombre  = txtRegNombre.getText().trim();
        String email   = txtRegEmail.getText().trim();
        String pass    = new String(txtRegPassword.getPassword());
        String confirm = new String(txtRegConfirm.getPassword());

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            error("Complete todos los campos obligatorios.");
            return;
        }
        if (!pass.equals(confirm)) {
            error("Las contrasennas no coinciden.");
            return;
        }
        if (pass.length() < 6) {
            error("La contrasena debe tener al menos 6 caracteres.");
            return;
        }

        RolUsuario rol      = (RolUsuario) cmbRegRol.getSelectedItem();
        Parroquia  parroquia = (Parroquia)  cmbRegParroquia.getSelectedItem();

        try {
            usuarioCtrl.registrar(nombre, email, pass, rol, parroquia);
            JOptionPane.showMessageDialog(this,
                    "Cuenta creada correctamente. Ya puede iniciar sesion.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarRegistro();
            mostrar(VISTA_LOGIN);
        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (Exception ex) {
            error("Error al registrar: " + ex.getMessage());
        }
    }

    private void accionRecuperar() {
        String email   = txtRecEmail.getText().trim();
        String pass    = new String(txtRecPassword.getPassword());
        String confirm = new String(txtRecConfirm.getPassword());

        if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            error("Complete todos los campos.");
            return;
        }
        if (!pass.equals(confirm)) {
            error("Las contrasennas no coinciden.");
            return;
        }
        if (pass.length() < 6) {
            error("La contrasena debe tener al menos 6 caracteres.");
            return;
        }

        boolean ok = usuarioCtrl.cambiarPassword(email, pass);
        if (!ok) {
            error("No se encontro ningun usuario con ese correo.");
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Contrasena restablecida correctamente.",
                "Exito", JOptionPane.INFORMATION_MESSAGE);
        limpiarRecuperar();
        mostrar(VISTA_LOGIN);
    }

    // ─── Navegación ──────────────────────────────────────────────────────────
    private void mostrar(String vista) {
        cardLayout.show(panelCards, vista);
        int alto = VISTA_LOGIN.equals(vista) ? 480
                 : VISTA_REGISTRO.equals(vista) ? 680 : 510;
        setSize(440, alto);
        setLocationRelativeTo(null);
    }

    // ─── Helpers de datos ────────────────────────────────────────────────────
    private void cargarParroquias() {
        cmbRegParroquia.removeAllItems();
        cmbRegParroquia.addItem(null);
        for (Parroquia p : parroquiaCtrl.findParroquiasDisponibles())
            cmbRegParroquia.addItem(p);
    }

    private void limpiarRegistro() {
        txtRegNombre.setText("");
        txtRegEmail.setText("");
        txtRegPassword.setText("");
        txtRegConfirm.setText("");
    }

    private void limpiarRecuperar() {
        txtRecEmail.setText("");
        txtRecPassword.setText("");
        txtRecConfirm.setText("");
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─── Componentes base ────────────────────────────────────────────────────
    private JPanel panelBase() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.PANEL);
        return p;
    }

    private JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(AppColors.TEXTO);
        tf.setBackground(AppColors.PANEL);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDE, 1),
                new EmptyBorder(7, 10, 7, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JPasswordField campoPass() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setForeground(AppColors.TEXTO);
        pf.setBackground(AppColors.PANEL);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDE, 1),
                new EmptyBorder(7, 10, 7, 10)));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel subtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(AppColors.TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton linkBtn(String texto, ActionListener accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(AppColors.AZUL);
        btn.setBackground(null);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(accion);
        return btn;
    }

    private <T> void estilizarCombo(JComboBox<T> cmb) {
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setBackground(AppColors.PANEL);
        cmb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private Component vspace(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    private JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }
}
