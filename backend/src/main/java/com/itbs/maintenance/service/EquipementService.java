package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import com.itbs.maintenance.mapper.EquipementMapper;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE DES ÉQUIPEMENTS
 * ============================================================
 * Ce service gère toutes les opérations liées aux équipements.
 *
 * Un équipement peut avoir 4 états :
 *   - OPERATIONNEL  : fonctionne normalement
 *   - EN_PANNE      : une panne a été signalée
 *   - EN_MAINTENANCE: une intervention est en cours
 *   - HORS_SERVICE  : retiré du service
 *
 * Note : Les changements d'état sont gérés automatiquement
 * par PanneService et InterventionService. Ici on fait
 * uniquement le CRUD classique.
 * ============================================================
 */
@Service
public class EquipementService {

    // ─── Dépendances injectées ────────────────────────────────────────
    private final EquipementDepot  equipementDepot;   // accès base de données équipements
    private final EquipementMapper equipementMapper;  // conversion Equipement ↔ EquipementDTO

    public EquipementService(EquipementDepot equipementDepot, EquipementMapper equipementMapper) {
        this.equipementDepot  = equipementDepot;
        this.equipementMapper = equipementMapper;
    }

    // ─── CRUD de base ────────────────────────────────────────────────

    /**
     * Retourne la liste de tous les équipements.
     */
    public List<EquipementDTO> obtenirTous() {
        return equipementDepot.findAll()
                .stream()
                .map(equipementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne un équipement par son identifiant.
     * Lance une exception si l'ID n'existe pas.
     */
    public EquipementDTO obtenirParId(Long id) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable. ID : " + id));
        return equipementMapper.toDto(equipement);
    }

    /**
     * Crée un nouvel équipement.
     */
    public EquipementDTO creer(EquipementDTO dto) {
        Equipement equipement = equipementMapper.toEntity(dto);
        Equipement equipementSauvegarde = equipementDepot.save(equipement);
        return equipementMapper.toDto(equipementSauvegarde);
    }

    /**
     * Modifie un équipement existant (nom, état, date d'acquisition).
     */
    public EquipementDTO mettreAJour(Long id, EquipementDTO dto) {
        // Chercher l'équipement existant (erreur si introuvable)
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable. ID : " + id));

        // Mettre à jour les champs
        equipement.setNom(dto.getNom());
        equipement.setEtat(dto.getEtat());
        equipement.setDateAcquisition(dto.getDateAcquisition());

        return equipementMapper.toDto(equipementDepot.save(equipement));
    }

    /**
     * Supprime un équipement.
     */
    public void supprimer(Long id) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable. ID : " + id));
        equipementDepot.delete(equipement);
    }

    // ─── Méthodes filtrées (pour les modaux du frontend) ────────────

    /**
     * Retourne uniquement les équipements EN PANNE.
     * Utilisé dans le modal "Planifier une intervention"
     * (on ne peut planifier que sur un équipement en panne).
     */
    public List<EquipementDTO> obtenirEnPanne() {
        return equipementDepot.findByEtat(EtatEquipement.EN_PANNE)
                .stream()
                .map(equipementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne uniquement les équipements OPÉRATIONNELS.
     * Utilisé dans le modal "Signaler une panne"
     * (on ne peut signaler une panne que sur un équipement qui fonctionne).
     */
    public List<EquipementDTO> obtenirOperationnels() {
        return equipementDepot.findByEtat(EtatEquipement.OPERATIONNEL)
                .stream()
                .map(equipementMapper::toDto)
                .collect(Collectors.toList());
    }

    // ─── Méthodes pour le tableau de bord ───────────────────────────

    /**
     * Compte le nombre d'équipements selon leur état.
     * Exemple : countByEtat(EN_PANNE) → nombre d'équipements en panne
     */
    public long countByEtat(EtatEquipement etat) {
        return equipementDepot.countByEtat(etat);
    }

    /** Alias utilisé par le tableau de bord */
    public List<EquipementDTO> findAll() {
        return obtenirTous();
    }
}
