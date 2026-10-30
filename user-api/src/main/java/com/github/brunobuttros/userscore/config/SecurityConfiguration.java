package com.github.brunobuttros.userscore.config;

import com.github.brunobuttros.userscore.service.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] AUTH_WHITE_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/index",
            "/index.jsp",
            "/auth/login",

    };

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        .requestMatchers(POST, "/auth/login").permitAll()
                        .requestMatchers(POST, "/user/cadastrar").permitAll()
                        .requestMatchers(DELETE, "/user/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/user/**").hasRole("ADMIN")
                        .requestMatchers("/user/buscar").hasRole("ADMIN")
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(AUTH_WHITE_LIST).permitAll()

                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
