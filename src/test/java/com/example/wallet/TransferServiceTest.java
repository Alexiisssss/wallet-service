package com.example.wallet;

import com.example.wallet.model.Account;
import com.example.wallet.model.User;
import com.example.wallet.repository.AccountRepository;
import com.example.wallet.service.AccountService;
import com.example.wallet.service.TransferService;
import com.example.wallet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    private User fromUser;
    private User toUser;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        fromUser = User.builder().id(1L).build();
        toUser = User.builder().id(2L).build();

        fromAccount = Account.builder()
                .id(11L)
                .user(fromUser)
                .balance(new BigDecimal("1000.00"))
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        toAccount = Account.builder()
                .id(22L)
                .user(toUser)
                .balance(new BigDecimal("500.00"))
                .initialBalance(new BigDecimal("500.00"))
                .build();

        fromUser.setAccount(fromAccount);
        toUser.setAccount(toAccount);
    }

    @Test
    void successfulTransfer() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(fromUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(toUser));

        when(accountRepository.findById(11L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(22L)).thenReturn(Optional.of(toAccount));

        transferService.transfer(1L, 2L, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("900.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), toAccount.getBalance());
    }

    @Test
    void transferToSelfShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 1L, new BigDecimal("100")));
    }

    @Test
    void insufficientFundsShouldThrow() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(fromUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(toUser));

        fromAccount.setBalance(new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("100")));
    }

    @Test
    void zeroOrNegativeAmountShouldThrow() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(fromUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(toUser));

        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("0.00")));

        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("-10.00")));
    }
}

