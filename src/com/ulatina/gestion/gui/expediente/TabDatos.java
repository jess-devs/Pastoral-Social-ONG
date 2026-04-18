package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Parroquia;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.Usuario;
import com.ulatina.gestion.model.enums.*;
import com.ulatina.gestion.util.SessionContext;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Tab 1 — Datos del titular y del expediente.
 * Implementa TabConDatosGuardables: validar() y aplicarAlModelo() son llamados
 * por FrmDetalleExpediente.guardar() antes de persistir.
 */
public class TabDatos implements TabConDatosGuardables {

  private final ExpedienteContext ctx;
  private final Consumer<Boolean> onMarcadorUrgente;

  private JTextField txtNombres;
  private JTextField txtApellidos;
  private JComboBox<TipoDocumentoPersona> cmbTipoDoc;
  private JTextField txtNumeroDoc;
  private JFormattedTextField txtFechaNac;
  private JComboBox<Sexo> cmbSexo;
  private JComboBox<EstadoCivil> cmbEstadoCivil;
  private JTextField txtTelefono;
  private JTextField txtDireccion;
  private JTextField txtNacionalidad;
  private JTextField txtPaisOrigen;
  private JTextField txtProfesion;
  private JTextField txtNivelEducacion;
  private JTextArea txtCondicionSalud;
  private JCheckBox chkTieneSeguro;
  private JTextField txtCondicionMigratoria;
  private JTextField txtEntrevistador;
  private JComboBox<EstadoExpediente> cmbEstado;
  private JComboBox<Parroquia> cmbParroquia;
  private JFormattedTextField txtFechaInicio;
  private JFormattedTextField txtFechaPrevista;
  private JTextArea txtObservaciones;
  private JComboBox<String> cmbColorMarcador;

  /**
   * @param ctx                contexto compartido.
   * @param onMarcadorUrgente  callback que recibe true cuando el marcador es URGENTE.
   *                           Permite que FrmDetalleExpediente actualice el badge.
   */
  public TabDatos(ExpedienteContext ctx, Consumer<Boolean> onMarcadorUrgente) {
    this.ctx = ctx;
    this.onMarcadorUrgente = onMarcadorUrgente;
  }

