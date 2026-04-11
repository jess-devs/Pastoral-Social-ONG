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
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FrmDetalleEvento extends JDialog {

    // ─── Estado ───────────────────────────────────────────────────────────────
    private Evento evento;
    private final boolean esNuevo;
    private final Runnable onGuardado;
    private final SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat sdfHora  = new SimpleDateFormat("HH:mm");

    // ─── Controllers ──────────────────────────────────────────────────────────
    private final EventoController eventoController = new EventoController();
    private final ParroquiaController parroquiaController = new ParroquiaController();

    // ─── Componentes ──────────────────────────────────────────────────────────
    private JLabel lblTituloPrincipal;

    private JTextField txtNombre;
    private JFormattedTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtLugar;
    private JTextArea txtDescripcion;
    private JComboBox<TipoEvento> cmbTipo;
    private JComboBox<Parroquia> cmbParroquia;

    // ─── Constructor ──────────────────────────────────────────────────────────
    public FrmDetalleEvento(Window owner, Evento evento, Runnable onGuardado) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.evento = evento;
        this.esNuevo = (evento == null);
        this.onGuardado = onGuardado;

        initComponents();
        if (!esNuevo) cargarDatos();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // INICIALIZACIÓN
    // ═════════════════════════════════════════════════════════════════════════
    private void initComponents() {
        setTitle(esNuevo ? "Nuevo Evento" : "Editar Evento");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(540, 520));
        setPreferredSize(new Dimension(560, 540));
        setResizable(false);
        setLayout(new BorderLayout());

        add(crearCabecera(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearPiePagina(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    // ─── Cabecera ─────────────────────────────────────────────────────────────
    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(AppColors.PANEL);
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE),
                new EmptyBorder(18, 24, 18, 24)));

        lblTituloPrincipal = new JLabel(esNuevo ? "Nuevo Evento" : "Editar Evento");
        lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloPrincipal.setForeground(AppColors.TEXTO);
        cab.add(lblTituloPrincipal, BorderLayout.WEST);

        // Badge de tipo (solo en edición)
        if (!esNuevo && evento.getTipo() != null) {
            JLabel badge = new JLabel(evento.getTipo().name().replace("_", " "));
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setForeground(AppColors.VERDE_FG);
            badge.setBackground(AppColors.VERDE_BG);
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(4, 12, 4, 12));
            cab.add(badge, BorderLayout.EAST);
        }
        return cab;
    }

    // ─── Cuerpo del formulario ────────────────────────────────────────────────
    private JScrollPane crearCuerpo() {
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(AppColors.FONDO);
        cuerpo.setBorder(new EmptyBorder(20, 24, 20, 24));

        cuerpo.add(crearSeccion("Datos del Evento"));
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(crearFormulario());

        JScrollPane scroll = new JScrollPane(cuerpo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppColors.FONDO);
        return scroll;
    }

    private JLabel crearSeccion(String titulo) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppColors.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints lc = new GridBagConstraints();
        lc.insets = new Insets(6, 0, 6, 10);
        lc.anchor = GridBagConstraints.WEST;
        lc.gridx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(6, 0, 6, 0);
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.gridx = 1;

        int row = 0;

        // Nombre
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Nombre *"), lc);
        txtNombre = campo();
        form.add(txtNombre, fc);

        // Fecha
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Fecha *  (dd/MM/yyyy)"), lc);
        txtFecha = UIFactory.crearCampoFecha();
        txtFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFecha.setPreferredSize(new Dimension(0, 32));
        form.add(txtFecha, fc);

        // Hora
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Hora  (HH:mm)"), lc);
        txtHora = campo();
        txtHora.setToolTipText("Formato 24h: 14:30");
        form.add(txtHora, fc);

        // Lugar
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Lugar"), lc);
        txtLugar = campo();
        form.add(txtLugar, fc);

        // Tipo
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Tipo"), lc);
        cmbTipo = new JComboBox<>(TipoEvento.values());
        cmbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipo.setPreferredSize(new Dimension(0, 32));
        form.add(cmbTipo, fc);

        // Parroquia
        lc.gridy = row; fc.gridy = row++;
        form.add(etiqueta("Parroquia *"), lc);
        cmbParroquia = new JComboBox<>();
        cmbParroquia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbParroquia.setPreferredSize(new Dimension(0, 32));
        cargarParroquias();
        form.add(cmbParroquia, fc);

        // Descripción (span completo)
        GridBagConstraints lFull = new GridBagConstraints();
        lFull.gridy = row; lFull.gridx = 0;
        lFull.anchor = GridBagConstraints.NORTHWEST;
        lFull.insets = new Insets(6, 0, 2, 10);
        form.add(etiqueta("Descripción"), lFull);

        row++;
        GridBagConstraints areaC = new GridBagConstraints();
        areaC.gridy = row; areaC.gridx = 0;
        areaC.gridwidth = 2;
        areaC.fill = GridBagConstraints.BOTH;
        areaC.weightx = 1.0;
        areaC.insets = new Insets(0, 0, 6, 0);

        txtDescripcion = new JTextArea(4, 0);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        JScrollPane scrollArea = new JScrollPane(txtDescripcion);
        scrollArea.setBorder(BorderFactory.createEmptyBorder());
        form.add(scrollArea, areaC);

        return form;
    }

    // ─── Pie de página ────────────────────────────────────────────────────────
    private JPanel crearPiePagina() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pie.setBackground(AppColors.PANEL);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.BORDE),
                new EmptyBorder(14, 24, 14, 24)));

        pie.add(UIFactory.crearBotonDialog("Cancelar", AppColors.GRIS_BTN, AppColors.TEXTO,
                e -> dispose()));
        pie.add(UIFactory.crearBotonDialog(
                esNuevo ? "Crear Evento" : "Guardar Cambios",
                AppColors.PRIMARIO, Color.WHITE,
                e -> guardar()));
        return pie;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CARGA Y GUARDADO
    // ═════════════════════════════════════════════════════════════════════════
    private void cargarParroquias() {
        List<Parroquia> parroquias = parroquiaController.findParroquiasDisponibles();
        for (Parroquia p : parroquias) cmbParroquia.addItem(p);
        cmbParroquia.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Parroquia)
                    setText(((Parroquia) value).getNombre());
                return this;
            }
        });
    }

    private void cargarDatos() {
        txtNombre.setText(evento.getNombre() != null ? evento.getNombre() : "");
        if (evento.getFecha() != null)
            txtFecha.setText(sdfFecha.format(evento.getFecha()));
        if (evento.getHora() != null)
            txtHora.setText(sdfHora.format(evento.getHora()));
        txtLugar.setText(evento.getLugar() != null ? evento.getLugar() : "");
        txtDescripcion.setText(evento.getDescripcion() != null ? evento.getDescripcion() : "");
        if (evento.getTipo() != null) cmbTipo.setSelectedItem(evento.getTipo());
        if (evento.getParroquia() != null) {
            for (int i = 0; i < cmbParroquia.getItemCount(); i++) {
                if (cmbParroquia.getItemAt(i).getId().equals(evento.getParroquia().getId())) {
                    cmbParroquia.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardar() {
        // ─── Validaciones ────────────────────────────────────────────────────
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre del evento es obligatorio.");
            txtNombre.requestFocus();
            return;
        }
        String fechaStr = txtFecha.getText().replace("_", "").trim();
        if (fechaStr.length() < 10) {
            mostrarError("Ingrese una fecha válida (dd/MM/yyyy).");
            txtFecha.requestFocus();
            return;
        }
        if (cmbParroquia.getSelectedItem() == null) {
            mostrarError("Seleccione una parroquia.");
            cmbParroquia.requestFocus();
            return;
        }

        // ─── Parsear fecha ───────────────────────────────────────────────────
        Date fecha;
        try {
            fecha = sdfFecha.parse(fechaStr);
        } catch (ParseException ex) {
            mostrarError("Fecha inválida. Use el formato dd/MM/yyyy.");
            txtFecha.requestFocus();
            return;
        }

        // ─── Parsear hora (opcional) ─────────────────────────────────────────
        Date hora = null;
        String horaStr = txtHora.getText().trim();
        if (!horaStr.isEmpty()) {
            try {
                hora = sdfHora.parse(horaStr);
            } catch (ParseException ex) {
                mostrarError("Hora inválida. Use el formato HH:mm (ej: 14:30).");
                txtHora.requestFocus();
                return;
            }
        }

        // ─── Construir / actualizar entidad ──────────────────────────────────
        if (esNuevo) evento = new Evento();
        evento.setNombre(nombre);
        evento.setFecha(fecha);
        evento.setHora(hora);
        evento.setLugar(txtLugar.getText().trim().isEmpty() ? null : txtLugar.getText().trim());
        evento.setDescripcion(txtDescripcion.getText().trim().isEmpty()
                ? null : txtDescripcion.getText().trim());
        evento.setTipo((TipoEvento) cmbTipo.getSelectedItem());
        evento.setParroquia((Parroquia) cmbParroquia.getSelectedItem());

        // ─── Persistir ───────────────────────────────────────────────────────
        try {
            eventoController.guardarEvento(evento);
            if (onGuardado != null) onGuardado.run();
            JOptionPane.showMessageDialog(this,
                    esNuevo ? "Evento creado correctamente." : "Cambios guardados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el evento:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ═════════════════════════════════════════════════════════════════════════
    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppColors.TEXTO);
        return lbl;
    }

    private JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDE, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        return tf;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
    }
}
