
package com.revature.expensemanager.dao;

import java.util.List;
import java.util.Optional;

import com.revature.expensemanager.model.Expense;

public interface ExpenseDAO {

    Optional<Expense> findById(int id);

    List<Expense> findPendingExpenses();
}
