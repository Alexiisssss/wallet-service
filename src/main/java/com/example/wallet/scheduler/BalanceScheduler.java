package com.example.wallet.scheduler;

import com.example.wallet.model.Account;
import com.example.wallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceScheduler {

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    public void increaseBalances() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            BigDecimal max = account.getInitialBalance().multiply(new BigDecimal("2.07"));
            BigDecimal next = account.getBalance()
                    .multiply(new BigDecimal("1.10"))
                    .setScale(2, RoundingMode.HALF_UP);
            if (next.compareTo(max) > 0) {
                next = max;
            }
            if (!next.equals(account.getBalance())) {
                account.setBalance(next);
                accountRepository.save(account);
                log.info("Account {} balance updated to {}", account.getId(), next);
            }
        }
    }
}
