package com.itbs.maintenance.depot;

import com.itbs.maintenance.entite.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
/**
 * Entité représentant un technicien de maintenance.
 */
/**
 * Dépôt (Repository) pour l'accès aux données des techniciens.
 */
public interface TechnicienDepot extends JpaRepository<Technicien, Long> {
    List<Technicien> findByDisponibilite(Boolean disponibilite);
    List<Technicien> findByNomContainingIgnoreCase(String nom);
    long countByDisponibilite(Boolean disponibilite);
}
