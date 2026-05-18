package com.emsi.miniprojet;

import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.repository.UserRepository;
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
    }
}