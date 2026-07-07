package com.revature.expensemanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.revature.expensemanager.dto.EmployeeSummary;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.model.ExpenseCategory;
import com.revature.expensemanager.service.ReportExportService;
import com.revature.expensemanager.service.ReportService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ReportController {
    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(ReportService reportService, ReportExportService reportExportService) {
        this.reportService = reportService;
        this.reportExportService = reportExportService;
    }

    public void getExpensesByEmployee(Context ctx) {
        String userIdParam = ctx.queryParam("userId");

        if (userIdParam == null || userIdParam.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId query parameter is required."));
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId must be a valid number."));
            return;
        }

        if (userId <= 0) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId must be a positive number."));
            return;
        }

        if (!reportService.employeeExists(userId)) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(new ErrorResponse("Employee not found."));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByEmployee(userId);
        exportIfRequested(ctx, expenses, "employee_report");
        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getExpensesByCategory(Context ctx) {
        String categoryParam = ctx.queryParam("category");

        ExpenseCategory category;

        try {
            category = ExpenseCategory.fromInput(categoryParam);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Invalid category. " + ExpenseCategory.validCategoriesMessage()));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByCategory(category.name());
        exportIfRequested(ctx, expenses, "category_report");
        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getExpensesByDateRange(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");

        if (startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate and endDate query parameters are required."));
            return;
        }

        LocalDate parsedStartDate;
        LocalDate parsedEndDate;

        try {
            parsedStartDate = LocalDate.parse(startDate);
            parsedEndDate = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate and endDate must use YYYY-MM-DD format."));
            return;
        }

        if (parsedStartDate.isAfter(parsedEndDate)) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate must be on or before endDate."));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByDateRange(startDate, endDate);
        exportIfRequested(ctx, expenses, "date_report");
        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getEmployees(Context ctx) {

    List<EmployeeSummary> employees =
            reportService.getEmployees();

    ctx.json(employees);
}

    private void exportIfRequested(Context ctx, List<Expense> expenses, String reportName) {
        String exportParam = ctx.queryParam("export");

        if ("true".equalsIgnoreCase(exportParam)) {
            String filePath = reportExportService.exportExpensesToCsv(expenses, reportName);
            ctx.header("Report-File", filePath);
        }
    }
}
