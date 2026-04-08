package com.ulatina.gestion.gui;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.util.JPAUtil;
import com.ulatina.gestion.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * FrmDashboard — Pantalla principal del sistema Pastoral Social.
 * Punto de entrada: main() en esta clase.
 */
public class FrmDashboard extends JFrame {

    // ─── Estado ──────────────────────────────────────────────────────────────
    private JPanel panelContenido;
    private JLabel lblTopbarTitulo;
    private JButton btnActivo = null;
    private JPanel sidebar;

    private final ExpedienteController expedienteController = new ExpedienteController();
    private final Usuario usuario;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public FrmDashboard(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Pastoral Social");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setPreferredSize(new Dimension(1080, 680));
        setLayout(new BorderLayout());

        sidebar = crearSidebar();
        add(sidebar, BorderLayout.WEST);
        add(crearAreaPrincipal(), BorderLayout.CENTER);

        // Cerrar JPA al salir
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JPAUtil.close();
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel crearSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(AppColors.SIDE_BG);
        sb.setPreferredSize(new Dimension(200, 0));

        // Brand
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(AppColors.SIDE_BG);
        brand.setBorder(new EmptyBorder(28, 22, 24, 22));
        brand.setMaximumSize(new Dimension(200, 88));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l1 = new JLabel("Pastoral");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l1.setForeground(AppColors.PANEL);
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l2 = new JLabel("Social");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l2.setForeground(AppColors.SIDE_ACTV);
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(l1);
        brand.add(l2);
        sb.add(brand);

        // Botones de navegación
        JButton bDash = navBtn("  Dashboard");
        JButton bExp = navBtn("  Expedientes");
        JButton bEven = navBtn("  Eventos");
        JButton bRep = navBtn("  Reportes");
        JButton bCons = navBtn("  Consulta Vicarial");

        activar(bDash);

        bDash.addActionListener(e -> {
            activar(bDash);
            mostrarDashboard();
        });
        bExp.addActionListener(e -> {
            activar(bExp);
            mostrarExpedientes();
        });
        bEven.addActionListener(e -> {
            activar(bEven);
            mostrarProximamente("Eventos");
        });
        bRep.addActionListener(e -> {
            activar(bRep);
            mostrarProximamente("Reportes");
        });
        bCons.addActionListener(e -> {
            activar(bCons);
            mostrarProximamente("Consulta Vicarial");
        });

        sb.add(bDash);
        sb.add(bExp);
        sb.add(bEven);
        sb.add(bRep);
        sb.add(bCons);
        sb.add(Box.createVerticalGlue());

        JButton bAdmin = navBtn("  Panel de Administrador");
        bAdmin.addActionListener(e -> {
            activar(bAdmin);
            mostrarProximamente("Administración");
        });
        sb.add(bAdmin);

        JButton bCerrar = navBtn("  Cerrar sesión");
        bCerrar.setForeground(AppColors.ROJO_LIGHT);
        bCerrar.addActionListener(e -> cerrarSesion());
        sb.add(bCerrar);
        sb.add(Box.createVerticalStrut(16));
        return sb;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar la sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        SessionContext.cerrarSesion();
        SwingUtilities.invokeLater(() -> {
            new FrmLogin().setVisible(true);
            dispose();
        });
    }

    private Component sep() {
        JPanel s = new JPanel();
        s.setBackground(AppColors.SIDE_SEP);
        s.setMaximumSize(new Dimension(200, 1));
        return s;
    }

