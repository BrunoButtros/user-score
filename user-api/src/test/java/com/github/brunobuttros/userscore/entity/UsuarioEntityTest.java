package com.github.brunobuttros.userscore.entity;

import com.github.brunobuttros.userscore.dto.EnderecoDTO;
import com.github.brunobuttros.userscore.dto.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsuarioEntityTest {

    private UsuarioEntity usuario;
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String cep;
    private int score;
    private EnderecoEntity endereco;

    @BeforeEach
    public void setUp() {
        id = 1L;
        nome = "Test User";
        email = "test@example.com";
        telefone = "1234567890";
        cpf = "12345678901";
        cep = "12345678";
        score = 100;
        endereco = new EnderecoEntity();
        endereco.setId(1L);
        endereco.setCep("12345678");
        endereco.setLogradouro("Rua Exemplo");
        endereco.setBairro("Bairro Exemplo");
        endereco.setLocalidade("Cidade Exemplo");
        endereco.setUf("EX");

        usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setCep(cep);
        usuario.setScore(score);
        usuario.setEndereco(endereco);
    }

    @Test
    public void testParaGettersAndSetters() {
        assertEquals(id, usuario.getId());
        assertEquals(nome, usuario.getNome());
        assertEquals(email, usuario.getEmail());
        assertEquals(telefone, usuario.getTelefone());
        assertEquals(cpf, usuario.getCpf());
        assertEquals(score, usuario.getScore());
    }

    @Test
    public void testConvertEntityToDTO() {
        UsuarioDTO usuarioDTO = usuario.convertEntityToDTO();

        assertThat(usuarioDTO.id()).isEqualTo(id);
        assertThat(usuarioDTO.nome()).isEqualTo(nome);
        assertThat(usuarioDTO.email()).isEqualTo(email);
        assertThat(usuarioDTO.telefone()).isEqualTo(telefone);
        assertThat(usuarioDTO.cpf()).isEqualTo(cpf);
        assertThat(usuarioDTO.cep()).isEqualTo(cep);
        assertThat(usuarioDTO.score()).isEqualTo(score);
        assertThat(usuarioDTO.endereco()).isEqualTo(new EnderecoDTO(1L, "12345678", "Rua Exemplo", "Bairro Exemplo", "Cidade Exemplo", "EX"));
    }
}
