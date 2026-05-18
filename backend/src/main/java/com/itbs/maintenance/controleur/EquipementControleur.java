package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.service.EquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * ============================================================
 * CONTRÔLEUR DES ÉQUIPEMENTS
 * ============================================================
 * Ce fichier est la "Porte d'entrée" (API) pour les équipements.
 * C'est ici que le frontend (Angular) envoie ses requêtes HTTP.
 * ============================================================
 */
@RestController
@RequestMapping("/api/equipements") // Toutes les URL commenceront par /api/equipements
@CrossOrigin(origins = "http://localhost:4200") // Autorise Angular à communiquer avec cette API
public class EquipementControleur {

    private final EquipementService equipementService;

    // Injection automatique du service
    public EquipementControleur(EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    /** GET /api/equipements → Récupère TOUS les équipements */
    @GetMapping
    public ResponseEntity<List<EquipementDTO>> getAll() {
        return ResponseEntity.ok(equipementService.obtenirTous());
    }

    /** GET /api/equipements/5 → Récupère un équipement spécifique (ID=5) */
    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipementService.obtenirParId(id));
    }

    /** POST /api/equipements → Ajoute un NOUVEL équipement */
    @PostMapping
    public ResponseEntity<EquipementDTO> create(@RequestBody EquipementDTO dto) {
        return new ResponseEntity<>(equipementService.creer(dto), HttpStatus.CREATED);
    }

    /** PUT /api/equipements/5 → Modifie un équipement existant (ID=5) */
    @PutMapping("/{id}")
    public ResponseEntity<EquipementDTO> update(@PathVariable Long id, @RequestBody EquipementDTO dto) {
        return ResponseEntity.ok(equipementService.mettreAJour(id, dto));
    }

    /** DELETE /api/equipements/5 → Supprime un équipement (ID=5) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    /** 
     * GET /api/equipements/en-panne 
     * → Utilisé par Angular dans le modal "Planifier intervention" 
     */
    @GetMapping("/en-panne")
    public ResponseEntity<List<EquipementDTO>> getEnPanne() {
        return ResponseEntity.ok(equipementService.obtenirEnPanne());
    }

    /** 
     * GET /api/equipements/operationnel 
     * → Utilisé par Angular dans le modal "Signaler une panne" 
     */
    @GetMapping("/operationnel")
    public ResponseEntity<List<EquipementDTO>> getOperationnels() {
        return ResponseEntity.ok(equipementService.obtenirOperationnels());
    }
}
