package com.revature.expensemanager.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtService {
    private static final String ISSUER = "revature-expense-manager-manager-app";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationHours;

    public JwtService(String secret, int expirationHours) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is required.");
        }
        if (expirationHours <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION_HOURS must be greater than zero.");
        }

        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        this.expirationHours = expirationHours;
    }

    public String generateToken(int userId, String username, String role) {
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(expirationHours, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        return verifier.verify(token);
    }

    public Integer getUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("userId").asInt();
    }

    public String getUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("username").asString();
    }

    public String getRole(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("role").asString();
    }
}
