package com.juvenr.mqc.reports.model;

public class ResumenSeveridad {
    private String severidad;
    private int cantidad;
    private double totalMXN;
    private double totalUSD;

    public ResumenSeveridad(String severidad, int cantidad, double totalMXN, double totalUSD) {
        this.severidad = severidad;
        this.cantidad = cantidad;
        this.totalMXN = totalMXN;
        this.totalUSD = totalUSD;
    }

    public String getSeveridad() {
        return severidad;
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
