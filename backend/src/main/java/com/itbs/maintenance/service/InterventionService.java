package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import com.itbs.maintenance.entite.Intervention;
import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import com.itbs.maintenance.mapper.InterventionMapper;
import com.itbs.maintenance.depot.InterventionDepot;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.depot.TechnicienDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE DES INTERVENTIONS
 * ============================================================
 * Ce service gère toutes les opérations liées aux interventions.
 *
 * Règles métier appliquées automatiquement :
 *  1. Créer une intervention       → équipement passe à EN_MAINTENANCE
 *  2. Intervention TERMINEE        → équipement passe à OPERATIONNEL
 *  3. Intervention ANNULEE         → équipement revient à EN_PANNE
 *
 * Calcul automatique du statut selon les dates :
 *  - dateDebut > aujourd'hui           → PLANIFIEE  (pas encore commencée)
 *  - dateDebut ≤ aujourd'hui ≤ dateFin → EN_COURS   (en cours de réalisation)
 *  - dateFin < aujourd'hui             → TERMINEE   (période dépassée)
 * ============================================================
 */
@Service
public class InterventionService {

    // ─── Dépendances injectées ────────────────────────────────────────
    private final InterventionDepot  interventionDepot;   // accès base de données interventions
    private final InterventionMapper interventionMapper;  // conversion Intervention ↔ InterventionDTO
    private final EquipementDepot    equipementDepot;     // accès base de données équipements
    private final TechnicienDepot    technicienDepot;     // accès base de données techniciens

    // Spring injecte automatiquement ces dépendances via le constructeur
    public InterventionService(InterventionDepot interventionDepot,
                               InterventionMapper interventionMapper,
                               EquipementDepot equipementDepot,
                               TechnicienDepot technicienDepot) {
        this.interventionDepot  = interventionDepot;
        this.interventionMapper = interventionMapper;
        this.equipementDepot    = equipementDepot;
        this.technicienDepot    = technicienDepot;
    }

    // ─── CRUD de base ────────────────────────────────────────────────

