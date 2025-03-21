package com.daniel.skaet_ussd.service;

public interface AuthService {
    String createSession(String msisdn);

    boolean validateSession(String sessionId);

    void registerUser(String msisdn, String pin);

    String login(String msisdn, String pin);
}
