package com.revature.expensemanager;

import com.revature.expensemanager.config.DatabaseConfig;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConfig.getConnection()) {

            if (conn != null) {
                System.out.println("Connected to database successfully.");
            }

        } catch (Exception e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

    }
}