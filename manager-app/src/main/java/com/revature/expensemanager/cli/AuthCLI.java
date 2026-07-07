package com.revature.expensemanager.cli;

//import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.expensemanager.dto.LoginResponse;

public class AuthCLI {

    //private static final Scanner scanner = new Scanner(System.in);

    public static boolean login() {
        System.out.println("\n--- Login ---");

        // System.out.print("Username: ");
        // String username = scanner.nextLine();

        // System.out.print("Password: ");
        // String password = scanner.nextLine();
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