package com.juvenr.mqc.reports;

public class SessionContext {

    private static String jwtToken;
    private static String username;

    public static String getJwtToken() {
        return jwtToken;
    }

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String user) {
        username = user;
    }

    private SessionContext() {
        // evitar instancias
    }
}
