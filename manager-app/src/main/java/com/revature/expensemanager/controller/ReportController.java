package com.revature.expensemanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.model.ExpenseCategory;
import com.revature.expensemanager.service.ReportService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
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
        ctx.status(HttpStatus.OK).json(expenses);
    }
}
