package com.revature.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.model.Approval;

public class JdbcApprovalDAO implements ApprovalDAO {

    @Override
    public Optional<Approval> findByExpenseId(int expenseId) {
        String sql = """
                SELECT id, expenseId, status, reviewer, comment, review_date
                FROM approvals
                WHERE expenseId = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, expenseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApproval(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding approval by expense id.", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean reviewExpense(int expenseId, String status, int reviewerId, String comment) {
        String sql = """
                UPDATE approvals
                SET status = ?,
                    reviewer = ?,
                    comment = ?,
                    review_date = ?
                WHERE expenseId = ?
                """;
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setString(3, comment);
            ps.setString(4, LocalDate.now().toString());
            ps.setInt(5, expenseId);

            int rowsUpdated = ps.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error reviewing expense.", e);
        }
    }

    private Approval mapResultSetToApproval(ResultSet rs) throws SQLException {
        Integer reviewer = rs.getObject("reviewer") == null
                ? null
                : rs.getInt("reviewer");

        return new Approval(
                rs.getInt("id"),
                rs.getInt("expenseId"),
                rs.getString("status"),
                reviewer,
                rs.getString("comment"),
                rs.getString("review_date"));
    }
}
