package com.github.brunobuttros.userscore.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationDTOTest {

    @Test
    public void testAuthenticationDTO() {
        String login = "username";
        String password = "password";

        AuthenticationDTO authenticationDTO = new AuthenticationDTO(login, password);

        assertEquals(login, authenticationDTO.login());
        assertEquals(password, authenticationDTO.password());
    }
}
