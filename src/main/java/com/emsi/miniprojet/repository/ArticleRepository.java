package com.emsi.miniprojet.repository;

import com.emsi.miniprojet.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Recherche multicritère : filtre par description ET/OU prix max ET/OU stock min
    // Le mot clé LIKE permet de chercher une partie du texte
    @Query("SELECT a FROM Article a WHERE " +
           "(:description IS NULL OR a.description LIKE %:description%) AND " +
           "(:prixMax IS NULL OR a.prix <= :prixMax) AND " +
           "(:stockMin IS NULL OR a.quantiteStock >= :stockMin)")
    List<Article> searchArticles(
        @Param("description") String description,
        @Param("prixMax") Double prixMax,
        @Param("stockMin") Integer stockMin
    );
}