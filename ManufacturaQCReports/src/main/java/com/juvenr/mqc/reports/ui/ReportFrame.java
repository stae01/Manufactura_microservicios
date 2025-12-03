/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.juvenr.mqc.reports.ui;

import javax.swing.JFileChooser;
import java.io.File;
import com.juvenr.mqc.reports.SessionContext;
import com.juvenr.mqc.reports.api.ReportsClient;
import com.juvenr.mqc.reports.model.DetalleDefecto;
import com.juvenr.mqc.reports.model.ResumenDefecto;
import com.juvenr.mqc.reports.model.ResumenSeveridad;
import com.juvenr.mqc.reports.ui.utilerias.PanelReporte;
import com.juvenr.mqc.reports.ui.utileriasutil.PdfReportUtil;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author carli
 */
public class ReportFrame extends javax.swing.JFrame {

    private final ReportsClient reportsClient = new ReportsClient();
    private final PanelReporte panelReporte = new PanelReporte();

    /**
     * Creates new form ReportFrame
     */
    public ReportFrame() {
        initComponents();
        setTitle("Sistema de Reportes - ManufacturaQC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1289, 826);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelReporte, BorderLayout.CENTER);

        hookButtons();
        cargarReporte();

    }

    private void hookButtons() {
        panelReporte.getBtnActualizar().addActionListener(e -> cargarReporte());

        panelReporte.getBtnGenerarReporte().addActionListener(e -> {
            if (panelReporte.getTblReporte().getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hay datos en la tabla para generar el reporte.",
                        "Sin datos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                //  Root del proyecto
                String projectRoot = System.getProperty("user.dir");

                // Nombre del archivo (puedes personalizarlo)
                File file = new File(projectRoot, "reporte-calidad.pdf");

                // Generar el PDF
                PdfReportUtil.exportTableToPdf(panelReporte.getTblReporte(), file);

                // Avisar dónde quedó
                JOptionPane.showMessageDialog(
                        this,
                        "Reporte generado en:\n" + file.getAbsolutePath(),
                        "PDF generado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo abrir automáticamente el PDF.\n"
                            + "Ábrelo manualmente desde:\n" + file.getAbsolutePath(),
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Error al generar/abrir el PDF: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void cargarReporte() {
        String token = SessionContext.getJwtToken();
        if (token == null || token.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay token de sesión. Inicia sesión nuevamente.",
                    "Sesión inválida",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        String tipoReporte = panelReporte.getTipoReporteSeleccionado();
        String filtroDefecto = panelReporte.getFiltroTipoDefecto();
        String filtroSeveridad = panelReporte.getFiltroSeveridad();

        new SwingWorker<Void, Void>() {

            private List<ResumenDefecto> resumenDefectos;
            private List<ResumenSeveridad> resumenSeveridades;
            private List<DetalleDefecto> detalleDefectos;
            private Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    if ("Resumen por tipo de defecto".equals(tipoReporte)) {
                        resumenDefectos = reportsClient.getResumenDefectos(token);
                    } else if ("Resumen por severidad".equals(tipoReporte)) {
                        resumenSeveridades = reportsClient.getResumenSeveridad(token);
                    } else { // "Detalle de piezas rechazadas"
                        detalleDefectos = reportsClient.getDetalleDefectos(
                                token,
                                filtroDefecto,
                                filtroSeveridad
                        );
                    }
                } catch (IOException | InterruptedException | RuntimeException ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(
                            ReportFrame.this,
                            "Error al cargar reportes: " + error.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                if ("Resumen por tipo de defecto".equals(tipoReporte)) {
                    llenarTablaResumenTipo(resumenDefectos);
                } else if ("Resumen por severidad".equals(tipoReporte)) {
                    llenarTablaResumenSeveridad(resumenSeveridades);
                } else {
                    llenarTablaDetalle(detalleDefectos);
                }
            }
        }.execute();
    }

    private void llenarTablaResumenTipo(List<ResumenDefecto> data) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Tipo de defecto", "Cantidad", "Total MXN", "Total USD"},
                0
        );

        if (data != null) {
            for (ResumenDefecto item : data) {
                model.addRow(new Object[]{
                    item.getTipo(),
                    item.getCantidad(),
                    String.format("%.2f", item.getTotalMXN()),
                    String.format("%.2f", item.getTotalUSD())
                });
            }
        }

        panelReporte.getTblReporte().setModel(model);
    }

    private void llenarTablaResumenSeveridad(List<ResumenSeveridad> data) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Severidad", "Cantidad", "Total MXN", "Total USD"},
                0
        );

        if (data != null) {
            for (ResumenSeveridad item : data) {
                model.addRow(new Object[]{
                    item.getSeveridad(),
                    item.getCantidad(),
                    String.format("%.2f", item.getTotalMXN()),
                    String.format("%.2f", item.getTotalUSD())
                });
            }
        }

        panelReporte.getTblReporte().setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1289, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 826, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void llenarTablaDetalle(List<DetalleDefecto> data) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Tipo", "Severidad", "Lote", "Costo MXN", "Costo USD", "Fecha"},
                0
        );

        if (data != null) {
            for (DetalleDefecto d : data) {
                model.addRow(new Object[]{
                    d.getId(),
                    d.getTipo(),
                    d.getSeveridad(),
                    d.getLote(),
                    String.format("%.2f", d.getCostoMXN()),
                    String.format("%.2f", d.getCostoUSD()),
                    d.getCreatedAt()
                });
            }
        }

        panelReporte.getTblReporte().setModel(model);
    }

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
