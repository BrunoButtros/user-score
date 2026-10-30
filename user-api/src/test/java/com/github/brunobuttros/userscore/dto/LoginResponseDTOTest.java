package com.github.brunobuttros.userscore.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginResponseDTOTest {

    @Test
    public void testLoginResponseDTO() {
        String token = "exampleToken";

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token);

        assertThat(loginResponseDTO.token()).isEqualTo(token);
    }
}
