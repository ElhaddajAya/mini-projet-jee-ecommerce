package com.emsi.miniprojet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "paniers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Panier {

    // Identifiant unique généré automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chaque utilisateur a un seul panier
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Les commandes dans ce panier
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<Commande> commandes;
    
	 // Total de toutes les commandes EN_COURS dans le panier
	 // Appelé dans panier.html : ${panier.totalGeneral}
	 public Double getTotalGeneral() {
	     if (commandes == null) return 0.0;
	     return commandes.stream()
	             .filter(c -> "EN_COURS".equals(c.getStatut()))
	             .mapToDouble(Commande::getTotal)
	             .sum();
	 }
}