package com.juvenr.mqc.reports.model;

public class DetalleDefecto {
    private int id;
    private String tipo;
    private String severidad;
    private String descripcion;
    private String lote;
    private double costoMXN;
    private double costoUSD;
    private String createdAt;

    public DetalleDefecto(int id, String tipo, String severidad,
                          String descripcion, String lote,
                          double costoMXN, double costoUSD,
                          String createdAt) {
        this.id = id;
        this.tipo = tipo;
        this.severidad = severidad;
        this.descripcion = descripcion;
        this.lote = lote;
        this.costoMXN = costoMXN;
        this.costoUSD = costoUSD;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getSeveridad() { return severidad; }
    public String getDescripcion() { return descripcion; }
    public String getLote() { return lote; }
    public double getCostoMXN() { return costoMXN; }
    public double getCostoUSD() { return costoUSD; }
    public String getCreatedAt() { return createdAt; }
}
