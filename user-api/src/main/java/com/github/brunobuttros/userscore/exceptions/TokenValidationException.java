package com.github.brunobuttros.userscore.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message, JWTVerificationException exception) {
        super(message);
    }
}
