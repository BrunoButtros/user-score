package com.github.brunobuttros.bff.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class WebSecurityConfiguration {
 @Bean SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
  return http.authorizeHttpRequests(a -> a.anyRequest().permitAll())
    .formLogin(f -> f.disable()).httpBasic(b -> b.disable())
    .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
    .build();
 }
}
