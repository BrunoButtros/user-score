package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.dto.AuthenticationDTO;
import com.github.brunobuttros.userscore.dto.LoginResponseDTO;
import com.github.brunobuttros.userscore.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public LoginResponseDTO authenticate(@Validated AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UsuarioEntity) auth.getPrincipal());
        return new LoginResponseDTO(token);
    }
}