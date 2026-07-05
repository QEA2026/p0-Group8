package com.revature.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.model.Expense;

public class JdbcReportDAO implements ReportDAO {

    @Override
    public List<Expense> findExpensesByCategory(String category) {
        String sql = """
                SELECT id, userId, amount, description, category, date
                FROM expenses
                WHERE category = ?
                """;

        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by category.", e);
        }

        return expenses;
    }

    @Override
    public List<Expense> findExpensesByDateRange(String startDate, String endDate) {
        String sql = """
                SELECT id, userId, amount, description, category, date
                FROM expenses
                WHERE date BETWEEN ? AND ?
                """;

        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by date range.", e);
        }
        return expenses;
    }

    @Override
    public List<Expense> findExpensesByEmployee(int userId) {
        String sql = """
                SELECT id, userId, amount, description, category, date
                FROM expenses
                WHERE userId = ?
                """;

        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding expenses by employee.", e);
        }

        return expenses;
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
