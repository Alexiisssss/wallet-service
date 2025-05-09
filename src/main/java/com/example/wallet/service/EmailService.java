package com.example.wallet.service;

import com.example.wallet.model.EmailData;
import com.example.wallet.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    public Optional<EmailData> findByEmail(String email) {
        return emailRepository.findByEmail(email);
    }

    public boolean isEmailTaken(String email) {
        return emailRepository.existsByEmail(email);
    }

    @Transactional
    public void save(EmailData email) {
        emailRepository.save(email);
    }

    @Transactional
    public void delete(EmailData email) {
        emailRepository.delete(email);
    }
}
