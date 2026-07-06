package com.revature.expensemanager.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.dto.LoginRequest;
import com.revature.expensemanager.dto.LoginResponse;
import com.revature.expensemanager.service.AuthService;
import com.revature.expensemanager.service.JwtService;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public void login(Context ctx) {
        LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);

        logger.info("Login request received for username={}", loginRequest.getUsername());

        Optional<LoginResponse> loginResponse = authService.login(loginRequest);

        if (loginResponse.isEmpty()) {
            logger.warn("Login failed for username={}", loginRequest.getUsername());

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

        logger.info(
                "Manager login successful: userId={}, username={}",
                response.getId(),
                response.getUsername());

        ctx.status(HttpStatus.OK).json(response);
    }
}
