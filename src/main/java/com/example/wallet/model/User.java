package com.example.wallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneData> phones = new HashSet<>();

    public void addEmail(String email) {
        EmailData emailData = new EmailData();
        emailData.setUser(this);
        emailData.setEmail(email);
        this.emails.add(emailData);
    }

    public void removeEmail(String email) {
        this.emails.removeIf(e -> e.getEmail().equalsIgnoreCase(email));
    }

    public void addPhone(String phone) {
        PhoneData phoneData = new PhoneData();
        phoneData.setUser(this);
        phoneData.setPhone(phone);
        this.phones.add(phoneData);
    }

    public void removePhone(String phone) {
        this.phones.removeIf(p -> p.getPhone().equals(phone));
    }
}
