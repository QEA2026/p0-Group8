package com.revature.expensemanager.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.expensemanager.model.Expense;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

public class ReportCLI {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void runReports() {

        while (true) {

            System.out.println("\n--- Reports ---");
            System.out.println("1. Employee");
            System.out.println("2. Category");
            System.out.println("3. Date Range");
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

        System.out.println("\n=========================");
        System.out.println(" Available Employees");
        System.out.println("=========================");

        String employees = ApiClient.get("/employees");

        try {
            List<Map<String, Object>> employeeList = mapper.readValue(
                    employees,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> employee : employeeList) {
                System.out.printf("%-3s %s%n",
                        employee.get("id"),
                        employee.get("username"));
            }

        } catch (Exception e) {
            System.out.println(employees);
        }

        int id = InputVal.readPositiveInt("Employee ID: ");
        String endpoint = "/reports/employee?userId=" + id;

        String response = ApiClient.get("/reports/employee?userId=" + id);

        boolean hasData = printExpenseResponse(response);

        if (hasData) {
            exportReport(endpoint);
        }
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

        boolean hasData = printExpenseResponse(response);

        if (hasData) {
            exportReport("/reports/category?category=" + category);
        }

        // exportReport(
        // "/reports/category?category=" + category
        // );
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

        String endpoint = "/reports/date?startDate=" + start +
                "&endDate=" + end;
        String response = ApiClient.get(
                "/reports/date?startDate=" + start +
                        "&endDate=" + end);

        boolean hasData = printExpenseResponse(response);

        if (hasData) {
            exportReport(endpoint);
        }
    }

    private static boolean printExpenseResponse(String response) {

        if (response == null || response.isBlank()) {
            System.out.println("No data found.");
            return false;
        }

        try {

            if (response.startsWith("{")) {

                class ErrorResponse {
                    public String message;
                }

                ErrorResponse error = mapper.readValue(response, ErrorResponse.class);

                System.out.println("\nError: " + error.message);
                return false;
            }

            Expense[] expenses = mapper.readValue(response, Expense[].class);

            if (expenses.length == 0) {
                System.out.println("No expenses found.");
                return false;
            }

            ExpensePrinter.printList("Report Result", expenses);

            return true;

        } catch (Exception e) {
            System.out.println(response);
            return false;
        }
    }

    private static void exportReport(String endpoint) {

        System.out.println("\nWould you like to export this report as CSV?");
        System.out.println("0. Yes");
        System.out.println("1. No");

        int choice = InputVal.readMenuChoice(0, 1);

        if (choice == 0) {

            String exportEndpoint = endpoint + "&export=true";

            ApiClient.getWithHeaders(exportEndpoint);

            System.out.println("CSV export completed.");
        }
    }
}