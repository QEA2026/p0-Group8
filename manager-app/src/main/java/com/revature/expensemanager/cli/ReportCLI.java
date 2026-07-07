package com.revature.expensemanager.cli;

//mport java.util.Scanner;

public class ReportCLI {

    // private static final Scanner scanner = new Scanner(System.in);

    public static void runReports() {

        while (true) {

            System.out.println("\n--- Reports ---");
            System.out.println("1. By Employee");
            System.out.println("2. By Category");
            System.out.println("3. By Date Range");
            System.out.println("0. Back");

            int choice = InputVal.readMenuChoice(0, 3);

            switch (choice) {

                case 1:
                    byEmployee();
                    break;

                case 2:
                    byCategory();
                    break;

                case 3:
                    byDate();
                    break;

                case 0:
                    return;
            }
        }
    }

    private static void byEmployee() {
        // System.out.print("User ID: ");
        // String id = scanner.nextLine();
        int id = InputVal.readPositiveInt("Employee ID: ");

        String response = ApiClient.get("/reports/employee?userId=" + id);
        System.out.println("\n=== Report Result ===");
        if (response == null || response.isBlank()) {
    System.out.println("No data found.");
    return;
}
        System.out.println(response);
    }

    private static void byCategory() {

        System.out.println("\nExpense Categories");
        System.out.println("1. Travel");
        System.out.println("2. Meals");
        System.out.println("3. Lodging");
        System.out.println("4. Office Supplies");
        System.out.println("5. Equipment");
        System.out.println("6. Software");
        System.out.println("7. Training");
        System.out.println("8. Other");

        int choice = InputVal.readMenuChoice(1, 8);

        String category;

        switch (choice) {

            case 1:
                category = "TRAVEL";
                break;

            case 2:
                category = "MEALS";
                break;

            case 3:
                category = "LODGING";
                break;

            case 4:
                category = "OFFICE_SUPPLIES";
                break;

            case 5:
                category = "EQUIPMENT";
                break;

            case 6:
                category = "SOFTWARE";
                break;

            case 7:
                category = "TRAINING";
                break;

            default:
                category = "OTHER";
        }

        String response = ApiClient.get("/reports/category?category=" + category);
        System.out.println("\n=== Report Result ===");
         if (response == null || response.isBlank()) {
    System.out.println("No data found.");
    return;
}
        System.out.println(response);
    }

    private static void byDate() {

        java.time.LocalDate start = InputVal.readDate("Start Date (YYYY-MM-DD): ");

        java.time.LocalDate end = InputVal.readDate("End Date (YYYY-MM-DD): ");

        while (end.isBefore(start)) {

            System.out.println(
                    "End date cannot be before the start date.");

            end = InputVal.readDate(
                    "End Date (YYYY-MM-DD): ");
        }

        String response = ApiClient.get(
                "/reports/date?startDate=" + start +
                        "&endDate=" + end);
        System.out.println("\n=== Report Result ===");
        if (response == null || response.isBlank()) {
    System.out.println("No data found.");
    return;
}
        System.out.println(response);
    }
}