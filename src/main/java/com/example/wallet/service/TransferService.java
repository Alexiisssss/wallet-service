package com.example.wallet.service;

import com.example.wallet.model.Account;
import com.example.wallet.model.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountService accountService;
    private final UserService userService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to self");
        }

        User fromUser = userService.getUserById(fromUserId).orElseThrow();
        User toUser = userService.getUserById(toUserId).orElseThrow();

        Account fromAccount = entityManager.find(Account.class, fromUser.getAccount().getId(), LockModeType.PESSIMISTIC_WRITE);
        Account toAccount = entityManager.find(Account.class, toUser.getAccount().getId(), LockModeType.PESSIMISTIC_WRITE);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        log.info("Перевод {} руб от user {} к user {}", amount, fromUserId, toUserId);
        log.info("Баланс отправителя (после перевода): {}", fromAccount.getBalance());
        log.info("Баланс получателя (после перевода): {}", toAccount.getBalance());

        accountService.save(fromAccount);
        accountService.save(toAccount);
    }
}
