package com.daniel.skaet_ussd.service;

import com.daniel.skaet_ussd.entity.Account;
import com.daniel.skaet_ussd.exception.AccountNotFoundException;
import com.daniel.skaet_ussd.exception.AuthenticationException;
import com.daniel.skaet_ussd.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final String SESSION_PREFIX = "ussd:session:";

    public AuthServiceImpl(AccountRepository accountRepository, RedisTemplate<String, String> redisTemplate, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createSession(String msisdn) {
        try {
            Account account = accountRepository.findByMsisdn(msisdn)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found for MSISDN: " + msisdn));

            String sessionId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(SESSION_PREFIX + sessionId, msisdn);
            redisTemplate.expire(SESSION_PREFIX + sessionId, 5, TimeUnit.MINUTES);

            return sessionId;
        } catch (Exception e) {
            log.error("Error creating USSD session for MSISDN: {}", msisdn, e);
            throw new AuthenticationException("Failed to create session");
        }
    }

    @Override
    public boolean validateSession(String sessionId) {
        try {
            String msisdn = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            if (msisdn == null) {
                throw new AuthenticationException("Invalid or expired session");
            }
            return true;
        } catch (Exception e) {
            log.error("Error validating USSD session: {}", sessionId, e);
            throw new AuthenticationException("Session validation failed");
        }
    }

    @Override
    public void registerUser(String msisdn, String pin) {
        try {
            if (accountRepository.findByMsisdn(msisdn).isPresent()) {
                throw new AuthenticationException("MSISDN already registered");
            }

            Account account = Account.builder()
                    .msisdn(msisdn)
                    .balance(BigDecimal.ZERO)
                    .pin(passwordEncoder.encode(pin))
                    .build();

            accountRepository.save(account);
        } catch (Exception e) {
            log.error("Error registering user with MSISDN: {}", msisdn, e);
            throw new AuthenticationException("Registration failed");
        }
    }

    @Override
    public String login(String msisdn, String pin) {
        try {
            Account account = accountRepository.findByMsisdn(msisdn)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            if (!passwordEncoder.matches(pin, account.getPin())) {
                throw new AuthenticationException("Invalid PIN");
            }

            return createSession(msisdn);
        } catch (Exception e) {
            log.error("Error logging in user with MSISDN: {}", msisdn, e);
            throw new AuthenticationException("Login failed");
        }
    }
}


