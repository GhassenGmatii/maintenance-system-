package com.itbs.maintenance.controleur;

import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import com.itbs.maintenance.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ============================================================
 * CONTRÔLEUR DU TABLEAU DE BORD
 * ============================================================
 * Fournit les statistiques et indicateurs clés pour le dashboard.
 * ============================================================
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Tableau de bord", description = "Statistiques globales et indicateurs clés de performance (KPI)")
public class TableauDeBordControleur {

    private final EquipementService equipementService;
    private final TechnicienService technicienService;
    private final PanneService panneService;
    private final InterventionService interventionService;

    @Operation(
        summary = "Statistiques globales",
        description = """
            Retourne un ensemble complet d'indicateurs clés :
            - Nombre total d'équipements et répartition par état
            - Nombre de techniciens et disponibilité
            - Nombre de pannes (total et ce mois-ci)
            - Nombre d'interventions par statut et coût total
            """
    )
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(schema = @Schema(type = "object",
                    example = "{\"totalEquipements\":10,\"equipementsOperationnels\":7,\"totalPannes\":5}")))
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalEquipements", equipementService.findAll().size());
        stats.put("equipementsOperationnels", equipementService.countByEtat(EtatEquipement.OPERATIONNEL));
        stats.put("equipementsEnPanne", equipementService.countByEtat(EtatEquipement.EN_PANNE));
        stats.put("equipementsEnMaintenance", equipementService.countByEtat(EtatEquipement.EN_MAINTENANCE));

        stats.put("totalTechniciens", technicienService.findAll().size());
        stats.put("techniciensDisponibles", technicienService.countDisponibles());

        stats.put("totalPannes", panneService.findAll().size());
        stats.put("pannesCeMois", panneService.countThisMonth());

        stats.put("totalInterventions", interventionService.findAll().size());
        stats.put("interventionsPlanifiees", interventionService.countByStatut(StatutIntervention.PLANIFIEE));
        stats.put("interventionsEnCours", interventionService.countByStatut(StatutIntervention.EN_COURS));
        stats.put("interventionsTerminees", interventionService.countByStatut(StatutIntervention.TERMINEE));
        stats.put("coutTotalInterventions", interventionService.getCoutTotal());

        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Pannes par catégorie",
            description = "Retourne le nombre de pannes regroupées par catégorie (ex: ELECTRIQUE, MECANIQUE...).")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(schema = @Schema(type = "object", example = "{\"ELECTRIQUE\":3,\"MECANIQUE\":5}")))
    @GetMapping("/pannes-par-categorie")
    public ResponseEntity<Map<String, Long>> pannesParCategorie() {
        List<Object[]> data = panneService.getStatsByCategorie();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Pannes par équipement",
            description = "Retourne le nombre de pannes regroupées par équipement.")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(schema = @Schema(type = "object", example = "{\"Compresseur A\":2,\"Tour CNC\":4}")))
    @GetMapping("/pannes-par-equipement")
    public ResponseEntity<Map<String, Long>> pannesParEquipement() {
        List<Object[]> data = panneService.getStatsByEquipement();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Interventions par statut",
            description = "Retourne le nombre d'interventions regroupées par statut (PLANIFIEE, EN_COURS, TERMINEE).")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(schema = @Schema(type = "object", example = "{\"PLANIFIEE\":3,\"EN_COURS\":1,\"TERMINEE\":8}")))
    @GetMapping("/interventions-par-statut")
    public ResponseEntity<Map<String, Long>> interventionsParStatut() {
        List<Object[]> data = interventionService.getStatsByStatut();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Interventions par technicien",
            description = "Retourne le nombre d'interventions assignées à chaque technicien.")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
            content = @Content(schema = @Schema(type = "object", example = "{\"Ali Ben Salah\":4,\"Fatima Zahra\":2}")))
    @GetMapping("/interventions-par-technicien")
    public ResponseEntity<Map<String, Long>> interventionsParTechnicien() {
        List<Object[]> data = interventionService.getStatsByTechnicien();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }
}
