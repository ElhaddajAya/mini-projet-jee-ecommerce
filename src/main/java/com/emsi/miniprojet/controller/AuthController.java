package com.emsi.miniprojet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @Controller = cette classe gère les requêtes HTTP et retourne des vues HTML
@Controller
public class AuthController {

    // Quand quelqu'un va sur /login, on affiche la page login.html
    @GetMapping("/login")
    public String loginPage(
            // Si ?error est dans l'URL → mauvais login/password
            @RequestParam(value = "error", required = false) String error,
            // Si ?logout est dans l'URL → utilisateur vient de se déconnecter
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // On envoie un message d'erreur à la vue si besoin
        if (error != null) {
            model.addAttribute("errorMessage", "Username ou mot de passe incorrect.");
        }

        // On envoie un message de succès si déconnexion réussie
        if (logout != null) {
            model.addAttribute("logoutMessage", "Vous êtes déconnecté.");
        }

        // Retourne le fichier templates/login.html
        return "login";
    }

    // Page d'accueil après connexion → redirige vers /articles
    @GetMapping("/")
    public String home() {
        return "redirect:/articles";
    }
}