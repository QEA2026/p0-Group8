package com.revature.expensemanager.dao;

import java.util.Optional;

import com.revature.expensemanager.model.Approval;

public interface ApprovalDAO {

    Optional<Approval> findByExpenseId(int expenseId);

    boolean reviewExpense(int expenseId, String status, int reviewerId, String comment);
}
