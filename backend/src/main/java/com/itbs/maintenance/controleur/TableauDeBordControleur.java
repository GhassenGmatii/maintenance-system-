package com.itbs.maintenance.controleur;

import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import com.itbs.maintenance.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
/**
 * Contrôleur REST gérant les requêtes pour le tableau de bord.
 */
public class TableauDeBordControleur {

    private final EquipementService equipementService;
    private final TechnicienService technicienService;
    private final PanneService panneService;
    private final InterventionService interventionService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Equipment stats
        stats.put("totalEquipements", equipementService.findAll().size());
        stats.put("equipementsOperationnels", equipementService.countByEtat(EtatEquipement.OPERATIONNEL));
        stats.put("equipementsEnPanne", equipementService.countByEtat(EtatEquipement.EN_PANNE));
        stats.put("equipementsEnMaintenance", equipementService.countByEtat(EtatEquipement.EN_MAINTENANCE));

        // Technician stats
        stats.put("totalTechniciens", technicienService.findAll().size());
        stats.put("techniciensDisponibles", technicienService.countDisponibles());

        // Panne stats
        stats.put("totalPannes", panneService.findAll().size());
        stats.put("pannesCeMois", panneService.countThisMonth());

        // Intervention stats
        stats.put("totalInterventions", interventionService.findAll().size());
        stats.put("interventionsPlanifiees", interventionService.countByStatut(StatutIntervention.PLANIFIEE));
        stats.put("interventionsEnCours", interventionService.countByStatut(StatutIntervention.EN_COURS));
        stats.put("interventionsTerminees", interventionService.countByStatut(StatutIntervention.TERMINEE));
        stats.put("coutTotalInterventions", interventionService.getCoutTotal());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pannes-par-categorie")
    public ResponseEntity<Map<String, Long>> pannesParCategorie() {
        List<Object[]> data = panneService.getStatsByCategorie();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pannes-par-equipement")
    public ResponseEntity<Map<String, Long>> pannesParEquipement() {
        List<Object[]> data = panneService.getStatsByEquipement();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/interventions-par-statut")
    public ResponseEntity<Map<String, Long>> interventionsParStatut() {
        List<Object[]> data = interventionService.getStatsByStatut();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return ResponseEntity.ok(result);
    }

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
