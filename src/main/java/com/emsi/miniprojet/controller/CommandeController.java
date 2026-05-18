package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.User;
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

@Controller
@RequestMapping("/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    private final UserService userService;

    // GET /commandes → liste selon le rôle
    @GetMapping
    public String mesCommandes(@AuthenticationPrincipal UserDetails ud, Model model) {
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("commandes", commandeService.findAll());
            model.addAttribute("isAdmin", true);
        } else {
            User user = userService.findByUsername(ud.getUsername());
            model.addAttribute("commandes", commandeService.findByUser(user));
            model.addAttribute("isAdmin", false);
        }
        return "commandes/commande-list";
    }

    // GET /commandes/{id} → détail en lecture seule
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("commande", commandeService.findById(id));
        return "commandes/commande-detail";
    }

    // GET /commandes/{id}/annuler → annule la commande
    @GetMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, RedirectAttributes ra) {
        commandeService.annulerCommande(id);
        ra.addFlashAttribute("successMessage", "Commande annulée.");
        return "redirect:/commandes";
    }

    // POST /commandes/{id}/valider-final → reçoit les quantités JS et valide
    @PostMapping("/{id}/valider-final")
    public String validerFinal(@PathVariable Long id,
                               @RequestParam Map<String, String> formData,
                               RedirectAttributes ra) {
        try {
            Map<Long, Integer> quantites = new java.util.HashMap<>();
            formData.forEach((key, value) -> {
                if (key.startsWith("quantite_")) {
                    Long itemId = Long.parseLong(key.replace("quantite_", ""));
                    quantites.put(itemId, Integer.parseInt(value));
                }
            });
            commandeService.mettreAJourEtValider(id, quantites);
            ra.addFlashAttribute("successMessage", "Commande validée ! Stocks mis à jour.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/commandes/" + id;
        }
        return "redirect:/commandes";
    }

    // GET /commandes/{id}/supprimer → suppression (ADMIN seulement)
    @GetMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        commandeService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Commande supprimée.");
        return "redirect:/commandes";
    }
    
}