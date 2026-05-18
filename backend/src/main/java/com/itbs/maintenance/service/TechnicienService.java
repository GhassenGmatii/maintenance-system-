package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.entite.Technicien;
import com.itbs.maintenance.mapper.TechnicienMapper;
import com.itbs.maintenance.depot.TechnicienDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE DES TECHNICIENS
 * ============================================================
 * Ce service gère toutes les opérations liées aux techniciens.
 *
 * La disponibilité du technicien (Disponible / Occupé) est
 * gérée automatiquement par le système :
 *  - Quand on lui assigne une intervention → Il devient OCCUPÉ
 *  - Quand l'intervention est terminée     → Il redevient DISPONIBLE
 * (voir InterventionService pour cette logique).
 * ============================================================
 */
@Service
public class TechnicienService {

    // ─── Dépendances injectées ────────────────────────────────────────
    private final TechnicienDepot  technicienDepot;   // accès base de données techniciens
    private final TechnicienMapper technicienMapper;  // conversion Technicien ↔ TechnicienDTO

    public TechnicienService(TechnicienDepot technicienDepot, TechnicienMapper technicienMapper) {
        this.technicienDepot  = technicienDepot;
        this.technicienMapper = technicienMapper;
    }

    // ─── CRUD de base ────────────────────────────────────────────────

    /**
     * Retourne la liste de tous les techniciens.
     */
    public List<TechnicienDTO> obtenirTous() {
        return technicienDepot.findAll()
                .stream()
                .map(technicienMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retourne un technicien par son identifiant.
     * Lance une exception si l'ID n'existe pas.
     */
    public TechnicienDTO obtenirParId(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable. ID : " + id));
        return technicienMapper.toDto(technicien);
    }

    /**
     * Ajoute un nouveau technicien.
     */
    public TechnicienDTO creer(TechnicienDTO dto) {
        Technicien technicien = technicienMapper.toEntity(dto);
        Technicien technicienSauvegarde = technicienDepot.save(technicien);
        return technicienMapper.toDto(technicienSauvegarde);
    }

    /**
     * Modifie un technicien existant (nom, compétences, disponibilité).
     */
    public TechnicienDTO mettreAJour(Long id, TechnicienDTO dto) {
        // Chercher le technicien existant (erreur si introuvable)
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable. ID : " + id));

        // Mettre à jour les champs
        technicien.setNom(dto.getNom());
        technicien.setCompetences(dto.getCompetences());
        technicien.setDisponibilite(dto.getDisponibilite());

        return technicienMapper.toDto(technicienDepot.save(technicien));
    }

    /**
     * Supprime un technicien de la base de données.
     */
    public void supprimer(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable. ID : " + id));
        technicienDepot.delete(technicien);
    }

    // ─── Méthodes pour le tableau de bord ───────────────────────────

    /**
     * Compte combien de techniciens sont actuellement disponibles.
     */
    public long countDisponibles() {
        return technicienDepot.countByDisponibilite(true);
    }

    /** Alias utilisé par le tableau de bord */
    public List<TechnicienDTO> findAll() {
        return obtenirTous();
    }
}
