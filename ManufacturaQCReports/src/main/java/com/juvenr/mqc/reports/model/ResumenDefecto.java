package com.juvenr.mqc.reports.model;

public class ResumenDefecto {
    private String tipo;
    private int cantidad;
    private double totalMXN;
    private double totalUSD;

    public ResumenDefecto(String tipo, int cantidad, double totalMXN, double totalUSD) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.totalMXN = totalMXN;
        this.totalUSD = totalUSD;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotalMXN() {
        return totalMXN;
    }

    public double getTotalUSD() {
        return totalUSD;
    }
}
