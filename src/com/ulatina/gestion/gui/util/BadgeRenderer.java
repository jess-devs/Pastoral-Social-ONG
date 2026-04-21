package com.ulatina.gestion.gui.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer de celda para la columna EstadoExpediente en tablas.
 * Muestra un badge de color según el estado: ACTIVO, EN_PROCESO, CERRADO,
 * SUSPENDIDO.
 */
public class BadgeRenderer extends DefaultTableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(
    JTable table,
    Object value,
    boolean isSelected,
    boolean hasFocus,
    int row,
    int column
  ) {
    String estado = value != null ? value.toString() : "";
    int idx = estadoIndex(estado);

    JLabel lbl = new JLabel(estado.replace("_", " "));
    lbl.setHorizontalAlignment(SwingConstants.CENTER);
    lbl.setOpaque(true);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lbl.setBackground(AppColors.BADGE_BG[idx]);
    lbl.setForeground(AppColors.BADGE_FG[idx]);
    lbl.setBorder(new EmptyBorder(3, 10, 3, 10));

    JPanel badge = new JPanel(new BorderLayout());
    badge.setBackground(AppColors.BADGE_BG[idx]);
    badge.setBorder(
      BorderFactory.createCompoundBorder(
        new LineBorder(AppColors.BADGE_FG[idx].brighter(), 1, true),
        new EmptyBorder(2, 8, 2, 8)
      )
    );
    badge.add(lbl);

    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setBackground(isSelected ? AppColors.FILA_SEL : AppColors.PANEL);
    wrapper.add(badge);
    return wrapper;
  }

  private static int estadoIndex(String estado) {
    switch (estado) {
      case "ACTIVO":
        return 0;
      case "EN_PROCESO":
        return 1;
      case "CERRADO":
        return 2;
      default:
        return 3;
    }
  }
}
