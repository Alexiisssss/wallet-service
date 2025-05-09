package com.example.wallet.service;

import com.example.wallet.model.User;
import com.example.wallet.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Optional<String> authenticate(String email, String phone, String rawPassword) {
        Optional<User> userOpt;

        if (email != null && phone != null) {
            userOpt = userService.getUserByEmailAndPhone(email, phone);
        } else if (email != null) {
            userOpt = userService.getUserByEmail(email);
        } else if (phone != null) {
            userOpt = userService.getUserByPhone(phone);
        } else {
            log.warn("Попытка аутентификации без email и phone");
            return Optional.empty();
        }

        if (userOpt.isEmpty()) {
            log.warn("Пользователь не найден: email={}, phone={}", email, phone);
            return Optional.empty();
        }

        User user = userOpt.get();

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        log.info("Проверка пароля для пользователя id={}: {}", user.getId(), matches);

        if (!matches) {
            return Optional.empty();
        }

        String token = jwtProvider.generateToken(user.getId());
        log.info("JWT токен успешно сгенерирован для пользователя id={}", user.getId());
        return Optional.of(token);
    }
}
