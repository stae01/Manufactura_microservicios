package com.juvenr.mqc.reports.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthClient {

    // Cambia el host/puerto si tu gateway está en otro lado
    private static final String BASE_URL = "http://localhost:3000";

    private final HttpClient httpClient;

    public AuthClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    // 👇 OJO: ahora también lanza InvalidCredentialsException
    public String login(String username, String password)
            throws IOException, InterruptedException, InvalidCredentialsException {

        String jsonBody = buildLoginJson(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        String body = response.body();

        if (status == 200) {
            String token = extractTokenFromJson(body);
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("No se encontró el token en la respuesta");
            }
            return token;
        } else if (status == 400 || status == 401 || status == 403) {
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos.");
        } else {
            throw new RuntimeException("Error en login. HTTP " + status + " Respuesta: " + body);
        }
    }

    private String buildLoginJson(String username, String password) {
        return "{"
                + "\"username\":\"" + escapeJson(username) + "\","
                + "\"password\":\"" + escapeJson(password) + "\""
                + "}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // Parser súper simple para {"token":"..."} sin usar librerías extra
    private String extractTokenFromJson(String json) {
        if (json == null) return null;

        String key = "\"token\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        int firstQuote = json.indexOf("\"", colonIndex + 1);
        if (firstQuote == -1) return null;

        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) return null;

        return json.substring(firstQuote + 1, secondQuote);
    }
}
