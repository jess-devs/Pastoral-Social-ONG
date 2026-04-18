package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.DocumentoAdjunto;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.enums.TipoDocumentoAdjunto;
import com.ulatina.gestion.util.FileStorageUtil;
import com.ulatina.gestion.util.SessionContext;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/** Tab 5 — Documentos adjuntos del expediente. */
public class TabDocs {

  private final ExpedienteContext ctx;

  private JPanel panelSubidaDoc;
  private JPanel panelListaDocs;
  private File archivoSeleccionado;
  private JLabel lblArchivoNombre;
  private JComboBox<TipoDocumentoAdjunto> cmbTipoDocAdjunto;
  private JTextField txtDescripcionDoc;
  private JCheckBox chkEsFirmado;
  private JPanel panelFirmante;
  private JTextField txtNombreFirmante;
  private JFormattedTextField txtFechaFirmaDoc;

  private final Paginador<DocumentoAdjunto> pagDocs = new Paginador<>();
  private final JPanel panelPagDocs = new JPanel(
    new FlowLayout(FlowLayout.CENTER, 8, 2)
  );

  public TabDocs(ExpedienteContext ctx) {
    this.ctx = ctx;
  }

  public JPanel construir() {
    JPanel p = new JPanel(new BorderLayout(0, 12));
    p.setBackground(AppColors.PANEL);
    p.setBorder(new EmptyBorder(16, 24, 16, 24));
    panelSubidaDoc = crearPanelSubidaDoc();
    p.add(panelSubidaDoc, BorderLayout.NORTH);

    panelListaDocs = new JPanel();
    panelListaDocs.setLayout(new BoxLayout(panelListaDocs, BoxLayout.Y_AXIS));
    panelListaDocs.setBackground(AppColors.PANEL);

    JScrollPane scroll = new JScrollPane(panelListaDocs);
    scroll.setBorder(new LineBorder(AppColors.BORDE, 1, true));
    scroll.getViewport().setBackground(AppColors.PANEL);

    JLabel lblDocs = new JLabel("Documentos adjuntos");
    lblDocs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblDocs.setForeground(AppColors.TEXTO_GRIS);

    panelPagDocs.setOpaque(false);
    JPanel scrollConPag = new JPanel(new BorderLayout(0, 0));
    scrollConPag.setOpaque(false);
    scrollConPag.add(scroll, BorderLayout.CENTER);
    scrollConPag.add(panelPagDocs, BorderLayout.SOUTH);

    JPanel centro = new JPanel(new BorderLayout(0, 6));
    centro.setBackground(AppColors.PANEL);
    centro.add(lblDocs, BorderLayout.NORTH);
    centro.add(scrollConPag, BorderLayout.CENTER);

    p.add(centro, BorderLayout.CENTER);
    return p;
  }

