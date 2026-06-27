package com.revature.expensemanager.dao;

import java.util.List;

import com.revature.expensemanager.model.Expense;

public interface ReportDAO {

    List<Expense> findExpensesByEmployee(int userId);

    List<Expense> findExpensesByCategory(String category);

    List<Expense> findExpensesByDateRange(String startDate, String endDate);
}
