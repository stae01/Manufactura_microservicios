package com.juvenr.mqc.reports.api;
// imports extra
import com.juvenr.mqc.reports.model.DetalleDefecto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.juvenr.mqc.reports.model.ResumenDefecto;
import com.juvenr.mqc.reports.model.ResumenSeveridad;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ReportsClient {

    private static final String BASE_URL = "http://localhost:3000"; // gateway

    private final HttpClient httpClient;

    public ReportsClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<ResumenDefecto> getResumenDefectos(String jwtToken)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/reportes/resumen-defectos"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode()
                    + " al obtener resumen de defectos: " + response.body());
        }

        String body = response.body();
        return parseResumenDefectos(body);
    }

    public List<ResumenSeveridad> getResumenSeveridad(String jwtToken)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/reportes/resumen-severidad"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode()
                    + " al obtener resumen de severidad: " + response.body());
        }

        String body = response.body();
        return parseResumenSeveridad(body);
    }

    // ==============
    // Parseo simple
    // ==============

    // Esperamos algo tipo:
    // [
    //   {"tipo":"Daño superficial","cantidad":3,"totalMXN":300.0,"totalUSD":15.5},
    //   {...}
    // ]
    private List<ResumenDefecto> parseResumenDefectos(String json) {
        List<ResumenDefecto> list = new ArrayList<>();

        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return list;
        }
        trimmed = trimmed.substring(1, trimmed.length() - 1).trim(); // sin [ ]

        if (trimmed.isEmpty()) {
            return list;
        }

        // Corto por "},{" para separar objetos (súper simple)
        String[] objects = trimmed.split("\\},\\s*\\{");

        for (String obj : objects) {
            String o = obj.trim();
            if (!o.startsWith("{")) o = "{" + o;
            if (!o.endsWith("}")) o = o + "}";

            String tipo = extractStringField(o, "tipo");
            int cantidad = (int) extractNumberField(o, "cantidad");
            double totalMXN = extractNumberField(o, "totalMXN");
            double totalUSD = extractNumberField(o, "totalUSD");

            list.add(new ResumenDefecto(tipo, cantidad, totalMXN, totalUSD));
        }

        return list;
    }

    // Esperamos:
    // [
    //   {"severidad":"CRITICA","cantidad":2,"totalMXN":600.0,"totalUSD":30.0},
    //   ...
    // ]
    private List<ResumenSeveridad> parseResumenSeveridad(String json) {
        List<ResumenSeveridad> list = new ArrayList<>();

        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return list;
        }
        trimmed = trimmed.substring(1, trimmed.length() - 1).trim();

        if (trimmed.isEmpty()) {
            return list;
        }

        String[] objects = trimmed.split("\\},\\s*\\{");

        for (String obj : objects) {
            String o = obj.trim();
            if (!o.startsWith("{")) o = "{" + o;
            if (!o.endsWith("}")) o = o + "}";

            String severidad = extractStringField(o, "severidad");
            int cantidad = (int) extractNumberField(o, "cantidad");
            double totalMXN = extractNumberField(o, "totalMXN");
            double totalUSD = extractNumberField(o, "totalUSD");

            list.add(new ResumenSeveridad(severidad, cantidad, totalMXN, totalUSD));
        }

        return list;
    }

    // Helpers para JSON ultra simple

    private String extractStringField(String jsonObj, String fieldName) {
        String key = "\"" + fieldName + "\"";
        int keyIndex = jsonObj.indexOf(key);
        if (keyIndex == -1) return "";

        int colonIndex = jsonObj.indexOf(":", keyIndex);
        if (colonIndex == -1) return "";

        int firstQuote = jsonObj.indexOf("\"", colonIndex + 1);
        if (firstQuote == -1) return "";

        int secondQuote = jsonObj.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) return "";

        return jsonObj.substring(firstQuote + 1, secondQuote);
    }

    private double extractNumberField(String jsonObj, String fieldName) {
        String key = "\"" + fieldName + "\"";
        int keyIndex = jsonObj.indexOf(key);
        if (keyIndex == -1) return 0.0;

        int colonIndex = jsonObj.indexOf(":", keyIndex);
        if (colonIndex == -1) return 0.0;

        int commaIndex = jsonObj.indexOf(",", colonIndex + 1);
        int endIndex = commaIndex == -1 ? jsonObj.indexOf("}", colonIndex + 1) : commaIndex;
        if (endIndex == -1) endIndex = jsonObj.length();

        String valueStr = jsonObj.substring(colonIndex + 1, endIndex).trim();
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    public List<DetalleDefecto> getDetalleDefectos(String jwtToken,
                                               String tipoFiltro,
                                               String severidadFiltro)
        throws IOException, InterruptedException {

    StringBuilder url = new StringBuilder(BASE_URL + "/api/reportes/detalle-defectos?page=1&pageSize=100");

    if (tipoFiltro != null && !tipoFiltro.equalsIgnoreCase("Todos")) {
        url.append("&tipo=").append(URLEncoder.encode(tipoFiltro, StandardCharsets.UTF_8));
    }
    if (severidadFiltro != null && !severidadFiltro.equalsIgnoreCase("Todas")) {
        url.append("&severidad=").append(URLEncoder.encode(severidadFiltro, StandardCharsets.UTF_8));
    }

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .header("Authorization", "Bearer " + jwtToken)
            .GET()
            .build();

    HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
    );

    if (response.statusCode() != 200) {
        throw new RuntimeException("HTTP " + response.statusCode()
                + " al obtener detalle de defectos: " + response.body());
    }

    String body = response.body();
    return parseDetalleDefectos(body);
}
// JSON esperado:
// {
//   "items":[{...},{...}],
//   "total": 12,
//   "page": 1,
//   "pageSize": 100
// }
private List<DetalleDefecto> parseDetalleDefectos(String json) {
    List<DetalleDefecto> list = new ArrayList<>();

    if (json == null || json.isEmpty()) return list;

    String keyItems = "\"items\"";
    int keyIndex = json.indexOf(keyItems);
    if (keyIndex == -1) return list;

    int bracketStart = json.indexOf("[", keyIndex);
    int bracketEnd = json.indexOf("]", bracketStart);
    if (bracketStart == -1 || bracketEnd == -1) return list;

    String arrayContent = json.substring(bracketStart + 1, bracketEnd).trim();
    if (arrayContent.isEmpty()) return list;

    String[] objects = arrayContent.split("\\},\\s*\\{");

    for (String obj : objects) {
        String o = obj.trim();
        if (!o.startsWith("{")) o = "{" + o;
        if (!o.endsWith("}")) o = o + "}";

        int id = (int) extractNumberField(o, "id");
        String tipo = extractStringField(o, "tipo");
        String severidad = extractStringField(o, "severidad");
        String descripcion = extractStringField(o, "descripcion");
        String lote = extractStringField(o, "lote");
        double costoMXN = extractNumberField(o, "costoMXN");
        double costoUSD = extractNumberField(o, "costoUSD");
        String createdAt = extractStringField(o, "createdAt");

        list.add(new DetalleDefecto(id, tipo, severidad, descripcion, lote,
                costoMXN, costoUSD, createdAt));
    }

    return list;
}

}
