package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.Panier;
import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.repository.PanierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Panier — Partenaire B
 *
 * Gère le panier de l'utilisateur connecté.
 * Le panier est créé automatiquement à la première utilisation
 * (pattern "lazy initialization").
 */
@Service
@RequiredArgsConstructor
public class PanierService {

    private final PanierRepository panierRepository;

    /**
     * Retourne le panier de l'utilisateur.
     * S'il n'existe pas encore en base, il est créé et sauvegardé automatiquement.
     *
     * @param user l'utilisateur connecté
     * @return le panier existant ou nouvellement créé
     */
    @Transactional
    public Panier getPanierOrCreate(User user) {
        return panierRepository.findByUser(user)
                .orElseGet(() -> panierRepository.save(
                        Panier.builder().user(user).build()
                ));
    }
}
