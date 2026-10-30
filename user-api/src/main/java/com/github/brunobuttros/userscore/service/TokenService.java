package com.github.brunobuttros.userscore.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import com.github.brunobuttros.userscore.exceptions.TokenGenerationException;
import com.github.brunobuttros.userscore.exceptions.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UsuarioEntity usuarioEntity) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("token-api")
                    .withSubject(usuarioEntity.getLogin())
                    .withExpiresAt(genExperationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException("Error while generating token", exception);
        }
    }
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("token-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new TokenValidationException("Error while validating token", exception);
        }
    }

    private Instant genExperationDate() {
        return Instant.now().plusSeconds(7200);
    }
}