package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.controller.ExpedienteController;
import com.ulatina.gestion.controller.ParroquiaController;
import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.gui.util.UIFactory;
import com.ulatina.gestion.model.Adendum;
import com.ulatina.gestion.model.Expediente;
import com.ulatina.gestion.model.Persona;
import com.ulatina.gestion.model.Vivienda;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Objeto de contexto compartido entre FrmDetalleExpediente y sus tabs.
 * Centraliza el estado mutable, los servicios y los helpers de UI comunes.
 */
public class ExpedienteContext {

  // ─── Estado mutable compartido ───────────────────────────────────────────
  private Expediente expediente;
  private Persona titular;
  private Vivienda viviendaActual;
  private Adendum adendumActual;

  // ─── Servicios ───────────────────────────────────────────────────────────
  private final ExpedienteController expedienteController;
  private final ParroquiaController parroquiaController;

  // ─── Constantes de sesión ────────────────────────────────────────────────
  private final boolean esNuevo;
  private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  // ─── Referencia a la ventana para JOptionPane ────────────────────────────
  private final Window owner;

  public ExpedienteContext(
    Expediente expediente,
    boolean esNuevo,
    ExpedienteController ec,
    ParroquiaController pc,
    Window owner
  ) {
    this.expediente = expediente;
    this.esNuevo = esNuevo;
    this.expedienteController = ec;
    this.parroquiaController = pc;
    this.owner = owner;
  }

  // ─── Getters / setters del estado mutable ────────────────────────────────

  public Expediente getExpediente() {
    return expediente;
  }

  public void setExpediente(Expediente e) {
    this.expediente = e;
  }

  public Persona getTitular() {
    return titular;
  }

  public void setTitular(Persona p) {
    this.titular = p;
  }

  public Vivienda getViviendaActual() {
    return viviendaActual;
  }

  public void setViviendaActual(Vivienda v) {
    this.viviendaActual = v;
  }

  public Adendum getAdendumActual() {
    return adendumActual;
  }

  public void setAdendumActual(Adendum a) {
    this.adendumActual = a;
  }

  // ─── Getters de constantes ───────────────────────────────────────────────

  public boolean isEsNuevo() {
    return esNuevo;
  }

  public SimpleDateFormat getSdf() {
    return sdf;
  }

  public ExpedienteController getExpedienteController() {
    return expedienteController;
  }

  public ParroquiaController getParroquiaController() {
    return parroquiaController;
  }

  public Window getOwner() {
    return owner;
  }

  // ─── Helpers de datos ────────────────────────────────────────────────────

  public String nvl(String s) {
    return s != null ? s : "";
  }

  public Date parseFecha(String texto) {
    if (
      texto == null || texto.trim().isEmpty() || texto.contains("_")
    ) return null;
    try {
      return sdf.parse(texto.trim());
    } catch (ParseException ignored) {
      return null;
    }
  }

  // ─── Helpers de UI ───────────────────────────────────────────────────────

  public JScrollPane crearScrollTabla(JTable tabla) {
    JScrollPane s = new JScrollPane(tabla);
    s.setBorder(new LineBorder(AppColors.BORDE, 1, true));
    s.getViewport().setBackground(AppColors.PANEL);
    return s;
  }

  public JPanel campoConEtiqueta(String texto, JComponent campo) {
    JPanel wrap = new JPanel(new BorderLayout(0, 3));
    wrap.setOpaque(false);
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lbl.setForeground(AppColors.TEXTO_GRIS);
    wrap.add(lbl, BorderLayout.NORTH);
    wrap.add(campo, BorderLayout.CENTER);
    return wrap;
  }

  /** Botón que alterna entre "Sí" (verde) y "No" (gris) al hacer clic. */
  public JButton crearBtnToggle(String estadoInicial) {
    boolean[] estado = { estadoInicial.equals("Sí") };
    JButton btn = UIFactory.crearBotonSmall(
      estado[0] ? "Sí" : "No",
      estado[0] ? AppColors.PRIMARIO : AppColors.GRIS_BTN,
      estado[0] ? AppColors.PANEL : AppColors.TEXTO
    );
    btn.setPreferredSize(new Dimension(52, 30));
    btn.addActionListener(e -> {
      estado[0] = !estado[0];
      btn.setText(estado[0] ? "Sí" : "No");
      btn.setBackground(estado[0] ? AppColors.PRIMARIO : AppColors.GRIS_BTN);
      btn.setForeground(estado[0] ? AppColors.PANEL : AppColors.TEXTO);
      btn.repaint();
    });
    return btn;
  }

  /** Sincroniza el estado visual de un botón toggle con un valor booleano. */
  public void resetearToggle(JButton btn, boolean estadoSi) {
    btn.setText(estadoSi ? "Sí" : "No");
    btn.setBackground(estadoSi ? AppColors.PRIMARIO : AppColors.GRIS_BTN);
    btn.setForeground(estadoSi ? AppColors.PANEL : AppColors.TEXTO);
    btn.repaint();
  }

  public void configurarTabla(JTable tabla, int rowHeight) {
    tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tabla.setRowHeight(rowHeight);
    tabla.setShowVerticalLines(false);
    tabla.setGridColor(AppColors.GRID_TBL);
    tabla.setFocusable(false);
    tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
    tabla.getTableHeader().setBackground(AppColors.HEADER_TBL);
    tabla.getTableHeader().setForeground(AppColors.TEXTO_GRIS);
    tabla
      .getTableHeader()
      .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDE));
  }

  public void configurarTabla(JTable tabla) {
    configurarTabla(tabla, 34);
  }

  public void actualizarPanelPaginacion(
    JPanel panel,
    Paginador<?> pag,
    Runnable refresh
  ) {
    panel.removeAll();
    if (pag.necesitaPaginacion()) {
      JButton btnAnt = new JButton("< Anterior");
      JButton btnSig = new JButton("Siguiente >");
      JLabel lblPag = new JLabel(pag.etiqueta());
      Font f = new Font("Segoe UI", Font.PLAIN, 11);
      btnAnt.setFont(f);
      btnSig.setFont(f);
      lblPag.setFont(f);
      lblPag.setForeground(AppColors.TEXTO_GRIS);
      btnAnt.setEnabled(pag.hayAnterior());
      btnSig.setEnabled(pag.haySiguiente());
      btnAnt.addActionListener(e -> {
        pag.anterior();
        refresh.run();
      });
      btnSig.addActionListener(e -> {
        pag.siguiente();
        refresh.run();
      });
      panel.add(btnAnt);
      panel.add(lblPag);
      panel.add(btnSig);
    }
    panel.revalidate();
    panel.repaint();
  }

  /** Badge redondeado con texto, color de fondo y color de texto. */
  public JLabel crearBadge(String texto, Color bg, Color fg) {
    JLabel badge = new JLabel(" " + texto + " ") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(getBackground());
        g2.fill(
          new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12)
        );
        g2.dispose();
        super.paintComponent(g);
      }
    };
    badge.setOpaque(false);
    badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
    badge.setBackground(bg);
    badge.setForeground(fg);
    return badge;
  }
}
