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
        HttpSession session = ctx.req().getSession(false);

        if (!isManagerLoggedIn(session)) {
            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Manager login required."));
            return;
        }

        List<Expense> pendingExpenses = expenseService.getPendingExpenses();

        ctx.status(HttpStatus.OK).json(pendingExpenses);
    }

    public void reviewExpense(Context ctx) {
        HttpSession session = ctx.req().getSession(false);

        if (!isManagerLoggedIn(session)) {
            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Manager login required."));
            return;
        }

        int expenseId;

        try {
            expenseId = Integer.parseInt(ctx.pathParam("expenseId"));
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
                    .json("Unable to review expense");

        }
        ctx.status(HttpStatus.OK)
                .json("Expense reviewed successfully");
    }

    private boolean isManagerLoggedIn(HttpSession session) {
        if (session == null) {
            return false;
        }

        Object role = session.getAttribute("role");

        return role instanceof String
                && "manager".equalsIgnoreCase((String) role);
    }
}
