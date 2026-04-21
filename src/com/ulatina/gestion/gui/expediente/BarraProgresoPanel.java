package com.ulatina.gestion.gui.expediente;

import com.ulatina.gestion.gui.util.AppColors;
import com.ulatina.gestion.model.enums.EtapaExpediente;
import java.awt.*;
import javax.swing.*;

/**
 * Panel que dibuja la barra de progreso del expediente como una línea de círculos conectados.
 * Cada círculo representa una etapa de EtapaExpediente.
 * Las etapas completadas se pintan en verde, la activa en azul primario y las pendientes en gris.
 */
public class BarraProgresoPanel extends JPanel {

  /** Índice de la etapa activa según EtapaExpediente.ordinal(). */
  private int idx;

  /**
   * Crea el panel inicializado en la etapa indicada.
   * @param idx índice de la etapa activa (valor de EtapaExpediente.ordinal())
   */
  public BarraProgresoPanel(int idx) {
    this.idx = idx;
    setOpaque(false);
    setPreferredSize(new Dimension(0, 38));
  }

  /**
   * Actualiza la etapa activa y fuerza un repintado inmediato.
   * @param idx nuevo índice de etapa activa
   */
  public void setIdx(int idx) {
    this.idx = idx;
    repaint();
  }

  /**
   * Dibuja la línea base, la línea de progreso y los nodos de cada etapa con antialiasing.
   * El nodo activo es más grande e incluye el nombre de la etapa debajo.
   * @param g contexto gráfico proporcionado por Swing
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    EtapaExpediente[] etapas = EtapaExpediente.values();
    int n = etapas.length;
    int w = getWidth();
    int cy = 12;
    int r = 5;
    int rAct = 7;
    int step = w / (n + 1);

    g2.setColor(AppColors.HEADER_TBL);
    g2.setStroke(
      new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    );
    g2.drawLine(step, cy, w - step, cy);

    if (idx > 0) {
      g2.setColor(AppColors.VERDE_BG);
      g2.setStroke(
        new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
      );
      g2.drawLine(step, cy, step * (idx + 1), cy);
    }

    for (int i = 0; i < n; i++) {
      int cx = step * (i + 1);
      if (i < idx) {
        g2.setColor(AppColors.VERDE_BG);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        g2.setColor(AppColors.VERDE_FG);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
      } else if (i == idx) {
        g2.setColor(AppColors.PRIMARIO);
        g2.fillOval(cx - rAct, cy - rAct, rAct * 2, rAct * 2);
        String nombre = etapas[i].getDisplay();
        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(AppColors.TEXTO);
        g2.drawString(
          nombre,
          cx - fm.stringWidth(nombre) / 2,
          cy + rAct + fm.getAscent() + 1
        );
      } else {
        g2.setColor(AppColors.HEADER_TBL);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        g2.setColor(AppColors.TEXTO_GRIS);
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
      }
    }
    g2.dispose();
  }
}
