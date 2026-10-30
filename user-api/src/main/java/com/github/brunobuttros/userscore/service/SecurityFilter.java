package com.github.brunobuttros.userscore.service;

import com.github.brunobuttros.userscore.exceptions.TokenValidationException;
import com.github.brunobuttros.userscore.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    TokenService tokenService;
    @Autowired
    UsuarioRepository usuarioRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var login = tokenService.validateToken(token);
                UserDetails user = usuarioRepository.findByLogin(login);
                if (user == null) {
                    response.sendError(401, "Usuário não encontrado");
                    return;
                }
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Usuário autenticado: {}", login);
            } catch (TokenValidationException e) {
                logger.error("Erro ao validar token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}