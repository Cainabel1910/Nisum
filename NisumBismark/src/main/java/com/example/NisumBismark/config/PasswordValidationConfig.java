package com.example.NisumBismark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordValidationConfig {

    @Value("${password.validation.regex.regexp}")
    private String passwordRegex;

    @Bean
    public String passwordRegex() {
        return passwordRegex;
    }
}
