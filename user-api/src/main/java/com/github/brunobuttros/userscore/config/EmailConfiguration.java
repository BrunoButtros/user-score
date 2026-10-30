package com.github.brunobuttros.userscore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfiguration {

    private String host;
    private String port;
    private String username;
    private String password;
}
