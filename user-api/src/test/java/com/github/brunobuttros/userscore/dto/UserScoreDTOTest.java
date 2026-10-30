package com.github.brunobuttros.userscore.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserScoreDTOTest {

    @Test
    public void testUserScoreDTOWithFields() {
        Long userId = 1L;
        int score = 100;

        UserScoreDTO userScoreDTO = new UserScoreDTO(userId, score);

        assertThat(userScoreDTO.userId()).isEqualTo(userId);
        assertThat(userScoreDTO.score()).isEqualTo(score);
    }
}