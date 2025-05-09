package com.example.wallet.service;

import com.example.wallet.model.Account;
import com.example.wallet.model.User;
import com.example.wallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Cacheable(value = "accounts", key = "#user.id")
    public Optional<Account> getAccountByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Transactional
    public void updateBalance(Account account, BigDecimal amount) {
        account.setBalance(amount);
        accountRepository.save(account);
    }

    @Transactional
    public void save(Account account) {
        accountRepository.save(account);
    }
}
