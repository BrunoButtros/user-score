package com.github.brunobuttros.userscore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brunobuttros.userscore.entity.EnderecoEntity;
import com.github.brunobuttros.userscore.exceptions.CepInvalidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class BuscaCepClient {
    private static final Logger logger = LoggerFactory.getLogger(BuscaCepClient.class);


    @Value("${viacep.url}")
    private String viaCepUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BuscaCepClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public EnderecoEntity buscarEnderecoPorCep(String cep) {
        logger.info("Buscando endereço para o CEP: {}", cep);
        return buscarEndereco(cep);
    }

    private EnderecoEntity buscarEndereco(String cep) {
        if (cep == null || !cep.matches("[0-9]{8}")) throw new CepInvalidoException("CEP deve conter 8 dígitos");
        String url = viaCepUrl + cep + "/json/";

        try {
            String json = restTemplate.getForObject(url, String.class);
            var node = objectMapper.readTree(json);
            if (node.path("erro").asBoolean(false) || node.path("cep").asText().isBlank()) {
                throw new CepInvalidoException("CEP inválido ou não encontrado: " + cep);
            }
            EnderecoEntity endereco = objectMapper.treeToValue(node, EnderecoEntity.class);
            endereco.setCep(cep);
            return endereco;
        } catch (RestClientException | IOException e) {
            throw new CepInvalidoException("CEP inválido ou não encontrado: " + cep);
        }
    }
}
