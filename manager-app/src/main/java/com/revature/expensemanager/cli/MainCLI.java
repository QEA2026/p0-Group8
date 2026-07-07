package com.revature.expensemanager.cli;

//import java.util.Scanner;

public class MainCLI {

    //private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
    System.out.println("=== Manager Expense CLI ===");

    while (!AuthCLI.login()) {
        System.out.println("\nPlease try again.");
    }

    while (true) {
        printMenu();

        //String choice = scanner.nextLine();
        int choice = InputVal.readMenuChoice(0,3);

        switch (choice) {
    case 1:
        ExpenseCLI.viewPendingExpenses();
        break;

    case 2:
        ExpenseCLI.viewPendingExpenses();
        ExpenseCLI.reviewExpense();
        break;

    case 3:
        ReportCLI.runReports();
        break;

    case 4:
        Session.logout();

        while (!AuthCLI.login()) {
            System.out.println("\nPlease try again.");
        }
        break;

    case 0:
        System.out.println("Exiting CLI...");
        return;

    default:
        System.out.println("Invalid option.");
}

        }
    }

    private static void printMenu() {
    System.out.println("\n--- Manager Menu ---");
    System.out.println("1. View Pending Expenses");
    System.out.println("2. Review Expense");
    System.out.println("3. Reports");
    System.out.println("4. Logout");
    System.out.println("0. Exit");
    System.out.print("Choose an option: ");
    }
}