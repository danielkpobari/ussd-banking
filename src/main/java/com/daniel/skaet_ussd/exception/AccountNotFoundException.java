package com.daniel.skaet_ussd.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends UssdBankingException {
    public AccountNotFoundException(String message) {
        super("ACC_001", message, HttpStatus.NOT_FOUND);
    }
}
