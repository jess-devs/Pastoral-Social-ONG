package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.MiembroFamiliar;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.enums.TipoDocumentoPersona;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

/**
 * Pestaña 2 — Grupo familiar del titular del expediente.
 * Permite buscar una persona existente por cédula o crear una nueva mediante
 * un diálogo modal, para luego registrarla como miembro familiar con relación,
 * jefatura, ocupación e ingreso mensual.
 */
public class TabFamilia {

  /** Contexto compartido con el resto de pestañas. */
  private final ExpedienteContext ctx;

  /** Modelo de datos de la tabla de miembros familiares. */
  private DefaultTableModel modeloFamilia;

  /** Tabla que muestra el grupo familiar con columna de acciones. */
  private JTable tablaFamilia;

  /** Panel del formulario de agregar/editar miembro; oculto por defecto. */
  private JPanel panelFormFamilia;

  /** Encabezado del formulario; cambia entre "Agregar" y "Editar". */
  private JLabel lblHeaderFormFamilia;

  /** Muestra el nombre de la persona encontrada o creada para el miembro. */
  private JLabel lblPersonaSeleccionada;

  /** Campo de búsqueda de persona por número de cédula. */
  private JTextField txtBuscarCedula;

  /** Relación del miembro con el titular (esposo/a, hijo/a, etc.). */
  private JComboBox<String> cmbRelacion;

  /** Toggle para indicar si este miembro es jefe de familia. */
  private JButton btnJefatura;

  /** Ocupación o trabajo del miembro familiar. */
  private JTextField txtOcupacion;

  /** Toggle para indicar si el miembro trabaja actualmente. */
  private JButton btnTrabaja;

  /** Ingreso mensual del miembro en colones. */
  private JTextField txtIngreso;

  /** Miembro que se está editando; null cuando se está agregando uno nuevo. */
  private MiembroFamiliar miembroEnEdicion;

  /** Persona seleccionada mediante búsqueda por cédula o creación nueva. */
  private Persona personaSeleccionada;

  /** Copia de la página actual de miembros, usada para mapear clics de la tabla. */
  private final List<MiembroFamiliar> listaMiembros = new ArrayList<>();

  /** Paginador de los miembros familiares del expediente. */
  private final Paginador<MiembroFamiliar> pagFamilia = new Paginador<>();

