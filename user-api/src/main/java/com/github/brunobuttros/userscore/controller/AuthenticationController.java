package com.github.brunobuttros.userscore.controller;

import com.github.brunobuttros.userscore.dto.AuthenticationDTO;
import com.github.brunobuttros.userscore.dto.LoginResponseDTO;
import com.github.brunobuttros.userscore.service.AuthenticationService;
import com.github.brunobuttros.userscore.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated AuthenticationDTO data) {
        LoginResponseDTO response = authenticationService.authenticate(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String cleanedToken = token.replace("Bearer ", "");
            tokenService.validateToken(cleanedToken);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}