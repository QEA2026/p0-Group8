package com.revature.expensemanager.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.revature.expensemanager.dto.ErrorResponse;
import com.revature.expensemanager.service.JwtService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class AuthMiddleware {
    private static final Logger logger = LoggerFactory.getLogger(AuthMiddleware.class);

    private final JwtService jwtService;

    public AuthMiddleware(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void requireManager(Context ctx) {
        String authHeader = ctx.header("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            logger.warn("Authorization failed: missing Authorization header.");

            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Authorization header is required."));
            ctx.skipRemainingHandlers();
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization failed: Authorization header did not use Bearer scheme.");

            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Authorization header must use Bearer token."));
            ctx.skipRemainingHandlers();
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT decodedJWT = jwtService.validateToken(token);
            Integer userId = decodedJWT.getClaim("userId").asInt();
            String username = decodedJWT.getClaim("username").asString();
            String role = decodedJWT.getClaim("role").asString();

            if (userId == null || username == null || username.isBlank()
                    || role == null || role.isBlank()) {

                logger.warn("Authorization failed: token had missing or invalid claims.");

                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(new ErrorResponse("Invalid token claims."));
                ctx.skipRemainingHandlers();
                return;
            }

            if (!role.equalsIgnoreCase("manager")) {
                logger.warn("Authorization denied: userId={}, username={}, role={} attempted manager access.",
                        userId,
                        username,
                        role);

                ctx.status(HttpStatus.FORBIDDEN)
                        .json(new ErrorResponse("Manager access required."));
                ctx.skipRemainingHandlers();
                return;
            }

            logger.info("Manager authorized: userId={}, username={}", userId, username);

            ctx.attribute("userId", userId);
            ctx.attribute("username", username);
            ctx.attribute("role", role);

        } catch (JWTVerificationException e) {
            logger.warn("Authorization failed: invalid or expired JWT.");

            ctx.status(HttpStatus.UNAUTHORIZED)
                    .json(new ErrorResponse("Invalid or expired token."));
            ctx.skipRemainingHandlers();
        }
    }
}
