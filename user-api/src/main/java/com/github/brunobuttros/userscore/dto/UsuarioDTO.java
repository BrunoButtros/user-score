package com.github.brunobuttros.userscore.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record UsuarioDTO(Long id,
                         String nome,
                         String email,
                         String telefone,
                         String cpf,
                         String cep,
                         @NotNull int score,
                         EnderecoDTO endereco) {


}
