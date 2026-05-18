package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Service pour récupérer les informations d'un utilisateur
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Trouve un utilisateur par son username
    // Utilisé dans les controllers pour récupérer l'utilisateur connecté
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User non trouvé : " + username));
    }
}