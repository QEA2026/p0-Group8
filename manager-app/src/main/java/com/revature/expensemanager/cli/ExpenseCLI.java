package com.revature.expensemanager.cli;

import com.revature.expensemanager.model.Expense;

public class ExpenseCLI {

    public static void viewPendingExpenses() {

        try {
            String response = ApiClient.get("/expenses/pending");

            Expense[] expenses = JsonUtil.mapper.readValue(response, Expense[].class);

            ExpensePrinter.printList("Pending Expenses", expenses);

        } catch (Exception e) {
            System.out.println("Error parsing expenses: " + e.getMessage());
        }
    }

    public static void reviewExpense() {

        String pending = ApiClient.get("/expenses/pending");

        if (pending.equals("[]")) {
            System.out.println("\nNo pending expenses available.");
            return;
        }

        System.out.println("\n--- Review Expense ---");

        int id = InputVal.readPositiveInt("Expense ID: ");

        System.out.println("\nDecision:");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        System.out.println("3. Cancel");

        int choice = InputVal.readMenuChoice(1, 3);

        String status;

        switch (choice) {
            case 1 -> status = "APPROVED";
            case 2 -> status = "DENIED";
            default -> {
                System.out.println("Cancelled.");
                return;
            }
        }

        String comment = InputVal.readNonEmptyString("Comment: ");

        String body = "{"
                + "\"status\":\"" + status + "\","
                + "\"comment\":\"" + comment + "\""
                + "}";

        String response = ApiClient.put("/expenses/" + id + "/review", body);

        System.out.println("\nResult: " + response);
    }
}