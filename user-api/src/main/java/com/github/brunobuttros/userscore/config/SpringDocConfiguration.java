package com.github.brunobuttros.userscore.config;


import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;


@Configuration
@SecurityScheme(type = HTTP,name = "basicAuth",scheme = "bearer", bearerFormat = "JWT")
public class SpringDocConfiguration {
}