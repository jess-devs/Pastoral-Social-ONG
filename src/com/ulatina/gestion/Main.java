package com.ulatina.gestion;

import com.ulatina.gestion.gui.FrmLogin;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error en Clase " + e.getMessage());
            }
            try {
                FrmLogin frmLogin = new FrmLogin();
                frmLogin.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }
}
