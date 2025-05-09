package com.example.wallet.service;

import com.example.wallet.dto.UserDto;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailsEmail(email);
    }

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhonesPhone(phone);
    }

    public Optional<User> getUserByEmailAndPhone(String email, String phone) {
        log.info("Поиск пользователя по email={} и phone={}", email, phone);
        Optional<User> user = userRepository.findByEmailAndPhone(email, phone);
        if (user.isPresent()) {
            log.info("Пользователь найден: id={}", user.get().getId());
        } else {
            log.warn("Пользователь не найден по заданным email и phone");
        }
        return user;
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmailsEmail(email);
    }

    public boolean isPhoneTaken(String phone) {
        return userRepository.existsByPhonesPhone(phone);
    }

    public List<UserDto> searchUsers(Optional<String> name,
                                     Optional<String> email,
                                     Optional<String> phone,
                                     Optional<String> dateOfBirth,
                                     int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        final LocalDate parsedDob = dateOfBirth.flatMap(s -> {
            try {
                return Optional.of(LocalDate.parse(s));
            } catch (DateTimeParseException e) {
                return Optional.empty();
            }
        }).orElse(null);

        return userRepository.findAll(pageable).stream()
                .filter(u -> name.map(n -> u.getName() != null && u.getName().toLowerCase().startsWith(n.toLowerCase())).orElse(true))
                .filter(u -> email.map(e -> u.getEmails().stream().anyMatch(em -> em.getEmail().equalsIgnoreCase(e))).orElse(true))
                .filter(u -> phone.map(p -> u.getPhones().stream().anyMatch(ph -> ph.getPhone().equals(p))).orElse(true))
                .filter(u -> parsedDob == null || (u.getDateOfBirth() != null && u.getDateOfBirth().isAfter(parsedDob)))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getDtoById(Long id) {
        return getUserById(id).map(this::toDto);
    }

    public void addEmail(Long userId, String email) {
        if (isEmailTaken(email)) throw new IllegalArgumentException("Email already in use");
        User user = userRepository.findById(userId).orElseThrow();
        user.addEmail(email);
        userRepository.save(user);
    }

    public void removeEmail(Long userId, String email) {
        User user = userRepository.findById(userId).orElseThrow();
        user.removeEmail(email);
        userRepository.save(user);
    }

    public void addPhone(Long userId, String phone) {
        if (isPhoneTaken(phone)) throw new IllegalArgumentException("Phone already in use");
        User user = userRepository.findById(userId).orElseThrow();
        user.addPhone(phone);
        userRepository.save(user);
    }

    public void removePhone(Long userId, String phone) {
        User user = userRepository.findById(userId).orElseThrow();
        user.removePhone(phone);
        userRepository.save(user);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setEmails(user.getEmails().stream().map(e -> e.getEmail()).toList());
        dto.setPhones(user.getPhones().stream().map(p -> p.getPhone()).toList());
        dto.setBalance(user.getAccount() != null ? user.getAccount().getBalance() : null);
        return dto;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
}
