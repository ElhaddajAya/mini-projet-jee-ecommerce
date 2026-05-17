package com.emsi.miniprojet.repository;

import com.emsi.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Spring Data JPA génère automatiquement les requêtes SQL
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);
}