package com.revature.expensemanager.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.expensemanager.dto.LoginResponse;

public class AuthCLI {


    public static boolean login() {
        System.out.println("\n--- Login ---");
  
        String username = InputVal.readNonEmptyString("Username: ");
        String password = InputVal.readNonEmptyString("Password: "); 

        String body = "{"
                + "\"username\":\"" + username + "\","
                + "\"password\":\"" + password + "\""
                + "}";

        String response = ApiClient.post("/login", body);

        try {
            ObjectMapper mapper = new ObjectMapper();

            LoginResponse loginResponse =
                    mapper.readValue(response, LoginResponse.class);

            Session.setToken(loginResponse.getToken());

            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + loginResponse.getUsername());

            return true;

        } catch (Exception e) {

            System.out.println("\nLogin failed.");
            System.out.println(response);

            return false;
        }
    }
}