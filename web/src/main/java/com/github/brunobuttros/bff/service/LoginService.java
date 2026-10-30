package com.github.brunobuttros.bff.service;

import com.github.brunobuttros.bff.dto.AuthenticationDTO;
import com.github.brunobuttros.bff.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${userscore.base-url}")
    private String baseUrl;

    public String authenticate(String login, String password) {
        String authUrl = baseUrl + "/auth/login";

        AuthenticationDTO credentials = new AuthenticationDTO(login, password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<AuthenticationDTO> request = new HttpEntity<>(credentials, headers);

        try {
            ResponseEntity<TokenDTO> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, TokenDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                TokenDTO tokenDTO = response.getBody();
                if (tokenDTO != null && tokenDTO.token() != null && !tokenDTO.token().isEmpty()) {
                    return tokenDTO.token();
                } else {
                    throw new RuntimeException("Empty token received from the authentication server");
                }
            } else {
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Authentication failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage());
        }
    }
}
