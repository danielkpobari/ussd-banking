package com.daniel.skaet_ussd.service;

import com.daniel.skaet_ussd.entity.Account;
import com.daniel.skaet_ussd.exception.AccountNotFoundException;
import com.daniel.skaet_ussd.exception.InsufficientBalanceException;
import com.daniel.skaet_ussd.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.math.BigDecimal;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public AccountServiceImpl(AccountRepository accountRepository, RedisTemplate<String, Object> redisTemplate) {
        this.accountRepository = accountRepository;
        this.redisTemplate = redisTemplate;
    }

    @Cacheable(value = "accountBalance", key = "#msisdn")
    @Override
    public BigDecimal getBalance(String msisdn) {
        return accountRepository.findByMsisdn(msisdn)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    @CachePut(value = "accountBalance", key = "#msisdn")
    @Override
    public void deposit(String msisdn, BigDecimal amount) {
        Account account = accountRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for MSISDN: " + msisdn));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @CacheEvict(value = "accountBalance", key = "#msisdn")
    @Override
    public void withdraw(String msisdn, BigDecimal amount) {
        Account account = accountRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for MSISDN: " + msisdn));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }
}

