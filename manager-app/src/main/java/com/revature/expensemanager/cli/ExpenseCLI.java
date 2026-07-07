// package com.revature.expensemanager.cli;

// //import java.util.Scanner;

// public class ExpenseCLI {

//     //private static final Scanner scanner = new Scanner(System.in);

//     public static void viewPendingExpenses() {
//         System.out.println("\n--- Pending Expenses ---");

//         String response = ApiClient.get("/expenses/pending");

//         System.out.println(response);
//     }

//     public static void reviewExpense() {
//         System.out.println("\n--- Review Expense ---");

//         int id = InputVal.readPositiveInt("Expense ID: ");

//         System.out.println("\nReview Decision");
//         System.out.println("1. Approve");
//         System.out.println("2. Reject");
//         System.out.println("3. Cancel");

//         int choice = InputVal.readMenuChoice(1, 3);

//         String status;

//         switch (choice) {
//             case 1:
//                 status = "APPROVED";
//                 break;

//             case 2:
//                 status = "REJECTED";
//                 break;

//             default:
//                 return;
//         }

//         String comment = InputVal.readNonEmptyString("Comment: ");

//         String body = "{"
//                 + "\"status\":\"" + status + "\","
//                 + "\"comment\":\"" + comment + "\""
//                 + "}";

//         String response = ApiClient.put("/expenses/" + id + "/review", body);

//         System.out.println(response);
//     }
// }

package com.revature.expensemanager.cli;

import com.revature.expensemanager.model.Expense;

public class ExpenseCLI {

    public static void viewPendingExpenses() {
    System.out.println("\n--- Pending Expenses ---");

    try {
        String response = ApiClient.get("/expenses/pending");

        Expense[] expenses =
                JsonUtil.mapper.readValue(response, Expense[].class);

        ExpensePrinter.printList("Pending Expenses", expenses);

    } catch (Exception e) {
        System.out.println("Error parsing expenses: " + e.getMessage());
    }
}

    public static void reviewExpense() {

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
        case 2 -> status = "REJECTED";
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