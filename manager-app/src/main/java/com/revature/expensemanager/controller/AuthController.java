package com.revature.expensemanager.controller;

import java.util.Optional;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.JwtService;

public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
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

        String token = jwtService.generateToken(
                response.getId(),
                response.getUsername(),
                response.getRole());

        response.setToken(token);

        ctx.status(HttpStatus.OK).json(response);
    }
}
