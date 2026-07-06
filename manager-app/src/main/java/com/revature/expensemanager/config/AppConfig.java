package com.revature.expensemanager.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    private final String jwtSecret;
    private final int jwtExpirationHours;

    public AppConfig() {
        Dotenv dotenv = Dotenv.load();

        this.jwtSecret = getRequired(dotenv, "JWT_SECRET");
        this.jwtExpirationHours = getRequiredInt(dotenv, "JWT_EXPIRATION_HOURS");
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtExpirationHours() {
        return jwtExpirationHours;
    }

    private String getRequired(Dotenv dotenv, String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(key + " is required.");
        }

        return value;
    }

    private int getRequiredInt(Dotenv dotenv, String key) {
        String value = getRequired(dotenv, key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(key + " must be an integer.", e);
        }
    }
}