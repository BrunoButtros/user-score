package com.github.brunobuttros.userscore.dto;

import com.github.brunobuttros.userscore.entity.UsuarioEntity;

public record UserScoreDTO(Long userId, int score) {
    public UserScoreDTO(UsuarioEntity usuario) {
        this(usuario.getId(), usuario.getScore());
    }
}