package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom d'utilisateur unique (utilisé pour login)
    @Column(nullable = false, unique = true)
    private String username;

    // Mot de passe encodé 
    @Column(nullable = false)
    private String password;

    // Rôle : "ROLE_USER" ou "ROLE_ADMIN"
    @Column(nullable = false)
    private String role;
}