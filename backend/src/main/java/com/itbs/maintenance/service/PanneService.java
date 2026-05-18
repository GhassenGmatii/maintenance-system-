package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import com.itbs.maintenance.entite.Panne;
import com.itbs.maintenance.mapper.PanneMapper;
import com.itbs.maintenance.depot.PanneDepot;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * SERVICE DES PANNES
 * ============================================================
 * Ce service gère toutes les opérations liées aux pannes.
 *
 * Règles métier appliquées automatiquement :
 *  1. Signaler une panne  → équipement passe à EN_PANNE
 *  2. Supprimer la dernière panne → équipement revient à OPERATIONNEL
 * ============================================================
 */
@Service
public class PanneService {

    // ─── Dépendances injectées ────────────────────────────────────────
    private final PanneDepot      panneDepot;       // accès base de données pannes
    private final PanneMapper     panneMapper;      // conversion Panne ↔ PanneDTO
    private final EquipementDepot equipementDepot;  // accès base de données équipements

    // Spring injecte automatiquement ces dépendances via le constructeur
    public PanneService(PanneDepot panneDepot,
                        PanneMapper panneMapper,
                        EquipementDepot equipementDepot) {
        this.panneDepot      = panneDepot;
        this.panneMapper     = panneMapper;
        this.equipementDepot = equipementDepot;
    }

    // ─── CRUD de base ────────────────────────────────────────────────

    /**
     * Retourne la liste de toutes les pannes.
     */
    public List<PanneDTO> obtenirTous() {
        return panneDepot.findAll()
                .stream()
                .map(panneMapper::toDto)   // convertit chaque Panne en PanneDTO
                .collect(Collectors.toList());
    }

    /**
     * Retourne une panne par son identifiant.
     * Lance une exception si l'ID n'existe pas.
     */
    public PanneDTO obtenirParId(Long id) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne introuvable. ID : " + id));
        return panneMapper.toDto(panne);
    }

    /**
     * Crée une nouvelle panne et met à jour l'état de l'équipement.
     *
     * Règle métier :
     *   → L'équipement concerné passe automatiquement à EN_PANNE
     */
    @Transactional  // si une erreur survient, tout est annulé (rollback)
    public PanneDTO creer(PanneDTO dto) {

        // 1. Convertir le DTO en entité et sauvegarder en base
        Panne panne = panneMapper.toEntity(dto);
        Panne panneSauvegardee = panneDepot.save(panne);

        // 2. Mettre l'équipement en état EN_PANNE (règle métier)
        if (panneSauvegardee.getEquipement() != null) {
            changerEtatEquipement(panneSauvegardee.getEquipement(), EtatEquipement.EN_PANNE);
        }

        return panneMapper.toDto(panneSauvegardee);
    }

    /**
     * Modifie une panne existante.
     * Permet de changer la description, catégorie, date et équipement.
     */
    public PanneDTO mettreAJour(Long id, PanneDTO dto) {

        // Chercher la panne existante (erreur si introuvable)
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne introuvable. ID : " + id));

        // Mettre à jour les champs simples
        panne.setDescription(dto.getDescription());
        panne.setCategorie(dto.getCategorie());
        panne.setDateSignalement(dto.getDateSignalement());

        // Mettre à jour l'équipement si un nouvel ID est fourni
        if (dto.getEquipementId() != null) {
            panne.setEquipement(
                equipementDepot.findById(dto.getEquipementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable. ID : " + dto.getEquipementId()))
            );
        }

        return panneMapper.toDto(panneDepot.save(panne));
    }

    /**
     * Supprime une panne.
     *
     * Règle métier :
     *   → Si l'équipement n'a plus aucune panne, il revient à OPERATIONNEL
     */
    @Transactional
    public void supprimer(Long id) {

        // Chercher la panne à supprimer
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne introuvable. ID : " + id));

        // Garder une référence à l'équipement avant suppression
        Equipement equipement = panne.getEquipement();

        // Supprimer la panne
        panneDepot.delete(panne);

        // Vérifier si l'équipement a encore des pannes
        if (equipement != null) {
            int nbPannesRestantes = panneDepot.findByEquipementId(equipement.getId()).size();

            // Si plus aucune panne → l'équipement redevient opérationnel
            if (nbPannesRestantes == 0 && equipement.getEtat() == EtatEquipement.EN_PANNE) {
                changerEtatEquipement(equipement, EtatEquipement.OPERATIONNEL);
            }
        }
    }

    // ─── Méthode utilitaire ──────────────────────────────────────────

    /**
     * Change l'état d'un équipement en base de données.
     * N'effectue la mise à jour que si l'état a réellement changé.
     */
    private void changerEtatEquipement(Equipement equipement, EtatEquipement nouvelEtat) {
        if (equipement.getEtat() != nouvelEtat) {
            equipement.setEtat(nouvelEtat);
            equipementDepot.save(equipement);
        }
    }

    // ─── Méthodes pour le tableau de bord ───────────────────────────

    /** Compte les pannes signalées ce mois-ci */
    public long countThisMonth() {
        LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
        LocalDate aujourd_hui = LocalDate.now();
        return panneDepot.countByDateSignalementBetween(debutMois, aujourd_hui);
    }

    /** Statistiques par catégorie (pour les graphiques) */
    public List<Object[]> getStatsByCategorie() {
        return panneDepot.countByCategorie();
    }

    /** Statistiques par équipement (pour les graphiques) */
    public List<Object[]> getStatsByEquipement() {
        return panneDepot.countByEquipement();
    }

    /** Alias utilisé par le tableau de bord */
    public List<PanneDTO> findAll() {
        return obtenirTous();
    }
}