  public JScrollPane construir() {
    JPanel p = new JPanel(new GridBagLayout());
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));

    GridBagConstraints lc = new GridBagConstraints();
    lc.anchor = GridBagConstraints.WEST;
    lc.insets = new Insets(5, 4, 2, 4);

    GridBagConstraints fc = new GridBagConstraints();
    fc.fill = GridBagConstraints.HORIZONTAL;
    fc.weightx = 1.0;
    fc.insets = new Insets(2, 4, 5, 12);

    GridBagConstraints wc = new GridBagConstraints();
    wc.gridwidth = GridBagConstraints.REMAINDER;
    wc.fill = GridBagConstraints.HORIZONTAL;
    wc.weightx = 1.0;
    wc.insets = new Insets(2, 4, 5, 12);

    int row = 0;

    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 4;
    JLabel secTitular = new JLabel("DATOS DEL TITULAR");
    secTitular.setFont(new Font("Segoe UI", Font.BOLD, 11));
    secTitular.setForeground(AppColors.TEXTO_GRIS);
    secTitular.setBorder(new EmptyBorder(0, 0, 4, 0));
    p.add(secTitular, lc);
    lc.gridwidth = 1;
    row++;

    txtNombres = new JTextField(15);
    txtApellidos = new JTextField(15);
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Nombres *",
      txtNombres,
      "Apellidos *",
      txtApellidos
    );

    cmbTipoDoc = new JComboBox<>(TipoDocumentoPersona.values());
    txtNumeroDoc = new JTextField(12);
    txtTelefono = new JTextField(12);
    JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    docPanel.setOpaque(false);
    docPanel.add(cmbTipoDoc);
    docPanel.add(txtNumeroDoc);
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Documento *",
      docPanel,
      "Teléfono",
      txtTelefono
    );

    txtFechaNac = UIFactory.crearCampoFecha();
    cmbSexo = new JComboBox<>(Sexo.values());
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Fecha Nacimiento",
      txtFechaNac,
      "Sexo",
      cmbSexo
    );

    cmbEstadoCivil = new JComboBox<>(EstadoCivil.values());
    txtProfesion = new JTextField(15);
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Estado Civil",
      cmbEstadoCivil,
      "Profesión/Oficio",
      txtProfesion
    );

    txtNivelEducacion = new JTextField(15);
    txtPaisOrigen = new JTextField(15);
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Nivel Educación",
      txtNivelEducacion,
      "País Origen",
      txtPaisOrigen
    );

    txtNacionalidad = new JTextField(15);
    txtCondicionMigratoria = new JTextField(15);
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Nacionalidad",
      txtNacionalidad,
      "Cond. Migratoria",
      txtCondicionMigratoria
    );

    txtDireccion = new JTextField();
    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 1;
    p.add(etiqueta("Dirección"), lc);
    wc.gridx = 1;
    wc.gridy = row;
    wc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(txtDireccion, wc);
    wc.gridwidth = 4;
    row++;

    chkTieneSeguro = new JCheckBox("Tiene seguro de salud");
    chkTieneSeguro.setOpaque(false);
    chkTieneSeguro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 4;
    p.add(chkTieneSeguro, lc);
    lc.gridwidth = 1;
    row++;

    txtCondicionSalud = new JTextArea(2, 20);
    txtCondicionSalud.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtCondicionSalud.setLineWrap(true);
    txtCondicionSalud.setWrapStyleWord(true);
    JScrollPane scrollSalud = new JScrollPane(txtCondicionSalud);
    scrollSalud.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.AMBAR_BG, 2, true),
        new EmptyBorder(2, 4, 2, 4)
      )
    );
    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 1;
    p.add(etiqueta("Condición de Salud"), lc);
    wc.gridx = 1;
    wc.gridy = row;
    wc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(scrollSalud, wc);
    wc.gridwidth = 4;
    row++;

    JSeparator sep = new JSeparator();
    sep.setForeground(AppColors.BORDE);
    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 4;
    lc.fill = GridBagConstraints.HORIZONTAL;
    lc.insets = new Insets(10, 4, 10, 4);
    p.add(sep, lc);
    lc.fill = GridBagConstraints.NONE;
    lc.insets = new Insets(5, 4, 2, 4);
    lc.gridwidth = 1;
    row++;

    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 4;
    JLabel secExp = new JLabel("DATOS DEL EXPEDIENTE");
    secExp.setFont(new Font("Segoe UI", Font.BOLD, 11));
    secExp.setForeground(AppColors.TEXTO_GRIS);
    secExp.setBorder(new EmptyBorder(0, 0, 4, 0));
    p.add(secExp, lc);
    lc.gridwidth = 1;
    row++;

    txtEntrevistador = new JTextField(15);
    cmbEstado = new JComboBox<>(EstadoExpediente.values());
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Entrevistador",
      txtEntrevistador,
      "Estado",
      cmbEstado
    );

    cmbParroquia = new JComboBox<>();
    cmbParroquia.setRenderer(
      new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
          JList<?> list,
          Object value,
          int idx2,
          boolean sel,
          boolean focus
        ) {
          super.getListCellRendererComponent(list, value, idx2, sel, focus);
          if (value instanceof Parroquia) setText(
            ((Parroquia) value).getNombre()
          );
          return this;
        }
      }
    );
    cmbColorMarcador = new JComboBox<>(
      new String[] { "NINGUNO", "URGENTE", "PRIORITARIO", "NORMAL" }
    );
    cmbColorMarcador.addActionListener(e ->
      onMarcadorUrgente.accept(
        "URGENTE".equals(cmbColorMarcador.getSelectedItem())
      )
    );
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Parroquia",
      cmbParroquia,
      "Color Marcador",
      cmbColorMarcador
    );

    txtFechaInicio = UIFactory.crearCampoFecha();
    txtFechaPrevista = UIFactory.crearCampoFecha();
    agregarFila(
      p,
      row++,
      lc,
      fc,
      "Fecha Inicio *",
      txtFechaInicio,
      "Fecha Prevista Concl.",
      txtFechaPrevista
    );

    txtObservaciones = new JTextArea(3, 20);
    txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtObservaciones.setLineWrap(true);
    txtObservaciones.setWrapStyleWord(true);
    JScrollPane scrollObs = new JScrollPane(txtObservaciones);
    scrollObs.setBorder(new LineBorder(AppColors.BORDE, 1, true));
    lc.gridx = 0;
    lc.gridy = row;
    lc.gridwidth = 1;
    p.add(etiqueta("Observaciones"), lc);
    wc.gridx = 1;
    wc.gridy = row;
    wc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(scrollObs, wc);
    row++;

    GridBagConstraints sc = new GridBagConstraints();
    sc.gridy = row;
    sc.gridwidth = 4;
    sc.weighty = 1.0;
    sc.fill = GridBagConstraints.VERTICAL;
    p.add(Box.createVerticalGlue(), sc);

    JScrollPane scroll = new JScrollPane(p);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getViewport().setBackground(AppColors.PANEL);
    return scroll;
  }

  public void cargarDatos() {
    for (Parroquia par : ctx
      .getParroquiaController()
      .findParroquiasDisponibles())
      cmbParroquia.addItem(par);

    if (ctx.isEsNuevo()) {
      cmbEstado.setSelectedItem(EstadoExpediente.EN_PROCESO);
      cmbColorMarcador.setSelectedIndex(0);
      txtFechaInicio.setValue(null);
      try {
        txtFechaInicio.setText(
          new SimpleDateFormat("dd/MM/yyyy").format(new Date())
        );
      } catch (Exception ignored) {}
      Usuario actual = SessionContext.getUsuarioActual();
      if (actual != null) txtEntrevistador.setText(actual.getNombre());
      return;
    }

    Expediente exp = ctx.getExpediente();
    Persona p = exp.getTitular();
    if (p != null) {
      txtNombres.setText(ctx.nvl(p.getNombres()));
      txtApellidos.setText(ctx.nvl(p.getApellidos()));
      if (p.getTipoDocumento() != null) cmbTipoDoc.setSelectedItem(
        p.getTipoDocumento()
      );
      txtNumeroDoc.setText(ctx.nvl(p.getNumeroDocumento()));
      if (p.getFechaNacimiento() != null) txtFechaNac.setText(
        ctx.getSdf().format(p.getFechaNacimiento())
      );
      if (p.getSexo() != null) cmbSexo.setSelectedItem(p.getSexo());
      if (p.getEstadoCivil() != null) cmbEstadoCivil.setSelectedItem(
        p.getEstadoCivil()
      );
      txtTelefono.setText(ctx.nvl(p.getTelefono()));
      txtDireccion.setText(ctx.nvl(p.getDireccion()));
      txtNacionalidad.setText(ctx.nvl(p.getNacionalidad()));
      txtPaisOrigen.setText(ctx.nvl(p.getPaisOrigen()));
      txtProfesion.setText(ctx.nvl(p.getProfesionOficio()));
      txtNivelEducacion.setText(ctx.nvl(p.getNivelEducacion()));
      txtCondicionSalud.setText(ctx.nvl(p.getCondicionSalud()));
      if (Boolean.TRUE.equals(p.getTieneSeguro())) chkTieneSeguro.setSelected(
        true
      );
      txtCondicionMigratoria.setText(ctx.nvl(p.getCondicionMigratoria()));
    }

    txtEntrevistador.setText(ctx.nvl(exp.getEntrevistador()));
    if (exp.getEstado() != null) cmbEstado.setSelectedItem(exp.getEstado());
    if (exp.getFechaInicio() != null) txtFechaInicio.setText(
      ctx.getSdf().format(exp.getFechaInicio())
    );
    if (exp.getFechaPrevistaConclusion() != null) txtFechaPrevista.setText(
      ctx.getSdf().format(exp.getFechaPrevistaConclusion())
    );
    txtObservaciones.setText(ctx.nvl(exp.getObservaciones()));

    String marcador = exp.getColorMarcador();
    if (marcador != null && !marcador.isEmpty()) {
      cmbColorMarcador.setSelectedItem(marcador);
      onMarcadorUrgente.accept("URGENTE".equals(marcador));
    }

    if (exp.getParroquia() != null) {
      for (int i = 0; i < cmbParroquia.getItemCount(); i++) {
        if (
          cmbParroquia.getItemAt(i).getId().equals(exp.getParroquia().getId())
        ) {
          cmbParroquia.setSelectedIndex(i);
          break;
        }
      }
    }
  }

  // ─── TabConDatosGuardables ────────────────────────────────────────────────

  @Override
  public void validar() throws IllegalStateException {
    if (
      txtNombres.getText().trim().isEmpty() ||
      txtApellidos.getText().trim().isEmpty()
    ) throw new IllegalStateException("Nombres y apellidos son obligatorios.");
    if (
      txtNumeroDoc.getText().trim().isEmpty()
    ) throw new IllegalStateException("El número de documento es obligatorio.");
    if (cmbParroquia.getItemCount() == 0) throw new IllegalStateException(
      "No hay parroquias disponibles. Registre una parroquia primero."
    );
  }

  /**
   * Escribe los valores de la UI en ctx.titular y ctx.expediente.
   * El titular debe haber sido cargado o creado por guardar() antes de llamar este método.
   */
  @Override
  public void aplicarAlModelo() {
    Persona p = ctx.getTitular();
    p.setNombres(txtNombres.getText().trim());
    p.setApellidos(txtApellidos.getText().trim());
    p.setTipoDocumento((TipoDocumentoPersona) cmbTipoDoc.getSelectedItem());
    p.setNumeroDocumento(txtNumeroDoc.getText().trim());
    p.setSexo((Sexo) cmbSexo.getSelectedItem());
    p.setEstadoCivil((EstadoCivil) cmbEstadoCivil.getSelectedItem());
    p.setTelefono(txtTelefono.getText().trim());
    p.setDireccion(txtDireccion.getText().trim());
    p.setNacionalidad(txtNacionalidad.getText().trim());
    p.setPaisOrigen(txtPaisOrigen.getText().trim());
    p.setProfesionOficio(txtProfesion.getText().trim());
    p.setNivelEducacion(txtNivelEducacion.getText().trim());
    p.setCondicionSalud(txtCondicionSalud.getText().trim());
    p.setTieneSeguro(chkTieneSeguro.isSelected());
    p.setCondicionMigratoria(txtCondicionMigratoria.getText().trim());
    p.setFechaNacimiento(ctx.parseFecha(txtFechaNac.getText()));

    Expediente exp = ctx.getExpediente();
    exp.setParroquia((Parroquia) cmbParroquia.getSelectedItem());
    exp.setEstado((EstadoExpediente) cmbEstado.getSelectedItem());
    exp.setEntrevistador(txtEntrevistador.getText().trim());
    exp.setFechaInicio(ctx.parseFecha(txtFechaInicio.getText()));
    exp.setFechaPrevistaConclusion(ctx.parseFecha(txtFechaPrevista.getText()));
    exp.setObservaciones(txtObservaciones.getText().trim());
    String marcador = (String) cmbColorMarcador.getSelectedItem();
    exp.setColorMarcador("NINGUNO".equals(marcador) ? null : marcador);
  }

  // ─── Getters para guardar() en FrmDetalleExpediente ──────────────────────

  public String getNumeroDocumento() {
    return txtNumeroDoc.getText().trim();
  }

  // ─── Helpers privados de layout ──────────────────────────────────────────

  private void agregarFila(
    JPanel p,
    int row,
    GridBagConstraints lc,
    GridBagConstraints fc,
    String lbl1,
    Component c1,
    String lbl2,
    Component c2
  ) {
    lc.gridx = 0;
    lc.gridy = row;
    p.add(etiqueta(lbl1), lc);
    fc.gridx = 1;
    fc.gridy = row;
    p.add(c1, fc);
    lc.gridx = 2;
    lc.gridy = row;
    p.add(etiqueta(lbl2), lc);
    fc.gridx = 3;
    fc.gridy = row;
    p.add(c2, fc);
  }

  private JLabel etiqueta(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    l.setForeground(AppColors.TEXTO_GRIS);
    return l;
  }
}
