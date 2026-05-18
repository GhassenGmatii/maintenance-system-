package com.itbs.maintenance.depot;

import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
/**
 * Entité représentant un équipement dans le système.
 */
/**
 * Dépôt (Repository) pour l'accès aux données des équipements.
 */
public interface EquipementDepot extends JpaRepository<Equipement, Long> {
    List<Equipement> findByEtat(EtatEquipement etat);
    List<Equipement> findByNomContainingIgnoreCase(String nom);
    long countByEtat(EtatEquipement etat);
}