  private JPanel crearPanelSubidaDoc() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBackground(AppColors.PANEL);
    p.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(12, 16, 12, 16)
      )
    );

    JPanel filePicker = new JPanel(new BorderLayout(8, 0));
    filePicker.setBackground(AppColors.FONDO);
    filePicker.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE, 1, true),
        new EmptyBorder(10, 12, 10, 12)
      )
    );
    filePicker.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

    lblArchivoNombre = new JLabel("Ningún archivo seleccionado");
    lblArchivoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblArchivoNombre.setForeground(AppColors.TEXTO_GRIS);

    JButton btnElegir = UIFactory.crearBotonSmall(
      "Seleccionar archivo...",
      AppColors.GRIS_BTN,
      AppColors.TEXTO,
      e -> elegirArchivo()
    );
    filePicker.add(lblArchivoNombre, BorderLayout.CENTER);
    filePicker.add(btnElegir, BorderLayout.EAST);

    JPanel fila1 = new JPanel(new GridLayout(1, 2, 12, 0));
    fila1.setBackground(AppColors.PANEL);
    fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

    JPanel grpTipo = new JPanel(new BorderLayout(0, 4));
    grpTipo.setBackground(AppColors.PANEL);
    JLabel lblTipoDoc = new JLabel("Tipo de documento");
    lblTipoDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblTipoDoc.setForeground(AppColors.TEXTO_GRIS);
    cmbTipoDocAdjunto = new JComboBox<>(TipoDocumentoAdjunto.values());
    cmbTipoDocAdjunto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    grpTipo.add(lblTipoDoc, BorderLayout.NORTH);
    grpTipo.add(cmbTipoDocAdjunto, BorderLayout.CENTER);

    JPanel grpDesc = new JPanel(new BorderLayout(0, 4));
    grpDesc.setBackground(AppColors.PANEL);
    JLabel lblDescDoc = new JLabel("Descripción (opcional)");
    lblDescDoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblDescDoc.setForeground(AppColors.TEXTO_GRIS);
    txtDescripcionDoc = new JTextField();
    txtDescripcionDoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtDescripcionDoc.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    grpDesc.add(lblDescDoc, BorderLayout.NORTH);
    grpDesc.add(txtDescripcionDoc, BorderLayout.CENTER);

    fila1.add(grpTipo);
    fila1.add(grpDesc);

    chkEsFirmado = new JCheckBox("Es consentimiento firmado");
    chkEsFirmado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    chkEsFirmado.setBackground(AppColors.PANEL);
    chkEsFirmado.setForeground(AppColors.TEXTO);

    panelFirmante = new JPanel(new GridLayout(1, 2, 12, 0));
    panelFirmante.setBackground(AppColors.PANEL);
    panelFirmante.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
    panelFirmante.setVisible(false);

    JPanel grpFirmante = new JPanel(new BorderLayout(0, 4));
    grpFirmante.setBackground(AppColors.PANEL);
    JLabel lblFirmante = new JLabel("Nombre firmante");
    lblFirmante.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblFirmante.setForeground(AppColors.TEXTO_GRIS);
    txtNombreFirmante = new JTextField();
    txtNombreFirmante.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtNombreFirmante.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    grpFirmante.add(lblFirmante, BorderLayout.NORTH);
    grpFirmante.add(txtNombreFirmante, BorderLayout.CENTER);

    JPanel grpFechaFirma = new JPanel(new BorderLayout(0, 4));
    grpFechaFirma.setBackground(AppColors.PANEL);
    JLabel lblFechaFirma = new JLabel("Fecha firma");
    lblFechaFirma.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblFechaFirma.setForeground(AppColors.TEXTO_GRIS);
    txtFechaFirmaDoc = UIFactory.crearCampoFecha();
    txtFechaFirmaDoc.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BORDE),
        new EmptyBorder(4, 8, 4, 8)
      )
    );
    grpFechaFirma.add(lblFechaFirma, BorderLayout.NORTH);
    grpFechaFirma.add(txtFechaFirmaDoc, BorderLayout.CENTER);

    panelFirmante.add(grpFirmante);
    panelFirmante.add(grpFechaFirma);
    chkEsFirmado.addActionListener(e ->
      panelFirmante.setVisible(chkEsFirmado.isSelected())
    );

    JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    btnRow.setBackground(AppColors.PANEL);
    JButton btnSubir = UIFactory.crearBoton(
      "Subir documento",
      AppColors.PRIMARIO,
      Color.WHITE,
      e -> subirDocumento()
    );
    btnRow.add(btnSubir);

    p.add(filePicker);
    p.add(Box.createVerticalStrut(10));
    p.add(fila1);
    p.add(Box.createVerticalStrut(8));
    p.add(chkEsFirmado);
    p.add(panelFirmante);
    p.add(Box.createVerticalStrut(10));
    p.add(btnRow);
    return p;
  }

  public void setReadOnly(boolean readOnly) {
    if (!readOnly) return;
    if (panelSubidaDoc != null) panelSubidaDoc.setVisible(false);
  }

  public void cargarDatos() {
    if (panelListaDocs == null) return;
    Expediente exp = ctx.getExpediente();
    if (exp == null || exp.getId() == null) return;
    pagDocs.cargar(
      ctx.getExpedienteController().findDocsByExpediente(exp.getId())
    );
    refrescarListaDocs();
  }

  private void elegirArchivo() {
    JFileChooser fc = new JFileChooser();
    fc.setDialogTitle("Seleccionar documento");
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (fc.showOpenDialog(ctx.getOwner()) == JFileChooser.APPROVE_OPTION) {
      archivoSeleccionado = fc.getSelectedFile();
      lblArchivoNombre.setText(archivoSeleccionado.getName());
      lblArchivoNombre.setForeground(AppColors.TEXTO);
    }
  }

  private void subirDocumento() {
    if (archivoSeleccionado == null) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Selecciona un archivo primero.",
        "Validación",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    if (archivoSeleccionado.length() > FileStorageUtil.MAX_BYTES) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "El archivo supera el límite de 5 MB.",
        "Archivo muy grande",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    Expediente exp = ctx.getExpediente();
    String nombreNuevo = archivoSeleccionado.getName();
    boolean yaExiste = ctx
      .getExpedienteController()
      .findDocsByExpediente(exp.getId())
      .stream()
      .anyMatch(
        d ->
          d.getArchivoUrl() != null && d.getArchivoUrl().endsWith(nombreNuevo)
      );
    if (yaExiste) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Ya existe un documento con el nombre \"" +
          nombreNuevo +
          "\" en este expediente.",
        "Documento duplicado",
        JOptionPane.WARNING_MESSAGE
      );
      return;
    }
    try {
      File copiado = FileStorageUtil.copiarArchivo(
        archivoSeleccionado,
        exp.getNumeroFicha()
      );
      String rutaRel = FileStorageUtil.rutaRelativa(
        exp.getNumeroFicha(),
        copiado.getName()
      );

      DocumentoAdjunto doc = new DocumentoAdjunto();
      doc.setExpediente(exp);
      doc.setTipo((TipoDocumentoAdjunto) cmbTipoDocAdjunto.getSelectedItem());
      String desc = txtDescripcionDoc.getText().trim();
      doc.setDescripcion(desc.isEmpty() ? null : desc);
      doc.setArchivoUrl(rutaRel);
      doc.setFechaSubida(new Date());
      doc.setEsDocFirmado(chkEsFirmado.isSelected());
      if (chkEsFirmado.isSelected()) {
        doc.setNombreFirmante(txtNombreFirmante.getText().trim());
        String fechaStr = txtFechaFirmaDoc.getText().replace("_", "").trim();
        if (fechaStr.length() == 10) {
          try {
            doc.setFechaFirma(
              new SimpleDateFormat("dd/MM/yyyy").parse(fechaStr)
            );
          } catch (ParseException ignored) {}
        }
      }
      doc.setSubidoPor(SessionContext.getUsuarioActual());
      ctx.getExpedienteController().guardarDocumento(doc);

      archivoSeleccionado = null;
      lblArchivoNombre.setText("Ningún archivo seleccionado");
      lblArchivoNombre.setForeground(AppColors.TEXTO_GRIS);
      txtDescripcionDoc.setText("");
      chkEsFirmado.setSelected(false);
      panelFirmante.setVisible(false);
      txtNombreFirmante.setText("");
      txtFechaFirmaDoc.setValue(null);
      cargarDatos();
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al copiar el archivo: " + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
        ctx.getOwner(),
        "Error al guardar el documento: " + ex.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  private void refrescarListaDocs() {
    panelListaDocs.removeAll();
    List<DocumentoAdjunto> pagina = pagDocs.getPagina();
    if (pagina.isEmpty() && pagDocs.getTodos().isEmpty()) {
      JLabel lblVacio = new JLabel("No hay documentos adjuntos.");
      lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
      lblVacio.setForeground(AppColors.TEXTO_GRIS);
      lblVacio.setBorder(new EmptyBorder(16, 12, 16, 12));
      panelListaDocs.add(lblVacio);
    } else {
      for (DocumentoAdjunto d : pagina) panelListaDocs.add(crearFilaDoc(d));
    }
    panelListaDocs.revalidate();
    panelListaDocs.repaint();
    ctx.actualizarPanelPaginacion(
      panelPagDocs,
      pagDocs,
      this::refrescarListaDocs
    );
  }

  private JPanel crearFilaDoc(DocumentoAdjunto doc) {
    String nombreArchivo =
      doc.getArchivoUrl() != null
        ? new File(doc.getArchivoUrl()).getName()
        : "—";

    JPanel fila = new JPanel(new BorderLayout(8, 0));
    fila.setBackground(AppColors.PANEL);
    fila.setBorder(
      BorderFactory.createCompoundBorder(
        new MatteBorder(0, 0, 1, 0, AppColors.BORDE),
        new EmptyBorder(10, 12, 10, 12)
      )
    );
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

    JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    izq.setBackground(AppColors.PANEL);
    JLabel lblNombreDoc = new JLabel(nombreArchivo);
    lblNombreDoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblNombreDoc.setForeground(AppColors.TEXTO);
    izq.add(lblNombreDoc);
    izq.add(crearBadgeTipo(doc.getTipo()));
    if (Boolean.TRUE.equals(doc.getEsDocFirmado())) izq.add(
      ctx.crearBadge("Firmado", AppColors.VERDE_BG, AppColors.VERDE_FG)
    );

    String fecha =
      doc.getFechaSubida() != null
        ? ctx.getSdf().format(doc.getFechaSubida())
        : "—";
    String subidoPor =
      doc.getSubidoPor() != null ? doc.getSubidoPor().getNombre() : "—";
    String firmaInfo =
      Boolean.TRUE.equals(doc.getEsDocFirmado()) &&
      doc.getNombreFirmante() != null
        ? "  ·  Firma: " + doc.getNombreFirmante()
        : "";
    JLabel lblMeta = new JLabel(
      fecha + "  ·  Subido por: " + subidoPor + firmaInfo
    );
    lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lblMeta.setForeground(AppColors.TEXTO_GRIS);

    JPanel centro = new JPanel(new BorderLayout(0, 2));
    centro.setBackground(AppColors.PANEL);
    centro.add(izq, BorderLayout.NORTH);
    centro.add(lblMeta, BorderLayout.SOUTH);

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    der.setBackground(AppColors.PANEL);
    JButton btnVer = UIFactory.crearBotonSmall(
      "Ver",
      AppColors.AZUL_PANEL,
      AppColors.AZUL,
      e -> {
        try {
          FileStorageUtil.abrirArchivo(doc.getArchivoUrl());
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(
            ctx.getOwner(),
            "No se pudo abrir el archivo: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    );
    JButton btnEliminar = UIFactory.crearBotonSmall(
      "X",
      AppColors.ROJO_CARD_BG,
      AppColors.ROJO,
      e -> {
        int conf = JOptionPane.showConfirmDialog(
          ctx.getOwner(),
          "¿Eliminar el documento \"" +
            nombreArchivo +
            "\"?" +
                  "\nEsta acción no se puede deshacer.",
          "Confirmar eliminación",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE
        );
        if (conf == JOptionPane.YES_OPTION) {
          FileStorageUtil.eliminarArchivo(doc.getArchivoUrl());
          ctx.getExpedienteController().eliminarDocumento(doc.getId());
          cargarDatos();
        }
      }
    );
    der.add(btnVer);
    der.add(btnEliminar);

    fila.add(centro, BorderLayout.CENTER);
    fila.add(der, BorderLayout.EAST);
    return fila;
  }

  private JLabel crearBadgeTipo(TipoDocumentoAdjunto tipo) {
    Color bg, fg;
    switch (tipo != null ? tipo : TipoDocumentoAdjunto.OTRO) {
      case CEDULA:
      case PASAPORTE:
        bg = AppColors.AZUL_CARD_BG;
        fg = AppColors.AZUL_CARD_FG;
        break;
      case CONSENTIMIENTO:
        bg = AppColors.VERDE_BG;
        fg = AppColors.VERDE_FG;
        break;
      case INFO_MEDICA:
      case DICTAMEN:
      case RECETA:
        bg = AppColors.ROJO_CARD_BG;
        fg = AppColors.ROJO_CARD_FG;
        break;
      case FACTURA:
        bg = AppColors.PURP_BG;
        fg = AppColors.PURPURA;
        break;
      default:
        bg = AppColors.AMBAR_BG;
        fg = AppColors.AMBAR_FG;
        break;
    }
    String texto = tipo != null ? tipo.name().replace("_", " ") : "OTRO";
    return ctx.crearBadge(texto, bg, fg);
  }
}
