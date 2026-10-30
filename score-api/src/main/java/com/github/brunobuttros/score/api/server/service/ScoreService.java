package com.github.brunobuttros.score.api.server.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ScoreService {

    public int getScore(String cpf) { // recebe string cpf
        Random random = new Random();
        return random.nextInt(101); //gera numero aleatório entre 0 a 100
    }
}
