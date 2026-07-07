package com.revature.expensemanager.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.service.ExpenseService;

public class ExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public void getPendingExpenses(Context ctx) {
        Integer managerId = ctx.attribute("userId");

        logger.info("Pending expenses requested by managerId={}", managerId);

        List<Expense> pendingExpenses = expenseService.getPendingExpenses();

        logger.info("Pending expenses returned: managerId={}, count={}",
                managerId,
                pendingExpenses.size());

        ctx.status(HttpStatus.OK).json(pendingExpenses);
    }

    public void reviewExpense(Context ctx) {
        int expenseId;
        String expenseIdPathParam = ctx.pathParam("id");

        try {
            expenseId = Integer.parseInt(expenseIdPathParam);
        } catch (NumberFormatException e) {
            logger.warn("Review expense failed: invalid expense id pathParam={}", expenseIdPathParam);

            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Invalid expense id."));
            return;
        }

        ApprovalRequest approvalRequest = ctx.bodyAsClass(ApprovalRequest.class);

        Integer reviewerId = ctx.attribute("userId");

        logger.info("Expense review requested: expenseId={}, reviewerId={}, status={}",
                expenseId,
                reviewerId,
                approvalRequest.getStatus());

        boolean reviewed = expenseService.reviewExpense(expenseId, reviewerId, approvalRequest);

        if (!reviewed) {
            logger.warn("Expense review failed: expenseId={}, reviewerId={}, status={}",
                    expenseId,
                    reviewerId,
                    approvalRequest.getStatus());

            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(new ErrorResponse("Unable to review expense"));

            return;
        }

        logger.info("Expense reviewed successfully: expenseId={}, reviewerId={}, status={}",
                expenseId,
                reviewerId,
                approvalRequest.getStatus());

        ctx.status(HttpStatus.OK)
                .json("Expense reviewed successfully");
    }
}
