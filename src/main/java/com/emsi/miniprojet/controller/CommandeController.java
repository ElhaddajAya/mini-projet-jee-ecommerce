package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.service.ArticleService;
import com.emsi.miniprojet.service.CommandeService;
import com.emsi.miniprojet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller Commande — Partenaire B
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
        // Récupère l'entité User depuis le username Spring Security
        User user = userService.findByUsername(ud.getUsername());
        model.addAttribute("commandes", commandeService.findByUser(user));
        return "commandes/commande-list"; // → templates/commandes/commande-list.html
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
    // Créer une nouvelle commande EN_COURS pour l'utilisateur connecté
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/new")
    public String nouvelleCommande(@AuthenticationPrincipal UserDetails ud,
                                   RedirectAttributes ra) {
        User user = userService.findByUsername(ud.getUsername());
        var commande = commandeService.creerCommande(user);
        ra.addFlashAttribute("successMessage",
                "Commande #" + commande.getId() + " créée ! Ajoutez des articles.");
        return "redirect:/commandes/" + commande.getId();
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
}
