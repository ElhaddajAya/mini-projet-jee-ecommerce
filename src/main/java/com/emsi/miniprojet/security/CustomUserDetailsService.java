package com.emsi.miniprojet.security;

import com.emsi.miniprojet.entity.User;
import com.emsi.miniprojet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// @Service = cette classe est un composant Spring géré automatiquement
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Spring injecte automatiquement le repository pour accéder à la base
    @Autowired
    private UserRepository userRepository;

    // Cette méthode est appelée automatiquement par Spring Security
    // quand quelqu'un essaie de se connecter avec un username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // On cherche l'utilisateur dans la base de données
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Utilisateur non trouvé : " + username
                ));

        // On retourne un objet UserDetails que Spring Security comprend
        // Il contient : username, password encodé, et le rôle
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
    }
}