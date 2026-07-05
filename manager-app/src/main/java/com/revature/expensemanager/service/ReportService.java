package com.revature.expensemanager.service;

import java.util.List;

import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.model.Expense;

public class ReportService {
    private final ReportDAO reportDAO;
    private final UserDAO userDAO;

    public ReportService(ReportDAO reportDAO, UserDAO userDAO) {
        this.reportDAO = reportDAO;
        this.userDAO = userDAO;
    }

    public List<Expense> getExpensesByEmployee(int userId) {
        return reportDAO.findExpensesByEmployee(userId);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return reportDAO.findExpensesByCategory(category);
    }

    public List<Expense> getExpensesByDateRange(String startDate, String endDate) {
        return reportDAO.findExpensesByDateRange(startDate, endDate);
    }

    public boolean employeeExists(int userId) {
        return userDAO.findById(userId)
                .filter(user -> "employee".equals(user.getRole()))
                .isPresent();
    }
}
