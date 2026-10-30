package com.github.brunobuttros.userscore.dto;

import com.github.brunobuttros.userscore.entity.UsuarioRole;

public record CadastrarDTO(
        @jakarta.validation.constraints.NotBlank String login,
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(min=8, max=72) String password,
        @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.NotBlank String nome,
        @jakarta.validation.constraints.Pattern(regexp="[0-9]{11}") @jakarta.validation.constraints.NotBlank String cpf,
        String telefone,
        @jakarta.validation.constraints.Pattern(regexp="[0-9]{8}") @jakarta.validation.constraints.NotBlank String cep,
        UsuarioRole role
) {
}
