package com.leetcode.authservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long expirationTime;


    public String generateToken(String username, String userId, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();

            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token Invalid");
        }
    }

    public String extractUserName(String token) {
        return validateToken(token).getSubject();
    }

    public String extractUserId(String token) {
        return validateToken(token).getClaim("userId").asString();
    }

    public String extractRole(String token) {
        return validateToken(token).getClaim("role").asString();
    }



}
