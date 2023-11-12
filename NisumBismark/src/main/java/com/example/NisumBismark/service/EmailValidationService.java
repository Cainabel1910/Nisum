package com.example.NisumBismark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailValidationService {
    private final String emailRegex;

    @Autowired
    public EmailValidationService(String emailRegex) {
        this.emailRegex = emailRegex;
    }

    public boolean validateEmail(String email) {
        return email.matches(emailRegex);
    }
}
