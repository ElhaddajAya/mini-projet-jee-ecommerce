package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.service.ArticleService;
import com.emsi.miniprojet.service.CommandeService;
import com.emsi.miniprojet.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Commande
 *
 * Gère toutes les URLs liées aux commandes :
 *   GET  /commandes              → liste "mes commandes" (dashboard)
 *   GET  /commandes/new          → créer une nouvelle commande
 *   GET  /commandes/{id}         → détail d'une commande
 *   POST /commandes/{id}/ajouter-article   → ajouter un article
 *   GET  /commandes/{cid}/supprimer-item/{iid} → retirer un article
 *   GET  /commandes/{id}/valider → valider la commande
 *   GET  /commandes/{id}/annuler → annuler la commande
 *
 * RÈGLE : ce Controller appelle UNIQUEMENT les Services, jamais les Repositories.
 */
@Controller
@RequestMapping("/commandes")
@RequiredArgsConstructor
public class CommandeController {

    // ── Injection des services (faible couplage) ──────────────────────────────
    private final CommandeService commandeService;
    private final ArticleService  articleService;
    private final UserService     userService;

    // ─────────────────────────────────────────────────────────────────────────
    // Dashboard : liste de toutes les commandes de l'utilisateur connecté
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public String mesCommandes(@AuthenticationPrincipal UserDetails ud, Model model) {
        // Vérifie si l'utilisateur est ADMIN
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // ADMIN voit TOUTES les commandes de tous les utilisateurs
            model.addAttribute("commandes", commandeService.findAll());
            model.addAttribute("isAdmin", true);
        } else {
            // USER voit seulement ses propres commandes
            User user = userService.findByUsername(ud.getUsername());
            model.addAttribute("commandes", commandeService.findByUser(user));
            model.addAttribute("isAdmin", false);
        }
        return "commandes/commande-list";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Détail d'une commande + formulaire d'ajout d'article
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("commande", commandeService.findById(id));
        model.addAttribute("articles", articleService.findAll()); // liste déroulante
        return "commandes/commande-detail"; // → templates/commandes/commande-detail.html
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ajouter un article à une commande (formulaire POST depuis commande-detail)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/{id}/ajouter-article")
    public String ajouterArticle(@PathVariable Long id,
                                 @RequestParam Long articleId,
                                 @RequestParam int quantite,
                                 RedirectAttributes ra) {
        try {
            commandeService.ajouterArticle(id, articleId, quantite);
            ra.addFlashAttribute("successMessage", "Article ajouté à la commande !");
        } catch (RuntimeException e) {
            // Ex : stock insuffisant → message d'erreur affiché dans la vue
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/commandes/" + id;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Supprimer un article d'une commande
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{commandeId}/supprimer-item/{itemId}")
    public String supprimerItem(@PathVariable Long commandeId,
                                @PathVariable Long itemId,
                                RedirectAttributes ra) {
        commandeService.supprimerItem(commandeId, itemId);
        ra.addFlashAttribute("successMessage", "Article retiré de la commande.");
        return "redirect:/commandes/" + commandeId;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Valider une commande (vérifie >= 1 article, décrémente stocks)
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/valider")
    public String valider(@PathVariable Long id, RedirectAttributes ra) {
        try {
            commandeService.validerCommande(id);
            ra.addFlashAttribute("successMessage",
                    "Commande validée avec succès ! Les stocks ont été mis à jour.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/commandes";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Annuler une commande
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes ra) {
        commandeService.annulerCommande(id);
        ra.addFlashAttribute("successMessage", "Commande annulée.");
        return "redirect:/commandes";
    }
    
	 // POST /commandes/{id}/valider-final
	 // Reçoit les quantités finales depuis le formulaire JS et valide
	 @PostMapping("/{id}/valider-final")
	 public String validerFinal(@PathVariable Long id,
	                            @RequestParam Map<String, String> formData,
	                            RedirectAttributes ra) {
	     try {
	         // Construit la map itemId → quantite depuis les paramètres du formulaire
	         // Les inputs s'appellent "quantite_123" (123 = itemId)
	         Map<Long, Integer> quantites = new java.util.HashMap<>();
	         formData.forEach((key, value) -> {
	             if (key.startsWith("quantite_")) {
	                 Long itemId = Long.parseLong(key.replace("quantite_", ""));
	                 Integer q = Integer.parseInt(value);
	                 quantites.put(itemId, q);
	             }
	         });
	
	         commandeService.mettreAJourEtValider(id, quantites);
	         ra.addFlashAttribute("successMessage", "Commande validée ! Les stocks ont été mis à jour.");
	     } catch (RuntimeException e) {
	         ra.addFlashAttribute("errorMessage", e.getMessage());
	         return "redirect:/commandes/" + id;
	     }
	     return "redirect:/commandes";
	 }
	 
	// Supprime une commande (ADMIN seulement)
	 @GetMapping("/{id}/supprimer")
	 public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
	     commandeService.deleteById(id);
	     ra.addFlashAttribute("successMessage", "Commande supprimée.");
	     return "redirect:/commandes";
	 }
}
