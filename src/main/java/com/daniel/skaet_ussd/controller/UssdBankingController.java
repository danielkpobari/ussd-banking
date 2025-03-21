package com.daniel.skaet_ussd.controller;


import com.daniel.skaet_ussd.dto.LoginRequest;
import com.daniel.skaet_ussd.dto.RegisterRequest;
import com.daniel.skaet_ussd.dto.UssdRequest;
import com.daniel.skaet_ussd.exception.AuthenticationException;
import com.daniel.skaet_ussd.exception.UssdBankingException;
import com.daniel.skaet_ussd.service.AccountService;
import com.daniel.skaet_ussd.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ussd")
@Slf4j
public class UssdBankingController {
    private final AuthService authService;
    private final AccountService accountService;

    public UssdBankingController(AuthService authService, AccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        log.info("Registering new user with MSISDN: {}", request.getMsisdn());
        try {
            authService.registerUser(request.getMsisdn(), request.getPin());
            return "CON Registration successful";
        } catch (AuthenticationException e) {
            log.error("Registration failed: {}", e.getMessage(), e);
            return String.format("CON %s", e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        log.info("Login attempt for MSISDN: {}", request.getMsisdn());
        try {
            String sessionId = authService.login(request.getMsisdn(), request.getPin());
            return String.format("CON Login successful. Session ID: %s", sessionId);
        } catch (AuthenticationException e) {
            log.error("Login failed: {}", e.getMessage(), e);
            return String.format("CON %s", e.getMessage());
        }
    }

    @PostMapping("/process")
    public String processUssdRequest(@RequestBody UssdRequest request) {
        log.info("Processing USSD request: {}", request);

        // Validate session
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            return "CON Invalid session";
        }

        try {
            authService.validateSession(sessionId);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for session: {}", sessionId, e);
            return "CON Session expired. Please start again.";
        }

        String msisdn = request.getMsisdn();
        String action = request.getAction();
        BigDecimal amount = request.getAmount();

        try {
            switch (action.toLowerCase()) {
                case "balance":
                    BigDecimal balance = accountService.getBalance(msisdn);
                    return String.format("CON Your balance is: %s", balance);
                case "deposit":
                    accountService.deposit(msisdn, amount);
                    return "CON Deposit successful";
                case "withdraw":
                    accountService.withdraw(msisdn, amount);
                    return "CON Withdrawal successful";
                default:
                    return "CON Invalid action";
            }
        } catch (UssdBankingException e) {
            log.error("Error processing USSD request: {}", e.getMessage(), e);
            return String.format("CON %s", e.getMessage());
        }
    }
}

