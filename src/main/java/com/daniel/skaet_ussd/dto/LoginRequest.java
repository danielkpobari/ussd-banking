package com.daniel.skaet_ussd.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String msisdn;
    private String pin;
}

