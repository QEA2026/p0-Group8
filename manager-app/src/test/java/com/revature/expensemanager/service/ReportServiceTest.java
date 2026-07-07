// 2 Report service tests: 2 Positive
package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.model.Expense;
import com.revature.expensemanager.model.User;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportDAO reportDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getExpensesByEmployee_returnsListFromDao() {
        // Arrange: Initialize employee, create employee-owned expenses, and stub DAO behavior
        int employeeId = 101;
        List<Expense> expenses = List.of(
                new Expense(1, employeeId, 50.00, "Taxi", "Travel", "2026-07-01"),
                new Expense(2, employeeId, 20.00, "Coffee", "Meals", "2026-07-02"));
        when(reportDAO.findExpensesByEmployee(employeeId)).thenReturn(expenses);

        // Act: Invoke getExpensesByEmployee
        List<Expense> result = reportService.getExpensesByEmployee(employeeId);

        // Assert: Verify all expenses exist, data is integral, and the reportDAO was executed
        assertEquals(2, result.size());
        assertEquals("Taxi", result.get(0).getDescription());
        verify(reportDAO).findExpensesByEmployee(employeeId);
    }

    @Test
    void employeeExists_returnsTrue_whenUserRoleIsEmployee() {
        // Arrange: Initialize user for employee and stub DAO behavior
        int employeeId = 101;
        User employee = new User(employeeId, "employee1", "hashed", "employee");
        when(userDAO.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act: Invoke employeeExists on created employee
        boolean result = reportService.employeeExists(employeeId);

        // Assert: Verify returned True
        assertTrue(result);
    }
}
