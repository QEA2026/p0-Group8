package com.revature.expensemanager.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.model.Approval;
import com.revature.expensemanager.model.Expense;

public class ExpenseService {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

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
            logger.warn("Expense review failed: missing approval status (e.g.: approved or denied).");
            return false;
        }

        String status = request.getStatus().toLowerCase();

        if (!status.equals("approved") && !status.equals("denied")) {
            logger.warn("Expense review failed: invalid status '{}'.", status);
            return false;
        }

        if (expenseDAO.findById(expenseId).isEmpty()) {
            logger.warn("Expense review failed: expenseId={} not found.", expenseId);
            return false;
        }

        Approval approval = approvalDAO.findByExpenseId(expenseId).orElse(null);

        if (approval == null || !"pending".equalsIgnoreCase(approval.getStatus())) {
            logger.warn("Expense review failed: expenseId={} is not pending (i.e. already reviewed).", expenseId);
            return false;
        }

        boolean reviewed = approvalDAO.reviewExpense(
                expenseId,
                status,
                reviewerId,
                request.getComment());

        if (reviewed) {
            logger.info("Expense {} by managerId={}.",
                    status,
                    expenseId,
                    reviewerId);
        } else {
            logger.error("Database failed to review expenseId={}.", expenseId);
        }

        return reviewed;
    }
}
