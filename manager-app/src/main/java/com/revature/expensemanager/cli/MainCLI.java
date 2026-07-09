package com.revature.expensemanager.cli;


public class MainCLI {

    public static void main(String[] args) {
    System.out.println("=== Manager Expense CLI ===");

    while (!AuthCLI.login()) {
        System.out.println("\nPlease try again.");
    }

    while (true) {
        printMenu();

        int choice = InputVal.readMenuChoice(0,4);

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
        System.exit(10);

        break;

    case 0:
        System.exit(0); 
        return;

    default:
        System.out.println("Invalid option.");
}

        }
    }

    private static void printMenu() {
    System.out.println("\n--- Manager Menu ---");
    System.out.println("1. View All Pending Expenses");
    System.out.println("2. Review Pending Expenses");
    System.out.println("3. View and Generate Reports");
    System.out.println("4. Logout");
    System.out.println("0. Exit");
    }
}