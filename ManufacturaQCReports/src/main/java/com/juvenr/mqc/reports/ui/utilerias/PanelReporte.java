package com.juvenr.mqc.reports.ui.utilerias;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelReporte extends javax.swing.JPanel {

    public PanelReporte() {
        initComponents();
        inicializarTablaVacia();
        actualizarEstadoFiltros(); 
    }

    /** Tipo de reporte: resumen por tipo, severidad o detalle */
    public String getTipoReporteSeleccionado() {
        return (String) cbxTipoReporte.getSelectedItem();
    }

    /** Filtro de tipo de defecto (puede ser "Todos") */
    public String getFiltroTipoDefecto() {
        return (String) cbxFiltroTipoDefecto.getSelectedItem();
    }

    /** Filtro de severidad (puede ser "Todas") */
    public String getFiltroSeveridad() {
        return (String) cbxFiltroSeveridad.getSelectedItem();
    }

    /** Tabla para que desde fuera le cambies el modelo */
    public JTable getTblReporte() {
        return tblReporte;
    }

    /** Botón de actualizar, por si quieres agregar listeners desde el frame */
    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    /** Botón de generar reporte*/
    public JButton getBtnGenerarReporte() {
        return btnGenerarReporte;
    }

    /**
     * Deja una estructura básica de columnas por default.
     * Luego la puedes cambiar según el tipo de reporte.
     */
    private void inicializarTablaVacia() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Columna 1", "Columna 2", "Columna 3", "Columna 4"}
        );
        tblReporte.setModel(model);
    }

    /**
     * Habilita o deshabilita filtros según el tipo de reporte.
     * Solo se activan cuando el tipo es "Detalle de piezas rechazadas".
     */
    private void actualizarEstadoFiltros() {
        String tipo = getTipoReporteSeleccionado();
        boolean esDetalle = "Detalle de piezas rechazadas".equals(tipo);

        cbxFiltroTipoDefecto.setEnabled(esDetalle);
        cbxFiltroSeveridad.setEnabled(esDetalle);

        if (!esDetalle) {
            // Resetear a valores neutros cuando no aplica detalle
            cbxFiltroTipoDefecto.setSelectedIndex(0); // "Todos"
            cbxFiltroSeveridad.setSelectedIndex(0);   // "Todas"
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanelTitulo = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jPanelContenido = new javax.swing.JPanel();
        jLabelTipoReporte = new javax.swing.JLabel();
        cbxTipoReporte = new javax.swing.JComboBox<>();
        btnActualizar = new javax.swing.JButton();
        btnGenerarReporte = new javax.swing.JButton();
        jLabelFiltroDefecto = new javax.swing.JLabel();
        cbxFiltroTipoDefecto = new javax.swing.JComboBox<>();
        jLabelFiltroSeveridad = new javax.swing.JLabel();
        cbxFiltroSeveridad = new javax.swing.JComboBox<>();
        jScrollPaneTabla = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jPanelTitulo.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTitulo.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabelTitulo.setText("Reportes de Calidad");

        javax.swing.GroupLayout jPanelTituloLayout = new javax.swing.GroupLayout(jPanelTitulo);
        jPanelTitulo.setLayout(jPanelTituloLayout);
        jPanelTituloLayout.setHorizontalGroup(
            jPanelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTituloLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabelTitulo)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanelTituloLayout.setVerticalGroup(
            jPanelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTituloLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabelTitulo)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        add(jPanelTitulo, java.awt.BorderLayout.PAGE_START);

        jPanelContenido.setBackground(new java.awt.Color(255, 255, 255));

        jLabelTipoReporte.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabelTipoReporte.setText("Tipo de reporte");

        cbxTipoReporte.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbxTipoReporte.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[] {
                "Resumen por tipo de defecto",
                "Resumen por severidad",
                "Detalle de piezas rechazadas"
            }
        ));
        cbxTipoReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTipoReporteActionPerformed(evt);
            }
        });

        btnActualizar.setBackground(new java.awt.Color(15, 23, 42));
        btnActualizar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setBorder(null);
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnGenerarReporte.setBackground(new java.awt.Color(15, 23, 42));
        btnGenerarReporte.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnGenerarReporte.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarReporte.setText("Generar reporte");
        btnGenerarReporte.setBorder(null);
        btnGenerarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReporteActionPerformed(evt);
            }
        });

        jLabelFiltroDefecto.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelFiltroDefecto.setText("Tipo de defecto");

        cbxFiltroTipoDefecto.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        cbxFiltroTipoDefecto.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[] {
                "Todos",
                "Fisura en carcasa",
                "Desalineación",
                "Falta de componente",
                "Daño superficial",
                "Error de ensamblaje",
                "Medida fuera de tolerancia"
            }
        ));

        jLabelFiltroSeveridad.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelFiltroSeveridad.setText("Severidad");

        cbxFiltroSeveridad.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        cbxFiltroSeveridad.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[] {
                "Todas",
                "BAJA",
                "MEDIA",
                "ALTA",
                "CRITICA"
            }
        ));

        tblReporte.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Columna 1", "Columna 2", "Columna 3", "Columna 4"
            }
        ));
        tblReporte.setRowHeight(28);
        jScrollPaneTabla.setViewportView(tblReporte);

        javax.swing.GroupLayout jPanelContenidoLayout = new javax.swing.GroupLayout(jPanelContenido);
        jPanelContenido.setLayout(jPanelContenidoLayout);
        jPanelContenidoLayout.setHorizontalGroup(
            jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenidoLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 1100, Short.MAX_VALUE)
                    .addGroup(jPanelContenidoLayout.createSequentialGroup()
                        .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTipoReporte)
                            .addComponent(cbxTipoReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelContenidoLayout.createSequentialGroup()
                        .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFiltroDefecto)
                            .addComponent(cbxFiltroTipoDefecto, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFiltroSeveridad)
                            .addComponent(cbxFiltroSeveridad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(40, 40, 40))
        );
        jPanelContenidoLayout.setVerticalGroup(
            jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenidoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelContenidoLayout.createSequentialGroup()
                        .addComponent(jLabelTipoReporte)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxTipoReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelContenidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContenidoLayout.createSequentialGroup()
                        .addComponent(jLabelFiltroDefecto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxFiltroTipoDefecto, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelContenidoLayout.createSequentialGroup()
                        .addComponent(jLabelFiltroSeveridad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxFiltroSeveridad, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addComponent(jScrollPaneTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        add(jPanelContenido, java.awt.BorderLayout.CENTER);
    }// </editor-fold>                        

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {                                              
    }                                             

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {                                                  
    }                                                 

    private void cbxTipoReporteActionPerformed(java.awt.event.ActionEvent evt) {                                               
        actualizarEstadoFiltros();
    }                                              

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnGenerarReporte;
    private javax.swing.JComboBox<String> cbxFiltroSeveridad;
    private javax.swing.JComboBox<String> cbxFiltroTipoDefecto;
    private javax.swing.JComboBox<String> cbxTipoReporte;
    private javax.swing.JLabel jLabelFiltroDefecto;
    private javax.swing.JLabel jLabelFiltroSeveridad;
    private javax.swing.JLabel jLabelTipoReporte;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanelContenido;
    private javax.swing.JPanel jPanelTitulo;
    private javax.swing.JScrollPane jScrollPaneTabla;
    private javax.swing.JTable tblReporte;
    // End of variables declaration                   
}
