package com.github.brunobuttros.userscore.controller;

import com.github.brunobuttros.userscore.dto.EnderecoDTO;
import com.github.brunobuttros.userscore.entity.EnderecoEntity;
import com.github.brunobuttros.userscore.service.BuscaCepClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
@SecurityRequirement(name = "basicAuth")
public class BuscaCepController {

    private final BuscaCepClient buscaCepClient;

    @Autowired
    public BuscaCepController(BuscaCepClient buscaCepClient) {
        this.buscaCepClient = buscaCepClient;
    }

    @GetMapping("/{cep}")
    public EnderecoDTO getEnderecoByCep(@PathVariable String cep) {
        EnderecoEntity enderecoEntity = buscaCepClient.buscarEnderecoPorCep(cep);
        return new EnderecoDTO(
                enderecoEntity.getId(),
                enderecoEntity.getCep(),
                enderecoEntity.getLogradouro(),
                enderecoEntity.getBairro(),
                enderecoEntity.getLocalidade(),
                enderecoEntity.getUf()
        );
    }
}
