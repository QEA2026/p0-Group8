package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.dao.ApprovalDAO;
import com.revature.expensemanager.dao.ExpenseDAO;
import com.revature.expensemanager.dto.ApprovalRequest;
import com.revature.expensemanager.model.Approval;
import com.revature.expensemanager.model.Expense;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private ApprovalDAO approvalDAO;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void getPendingExpenses_returnsListFromDao() {
        // Arrange: Create 2 mock pending expenses stored as a list and stub expenseDAO
        List<Expense> pendingExpenses = List.of(
                new Expense(1, 101, 45.50, "Lunch", "Meals", "2026-07-01"),
                new Expense(2, 102, 75.00, "Hotel", "Travel", "2026-07-02"));
        when(expenseDAO.findPendingExpenses()).thenReturn(pendingExpenses);

        // Act: Invoke getPendingExpenses 
        List<Expense> result = expenseService.getPendingExpenses();

        // Assert: Verify all elements passed, data is integral, and the DAO executed
        assertEquals(2, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
        verify(expenseDAO).findPendingExpenses();
    }

    @Test
    void reviewExpense_returnsTrue_whenStatusApprovedAndDaosAllowReview() {
        // Arrange: Create request data. mock objects, and stub DAO behaviors 
        int expenseId = 1;
        int reviewerId = 999;
        ApprovalRequest request = new ApprovalRequest("approved", "Looks good");

        Expense expense = new Expense(expenseId, 101, 45.50, "Lunch", "Meals", "2026-07-01");
        Approval approval = new Approval(5, expenseId, "pending", null, null, null);

        when(expenseDAO.findById(expenseId)).thenReturn(Optional.of(expense));
        when(approvalDAO.findByExpenseId(expenseId)).thenReturn(Optional.of(approval));
        when(approvalDAO.reviewExpense(expenseId, "approved", reviewerId, "Looks good")).thenReturn(true);

        // Act: Invoke reviewExpense to the service
        boolean result = expenseService.reviewExpense(expenseId, reviewerId, request);

        // Assert: Verify returns true and the approvalDAO was executed
        assertTrue(result);
        verify(approvalDAO).reviewExpense(expenseId, "approved", reviewerId, "Looks good");
    }

    @Test
    void reviewExpense_returnsFalse_whenStatusIsInvalid() {
        // Arrange: Create bad ApprovalRequest object 
        ApprovalRequest request = new ApprovalRequest("penguin", "Invalid status test");

        // Act: Invoke reviewExpense on bad request
        boolean result = expenseService.reviewExpense(1, 999, request);

        // Assert: Verify returns false and ensure the service aborted early without calling the DAOs
        assertFalse(result);
        verify(expenseDAO, never()).findById(1);
        verify(approvalDAO, never()).reviewExpense(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }
}
