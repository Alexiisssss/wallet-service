package com.example.wallet;

import com.example.wallet.model.Account;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.service.AccountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) return;

        User user1 = User.builder()
                .name("Test User")
                .dateOfBirth(java.time.LocalDate.of(1990, 1, 1))
                .password(passwordEncoder.encode("password123"))
                .build();

        user1.addEmail("test@example.com");
        user1.addPhone("+71234567890");
        userRepository.save(user1);

        Account account1 = Account.builder()
                .user(user1)
                .balance(new BigDecimal("1000.00"))
                .initialBalance(new BigDecimal("1000.00"))
                .build();
        accountService.save(account1);

        User user2 = User.builder()
                .name("Receiver")
                .dateOfBirth(java.time.LocalDate.of(1992, 5, 5))
                .password(passwordEncoder.encode("receiverpass"))
                .build();

        user2.addEmail("receiver@example.com");
        user2.addPhone("+79876543210");
        userRepository.save(user2);

        Account account2 = Account.builder()
                .user(user2)
                .balance(new BigDecimal("500.00"))
                .initialBalance(new BigDecimal("500.00"))
                .build();
        accountService.save(account2);
    }
}
