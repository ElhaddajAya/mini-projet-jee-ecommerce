package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commande {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date à laquelle la commande a été passée
    @Column(nullable = false)
    private LocalDate dateCommande;

    // Statut : "EN_ATTENTE", "CONFIRMEE", "ANNULEE"
    @Column(nullable = false)
    private String statut;

    // L'utilisateur qui a passé la commande (plusieurs commandes → 1 user)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Les articles dans cette commande (une commande ↔ plusieurs articles)
    @ManyToMany
    @JoinTable(
        name = "commande_articles",
        joinColumns = @JoinColumn(name = "commande_id"),
        inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;
    
    // Le panier auquel appartient cette commande
    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;
}