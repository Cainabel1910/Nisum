package com.example.NisumBismark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailValidationConfig {
    @Value("${email.validation.regex.regexp}")
    private String emailRegex;

    @Bean
    public String emailRegex() {
        return emailRegex;
    }
}
