package com.revature.expensemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.model.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsManagerResponse_whenManagerCredentialsAreValid() {
        // Arrange: Hash password, create mock objects, and stub DAO lookup
        String rawPassword = "password123";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
        User manager = new User(10, "manager1", hashedPassword, "manager");
        LoginRequest request = new LoginRequest("manager1", rawPassword);
        when(userDAO.findByUsername("manager1")).thenReturn(Optional.of(manager));

        // Act: Invoke login method on auth service with manager role
        Optional<LoginResponse> result = authService.login(request);

        // Assert: Verify successful manager response payload
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
        assertEquals("manager1", result.get().getUsername());
        assertEquals("manager", result.get().getRole());
    }

    @Test
    void login_returnsEmpty_whenEmployeeAttemptsLogin() {
        // Arrange: Create an employee account and stub DAO lookup
        String rawPassword = "password123";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());
        User employee = new User(20, "employee1", hashedPassword, "employee");
        LoginRequest request = new LoginRequest("employee1", rawPassword);
        when(userDAO.findByUsername("employee1")).thenReturn(Optional.of(employee));

        // Act: Invoke login method with employee role
        Optional<LoginResponse> result = authService.login(request);

        // Assert: Verify returns an empty optional (no object assigned = no login success)
        assertTrue(result.isEmpty());
    }
}
