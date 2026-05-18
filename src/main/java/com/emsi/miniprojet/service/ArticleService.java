package com.emsi.miniprojet.service;

import com.emsi.miniprojet.entity.Article;
import com.emsi.miniprojet.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    // Retourne tous les articles
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // Retourne un article par son id
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé : " + id));
    }

    // Sauvegarde ou modifie un article
    @Transactional
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    // Supprime un article
    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // Recherche multicritère
    public List<Article> searchArticles(String description, Double prixMax, Integer stockMin) {
        return articleRepository.searchArticles(description, prixMax, stockMin);
    }
}