package com.revature.expensemanager.cli;

import com.revature.expensemanager.model.Expense;

public class ExpensePrinter {

    public static void printList(String title, Expense[] expenses) {

        System.out.println("\n=== " + title + " ===\n");

        if (expenses == null || expenses.length == 0) {
            System.out.println("No expenses found.\n");
            return;
        }

        for (Expense e : expenses) {
            printSingle(e);
        }
    }

    public static void printSingle(Expense e) {

        System.out.println("----------------------------------");
        System.out.println("Expense ID : " + e.getId());
        System.out.println("User ID    : " + e.getUserId());
        System.out.println("Amount     : $" + e.getAmount());
        System.out.println("Category   : " + e.getCategory());
        System.out.println("Date       : " + e.getDate());

        if (e.getDescription() != null && !e.getDescription().isBlank()) {
            System.out.println("Description: " + e.getDescription());
        }

        System.out.println("----------------------------------\n");
    }
}