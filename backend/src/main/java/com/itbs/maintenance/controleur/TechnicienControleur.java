package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.service.TechnicienService;
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
 * CONTRÔLEUR DES TECHNICIENS
 * ============================================================
 * Gère les requêtes HTTP (API REST) pour les techniciens.
 * ============================================================
 */
@RestController
@RequestMapping("/api/techniciens")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Techniciens", description = "Gestion des techniciens de maintenance et de leur disponibilité")
public class TechnicienControleur {

    private final TechnicienService technicienService;

    public TechnicienControleur(TechnicienService technicienService) {
        this.technicienService = technicienService;
    }

    @Operation(summary = "Lister tous les techniciens", description = "Retourne la liste complète de tous les techniciens.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicienDTO.class))))
    @GetMapping
    public ResponseEntity<List<TechnicienDTO>> getAll() {
        return ResponseEntity.ok(technicienService.obtenirTous());
    }

    @Operation(summary = "Obtenir un technicien par ID", description = "Retourne les détails d'un technicien spécifique.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Technicien trouvé",
                content = @Content(schema = @Schema(implementation = TechnicienDTO.class))),
        @ApiResponse(responseCode = "404", description = "Technicien introuvable", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TechnicienDTO> getById(
            @Parameter(description = "ID du technicien", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(technicienService.obtenirParId(id));
    }

    @Operation(summary = "Créer un nouveau technicien", description = "Ajoute un nouveau technicien dans le système.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Technicien créé avec succès",
                content = @Content(schema = @Schema(implementation = TechnicienDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TechnicienDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du nouveau technicien", required = true,
                    content = @Content(schema = @Schema(implementation = TechnicienDTO.class)))
            @RequestBody TechnicienDTO dto) {
        return new ResponseEntity<>(technicienService.creer(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un technicien", description = "Modifie les informations d'un technicien existant.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Technicien mis à jour",
                content = @Content(schema = @Schema(implementation = TechnicienDTO.class))),
        @ApiResponse(responseCode = "404", description = "Technicien introuvable", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TechnicienDTO> update(
            @Parameter(description = "ID du technicien à modifier", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody TechnicienDTO dto) {
        return ResponseEntity.ok(technicienService.mettreAJour(id, dto));
    }

    @Operation(summary = "Supprimer un technicien", description = "Supprime définitivement un technicien du système.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Technicien supprimé avec succès", content = @Content),
        @ApiResponse(responseCode = "404", description = "Technicien introuvable", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du technicien à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        technicienService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
