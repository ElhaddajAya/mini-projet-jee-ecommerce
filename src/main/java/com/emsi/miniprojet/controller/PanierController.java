package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.Commande;
import com.emsi.miniprojet.entity.Panier;
import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.service.CommandeService;
import com.emsi.miniprojet.service.PanierService;
import com.emsi.miniprojet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;
    private final UserService   userService;
    private final CommandeService commandeService;

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
    
	 // POST /panier/ajouter?articleId=X&quantite=1
	 // Ajoute un article au panier (crée une commande EN_COURS si besoin)
	 @PostMapping("/ajouter")
	 public String ajouterAuPanier(@AuthenticationPrincipal UserDetails ud,
	                                @RequestParam Long articleId,
	                                @RequestParam(defaultValue = "1") int quantite,
	                                RedirectAttributes ra) {
	     User user = userService.findByUsername(ud.getUsername());
	
	     // Récupère ou crée le panier
	     Panier panier = panierService.getPanierOrCreate(user);
	
	     // Cherche une commande EN_COURS existante dans le panier
	     // Si aucune → en crée une nouvelle automatiquement
	     Commande commandeEnCours = panier.getCommandes() == null ? null :
	             panier.getCommandes().stream()
	                     .filter(c -> "EN_COURS".equals(c.getStatut()))
	                     .findFirst()
	                     .orElse(null);
	
	     if (commandeEnCours == null) {
	         commandeEnCours = commandeService.creerCommande(user);
	     }
	
	     try {
	         commandeService.ajouterArticle(commandeEnCours.getId(), articleId, quantite);
	         ra.addFlashAttribute("successMessage", "Article ajouté au panier !");
	     } catch (RuntimeException e) {
	         ra.addFlashAttribute("errorMessage", e.getMessage());
	     }
	
	     return "redirect:/articles";
	 }
}
