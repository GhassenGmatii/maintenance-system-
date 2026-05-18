package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.service.InterventionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * ============================================================
 * CONTRÔLEUR DES INTERVENTIONS
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les interventions.
 * ============================================================
 */
@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = "http://localhost:4200")
public class InterventionControleur {

    private final InterventionService interventionService;

    public InterventionControleur(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    /** GET /api/interventions → Récupère TOUTES les interventions */
    @GetMapping
    public ResponseEntity<List<InterventionDTO>> getAll() {
        return ResponseEntity.ok(interventionService.obtenirTous());
    }

    /** GET /api/interventions/5 → Récupère une intervention par ID */
    @GetMapping("/{id}")
    public ResponseEntity<InterventionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.obtenirParId(id));
    }

    /** POST /api/interventions → Crée une nouvelle intervention */
    @PostMapping
    public ResponseEntity<InterventionDTO> create(@RequestBody InterventionDTO dto) {
        return new ResponseEntity<>(interventionService.creer(dto), HttpStatus.CREATED);
    }

    /** PUT /api/interventions/5 → Modifie une intervention existante (ex: changer le statut) */
    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> update(@PathVariable Long id, @RequestBody InterventionDTO dto) {
        return ResponseEntity.ok(interventionService.mettreAJour(id, dto));
    }

    /** DELETE /api/interventions/5 → Supprime une intervention */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interventionService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
