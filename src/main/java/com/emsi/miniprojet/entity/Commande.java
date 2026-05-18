package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateCommande;

    // Statut : "EN_COURS", "VALIDEE", "ANNULEE"
    @Column(nullable = false)
    private String statut;

    // L'utilisateur qui a passé la commande
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Le panier auquel appartient cette commande
    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    // Les lignes de commande (article + quantité)
    // orphanRemoval = si on retire un item de la liste, il est supprimé en base
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommandeItem> items = new ArrayList<>();

    // Calcule le total de la commande = somme des sous-totaux
    // Appelé dans les templates : ${commande.total}
    public Double getTotal() {
        return items.stream()
                .mapToDouble(CommandeItem::getSousTotal)
                .sum();
    }
}