package com.github.brunobuttros.userscore.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(LocalDateTime timestamp, String messages) {
}
