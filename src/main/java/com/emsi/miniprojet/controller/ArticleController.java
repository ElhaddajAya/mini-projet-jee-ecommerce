package com.emsi.miniprojet.controller;

import com.emsi.miniprojet.entity.Article;
import com.emsi.miniprojet.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// @Controller = gère les requêtes et retourne des vues Thymeleaf
@Controller
@RequestMapping("/articles") // toutes les URLs commencent par /articles
public class ArticleController {

    // On utilise le service, jamais le repository directement
    @Autowired
    private ArticleService articleService;

    // GET /articles → affiche la liste de tous les articles
    @GetMapping
    public String listArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        // "articles" = le nom de la variable accessible dans le HTML
        model.addAttribute("articles", articles);
        return "articles/list"; // → templates/articles/list.html
    }

    // GET /articles/new → affiche le formulaire de création
    @GetMapping("/new")
    public String newArticleForm(Model model) {
        // On envoie un article vide pour remplir le formulaire
        model.addAttribute("article", new Article());
        return "articles/form"; // → templates/articles/form.html
    }

    // POST /articles/save → enregistre l'article en base
    @PostMapping("/save")
    public String saveArticle(@ModelAttribute Article article) {
        articleService.saveArticle(article);
        // Après sauvegarde, retour à la liste
        return "redirect:/articles";
    }

    // GET /articles/edit/{id} → affiche le formulaire de modification
    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "articles/form";
    }

    // GET /articles/delete/{id} → supprime l'article
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    // GET /articles/search → recherche multicritère
    @GetMapping("/search")
    public String searchArticles(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double prixMax,
            @RequestParam(required = false) Integer stockMin,
            Model model) {
        List<Article> articles = articleService.searchArticles(description, prixMax, stockMin);
        model.addAttribute("articles", articles);
        return "articles/list";
    }
}