package com.itbs.maintenance.depot;

import com.itbs.maintenance.entite.Intervention;
import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
/**
 * Entité représentant une intervention de maintenance.
 */
/**
 * Dépôt (Repository) pour l'accès aux données des interventions.
 */
public interface InterventionDepot extends JpaRepository<Intervention, Long> {
    List<Intervention> findByStatut(StatutIntervention statut);
    List<Intervention> findByEquipementId(Long equipementId);
    List<Intervention> findByTechnicienId(Long technicienId);
    List<Intervention> findByDateBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT SUM(i.cout) FROM Intervention i WHERE i.statut = 'TERMINEE'")
    Double sumCoutTotal();

    @Query("SELECT i.statut, COUNT(i) FROM Intervention i GROUP BY i.statut")
    List<Object[]> countByStatut();

    @Query("SELECT i.technicien.nom, COUNT(i) FROM Intervention i GROUP BY i.technicien ORDER BY COUNT(i) DESC")
    List<Object[]> countByTechnicien();

    long countByStatut(StatutIntervention statut);
}
