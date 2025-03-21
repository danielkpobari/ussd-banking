package com.daniel.skaet_ussd.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends UssdBankingException {
    public AuthenticationException(String message) {
        super("AUTH_001", message, HttpStatus.UNAUTHORIZED);
    }
}
