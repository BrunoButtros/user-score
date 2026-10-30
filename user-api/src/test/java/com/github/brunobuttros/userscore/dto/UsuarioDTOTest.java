package com.github.brunobuttros.userscore.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioDTOTest {

    @Test
    public void testUsuarioDTO() {
        Long id = 1L;
        String nome = "Test User";
        String email = "test@example.com";
        String telefone = "123456789";
        String cpf = "123456789";
        String cep = "123456789";
        int score = 100;
        EnderecoDTO endereco = new EnderecoDTO(1L, "12345678", "Rua Exemplo", "Bairro Exemplo", "Cidade Exemplo", "EX");

        UsuarioDTO usuarioDTO = new UsuarioDTO(id, nome, email, telefone, cpf, cep, score, endereco);

        assertThat(usuarioDTO.id()).isEqualTo(id);
        assertThat(usuarioDTO.nome()).isEqualTo(nome);
        assertThat(usuarioDTO.email()).isEqualTo(email);
        assertThat(usuarioDTO.telefone()).isEqualTo(telefone);
        assertThat(usuarioDTO.cpf()).isEqualTo(cpf);
        assertThat(usuarioDTO.cep()).isEqualTo(cep);
        assertThat(usuarioDTO.score()).isEqualTo(score);
        assertThat(usuarioDTO.endereco()).isEqualTo(endereco);
    }

    @Test
    public void testUsuarioDTOEnderecoNulo() {
        Long id = 1L;
        String nome = "Test User";
        String email = "test@example.com";
        String telefone = "1234567890";
        String cpf = "12345678901";
        String cep = "12345678";
        int score = 100;

        UsuarioDTO usuarioDTO = new UsuarioDTO(id, nome, email, telefone, cpf, cep, score, null);

        assertThat(usuarioDTO.id()).isEqualTo(id);
        assertThat(usuarioDTO.nome()).isEqualTo(nome);
        assertThat(usuarioDTO.email()).isEqualTo(email);
        assertThat(usuarioDTO.telefone()).isEqualTo(telefone);
        assertThat(usuarioDTO.cpf()).isEqualTo(cpf);
        assertThat(usuarioDTO.cep()).isEqualTo(cep);
        assertThat(usuarioDTO.score()).isEqualTo(score);
        assertThat(usuarioDTO.endereco()).isNull();
    }
}
