package com.emsi.miniprojet;

import com.emsi.miniprojet.entity.Article;
import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.repository.ArticleRepository;
import com.emsi.miniprojet.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// @Component = Spring lance cette classe automatiquement au démarrage
// CommandLineRunner = exécute la méthode run() une fois l'app démarrée
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired private ArticleRepository articleRepository;

    @Override
    public void run(String... args) throws Exception {

        // On vérifie d'abord si l'admin existe déjà
        // pour ne pas créer un doublon à chaque redémarrage
        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = User.builder()
                    .username("admin")
                    // passwordEncoder.encode() transforme "1234" en hash BCrypt
                    .password(passwordEncoder.encode("1234"))
                    .role("ROLE_ADMIN")
                    .build();

            userRepository.save(admin);
            System.out.println(">>> Utilisateur admin créé avec succès !");
        }

        if (userRepository.findByUsername("user").isEmpty()) {

            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("1234"))
                    .role("ROLE_USER")
                    .build();

            userRepository.save(user);
            System.out.println(">>> Utilisateur user créé avec succès !");
        }
        
        if (userRepository.findByUsername("malak").isEmpty()) {
            userRepository.save(User.builder()
                    .username("malak")
                    .password(passwordEncoder.encode("1234"))
                    .role("ROLE_USER")
                    .build());
            System.out.println(">>> malak créé");
        }

        // ── Articles (seulement si la base est vide) ──────────────────────────
        if (articleRepository.count() == 0) {

            articleRepository.save(Article.builder()
                    .description("Laptop Dell Inspiron 15")
                    .prix(8999.99)
                    .dateExpiration(LocalDate.of(2027, 12, 31))
                    .quantiteStock(15)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Souris Logitech MX Master 3")
                    .prix(649.00)
                    .dateExpiration(LocalDate.of(2026, 10, 15))
                    .quantiteStock(40)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Clavier Mécanique Keychron K2")
                    .prix(1199.00)
                    .dateExpiration(LocalDate.of(2027, 6, 30))
                    .quantiteStock(25)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Écran Samsung 24\" Full HD")
                    .prix(2499.00)
                    .dateExpiration(LocalDate.of(2028, 1, 1))
                    .quantiteStock(10)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Casque Audio Sony WH-1000XM5")
                    .prix(3299.00)
                    .dateExpiration(LocalDate.of(2027, 3, 20))
                    .quantiteStock(8)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Webcam Logitech C920 HD")
                    .prix(799.00)
                    .dateExpiration(LocalDate.of(2026, 8, 31))
                    .quantiteStock(30)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Disque SSD Samsung 1To")
                    .prix(1099.00)
                    .dateExpiration(LocalDate.of(2029, 5, 1))
                    .quantiteStock(50)
                    .build());

            articleRepository.save(Article.builder()
                    .description("Hub USB-C 7 ports")
                    .prix(349.00)
                    .dateExpiration(LocalDate.of(2026, 11, 30))
                    .quantiteStock(5)  // stock faible → badge rouge
                    .build());

            System.out.println(">>> 8 articles créés");
        }
    }
}