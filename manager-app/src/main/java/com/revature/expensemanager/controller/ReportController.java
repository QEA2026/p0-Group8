package com.revature.expensemanager.controller;

import java.util.List;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
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

        try {
            int userId = Integer.parseInt(userIdParam);
            List<Expense> expenses = reportService.getExpensesByEmployee(userId);

            ctx.status(HttpStatus.OK).json(expenses);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId must be a valid number."));
        }
    }

    public void getExpensesByCategory(Context ctx) {
        String category = ctx.queryParam("category");

        if (category == null || category.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Category query parameter is required."));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByCategory(category);

        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getExpensesByDateRange(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");

        if (startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank()) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate and endDate query parameters are required."));
        }

        List<Expense> expenses = reportService.getExpensesByDateRange(startDate, endDate);

        ctx.status(HttpStatus.OK).json(expenses);
    }
}
