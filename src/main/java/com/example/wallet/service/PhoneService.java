package com.example.wallet.service;

import com.example.wallet.model.PhoneData;
import com.example.wallet.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final PhoneRepository phoneRepository;

    public Optional<PhoneData> findByPhone(String phone) {
        return phoneRepository.findByPhone(phone);
    }

    public boolean isPhoneTaken(String phone) {
        return phoneRepository.existsByPhone(phone);
    }

    @Transactional
    public void save(PhoneData phone) {
        phoneRepository.save(phone);
    }

    @Transactional
    public void delete(PhoneData phone) {
        phoneRepository.delete(phone);
    }
}
