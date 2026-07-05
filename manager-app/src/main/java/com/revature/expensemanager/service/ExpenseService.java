package com.revature.expensemanager.service;

import java.util.List;

import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.model.Approval;
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
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            return false;
        }

        String status = request.getStatus().toLowerCase();

        if (!status.equals("approved") && !status.equals("denied")) {
            return false;
        }

        if (expenseDAO.findById(expenseId).isEmpty()) {
            return false;
        }

        Approval approval = approvalDAO.findByExpenseId(expenseId).orElse(null);

        if (approval == null || !"pending".equalsIgnoreCase(approval.getStatus())) {
            return false;
        }

        return approvalDAO.reviewExpense(expenseId, status, reviewerId, request.getComment());
    }
}
