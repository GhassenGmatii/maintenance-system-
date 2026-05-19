package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.service.PanneService;
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
 * CONTRÔLEUR DES PANNES
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les pannes.
 * ============================================================
 */
@RestController
@RequestMapping("/api/pannes")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Pannes", description = "Signalement et suivi des pannes d'équipements")
public class PanneControleur {

    private final PanneService panneService;

    public PanneControleur(PanneService panneService) {
        this.panneService = panneService;
    }

    @Operation(summary = "Lister toutes les pannes", description = "Retourne la liste complète de toutes les pannes signalées.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PanneDTO.class))))
    @GetMapping
    public ResponseEntity<List<PanneDTO>> getAll() {
        return ResponseEntity.ok(panneService.obtenirTous());
    }

    @Operation(summary = "Obtenir une panne par ID", description = "Retourne les détails d'une panne spécifique.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Panne trouvée",
                content = @Content(schema = @Schema(implementation = PanneDTO.class))),
        @ApiResponse(responseCode = "404", description = "Panne introuvable", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PanneDTO> getById(
            @Parameter(description = "ID de la panne", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(panneService.obtenirParId(id));
    }

    @Operation(summary = "Signaler une nouvelle panne", description = "Enregistre une nouvelle panne sur un équipement.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Panne signalée avec succès",
                content = @Content(schema = @Schema(implementation = PanneDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PanneDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de la panne à signaler", required = true,
                    content = @Content(schema = @Schema(implementation = PanneDTO.class)))
            @RequestBody PanneDTO dto) {
        return new ResponseEntity<>(panneService.creer(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour une panne", description = "Modifie les informations d'une panne existante.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Panne mise à jour",
                content = @Content(schema = @Schema(implementation = PanneDTO.class))),
        @ApiResponse(responseCode = "404", description = "Panne introuvable", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PanneDTO> update(
            @Parameter(description = "ID de la panne à modifier", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody PanneDTO dto) {
        return ResponseEntity.ok(panneService.mettreAJour(id, dto));
    }

    @Operation(summary = "Supprimer une panne", description = "Supprime définitivement une panne du système.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Panne supprimée avec succès", content = @Content),
        @ApiResponse(responseCode = "404", description = "Panne introuvable", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la panne à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        panneService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
