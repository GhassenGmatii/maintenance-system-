package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.service.EquipementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/equipements")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Équipements", description = "Gestion du parc d'équipements industriels")
public class EquipementControleur {

    private final EquipementService equipementService;

    public EquipementControleur(EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @Operation(summary = "Lister tous les équipements", description = "Retourne la liste complète de tous les équipements enregistrés.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipementDTO.class))))
    @GetMapping
    public ResponseEntity<List<EquipementDTO>> getAll() {
        return ResponseEntity.ok(equipementService.obtenirTous());
    }

    @Operation(summary = "Obtenir un équipement par ID", description = "Retourne les détails d'un équipement spécifique.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Équipement trouvé",
                content = @Content(schema = @Schema(implementation = EquipementDTO.class))),
        @ApiResponse(responseCode = "404", description = "Équipement introuvable", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> getById(
            @Parameter(description = "ID de l'équipement", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(equipementService.obtenirParId(id));
    }

    @Operation(summary = "Créer un nouvel équipement", description = "Ajoute un nouvel équipement dans le système.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Équipement créé avec succès",
                content = @Content(schema = @Schema(implementation = EquipementDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EquipementDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du nouvel équipement", required = true,
                    content = @Content(schema = @Schema(implementation = EquipementDTO.class)))
            @RequestBody EquipementDTO dto) {
        return new ResponseEntity<>(equipementService.creer(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un équipement", description = "Modifie les informations d'un équipement existant.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Équipement mis à jour",
                content = @Content(schema = @Schema(implementation = EquipementDTO.class))),
        @ApiResponse(responseCode = "404", description = "Équipement introuvable", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EquipementDTO> update(
            @Parameter(description = "ID de l'équipement à modifier", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody EquipementDTO dto) {
        return ResponseEntity.ok(equipementService.mettreAJour(id, dto));
    }

    @Operation(summary = "Supprimer un équipement", description = "Supprime définitivement un équipement du système.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Équipement supprimé avec succès", content = @Content),
        @ApiResponse(responseCode = "404", description = "Équipement introuvable", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de l'équipement à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        equipementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lister les équipements en panne",
            description = "Retourne uniquement les équipements dont l'état est EN_PANNE. Utilisé pour planifier une intervention.")
    @ApiResponse(responseCode = "200", description = "Liste des équipements en panne",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipementDTO.class))))
    @GetMapping("/en-panne")
    public ResponseEntity<List<EquipementDTO>> getEnPanne() {
        return ResponseEntity.ok(equipementService.obtenirEnPanne());
    }

    @Operation(summary = "Lister les équipements opérationnels",
            description = "Retourne uniquement les équipements dont l'état est OPERATIONNEL. Utilisé pour signaler une panne.")
    @ApiResponse(responseCode = "200", description = "Liste des équipements opérationnels",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipementDTO.class))))
    @GetMapping("/operationnel")
    public ResponseEntity<List<EquipementDTO>> getOperationnels() {
        return ResponseEntity.ok(equipementService.obtenirOperationnels());
    }
}
