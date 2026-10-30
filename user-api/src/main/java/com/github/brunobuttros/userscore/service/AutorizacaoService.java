package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AutorizacaoService.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Carregando informações do usuário com nome de usuário: {}", username);
        UserDetails userDetails = usuarioRepository.findByLogin(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o nome de usuário: " + username);
        }
        logger.info("Informações do usuário carregadas com sucesso.");
        return userDetails;
    }}