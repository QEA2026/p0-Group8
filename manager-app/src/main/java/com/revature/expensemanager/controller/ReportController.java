package com.revature.expensemanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.model.ExpenseCategory;
import com.revature.expensemanager.service.ReportExportService;
import com.revature.expensemanager.service.ReportService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    public ReportController(ReportService reportService, ReportExportService reportExportService) {
        this.reportService = reportService;
        this.reportExportService = reportExportService;
    }

    public void getExpensesByEmployee(Context ctx) {
        Integer managerId = ctx.attribute("userId");
        String userIdParam = ctx.queryParam("userId");

        logger.info("Employee report requested: managerId={}, employeeIdParam={}", managerId, userIdParam);

        if (userIdParam == null || userIdParam.isBlank()) {
            logger.warn("Employee report failed: missing userId query parameter.");
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId query parameter is required."));
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            logger.warn("Employee report failed: invalid userId={}", userIdParam);
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId must be a valid number."));
            return;
        }

        if (userId <= 0) {
            logger.warn("Employee report failed: non-positive userId={}", userId);
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("userId must be a positive number."));
            return;
        }

        if (!reportService.employeeExists(userId)) {
            logger.warn("Employee report failed: employeeId={} not found.", userId);
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(new ErrorResponse("Employee not found."));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByEmployee(userId);

        logger.info("Employee report generated: managerId={}, employeeId={}, count={}",
                managerId,
                userId,
                expenses.size());

        exportIfRequested(ctx, expenses, "employee_report", managerId);
        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getExpensesByCategory(Context ctx) {
        String categoryParam = ctx.queryParam("category");
        Integer managerId = ctx.attribute("userId");

        logger.info("Category report requested: managerId={}, categoryParam={}", managerId, categoryParam);

        ExpenseCategory category;

        try {
            category = ExpenseCategory.fromInput(categoryParam);
        } catch (IllegalArgumentException e) {
            logger.warn("Category report failed: invalid category={}", categoryParam);
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Invalid category. " + ExpenseCategory.validCategoriesMessage()));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByCategory(category.name());

        logger.info("Category report generated: managerId={}, category={}, count={}",
                managerId,
                category.name(),
                expenses.size());

        exportIfRequested(ctx, expenses, "category_report", managerId);
        ctx.status(HttpStatus.OK).json(expenses);
    }

    public void getExpensesByDateRange(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");

        Integer managerId = ctx.attribute("userId");
        logger.info("Date report requested: managerId={}, startDate={}, endDate={}",
                managerId,
                startDate,
                endDate);

        if (startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank()) {
            logger.warn("Date report failed: missing startDate or endDate.");
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
            logger.warn("Date report failed: invalid date format. startDate={}, endDate={}",
                    startDate,
                    endDate);
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate and endDate must use YYYY-MM-DD format."));
            return;
        }

        if (parsedStartDate.isAfter(parsedEndDate)) {
            logger.warn("Date report failed: startDate={} was after endDate={}",
                    startDate,
                    endDate);
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("startDate must be on or before endDate."));
            return;
        }

        List<Expense> expenses = reportService.getExpensesByDateRange(startDate, endDate);

        logger.info("Date report generated: managerId={}, startDate={}, endDate={}, count={}",
                managerId,
                startDate,
                endDate,
                expenses.size());

        exportIfRequested(ctx, expenses, "date_report", managerId);
        ctx.status(HttpStatus.OK).json(expenses);
    }

    private void exportIfRequested(Context ctx, List<Expense> expenses, String reportName, Integer managerId) {
        String exportParam = ctx.queryParam("export");

        if ("true".equalsIgnoreCase(exportParam)) {
            logger.info("CSV export requested: managerId={}, reportName={}, count={}",
                    managerId,
                    reportName,
                    expenses.size());

            String filePath = reportExportService.exportExpensesToCsv(expenses, reportName);

            logger.info("CSV export completed: managerId={}, reportName={}, filePath={}",
                    managerId,
                    reportName,
                    filePath);

            ctx.header("Report-File", filePath);
        }
    }
}
