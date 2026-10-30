package com.github.brunobuttros.bff.service;

import com.github.brunobuttros.bff.dto.UserListDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ListaService {

    private final RestTemplate restTemplate;
    private final String userscoreBaseUrl;

    @Autowired
    public ListaService(RestTemplate restTemplate, @org.springframework.beans.factory.annotation.Value("${userscore.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.userscoreBaseUrl = baseUrl;
    }

    public List<UserListDTO> getAllUserScores(HttpSession session) {
        String url = userscoreBaseUrl + "/user/scores";
        HttpHeaders headers = new HttpHeaders();
        String token = (String) session.getAttribute("token");
        headers.set("Authorization", "Bearer ".concat(token));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<UserListDTO>>() {}
        ).getBody();
    }
}
