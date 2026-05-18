package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.Article;
import com.emsi.miniprojet.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// @Service = contient la logique métier, appelé par le Controller
@Service
public class ArticleService {

    // Le repository fait les requêtes SQL, le service l'utilise
    @Autowired
    private ArticleRepository articleRepository;

    // Retourne tous les articles de la base
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // Retourne un article par son id
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé : " + id));
    }

    // Sauvegarde un nouvel article OU modifie un existant
    // @Transactional = si une erreur arrive, annule tout (rollback)
    @Transactional
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    // Supprime un article par son id
    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // Recherche multicritère (description, prix max, stock min)
    public List<Article> searchArticles(String description, Double prixMax, Integer stockMin) {
        return articleRepository.searchArticles(description, prixMax, stockMin);
    }
}