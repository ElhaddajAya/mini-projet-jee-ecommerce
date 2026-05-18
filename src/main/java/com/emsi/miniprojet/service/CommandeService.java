package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.*;
import com.emsi.miniprojet.repository.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Commande — Partenaire B
 *
 * Contient toute la logique métier liée aux commandes :
 *   - Création d'une commande dans le panier
 *   - Ajout / suppression d'articles
 *   - Validation (avec vérification stock et règle >= 1 article)
 *   - Annulation
 *
 * RÈGLE : le Controller ne parle JAMAIS directement au Repository.
 *         Il passe toujours par ce Service (faible couplage).
 */
@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final PanierRepository   panierRepository;
    private final ArticleRepository  articleRepository;

    // ── Lecture ───────────────────────────────────────────────────────────────

    /** Retourne toutes les commandes d'un utilisateur (dashboard) */
    public List<Commande> findByUser(User user) {
        return commandeRepository.findByUser(user);
    }

    /** Retourne une commande par son id — lance une exception si introuvable */
    public Commande findById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : id=" + id));
    }

    // ── Création ──────────────────────────────────────────────────────────────

    /**
     * Crée une nouvelle commande EN_COURS dans le panier de l'utilisateur.
     * Si le panier n'existe pas encore, il est créé automatiquement.
     *
     * @param user l'utilisateur connecté
     * @return la commande nouvellement créée
     */
    @Transactional
    public Commande creerCommande(User user) {
        // Récupère le panier existant ou en crée un nouveau
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

    // ── Gestion des articles ──────────────────────────────────────────────────

    /**
     * Ajoute un article à une commande EN_COURS.
     *
     * Règles métier :
     *   1. Vérifier que le stock de l'article est suffisant.
     *   2. Si l'article est déjà dans la commande → incrémenter la quantité.
     *   3. Sinon → créer un nouveau CommandeItem.
     *
     * @throws RuntimeException si le stock est insuffisant
     */
    @Transactional
    public void ajouterArticle(Long commandeId, Long articleId, int quantite) {
        Commande commande = findById(commandeId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable : id=" + articleId));

        // ── Règle métier : vérification du stock ──────────────────────────────
        if (article.getQuantiteStock() < quantite) {
            throw new RuntimeException(
                "Stock insuffisant pour \"" + article.getDescription() + "\" " +
                "(disponible : " + article.getQuantiteStock() + ")"
            );
        }

        // ── Si l'article est déjà présent → on incrémente la quantité ─────────
        commande.getItems().stream()
                .filter(i -> i.getArticle().getId().equals(articleId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantite(item.getQuantite() + quantite),
                        () -> {
                            CommandeItem nouvelItem = CommandeItem.builder()
                                    .commande(commande)
                                    .article(article)
                                    .quantite(quantite)
                                    .build();
                            commande.getItems().add(nouvelItem);
                        }
                );

        commandeRepository.save(commande);
    }

    /**
     * Retire un CommandeItem d'une commande.
     * Grâce à orphanRemoval = true sur la relation, l'item est supprimé en base.
     */
    @Transactional
    public void supprimerItem(Long commandeId, Long itemId) {
        Commande commande = findById(commandeId);
        commande.getItems().removeIf(i -> i.getId().equals(itemId));
        commandeRepository.save(commande);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Valide une commande EN_COURS.
     *
     * Règles métier :
     *   1. La commande doit contenir AU MOINS 1 article.
     *   2. Décrémenter le stock de chaque article commandé.
     *   3. Passer le statut à VALIDEE.
     *
     * @throws RuntimeException si la commande est vide
     */
    @Transactional
    public void validerCommande(Long commandeId) {
        Commande commande = findById(commandeId);

        // ── Règle métier : au moins 1 article ────────────────────────────────
        if (commande.getItems().isEmpty()) {
            throw new RuntimeException(
                "Impossible de valider : la commande doit contenir au moins un article."
            );
        }

        // ── Décrémentation des stocks ─────────────────────────────────────────
        commande.getItems().forEach(item -> {
            Article article = item.getArticle();
            article.setQuantiteStock(article.getQuantiteStock() - item.getQuantite());
            articleRepository.save(article);
        });

        commande.setStatut("VALIDEE");
        commandeRepository.save(commande);
    }

    // ── Annulation ────────────────────────────────────────────────────────────

    /** Annule une commande (statut → ANNULEE). Le stock n'est pas modifié. */
    @Transactional
    public void annulerCommande(Long commandeId) {
        Commande commande = findById(commandeId);
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
    }

    /** Suppression définitive d'une commande */
    @Transactional
    public void deleteById(Long id) {
        commandeRepository.deleteById(id);
    }
    
    // Met à jour toutes les quantités ET valide en une seule transaction
    @Transactional
    public void mettreAJourEtValider(Long commandeId, Map<Long, Integer> quantites) {
        Commande commande = findById(commandeId);

        // Supprime les items avec quantité = 0
        commande.getItems().removeIf(i -> {
            Integer q = quantites.get(i.getId());
            return q == null || q <= 0;
        });

        // Met à jour les quantités restantes
        commande.getItems().forEach(item -> {
            Integer q = quantites.get(item.getId());
            if (q != null && q > 0) {
                item.setQuantite(q);
            }
        });

        // Vérifie qu'il reste au moins 1 article
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
    
    // Retourne toutes les commandes (vue admin)
    	public List<Commande> findAll() {
        return commandeRepository.findAll();
    }
}
