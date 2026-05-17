package com.emsi.miniprojet.repository;

import com.emsi.miniprojet.entity.Commande;
import com.emsi.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Récupère toutes les commandes d'un utilisateur donné
    List<Commande> findByUser(User user);
}