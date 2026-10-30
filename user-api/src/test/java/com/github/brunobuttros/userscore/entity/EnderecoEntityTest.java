package com.github.brunobuttros.userscore.entity;


import com.github.brunobuttros.userscore.dto.EnderecoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnderecoEntityTest {
    @Test
    public void testConstrutor() {
        String cep = "17014270";
        String logradouro = "Rua";
        String bairro = "Bairro";
        String localidade = "localidade";
        String uf = "UF";


        EnderecoEntity endereco = new EnderecoEntity(cep, logradouro, bairro, localidade, uf);

        Assertions.assertEquals(cep, endereco.getCep());
        Assertions.assertEquals(logradouro, endereco.getLogradouro());
        Assertions.assertEquals(bairro, endereco.getBairro());
        Assertions.assertEquals(localidade, endereco.getLocalidade());
        Assertions.assertEquals(uf, endereco.getUf());
    }
    @Test
    public void testGettersAndSetters() {
        Long id = 1L;
        String cep = "12345678";
        String logradouro = "Rua Exemplo";
        String bairro = "Bairro Exemplo";
        String localidade = "Cidade Exemplo";
        String uf = "EX";

        EnderecoEntity enderecoEntity = new EnderecoEntity();
        enderecoEntity.setId(id);
        enderecoEntity.setCep(cep);
        enderecoEntity.setLogradouro(logradouro);
        enderecoEntity.setBairro(bairro);
        enderecoEntity.setLocalidade(localidade);
        enderecoEntity.setUf(uf);

        assertEquals(id, enderecoEntity.getId());
        assertEquals(cep, enderecoEntity.getCep());
        assertEquals(logradouro, enderecoEntity.getLogradouro());
        assertEquals(bairro, enderecoEntity.getBairro());
        assertEquals(localidade, enderecoEntity.getLocalidade());
        assertEquals(uf, enderecoEntity.getUf());
    }

    @Test
    public void testToDTO() {
        Long id = 1L;
        String cep = "12345678";
        String logradouro = "Rua Exemplo";
        String bairro = "Bairro Exemplo";
        String localidade = "Cidade Exemplo";
        String uf = "EX";

        EnderecoEntity enderecoEntity = new EnderecoEntity(id, cep, logradouro, bairro, localidade, uf);

        EnderecoDTO enderecoDTO = enderecoEntity.toDTO();

        assertThat(enderecoDTO.id()).isEqualTo(id);
        assertThat(enderecoDTO.cep()).isEqualTo(cep);
        assertThat(enderecoDTO.logradouro()).isEqualTo(logradouro);
        assertThat(enderecoDTO.bairro()).isEqualTo(bairro);
        assertThat(enderecoDTO.localidade()).isEqualTo(localidade);
        assertThat(enderecoDTO.uf()).isEqualTo(uf);
    }
}
