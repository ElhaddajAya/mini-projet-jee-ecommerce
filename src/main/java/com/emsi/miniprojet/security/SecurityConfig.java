package com.emsi.miniprojet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration = ce fichier contient des configurations Spring
// @EnableWebSecurity = active Spring Security dans l'application

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // On injecte notre service qui charge les users depuis la base
    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Bean = un objet géré par Spring, disponible partout dans l'app
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cette méthode définit toutes les règles de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // --- Règles d'accès aux URLs ---
	        .authorizeHttpRequests(auth -> auth
	
	        	    // Pages publiques sans connexion
	        	    .requestMatchers("/login", "/css/**", "/js/**").permitAll()
	
	        	    // Seulement ADMIN peut créer, modifier, supprimer
	        	    .requestMatchers("/articles/new", "/articles/save",
	        	                     "/articles/edit/**", "/articles/delete/**").hasRole("ADMIN")
	
	        	    // Toutes les autres pages → juste être connecté
	        	    .anyRequest().authenticated()
	        	)

            // --- Configuration de la page de login ---
            .formLogin(form -> form

                // URL de la page de login (notre formulaire HTML)
                .loginPage("/login")

                // Après connexion réussie, rediriger vers la liste des articles
                .defaultSuccessUrl("/articles", true)

                // Si login échoué, rediriger vers /login?error
                .failureUrl("/login?error")

                // Accessible sans être connecté
                .permitAll()
            )

            // --- Configuration du logout ---
            .logout(logout -> logout

                // URL pour se déconnecter (un simple lien suffit)
                .logoutUrl("/logout")

                // Après déconnexion, aller à /login?logout
                .logoutSuccessUrl("/login?logout")

                .permitAll()
            );

        return http.build();
    }

    // Cette méthode connecte Spring Security avec notre service
    // et lui dit d'utiliser BCrypt pour vérifier les mots de passe
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
            http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());

        return builder.build();
    }
}