package com.revature.expensemanager.controller;

import java.util.Optional;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void login(Context ctx) {
        LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);

        Optional<LoginResponse> loginResponse = authService.login(loginRequest);

        if (loginResponse.isEmpty()) {
            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse(
                            "Invalid username or password. This login portal is for managers only."));
            return;
        }

        LoginResponse response = loginResponse.orElseThrow();

        HttpSession session = ctx.req().getSession();

        session.setAttribute("userId", response.getId());
        session.setAttribute("username", response.getUsername());
        session.setAttribute("role", response.getRole());

        ctx.status(HttpStatus.OK).json(response);
    }
}
