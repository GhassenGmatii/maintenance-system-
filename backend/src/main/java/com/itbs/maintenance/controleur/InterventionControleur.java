package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.service.InterventionService;
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
 * CONTRÔLEUR DES INTERVENTIONS
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les interventions.
 * ============================================================
 */
@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Interventions", description = "Planification et suivi des interventions de maintenance")
public class InterventionControleur {

    private final InterventionService interventionService;

    public InterventionControleur(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @Operation(summary = "Lister toutes les interventions", description = "Retourne la liste complète de toutes les interventions.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = InterventionDTO.class))))
    @GetMapping
    public ResponseEntity<List<InterventionDTO>> getAll() {
        return ResponseEntity.ok(interventionService.obtenirTous());
    }

    @Operation(summary = "Obtenir une intervention par ID", description = "Retourne les détails d'une intervention spécifique.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intervention trouvée",
                content = @Content(schema = @Schema(implementation = InterventionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Intervention introuvable", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<InterventionDTO> getById(
            @Parameter(description = "ID de l'intervention", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(interventionService.obtenirParId(id));
    }

    @Operation(summary = "Créer une nouvelle intervention", description = "Planifie une nouvelle intervention de maintenance.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Intervention créée avec succès",
                content = @Content(schema = @Schema(implementation = InterventionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping
    public ResponseEntity<InterventionDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de la nouvelle intervention", required = true,
                    content = @Content(schema = @Schema(implementation = InterventionDTO.class)))
            @RequestBody InterventionDTO dto) {
        return new ResponseEntity<>(interventionService.creer(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour une intervention",
            description = "Modifie une intervention existante (ex: changer le statut, mettre à jour le coût).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intervention mise à jour",
                content = @Content(schema = @Schema(implementation = InterventionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Intervention introuvable", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> update(
            @Parameter(description = "ID de l'intervention à modifier", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody InterventionDTO dto) {
        return ResponseEntity.ok(interventionService.mettreAJour(id, dto));
    }

    @Operation(summary = "Supprimer une intervention", description = "Supprime définitivement une intervention du système.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Intervention supprimée avec succès", content = @Content),
        @ApiResponse(responseCode = "404", description = "Intervention introuvable", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de l'intervention à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        interventionService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