    private JButton navBtn(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(AppColors.SIDE_TXT);
        btn.setBackground(AppColors.SIDE_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 46));
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setBorder(new EmptyBorder(0, 22, 0, 12));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(AppColors.SIDE_HOVR);
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(AppColors.SIDE_BG);
                    btn.repaint();
                }
            }
        });
        return btn;
    }

    private void activar(JButton btn) {
        if (btnActivo != null) {
            btnActivo.setBackground(AppColors.SIDE_BG);
            btnActivo.setForeground(AppColors.SIDE_TXT);
            btnActivo.repaint();
        }
        btnActivo = btn;
        btn.setBackground(AppColors.SIDE_ACTV);
        btn.setForeground(AppColors.PANEL);
        btn.repaint();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ÁREA PRINCIPAL
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel crearAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(AppColors.FONDO);
        area.add(crearTopbar(), BorderLayout.NORTH);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(AppColors.FONDO);
        panelContenido.add(crearVistaDashboard(), BorderLayout.CENTER);
        area.add(panelContenido, BorderLayout.CENTER);
        return area;
    }

    // ─── Topbar ───────────────────────────────────────────────────────────────
    private JPanel crearTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(AppColors.PANEL);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE),
                new EmptyBorder(14, 26, 14, 26)));
        top.setPreferredSize(new Dimension(0, 58));

        lblTopbarTitulo = new JLabel("Dashboard");
        lblTopbarTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTopbarTitulo.setForeground(AppColors.TEXTO);
        top.add(lblTopbarTitulo, BorderLayout.WEST);

        JLabel lblUser = new JLabel(usuario.getNombre() + "  (" + usuario.getRol().name() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(AppColors.TEXTO_GRIS);
        top.add(lblUser, BorderLayout.EAST);
        return top;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // VISTA DASHBOARD
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel crearVistaDashboard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.FONDO);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Métricas desde el controller
        ExpedienteController.DashboardMetrics m = expedienteController.getMetrics();

        // Tarjetas métricas
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 14, 0));
        tarjetas.setOpaque(false);
        tarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        tarjetas.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetas.add(tarjeta(String.valueOf(m.total), "Expedientes", AppColors.AZUL_CARD_BG, AppColors.AZUL_CARD_FG));
        tarjetas.add(tarjeta(String.valueOf(m.activos), "Activos", AppColors.VERDE_BG, AppColors.VERDE_FG));
        tarjetas.add(tarjeta(String.valueOf(m.enProceso), "En Proceso", AppColors.AMBAR_BG, AppColors.AMBAR_FG));
        tarjetas.add(tarjeta(String.valueOf(m.cerrados), "Cerrados", AppColors.ROJO_CARD_BG, AppColors.ROJO_CARD_FG));
        p.add(tarjetas);
        p.add(Box.createVerticalStrut(28));

        JLabel lblMod = new JLabel("Módulos del sistema");
        lblMod.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMod.setForeground(AppColors.TEXTO);
        lblMod.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblMod);
        p.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        grid.add(modulo("Expedientes", "Gestión de casos y seguimiento", AppColors.AZUL_CARD_BG, AppColors.AZUL_CARD_FG,
                () -> {
                    activarNav("Expedientes");
                    mostrarExpedientes();
                }));
        grid.add(modulo("Eventos", "Registro y asistencia", AppColors.VERDE_BG, AppColors.VERDE_FG,
                () -> mostrarProximamente("Eventos")));
        grid.add(modulo("Reportes", "Estadísticas y análisis", AppColors.AMBAR_BG, AppColors.AMBAR_FG,
                () -> mostrarProximamente("Reportes")));
        grid.add(modulo("Consulta Vicarial", "Búsqueda por vicaria / sector", AppColors.PURP_BG, AppColors.PURPURA,
                () -> mostrarProximamente("Consulta Vicarial")));
        grid.add(modulo("Familias", "Miembros y núcleo familiar", AppColors.VERDE_BG, AppColors.VERDE_FG,
                () -> mostrarProximamente("Familias")));
        grid.add(modulo("Administración", "Usuarios, roles y parroquias", AppColors.ROJO_CARD_BG,
                AppColors.ROJO_CARD_FG, () -> mostrarProximamente("Administración")));
        p.add(grid);
        return p;
    }

    // ─── Tarjeta métrica ──────────────────────────────────────────────────────
    private JPanel tarjeta(String num, String etq, Color bg, Color fg) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(bg);
        c.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lNum = new JLabel(num);
        lNum.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lNum.setForeground(fg);
        lNum.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lEtq = new JLabel(etq);
        lEtq.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lEtq.setForeground(fg);
        lEtq.setAlignmentX(Component.CENTER_ALIGNMENT);

        c.add(Box.createVerticalGlue());
        c.add(lNum);
        c.add(Box.createVerticalStrut(4));
        c.add(lEtq);
        c.add(Box.createVerticalGlue());
        return c;
    }

    // ─── Módulo clickeable ────────────────────────────────────────────────────
    private JPanel modulo(String titulo, String desc, Color bg, Color fg, Runnable accion) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(AppColors.PANEL);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDE, 1),
                new EmptyBorder(18, 20, 18, 20)));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Cuadro de color con inicial
        JPanel icono = new JPanel(new GridBagLayout());
        icono.setBackground(bg);
        icono.setPreferredSize(new Dimension(36, 36));
        icono.setMaximumSize(new Dimension(36, 36));
        icono.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ini = new JLabel(String.valueOf(titulo.charAt(0)));
        ini.setFont(new Font("Segoe UI", Font.BOLD, 15));
        ini.setForeground(fg);
        icono.add(ini);

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTit.setForeground(AppColors.TEXTO);
        lTit.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lDesc = new JLabel(desc);
        lDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lDesc.setForeground(AppColors.TEXTO_GRIS);
        lDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAbr = new JButton("Abrir →");
        btnAbr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAbr.setForeground(AppColors.AZUL);
        btnAbr.setBackground(null);
        btnAbr.setBorderPainted(false);
        btnAbr.setContentAreaFilled(false);
        btnAbr.setFocusPainted(false);
        btnAbr.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbr.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAbr.addActionListener(e -> accion.run());

        c.add(icono);
        c.add(Box.createVerticalStrut(10));
        c.add(lTit);
        c.add(Box.createVerticalStrut(4));
        c.add(lDesc);
        c.add(Box.createVerticalGlue());
        c.add(Box.createVerticalStrut(12));
        c.add(btnAbr);

        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.AZUL_BORDE, 1),
                        new EmptyBorder(18, 20, 18, 20)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppColors.BORDE, 1),
                        new EmptyBorder(18, 20, 18, 20)));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                accion.run();
            }
        });
        return c;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN
    // ═════════════════════════════════════════════════════════════════════════
    private void cambiarVista(JPanel vista, String titulo) {
        panelContenido.removeAll();
        panelContenido.add(vista, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
        lblTopbarTitulo.setText(titulo);
    }

    private void mostrarDashboard() {
        cambiarVista(crearVistaDashboard(), "Dashboard");
    }

    private void mostrarExpedientes() {
        cambiarVista(new FrmExpedientesPanel(), "Expedientes");
    }

    private void mostrarProximamente(String nombre) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppColors.FONDO);
        JLabel lbl = new JLabel(nombre + "  —  Próximamente");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        p.add(lbl);
        cambiarVista(p, nombre);
    }

    /** Marca el botón del sidebar que corresponde al nombre del módulo */
    private void activarNav(String nombre) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b.getText().trim().equalsIgnoreCase(nombre)) {
                    activar(b);
                    break;
                }
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MAIN — punto de entrada de la aplicación
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new com.ulatina.gestion.gui.FrmLogin().setVisible(true);
        });
    }
}
