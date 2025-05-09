package com.example.wallet.repository;

import com.example.wallet.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"emails"})
    Optional<User> findByEmailsEmail(String email);

    @EntityGraph(attributePaths = {"phones"})
    Optional<User> findByPhonesPhone(String phone);

    @EntityGraph(attributePaths = {"emails", "phones"})
    Optional<User> findByEmailsEmailAndPhonesPhone(String email, String phone);

    @Query("SELECT DISTINCT u FROM User u JOIN u.emails e JOIN u.phones p WHERE e.email = :email AND p.phone = :phone")
    @EntityGraph(attributePaths = {"emails", "phones"})
    Optional<User> findByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);

    boolean existsByEmailsEmail(String email);

    boolean existsByPhonesPhone(String phone);
}
