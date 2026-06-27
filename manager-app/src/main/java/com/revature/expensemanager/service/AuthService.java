package com.revature.expensemanager.service;

import java.util.Optional;

import com.revature.expensemanager.dao.UserDAO;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<LoginResponse> login(LoginRequest loginRequest) {
        if (loginRequest == null) {
            return Optional.empty();
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return Optional.empty();
        }

        return userDAO.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.getRole().equalsIgnoreCase("manager"))
                .map(user -> new LoginResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()));
    }
}
