package com.daniel.skaet_ussd.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;

public interface AccountService {
    @Cacheable(value = "accountBalance", key = "#msisdn")
    BigDecimal getBalance(String msisdn);

    @CachePut(value = "accountBalance", key = "#msisdn")
    void deposit(String msisdn, BigDecimal amount);

    @CacheEvict(value = "accountBalance", key = "#msisdn")
    void withdraw(String msisdn, BigDecimal amount);
}
