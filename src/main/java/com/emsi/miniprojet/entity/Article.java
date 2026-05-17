package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "articles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Article {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Description de l'article
    @Column(nullable = false)
    private String description;

    // Prix de l'article
    @Column(nullable = false)
    private Double prix;

    // Date d'expiration de l'article
    @Column(nullable = false)
    private LocalDate dateExpiration;

    // Quantité disponible en stock
    @Column(nullable = false)
    private Integer quantiteStock;
}