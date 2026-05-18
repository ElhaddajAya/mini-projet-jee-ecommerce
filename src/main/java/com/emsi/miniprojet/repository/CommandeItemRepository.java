package com.emsi.miniprojet.repository;

import com.emsi.miniprojet.entity.CommandeItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository CommandeItem — Partenaire B
 * Les opérations CRUD de base suffisent ici.
 * La suppression d'un item se fait via orphanRemoval sur Commande.
 */
public interface CommandeItemRepository extends JpaRepository<CommandeItem, Long> {
}
