package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.exceptions.ScoreApiException;
import com.github.brunobuttros.userscore.integration.CpfRequest;
import com.github.brunobuttros.userscore.integration.ScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ScoreApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ScoreApiClient.class);



    private final RestTemplate restTemplate;

    @Value("${score.api.url}")
    private String scoreApiUrl;

    public ScoreApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int getScore(String cpf) {
        String apiUrl = scoreApiUrl;
        try {
            CpfRequest cpfRequest = new CpfRequest(cpf);
            ResponseEntity<ScoreResponse> response = restTemplate.postForEntity(apiUrl, cpfRequest, ScoreResponse.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ScoreResponse scoreResponse = response.getBody();
                if (scoreResponse != null) {
                    return scoreResponse.getScore();
                } else {
                    throw new ScoreApiException("API de pontuação retornou uma resposta nula");
                }
            } else {
                throw new ScoreApiException("Falha ao obter a pontuação do usuário: ");
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new ScoreApiException("API de pontuação retornou 404: Recurso não encontrado");
        } catch (Exception e) {
            throw new ScoreApiException("Erro ao chamar a API de pontuação: " + e.getMessage());
        }
    }
}