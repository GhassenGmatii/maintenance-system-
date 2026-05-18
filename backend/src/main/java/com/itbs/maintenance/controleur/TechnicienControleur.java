package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.service.TechnicienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * ============================================================
 * CONTRÔLEUR DES TECHNICIENS
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les techniciens.
 * ============================================================
 */
@RestController
@RequestMapping("/api/techniciens")
@CrossOrigin(origins = "http://localhost:4200")
public class TechnicienControleur {

    private final TechnicienService technicienService;

    public TechnicienControleur(TechnicienService technicienService) {
        this.technicienService = technicienService;
    }

    /** GET /api/techniciens → Récupère TOUS les techniciens */
    @GetMapping
    public ResponseEntity<List<TechnicienDTO>> getAll() {
        return ResponseEntity.ok(technicienService.obtenirTous());
    }

    /** GET /api/techniciens/5 → Récupère un technicien par ID */
    @GetMapping("/{id}")
    public ResponseEntity<TechnicienDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicienService.obtenirParId(id));
    }

    /** POST /api/techniciens → Crée un technicien */
    @PostMapping
    public ResponseEntity<TechnicienDTO> create(@RequestBody TechnicienDTO dto) {
        return new ResponseEntity<>(technicienService.creer(dto), HttpStatus.CREATED);
    }

    /** PUT /api/techniciens/5 → Modifie un technicien existant */
    @PutMapping("/{id}")
    public ResponseEntity<TechnicienDTO> update(@PathVariable Long id, @RequestBody TechnicienDTO dto) {
        return ResponseEntity.ok(technicienService.mettreAJour(id, dto));
    }

    /** DELETE /api/techniciens/5 → Supprime un technicien */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        technicienService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
