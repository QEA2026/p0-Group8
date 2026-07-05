package com.revature.expensemanager.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.service.ExpenseService;

public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public void getPendingExpenses(Context ctx) {
        List<Expense> pendingExpenses = expenseService.getPendingExpenses();

        ctx.status(HttpStatus.OK).json(pendingExpenses);
    }

    public void reviewExpense(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        int expenseId;

        try {
            expenseId = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Invalid expense id."));
            return;
        }

        ApprovalRequest approvalRequest = ctx.bodyAsClass(ApprovalRequest.class);

        int reviewerId = (int) session.getAttribute("userId");

        boolean reviewed = expenseService.reviewExpense(expenseId, reviewerId, approvalRequest);

        if (!reviewed) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Unable to review expense"));
            return;
        }
        ctx.status(HttpStatus.OK)
                .json("Expense reviewed successfully");
    }
}
