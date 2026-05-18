package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.*;
import com.emsi.miniprojet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final PanierRepository   panierRepository;
    private final ArticleRepository  articleRepository;

    // Retourne les commandes d'un utilisateur
    public List<Commande> findByUser(User user) {
        return commandeRepository.findByUser(user);
    }

    // Retourne toutes les commandes (vue admin)
    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    // Retourne une commande par son id
    public Commande findById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : id=" + id));
    }

    // Crée une commande EN_COURS dans le panier de l'utilisateur
    // Si le panier n'existe pas encore, il est créé automatiquement
    @Transactional
    public Commande creerCommande(User user) {
        Panier panier = panierRepository.findByUser(user)
                .orElseGet(() -> panierRepository.save(
                        Panier.builder().user(user).build()
                ));

        Commande commande = Commande.builder()
                .dateCommande(LocalDate.now())
                .statut("EN_COURS")
                .user(user)
                .panier(panier)
                .build();

        return commandeRepository.save(commande);
    }

    // Ajoute un article à une commande EN_COURS
    // Si l'article est déjà présent → incrémente la quantité
    @Transactional
    public void ajouterArticle(Long commandeId, Long articleId, int quantite) {
        Commande commande = findById(commandeId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable : id=" + articleId));

        // Vérification du stock
        if (article.getQuantiteStock() < quantite) {
            throw new RuntimeException(
                "Stock insuffisant pour \"" + article.getDescription() +
                "\" (disponible : " + article.getQuantiteStock() + ")"
            );
        }

        // Si déjà présent → incrémente, sinon → crée un nouvel item
        commande.getItems().stream()
                .filter(i -> i.getArticle().getId().equals(articleId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantite(item.getQuantite() + quantite),
                        () -> commande.getItems().add(
                                CommandeItem.builder()
                                        .commande(commande)
                                        .article(article)
                                        .quantite(quantite)
                                        .build()
                        )
                );

        commandeRepository.save(commande);
    }

    // Annule une commande (statut → ANNULEE)
    @Transactional
    public void annulerCommande(Long commandeId) {
        Commande commande = findById(commandeId);
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
    }

    // Suppression définitive d'une commande
    @Transactional
    public void deleteById(Long id) {
        commandeRepository.deleteById(id);
    }

    // Met à jour les quantités finales et valide la commande
    // Appelé depuis le panier via le formulaire JS
    @Transactional
    public void mettreAJourEtValider(Long commandeId, Map<Long, Integer> quantites) {
        Commande commande = findById(commandeId);

        // Supprime les items absents ou à quantité 0
        commande.getItems().removeIf(i -> {
            Integer q = quantites.get(i.getId());
            return q == null || q <= 0;
        });

        // Met à jour les quantités restantes
        commande.getItems().forEach(item -> {
            Integer q = quantites.get(item.getId());
            if (q != null && q > 0) item.setQuantite(q);
        });

        // Règle métier : au moins 1 article
        if (commande.getItems().isEmpty()) {
            throw new RuntimeException(
                "Impossible de valider : la commande doit contenir au moins un article."
            );
        }

        // Décrémente les stocks
        commande.getItems().forEach(item -> {
            Article article = item.getArticle();
            article.setQuantiteStock(article.getQuantiteStock() - item.getQuantite());
            articleRepository.save(article);
        });

        commande.setStatut("VALIDEE");
        commandeRepository.save(commande);
    }
    
}