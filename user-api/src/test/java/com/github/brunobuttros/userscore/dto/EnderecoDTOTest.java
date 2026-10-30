package com.github.brunobuttros.userscore.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnderecoDTOTest {

    @Test
    public void testEnderecoDTO() {
        Long id = 1L;
        String cep = "123456789";
        String logradouro = "Rua Exemplo";
        String bairro = "Bairro Exemplo";
        String localidade = "Cidade Exemplo";
        String uf = "EX";

        EnderecoDTO enderecoDTO = new EnderecoDTO(id, cep, logradouro, bairro, localidade, uf);


        assertThat(enderecoDTO.id()).isEqualTo(id);
        assertThat(enderecoDTO.cep()).isEqualTo(cep);
        assertThat(enderecoDTO.logradouro()).isEqualTo(logradouro);
        assertThat(enderecoDTO.bairro()).isEqualTo(bairro);
        assertThat(enderecoDTO.localidade()).isEqualTo(localidade);
        assertThat(enderecoDTO.uf()).isEqualTo(uf);
    }
}