  /** Panel de controles de paginación para la tabla de familia. */
  private final JPanel panelPagFamilia = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  /**
   * Crea la pestaña con el contexto compartido.
   *
   * @param ctx contexto compartido del diálogo
   */
  public TabFamilia(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Construye y devuelve el panel principal de la pestaña.
   * Incluye barra superior con título y botón Agregar, tabla de miembros y
   * formulario de edición oculto en la parte inferior.
   *
   * @return JPanel con la estructura completa de la pestaña
   */
  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 0));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));

    JPanel barraTop = new JPanel(new BorderLayout());
    barraTop.setOpaque(false);
    barraTop.setBorder(new EmptyBorder(0, 0, 10, 0));
    JLabel lblTit = new JLabel("Grupo Familiar");
    lblTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblTit.setForeground(AppColors.TEXTO);
    JButton btnAgregar = UIFactory.crearBotonSmall(
      "Agregar",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> mostrarFormFamilia(true)
    );
    barraTop.add(lblTit, BorderLayout.WEST);
    barraTop.add(btnAgregar, BorderLayout.EAST);

    modeloFamilia = new DefaultTableModel(
      new String[] {
        "Nombre",
        "Cédula",
        "Relación",
        "Jefatura",
        "Ocupación",
        "Ingreso",
        "Acciones",
      },
      0
    ) {
      @Override
      public boolean isCellEditable(int r, int c) {
        return false;
      }
    };

    tablaFamilia = new JTable(modeloFamilia);
    ctx.configurarTabla(tablaFamilia, 38);
    tablaFamilia.setShowHorizontalLines(true);
    tablaFamilia.setSelectionBackground(AppColors.FILA_SEL);

    tablaFamilia.getColumnModel().getColumn(0).setPreferredWidth(160);
    tablaFamilia.getColumnModel().getColumn(1).setPreferredWidth(110);
    tablaFamilia.getColumnModel().getColumn(2).setPreferredWidth(90);
    tablaFamilia.getColumnModel().getColumn(3).setPreferredWidth(80);
    tablaFamilia.getColumnModel().getColumn(4).setPreferredWidth(100);
    tablaFamilia.getColumnModel().getColumn(5).setPreferredWidth(90);
    tablaFamilia.getColumnModel().getColumn(6).setPreferredWidth(90);
    tablaFamilia.getColumnModel().getColumn(6).setMaxWidth(100);
    tablaFamilia.getColumnModel().getColumn(6).setMinWidth(80);

    tablaFamilia
      .getColumnModel()
      .getColumn(3)
      .setCellRenderer((tbl, val, sel, foc, row, col) -> {
        boolean esJef = "Sí".equals(val);
        Color bg = esJef ? AppColors.BADGE_BG[0] : AppColors.BADGE_BG[3];
        Color fg = esJef ? AppColors.BADGE_FG[0] : AppColors.BADGE_FG[3];
        JLabel badge = new JLabel(String.valueOf(val), SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        cell.setBackground(
          sel ? tbl.getSelectionBackground() : AppColors.PANEL
        );
        cell.add(badge);
        return cell;
      });

    tablaFamilia
      .getColumnModel()
      .getColumn(6)
      .setCellRenderer((tbl, val, sel, foc, row, col) -> {
        JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        cell.setOpaque(true);
        cell.setBackground(
          sel ? tbl.getSelectionBackground() : AppColors.PANEL
        );
        JLabel editar = new JLabel("Editar");
        editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editar.setForeground(AppColors.AZUL);
        JLabel sep = new JLabel("|");
        sep.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sep.setForeground(AppColors.TEXTO_GRIS);
        JLabel eliminar = new JLabel("X");
        eliminar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eliminar.setForeground(AppColors.ROJO);
        cell.add(editar);
        cell.add(sep);
        cell.add(eliminar);
        return cell;
      });

    tablaFamilia.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          int col = tablaFamilia.columnAtPoint(e.getPoint());
          int row = tablaFamilia.rowAtPoint(e.getPoint());
          if (col != 6 || row < 0 || row >= listaMiembros.size()) return;
          Rectangle rect = tablaFamilia.getCellRect(row, col, false);
          int relX = e.getX() - rect.x;
          if (relX < 50) cargarMiembroEnFormulario(row);
          else eliminarMiembro(row);
        }
      }
    );

    JScrollPane scrollTabla = ctx.crearScrollTabla(tablaFamilia);
    panelPagFamilia.setOpaque(false);
    JPanel tablaConPag = new JPanel(new BorderLayout(0, 0));
    tablaConPag.setOpaque(false);
    tablaConPag.add(scrollTabla, BorderLayout.CENTER);
    tablaConPag.add(panelPagFamilia, BorderLayout.SOUTH);

    panelFormFamilia = crearPanelFormFamilia();
    panelFormFamilia.setVisible(false);

    p.add(barraTop, BorderLayout.NORTH);
    p.add(tablaConPag, BorderLayout.CENTER);
    p.add(panelFormFamilia, BorderLayout.SOUTH);
    return p;
  }

  /**
   * Construye el panel del formulario de agregar/editar miembro familiar.
   * Incluye búsqueda por cédula, botones de buscar/crear persona y campos de relación,
   * jefatura, ocupación, trabajo e ingreso.
   *
   * @return JPanel con encabezado azul y cuerpo del formulario
   */
  private JPanel crearPanelFormFamilia() {
    JPanel contenedor = new JPanel(new BorderLayout(0, 0));
    contenedor.setOpaque(false);
    contenedor.setBorder(new EmptyBorder(10, 0, 0, 0));

    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(AppColors.AZUL);
    header.setBorder(new EmptyBorder(8, 14, 8, 14));
    lblHeaderFormFamilia = new JLabel("Agregar miembro familiar");
    lblHeaderFormFamilia.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblHeaderFormFamilia.setForeground(AppColors.PANEL);
    header.add(lblHeaderFormFamilia, BorderLayout.WEST);

    JPanel body = new JPanel();
    body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
    body.setBackground(AppColors.PANEL);
    body.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1),
        new EmptyBorder(12, 14, 12, 14)
      )
    );

    txtBuscarCedula = new JTextField(14);
    txtBuscarCedula.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtBuscarCedula.putClientProperty(
      "JTextField.placeholderText",
      "Buscar por cédula..."
    );
    txtBuscarCedula.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    txtBuscarCedula.setPreferredSize(new Dimension(160, 30));
    txtBuscarCedula.addActionListener(e -> buscarPersonaPorCedula());

    JButton btnBuscar = UIFactory.crearBotonSmall(
      "Buscar",
      AppColors.AZUL,
      Color.WHITE,
      e -> buscarPersonaPorCedula()
    );
    JButton btnCrearNueva = UIFactory.crearBotonSmall(
      "Crear nueva",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> abrirFormNuevaPersona()
    );

    lblPersonaSeleccionada = new JLabel("Ninguna persona seleccionada");
    lblPersonaSeleccionada.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    lblPersonaSeleccionada.setForeground(AppColors.TEXTO_GRIS);

    JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    fila1.setOpaque(false);
    fila1.add(ctx.campoConEtiqueta("Persona (cédula):", txtBuscarCedula));
    JPanel btnsBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    btnsBusqueda.setOpaque(false);
    btnsBusqueda.setBorder(new EmptyBorder(18, 0, 0, 0));
    btnsBusqueda.add(btnBuscar);
    btnsBusqueda.add(btnCrearNueva);
    fila1.add(btnsBusqueda);
    JPanel wrapLbl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    wrapLbl.setOpaque(false);
    wrapLbl.setBorder(new EmptyBorder(14, 0, 0, 0));
    wrapLbl.add(lblPersonaSeleccionada);
    fila1.add(wrapLbl);
    fila1.setAlignmentX(Component.LEFT_ALIGNMENT);

    cmbRelacion = new JComboBox<>(
      new String[] {
        "Esposo/a",
        "Hijo/a",
        "Padre",
        "Madre",
        "Hermano/a",
        "Abuelo/a",
        "Tío/a",
        "Sobrino/a",
        "Otro",
      }
    );
    cmbRelacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cmbRelacion.setPreferredSize(new Dimension(130, 30));

    btnJefatura = ctx.crearBtnToggle("No");
    txtOcupacion = new JTextField(12);
    txtOcupacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtOcupacion.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    txtOcupacion.setPreferredSize(new Dimension(130, 30));

    JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    fila2.setOpaque(false);
    fila2.add(ctx.campoConEtiqueta("Relación con titular", cmbRelacion));
    fila2.add(ctx.campoConEtiqueta("Jefatura?", btnJefatura));
    fila2.add(ctx.campoConEtiqueta("Ocupación", txtOcupacion));
    fila2.setAlignmentX(Component.LEFT_ALIGNMENT);

    btnTrabaja = ctx.crearBtnToggle("Sí");
    txtIngreso = new JTextField(10);
    txtIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtIngreso.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    txtIngreso.setPreferredSize(new Dimension(120, 30));
    txtIngreso.putClientProperty("JTextField.placeholderText", "0");

    JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    fila3.setOpaque(false);
    fila3.add(ctx.campoConEtiqueta("Trabaja?", btnTrabaja));
    fila3.add(ctx.campoConEtiqueta("Ingreso mensual (₡)", txtIngreso));
    fila3.setAlignmentX(Component.LEFT_ALIGNMENT);

    JButton btnGuardar = UIFactory.crearBotonDialog(
      "Guardar",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> confirmarMiembro()
    );
    JButton btnCancelar = UIFactory.crearBotonDialog(
      "Cancelar",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> mostrarFormFamilia(false)
    );
    JPanel filaBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
    filaBtns.setOpaque(false);
    filaBtns.add(btnGuardar);
    filaBtns.add(btnCancelar);
    filaBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

    body.add(fila1);
    body.add(Box.createVerticalStrut(4));
    body.add(fila2);
    body.add(fila3);
    body.add(filaBtns);

    contenedor.add(header, BorderLayout.NORTH);
    contenedor.add(body, BorderLayout.CENTER);
    return contenedor;
  }

  /**
   * Activa el modo de solo lectura eliminando la columna de acciones de la tabla.
   *
   * @param readOnly true para activar el modo consulta; false no hace nada
   */
  public void setReadOnly(boolean readOnly) {
    if (!readOnly) return;
    if (tablaFamilia != null) tablaFamilia.removeColumn(
      tablaFamilia.getColumnModel().getColumn(6)
    );
  }

  /**
   * Carga los miembros familiares del expediente y los muestra en la tabla.
   * No hace nada si el expediente aún no tiene ID.
   */
  public void cargarDatos() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    pagFamilia.cargar(
      ctx.getExpedienteController().findMiembrosByExpediente(exp.getId())
    );
    refrescarTablaFamilia();
  }

  /**
   * Repopula la tabla con la página actual del paginador y actualiza los controles de paginación.
   */
  private void refrescarTablaFamilia() {
    modeloFamilia.setRowCount(0);
    listaMiembros.clear();
    for (MiembroFamiliar m : pagFamilia.getPagina()) {
      listaMiembros.add(m);
      String nombre = "—",
        cedula = "—";
      try {
        if (m.getPersona() != null) {
          nombre =
            ctx.nvl(m.getPersona().getNombres()) +
            " " +
            ctx.nvl(m.getPersona().getApellidos());
          cedula = ctx.nvl(m.getPersona().getNumeroDocumento());
        }
      } catch (Exception ignored) {}
      modeloFamilia.addRow(
        new Object[] {
          nombre,
          cedula,
          ctx.nvl(m.getRelacionTitular()),
          Boolean.TRUE.equals(m.getEsJefatura()) ? "Sí" : "No",
          ctx.nvl(m.getOcupacion()),
          m.getIngresoMensual() != null
            ? m.getIngresoMensual().toPlainString()
            : "0.00",
          "",
        }
      );
    }
    ctx.actualizarPanelPaginacion(
      panelPagFamilia,
      pagFamilia,
      this::refrescarTablaFamilia
    );
  }

  /**
   * Muestra u oculta el formulario de miembro.
   * Al ocultar limpia todos los campos y reinicia el estado del formulario.
   *
   * @param visible true para mostrar el formulario, false para ocultarlo y limpiar
   */
  private void mostrarFormFamilia(boolean visible) {
    if (!visible) {
      miembroEnEdicion = null;
      personaSeleccionada = null;
      txtBuscarCedula.setText("");
      lblPersonaSeleccionada.setText("Ninguna persona seleccionada");
      lblPersonaSeleccionada.setForeground(AppColors.TEXTO_GRIS);
      cmbRelacion.setSelectedIndex(0);
      ctx.resetearToggle(btnJefatura, false);
      txtOcupacion.setText("");
      ctx.resetearToggle(btnTrabaja, true);
      txtIngreso.setText("");
      lblHeaderFormFamilia.setText("Agregar miembro familiar");
    }
    panelFormFamilia.setVisible(visible);
    panelFormFamilia.getParent().revalidate();
    panelFormFamilia.getParent().repaint();
  }

  /**
   * Busca una persona por el número de cédula introducido en txtBuscarCedula.
   * Si la encuentra, asigna personaSeleccionada y muestra el nombre en verde.
   * Si no existe, limpia personaSeleccionada y muestra un mensaje en rojo.
   */
  private void buscarPersonaPorCedula() {
    String cedula = txtBuscarCedula.getText().trim();
    if (cedula.isEmpty()) return;
    Persona p = ctx
      .getExpedienteController()
      .findPersonaByNumeroDocumento(cedula);
    if (p != null) {
      personaSeleccionada = p;
      lblPersonaSeleccionada.setText(
        ctx.nvl(p.getNombres()) + " " + ctx.nvl(p.getApellidos())
      );
      lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
    } else {
      personaSeleccionada = null;
      lblPersonaSeleccionada.setText("Persona no encontrada.");
      lblPersonaSeleccionada.setForeground(AppColors.ROJO);
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "No se encontró persona con cédula \"" +
          cedula +
          "\".\nUse 'Crear nueva' para registrarla.",
        "Persona no encontrada",
        JOptionPane.INFORMATION_MESSAGE
      );
    }
  }

  /**
   * Abre un diálogo modal para registrar una nueva persona con datos mínimos
   * (nombres, apellidos, tipo de documento, número de documento y teléfono).
   * Al guardar, persiste la persona, la asigna como personaSeleccionada y cierra el diálogo.
   */
  private void abrirFormNuevaPersona() {
    JDialog dlg = new JDialog(
      ctx.getOwner(),
      "Nueva Persona",
      Dialog.ModalityType.APPLICATION_MODAL
    );
    dlg.setLayout(new BorderLayout());
    dlg.getContentPane().setBackground(AppColors.PANEL);

    JPanel form = new JPanel(new GridBagLayout());
    form.setBackground(AppColors.PANEL);
    form.setBorder(new EmptyBorder(16, 20, 8, 20));

    GridBagConstraints lc = new GridBagConstraints();
    lc.anchor = GridBagConstraints.WEST;
    lc.insets = new Insets(4, 4, 2, 4);
    GridBagConstraints fc = new GridBagConstraints();
    fc.fill = GridBagConstraints.HORIZONTAL;
    fc.weightx = 1.0;
    fc.insets = new Insets(2, 4, 4, 4);

    JTextField txtNom = new JTextField(14);
    JTextField txtApe = new JTextField(14);
    JComboBox<TipoDocumentoPersona> cmbTipo = new JComboBox<>(
      TipoDocumentoPersona.values()
    );
    JTextField txtDoc = new JTextField(12);
    JTextField txtTel = new JTextField(12);

    agregarFila(form, 0, lc, fc, "Nombres *", txtNom, "Apellidos *", txtApe);
    agregarFila(form, 1, lc, fc, "Tipo Doc *", cmbTipo, "Número Doc *", txtDoc);
    lc.gridx = 0;
    lc.gridy = 2;
    form.add(etiqueta("Teléfono"), lc);
    fc.gridx = 1;
    fc.gridy = 2;
    form.add(txtTel, fc);

    JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
    btns.setOpaque(false);
    btns.add(
      UIFactory.crearBotonDialog(
        "Guardar",
        AppColors.PRIMARIO,
        Color.WHITE,
        e -> {
          String nom = txtNom.getText().trim();
          String ape = txtApe.getText().trim();
          String doc = txtDoc.getText().trim();
          if (nom.isEmpty() || ape.isEmpty() || doc.isEmpty()) {
            JOptionPane.showMessageDialog(
              dlg,
              "Nombres, Apellidos y Número de Documento son obligatorios.",
              "Datos incompletos",
              JOptionPane.WARNING_MESSAGE
            );
            return;
          }
          if (
            ctx.getExpedienteController().findPersonaByNumeroDocumento(doc) !=
            null
          ) {
            JOptionPane.showMessageDialog(
              dlg,
              "Ya existe una persona con ese número de documento.",
              "Documento duplicado",
              JOptionPane.WARNING_MESSAGE
            );
            return;
          }
          Persona nueva = new Persona();
          nueva.setNombres(nom);
          nueva.setApellidos(ape);
          nueva.setTipoDocumento(
            (TipoDocumentoPersona) cmbTipo.getSelectedItem()
          );
          nueva.setNumeroDocumento(doc);
          nueva.setTelefono(txtTel.getText().trim());
          ctx.getExpedienteController().guardarPersona(nueva);
          personaSeleccionada = nueva;
          txtBuscarCedula.setText(doc);
          lblPersonaSeleccionada.setText(nom + " " + ape);
          lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
          dlg.dispose();
        }
      )
    );
    btns.add(
      UIFactory.crearBotonDialog(
        "Cancelar",
        AppColors.GRIS_BTN,
        AppColors.TEXTO,
        e -> dlg.dispose()
      )
    );

    dlg.add(form, BorderLayout.CENTER);
    dlg.add(btns, BorderLayout.SOUTH);
    dlg.pack();
    dlg.setMinimumSize(new Dimension(480, 200));
    dlg.setLocationRelativeTo(ctx.getOwner());
    dlg.setVisible(true);
  }

  /**
   * Valida y persiste el miembro familiar con los datos del formulario.
   * Requiere que el expediente esté guardado y que haya una persona seleccionada.
   * En modo adición detecta duplicados comparando el ID de la persona.
   */
  private void confirmarMiembro() {
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Guarde el expediente antes de agregar miembros familiares.",
        "Expediente no guardado",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    if (personaSeleccionada == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Seleccione o cree una persona primero.",
        "Persona requerida",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    if (miembroEnEdicion == null) {
      for (MiembroFamiliar m : listaMiembros) {
        if (
          m.getPersona() != null &&
          m.getPersona().getId() != null &&
          m.getPersona().getId().equals(personaSeleccionada.getId())
        ) {
          JOptionPane.showMessageDialog(
            ctx.getOwner(),
            "Esta persona ya es miembro de este expediente.",
            "Duplicado",
            JOptionPane.WARNING_MESSAGE
          );
          return;
        }
      }
    }

    MiembroFamiliar miembro = (miembroEnEdicion != null)
      ? miembroEnEdicion
      : new MiembroFamiliar();
    miembro.setPersona(personaSeleccionada);
    miembro.setExpediente(exp);
    miembro.setRelacionTitular((String) cmbRelacion.getSelectedItem());
    miembro.setEsJefatura("Sí".equals(btnJefatura.getText()));
    miembro.setOcupacion(txtOcupacion.getText().trim());
    miembro.setTrabaja("Sí".equals(btnTrabaja.getText()));
    String ingresoTxt = txtIngreso.getText().trim().replace(",", "");
    try {
      miembro.setIngresoMensual(
        !ingresoTxt.isEmpty() ? new BigDecimal(ingresoTxt) : BigDecimal.ZERO
      );
    } catch (NumberFormatException ex) {
      miembro.setIngresoMensual(BigDecimal.ZERO);
    }

    ctx.getExpedienteController().guardarMiembro(miembro);
    cargarDatos();
    mostrarFormFamilia(false);
  }

  /**
   * Carga los datos del miembro en la fila indicada en el formulario para su edición.
   *
   * @param row índice de la fila en listaMiembros (página actual)
   */
  private void cargarMiembroEnFormulario(int row) {
    if (row < 0 || row >= listaMiembros.size()) return;
    MiembroFamiliar m = listaMiembros.get(row);
    miembroEnEdicion = m;
    try {
      personaSeleccionada = m.getPersona();
    } catch (Exception ignored) {
      personaSeleccionada = null;
    }
    if (personaSeleccionada != null) {
      txtBuscarCedula.setText(
        ctx.nvl(personaSeleccionada.getNumeroDocumento())
      );
      lblPersonaSeleccionada.setText(
        ctx.nvl(personaSeleccionada.getNombres()) +
          " " +
          ctx.nvl(personaSeleccionada.getApellidos())
      );
      lblPersonaSeleccionada.setForeground(AppColors.VERDE_FG);
    }
    if (m.getRelacionTitular() != null) cmbRelacion.setSelectedItem(
      m.getRelacionTitular()
    );
    ctx.resetearToggle(btnJefatura, Boolean.TRUE.equals(m.getEsJefatura()));
    txtOcupacion.setText(ctx.nvl(m.getOcupacion()));
    ctx.resetearToggle(btnTrabaja, Boolean.TRUE.equals(m.getTrabaja()));
    txtIngreso.setText(
      m.getIngresoMensual() != null ? m.getIngresoMensual().toPlainString() : ""
    );
    lblHeaderFormFamilia.setText("Editar miembro familiar");
    mostrarFormFamilia(true);
  }

  /**
   * Pide confirmación y elimina el miembro en la fila indicada.
   * Si el miembro tiene ID en base de datos, lo borra a través del controlador.
   *
   * @param row índice de la fila en listaMiembros (página actual)
   */
  private void eliminarMiembro(int row) {
    if (row < 0 || row >= listaMiembros.size()) return;
    int confirm = JOptionPane.showConfirmDialog(
      ctx.getOwner(),
      "¿Eliminar este miembro del grupo familiar?",
      "Confirmar eliminación",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.WARNING_MESSAGE
    );
    if (confirm != JOptionPane.YES_OPTION) return;
    MiembroFamiliar m = listaMiembros.get(row);
    if (m.getId() != null) ctx
      .getExpedienteController()
      .eliminarMiembro(m.getId());
    cargarDatos();
  }

  /**
   * Agrega dos pares etiqueta-campo en una misma fila del GridBagLayout.
   *
   * @param p    panel destino
   * @param row  fila destino
   * @param lc   constraints para etiquetas
   * @param fc   constraints para campos
   * @param lbl1 texto de la primera etiqueta
   * @param c1   primer campo
   * @param lbl2 texto de la segunda etiqueta
   * @param c2   segundo campo
   */
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

  /**
   * Crea una etiqueta con la fuente y el color estándar para los campos del formulario.
   *
   * @param texto texto de la etiqueta
   * @return JLabel con Segoe UI 12pt en color TEXTO_GRIS
   */
  private JLabel etiqueta(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    l.setForeground(AppColors.TEXTO_GRIS);
    return l;
  }
}
