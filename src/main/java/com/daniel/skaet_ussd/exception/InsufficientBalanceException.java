package com.daniel.skaet_ussd.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends UssdBankingException {
    public InsufficientBalanceException(String message) {
        super("BAL_001", message, HttpStatus.BAD_REQUEST);
    }
}

