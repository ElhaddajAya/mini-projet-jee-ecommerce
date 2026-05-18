package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.Panier;
import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.service.PanierService;
import com.emsi.miniprojet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller Panier — Partenaire B
 *
 * Affiche le panier de l'utilisateur connecté avec :
 *   - toutes ses commandes EN_COURS
 *   - le total dynamique de chaque commande
 *   - le total général du panier
 *   - les boutons pour valider chaque commande
 */
@Controller
@RequestMapping("/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;
    private final UserService   userService;

    /**
     * GET /panier → affiche le panier de l'utilisateur connecté.
     * Si le panier n'existe pas encore, il est créé automatiquement.
     */
    @GetMapping
    public String panier(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userService.findByUsername(ud.getUsername());
        Panier panier = panierService.getPanierOrCreate(user);
        model.addAttribute("panier", panier);
        return "panier"; // → templates/panier.html
    }
}
