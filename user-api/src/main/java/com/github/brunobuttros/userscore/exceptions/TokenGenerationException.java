package com.github.brunobuttros.userscore.exceptions;

import com.auth0.jwt.exceptions.JWTCreationException;

public class TokenGenerationException extends RuntimeException {
    public TokenGenerationException(String message, JWTCreationException exception) {
        super(message);
    }
}
