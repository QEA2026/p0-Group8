package com.revature.expensemanager.service;

import java.util.List;

import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.model.Expense;

public class ExpenseService {
    private final ExpenseDAO expenseDAO;
    private final ApprovalDAO approvalDAO;

    public ExpenseService(ExpenseDAO expenseDAO, ApprovalDAO approvalDAO) {
        this.expenseDAO = expenseDAO;
        this.approvalDAO = approvalDAO;
    }

    public List<Expense> getPendingExpenses() {
        return expenseDAO.findPendingExpenses();
    }

    public boolean reviewExpense(int expenseId, int reviewerId, ApprovalRequest request) {
        String status = request.getStatus().toLowerCase();

        if (!status.equals("approved") && !status.equals("denied")) {
            return false;
        }

        if (expenseDAO.findById(expenseId).isEmpty()) {
            return false;
        }

        if (approvalDAO.findByExpenseId(expenseId).isEmpty()) {
            return false;
        }

        return approvalDAO.reviewExpense(expenseId, status, reviewerId, request.getComment());
    }
}
