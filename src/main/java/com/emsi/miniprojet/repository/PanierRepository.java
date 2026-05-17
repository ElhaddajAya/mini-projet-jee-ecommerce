package com.emsi.miniprojet.repository;

import com.emsi.miniprojet.entity.Panier;
import com.emsi.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {

    // Récupère le panier d'un utilisateur (chaque user a un seul panier)
    Optional<Panier> findByUser(User user);
}