package com.revature.expensemanager.cli;

public class Session {

    private static String token;

    public static void setToken(String jwt) {
        token = jwt;
    }

    public static String getToken() {
        return token;
    }

    public static boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public static void logout() {
        token = null;
    }
}