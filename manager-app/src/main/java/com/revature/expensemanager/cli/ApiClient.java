package com.revature.expensemanager.cli;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:7001";

    public static String get(String endpoint) {
        try {
            URL url = java.net.URI.create(BASE_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (Session.isLoggedIn()) {
                conn.setRequestProperty(
                "Authorization",
                "Bearer " + Session.getToken()
                );
            }

            conn.setRequestMethod("GET");

            return readResponse(conn);

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static String post(String endpoint, String jsonBody) {
        try {
            URL url = java.net.URI.create(BASE_URL + endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (Session.isLoggedIn()) {
                conn.setRequestProperty(
                "Authorization",
                "Bearer " + Session.getToken()
                );
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes());
                os.flush();
            }

            return readResponse(conn);

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static String put(String endpoint, String jsonBody) {
    try {
        URL url = java.net.URI.create(BASE_URL + endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (Session.isLoggedIn()) {
                conn.setRequestProperty(
                "Authorization",
                "Bearer " + Session.getToken()
                );
            }

        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes());
            os.flush();
        }

        return readResponse(conn);

    } catch (Exception e) {
        return "ERROR: " + e.getMessage();
    }
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {

    InputStream stream;

    if (conn.getResponseCode() >= 400) {
        stream = conn.getErrorStream();
    } else {
        stream = conn.getInputStream();
    }

    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

    StringBuilder response = new StringBuilder();
    String line;

    while ((line = br.readLine()) != null) {
        response.append(line);
    }

    br.close();
    return response.toString();
    }
}