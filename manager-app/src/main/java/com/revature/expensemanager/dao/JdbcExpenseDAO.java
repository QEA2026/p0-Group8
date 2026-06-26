package com.revature.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.model.Expense;

public class JdbcExpenseDAO implements ExpenseDAO {

    @Override
    public Optional<Expense> findById(int id) {
        String sql = """
                SELECT id, userId, amount, description, category, date
                FROM expenses
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExpense(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding expense by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Expense> findPendingExpenses() {
        String sql = """
                SELECT e.id, e.userId, e.amount, e.description, e.category, e.date
                FROM expenses e
                JOIN approvals a ON e.id = a.expenseId
                WHERE a.status = 'pending'
                """;

        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding pending expenses.", e);
        }

        return null;
    }

    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("id"),
                rs.getInt("userId"),
                rs.getDouble("amount"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getString("date"));
    }
}