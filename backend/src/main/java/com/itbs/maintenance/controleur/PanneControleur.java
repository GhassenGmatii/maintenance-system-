package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.service.PanneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * ============================================================
 * CONTRÔLEUR DES PANNES
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les pannes.
 * ============================================================
 */
@RestController
@RequestMapping("/api/pannes")
@CrossOrigin(origins = "http://localhost:4200")
public class PanneControleur {

    private final PanneService panneService;

    public PanneControleur(PanneService panneService) {
        this.panneService = panneService;
    }

    /** GET /api/pannes → Récupère TOUTES les pannes */
    @GetMapping
    public ResponseEntity<List<PanneDTO>> getAll() {
        return ResponseEntity.ok(panneService.obtenirTous());
    }

    /** GET /api/pannes/5 → Récupère une panne par ID */
    @GetMapping("/{id}")
    public ResponseEntity<PanneDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(panneService.obtenirParId(id));
    }

    /** POST /api/pannes → Signale une nouvelle panne */
    @PostMapping
    public ResponseEntity<PanneDTO> create(@RequestBody PanneDTO dto) {
        return new ResponseEntity<>(panneService.creer(dto), HttpStatus.CREATED);
    }

    /** PUT /api/pannes/5 → Modifie une panne existante */
    @PutMapping("/{id}")
    public ResponseEntity<PanneDTO> update(@PathVariable Long id, @RequestBody PanneDTO dto) {
        return ResponseEntity.ok(panneService.mettreAJour(id, dto));
    }

    /** DELETE /api/pannes/5 → Supprime une panne */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        panneService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
