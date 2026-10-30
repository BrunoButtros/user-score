package com.github.brunobuttros.userscore.dto;

import com.github.brunobuttros.userscore.entity.UsuarioRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CadastrarDTOTest {

    @Test
    public void testCadastrarDTO() {
        String login = "user1";
        String password = "password";
        String email = "user@example.com";
        String nome = "User Example";
        String cpf = "123456789";
        String telefone = "123456789";
        String cep = "123456789";
        UsuarioRole role = UsuarioRole.USER;

        CadastrarDTO cadastrarDTO = new CadastrarDTO(
                login, password, email, nome, cpf, telefone, cep, role
        );

        assertThat(cadastrarDTO.login()).isEqualTo(login);
        assertThat(cadastrarDTO.password()).isEqualTo(password);
        assertThat(cadastrarDTO.email()).isEqualTo(email);
        assertThat(cadastrarDTO.nome()).isEqualTo(nome);
        assertThat(cadastrarDTO.cpf()).isEqualTo(cpf);
        assertThat(cadastrarDTO.telefone()).isEqualTo(telefone);
        assertThat(cadastrarDTO.cep()).isEqualTo(cep);
        assertThat(cadastrarDTO.role()).isEqualTo(role);
    }
}
