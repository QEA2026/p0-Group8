package com.revature.expensemanager.service;

import java.util.List;

import com.revature.expensemanager.dao.ReportDAO;
import com.revature.expensemanager.model.Expense;

public class ReportService {
    private final ReportDAO reportDAO;

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
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
}
