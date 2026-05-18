package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entité CommandeItem — Partenaire B
 * Représente la ligne d'une commande : 1 article + 1 quantité.
 * Remplace une relation ManyToMany brute entre Commande et Article,
 * ce qui permet de stocker la quantité commandée.
 *
 * Relation : Commande (1) ──< CommandeItem >── (1) Article
 */
@Entity
@Table(name = "commande_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** La commande à laquelle appartient cet item */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    /**
     * L'article commandé.
     * EAGER : on a besoin du prix et de la description à chaque affichage.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    @NotNull(message = "L'article est obligatoire")
    private Article article;

    /** Quantité commandée — doit être >= 1 */
    @Min(value = 1, message = "La quantité doit être au minimum 1")
    @Column(nullable = false)
    private Integer quantite;

    /**
     * Sous-total de la ligne = prix unitaire × quantité.
     * Appelé dans les templates : ${item.sousTotal}
     */
    public Double getSousTotal() {
        return article.getPrix() * quantite;
    }
}
