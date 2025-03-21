package com.daniel.skaet_ussd.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UssdRequest {
    private String sessionId;
    private String msisdn;
    private String action;
    private BigDecimal amount;
}

