package com.itbs.maintenance.depot;

import com.itbs.maintenance.entite.Panne;
import com.itbs.maintenance.entite.Panne.CategoriePanne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
/**
 * Entité représentant une panne ou une anomalie signalée.
 */
/**
 * Dépôt (Repository) pour l'accès aux données des pannes.
 */
public interface PanneDepot extends JpaRepository<Panne, Long> {
    List<Panne> findByEquipementId(Long equipementId);
    List<Panne> findByCategorie(CategoriePanne categorie);
    List<Panne> findByDateSignalementBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT p.categorie, COUNT(p) FROM Panne p GROUP BY p.categorie")
    List<Object[]> countByCategorie();

    @Query("SELECT p.equipement.nom, COUNT(p) FROM Panne p GROUP BY p.equipement ORDER BY COUNT(p) DESC")
    List<Object[]> countByEquipement();

    long countByDateSignalementBetween(LocalDate debut, LocalDate fin);
}
