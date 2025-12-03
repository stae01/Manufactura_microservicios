package com.juvenr.mqc.reports;

import com.juvenr.mqc.reports.ui.LoginFrame;

public class ManufacturaQCReports {
    public static void main(String[] args) {
        // asegurarnos de levantar Swing en el hilo de UI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