    /**
     * Retourne la liste de toutes les interventions.
     */
    public List<InterventionDTO> obtenirTous() {
        return interventionDepot.findAll()
                .stream()
                .map(interventionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne une intervention par son identifiant.
     */
    public InterventionDTO obtenirParId(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention introuvable. ID : " + id));
        return interventionMapper.toDto(intervention);
    }

    /**
     * Crée une nouvelle intervention et met à jour l'état de l'équipement.
     *
     * Règles métier :
     *   → Le statut est calculé automatiquement selon les dates
     *   → L'équipement passe automatiquement à EN_MAINTENANCE
     */
    @Transactional
    public InterventionDTO creer(InterventionDTO dto) {

        // 1. Convertir le DTO en entité
        Intervention intervention = interventionMapper.toEntity(dto);

        // 2. Calculer automatiquement le statut selon les dates
        StatutIntervention statut = calculerStatut(intervention.getDate(), intervention.getDateFin());
        intervention.setStatut(statut);

        // 3. Sauvegarder en base
        Intervention interventionSauvegardee = interventionDepot.save(intervention);

        // 4. L'équipement passe en maintenance (règle métier)
        changerEtatEquipement(interventionSauvegardee.getEquipement(), EtatEquipement.EN_MAINTENANCE);

        // 5. Le technicien devient OCCUPÉ (il est assigné à cette intervention)
        changerDisponibiliteTechnicien(interventionSauvegardee.getTechnicien(), false);

        return interventionMapper.toDto(interventionSauvegardee);
    }

    /**
     * Modifie une intervention existante.
     *
     * Règles métier selon le nouveau statut :
     *   TERMINEE → équipement passe à OPERATIONNEL
     *   ANNULEE  → équipement revient à EN_PANNE
     *   Autres   → équipement reste EN_MAINTENANCE
     *
     * Si le statut envoyé est PLANIFIEE ou EN_COURS,
     * il est recalculé automatiquement selon les dates.
     */
    @Transactional
    public InterventionDTO mettreAJour(Long id, InterventionDTO dto) {

        // 1. Chercher l'intervention existante (erreur si introuvable)
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention introuvable. ID : " + id));

        // 2. Mettre à jour les champs simples
        intervention.setStatut(dto.getStatut());
        intervention.setDate(dto.getDate());
        intervention.setDateFin(dto.getDateFin());
        intervention.setCout(dto.getCout());

        // 3. Mettre à jour l'équipement si un nouvel ID est fourni
        if (dto.getEquipementId() != null) {
            intervention.setEquipement(
                equipementDepot.findById(dto.getEquipementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable. ID : " + dto.getEquipementId()))
            );
        }

        // 4. Mettre à jour le technicien si un nouvel ID est fourni
        if (dto.getTechnicienId() != null) {
            intervention.setTechnicien(
                technicienDepot.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable. ID : " + dto.getTechnicienId()))
            );
        }

        // 5. Recalculer le statut selon les dates
        //    (seulement si ce n'est pas TERMINEE ou ANNULEE — ces deux statuts sont manuels)
        if (dto.getStatut() == StatutIntervention.PLANIFIEE
         || dto.getStatut() == StatutIntervention.EN_COURS) {
            intervention.setStatut(calculerStatut(intervention.getDate(), intervention.getDateFin()));
        }

        // 6. Sauvegarder
        Intervention interventionSauvegardee = interventionDepot.save(intervention);

        // 7. Mettre à jour l'état de l'équipement selon le statut final
        switch (interventionSauvegardee.getStatut()) {
            case TERMINEE -> {
                // Intervention finie → équipement opérationnel, technicien redevient disponible
                changerEtatEquipement(interventionSauvegardee.getEquipement(), EtatEquipement.OPERATIONNEL);
                changerDisponibiliteTechnicien(interventionSauvegardee.getTechnicien(), true);
            }
            case ANNULEE -> {
                // Intervention annulée → équipement en panne, technicien redevient disponible
                changerEtatEquipement(interventionSauvegardee.getEquipement(), EtatEquipement.EN_PANNE);
                changerDisponibiliteTechnicien(interventionSauvegardee.getTechnicien(), true);
            }
            default -> {
                // En cours ou planifiée → équipement en maintenance, technicien occupé
                changerEtatEquipement(interventionSauvegardee.getEquipement(), EtatEquipement.EN_MAINTENANCE);
                changerDisponibiliteTechnicien(interventionSauvegardee.getTechnicien(), false);
            }
        }

        return interventionMapper.toDto(interventionSauvegardee);
    }

    /**
     * Supprime une intervention.
     */
    public void supprimer(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention introuvable. ID : " + id));
        interventionDepot.delete(intervention);
    }

    // ─── Méthodes utilitaires ────────────────────────────────────────

    /**
     * Change la disponibilité d'un technicien.
     *   false = Occupé    (quand il est assigné à une intervention active)
     *   true  = Disponible (quand l'intervention est terminée ou annulée)
     */
    private void changerDisponibiliteTechnicien(com.itbs.maintenance.entite.Technicien technicien, boolean disponible) {
        if (technicien != null && technicien.getDisponibilite() != disponible) {
            technicien.setDisponibilite(disponible);
            technicienDepot.save(technicien);
        }
    }
    /**
     * Calcule automatiquement le statut d'une intervention selon ses dates.
     *
     * Logique :
     *   Si la date de début est dans le futur  → PLANIFIEE
     *   Si on est dans la période prévue        → EN_COURS
     *   Si la date de fin est dépassée          → TERMINEE
     */
    private StatutIntervention calculerStatut(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate aujourd_hui = LocalDate.now();

        // L'intervention n'a pas encore commencé
        if (dateDebut != null && dateDebut.isAfter(aujourd_hui)) {
            return StatutIntervention.PLANIFIEE;
        }

        // L'intervention est terminée (date de fin dépassée)
        if (dateFin != null && dateFin.isBefore(aujourd_hui)) {
            return StatutIntervention.TERMINEE;
        }

        // On est dans la période prévue → en cours
        return StatutIntervention.EN_COURS;
    }

    /**
     * Change l'état d'un équipement en base de données.
     * N'effectue la mise à jour que si l'état a réellement changé.
     */
    private void changerEtatEquipement(Equipement equipement, EtatEquipement nouvelEtat) {
        if (equipement != null && equipement.getEtat() != nouvelEtat) {
            equipement.setEtat(nouvelEtat);
            equipementDepot.save(equipement);
        }
    }

    // ─── Méthodes pour le tableau de bord ───────────────────────────

    /** Compte les interventions par statut */
    public long countByStatut(StatutIntervention statut) {
        return interventionDepot.countByStatut(statut);
    }

    /** Calcule le coût total des interventions terminées */
    public Double getCoutTotal() {
        Double total = interventionDepot.sumCoutTotal();
        return total != null ? total : 0.0;  // évite le NullPointerException
    }

    /** Statistiques par statut (pour les graphiques) */
    public List<Object[]> getStatsByStatut() {
        return interventionDepot.countByStatut();
    }

    /** Statistiques par technicien (pour les graphiques) */
    public List<Object[]> getStatsByTechnicien() {
        return interventionDepot.countByTechnicien();
    }

    /** Alias utilisé par le tableau de bord */
    public List<InterventionDTO> findAll() {
        return obtenirTous();
    }
}
