package com.daniel.skaet_ussd.exception;

import org.springframework.http.HttpStatus;

public class UssdBankingException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public UssdBankingException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

}