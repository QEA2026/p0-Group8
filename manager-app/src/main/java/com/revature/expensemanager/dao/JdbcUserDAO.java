package com.revature.expensemanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import com.revature.expensemanager.config.DatabaseConfig;
import com.revature.expensemanager.dto.EmployeeSummary;
import com.revature.expensemanager.model.User;

public class JdbcUserDAO implements UserDAO {

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, role FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id.", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username.", e);
        }

        return Optional.empty();
    }

    @Override
public List<EmployeeSummary> getEmployees() {

    List<EmployeeSummary> employees = new ArrayList<>();

    String sql = """
            SELECT id, username, password, role
            FROM users
            WHERE role = 'Employee'
            ORDER BY username
            """;

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            employees.add(new EmployeeSummary(
    rs.getInt("id"),
    rs.getString("username")
));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error retrieving employees.", e);
    }

    return employees;
}

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"));
    }

}
