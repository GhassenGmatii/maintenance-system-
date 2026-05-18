import os

base_pkg = r"C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\backend\src\main\java\com\itbs\maintenance"

# 1. Services
services = {
    "EquipementService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.mapper.EquipementMapper;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier contenant la logique pour les équipements.
 */
@Service
public class EquipementService {

    private final EquipementDepot equipementDepot;
    private final EquipementMapper equipementMapper;

    public EquipementService(EquipementDepot equipementDepot, EquipementMapper equipementMapper) {
        this.equipementDepot = equipementDepot;
        this.equipementMapper = equipementMapper;
    }

    public List<EquipementDTO> obtenirTousLesEquipements() {
        return equipementDepot.findAll().stream()
                .map(equipementMapper::toDto)
                .collect(Collectors.toList());
    }

    public EquipementDTO obtenirEquipementParId(Long id) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + id));
        return equipementMapper.toDto(equipement);
    }

    public EquipementDTO creerEquipement(EquipementDTO equipementDTO) {
        Equipement equipement = equipementMapper.toEntity(equipementDTO);
        Equipement saved = equipementDepot.save(equipement);
        return equipementMapper.toDto(saved);
    }

    public EquipementDTO mettreAJourEquipement(Long id, EquipementDTO dtoDetails) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + id));

        equipement.setNom(dtoDetails.getNom());
        equipement.setType(dtoDetails.getType());
        equipement.setStatut(dtoDetails.getStatut());
        equipement.setDateAchat(dtoDetails.getDateAchat());

        Equipement updated = equipementDepot.save(equipement);
        return equipementMapper.toDto(updated);
    }

    public void supprimerEquipement(Long id) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + id));
        equipementDepot.delete(equipement);
    }
}
""",
    "TechnicienService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.entite.Technicien;
import com.itbs.maintenance.mapper.TechnicienMapper;
import com.itbs.maintenance.depot.TechnicienDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier contenant la logique pour les techniciens.
 */
@Service
public class TechnicienService {

    private final TechnicienDepot technicienDepot;
    private final TechnicienMapper technicienMapper;

    public TechnicienService(TechnicienDepot technicienDepot, TechnicienMapper technicienMapper) {
        this.technicienDepot = technicienDepot;
        this.technicienMapper = technicienMapper;
    }

    public List<TechnicienDTO> obtenirTousLesTechniciens() {
        return technicienDepot.findAll().stream()
                .map(technicienMapper::toDto)
                .collect(Collectors.toList());
    }

    public TechnicienDTO obtenirTechnicienParId(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));
        return technicienMapper.toDto(technicien);
    }

    public TechnicienDTO creerTechnicien(TechnicienDTO technicienDTO) {
        Technicien technicien = technicienMapper.toEntity(technicienDTO);
        Technicien saved = technicienDepot.save(technicien);
        return technicienMapper.toDto(saved);
    }

    public TechnicienDTO mettreAJourTechnicien(Long id, TechnicienDTO dtoDetails) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));

        technicien.setNom(dtoDetails.getNom());
        technicien.setSpecialite(dtoDetails.getSpecialite());
        technicien.setDisponible(dtoDetails.isDisponible());

        Technicien updated = technicienDepot.save(technicien);
        return technicienMapper.toDto(updated);
    }

    public void supprimerTechnicien(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));
        technicienDepot.delete(technicien);
    }
}
""",
    "PanneService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.entite.Panne;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.mapper.PanneMapper;
import com.itbs.maintenance.depot.PanneDepot;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier contenant la logique pour les pannes.
 */
@Service
public class PanneService {

    private final PanneDepot panneDepot;
    private final PanneMapper panneMapper;
    private final EquipementDepot equipementDepot;

    public PanneService(PanneDepot panneDepot, PanneMapper panneMapper, EquipementDepot equipementDepot) {
        this.panneDepot = panneDepot;
        this.panneMapper = panneMapper;
        this.equipementDepot = equipementDepot;
    }

    public List<PanneDTO> obtenirToutesLesPannes() {
        return panneDepot.findAll().stream()
                .map(panneMapper::toDto)
                .collect(Collectors.toList());
    }

    public PanneDTO obtenirPanneParId(Long id) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));
        return panneMapper.toDto(panne);
    }

    public PanneDTO creerPanne(PanneDTO panneDTO) {
        Panne panne = panneMapper.toEntity(panneDTO);
        Panne saved = panneDepot.save(panne);
        return panneMapper.toDto(saved);
    }

    public PanneDTO mettreAJourPanne(Long id, PanneDTO dtoDetails) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));

        panne.setDescription(dtoDetails.getDescription());
        panne.setDateSignalement(dtoDetails.getDateSignalement());
        panne.setCategorie(dtoDetails.getCategorie());
        
        if (dtoDetails.getEquipementId() != null) {
            Equipement eq = equipementDepot.findById(dtoDetails.getEquipementId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé"));
            panne.setEquipement(eq);
        }

        Panne updated = panneDepot.save(panne);
        return panneMapper.toDto(updated);
    }

    public void supprimerPanne(Long id) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));
        panneDepot.delete(panne);
    }
}
""",
    "InterventionService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.entite.Intervention;
import com.itbs.maintenance.entite.Equipement;
import com.itbs.maintenance.entite.Technicien;
import com.itbs.maintenance.mapper.InterventionMapper;
import com.itbs.maintenance.depot.InterventionDepot;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.depot.TechnicienDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier contenant la logique pour les interventions.
 */
@Service
public class InterventionService {

    private final InterventionDepot interventionDepot;
    private final InterventionMapper interventionMapper;
    private final EquipementDepot equipementDepot;
    private final TechnicienDepot technicienDepot;

    public InterventionService(InterventionDepot interventionDepot, InterventionMapper interventionMapper, 
                               EquipementDepot equipementDepot, TechnicienDepot technicienDepot) {
        this.interventionDepot = interventionDepot;
        this.interventionMapper = interventionMapper;
        this.equipementDepot = equipementDepot;
        this.technicienDepot = technicienDepot;
    }

    public List<InterventionDTO> obtenirToutesLesInterventions() {
        return interventionDepot.findAll().stream()
                .map(interventionMapper::toDto)
                .collect(Collectors.toList());
    }

    public InterventionDTO obtenirInterventionParId(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));
        return interventionMapper.toDto(intervention);
    }

    public InterventionDTO creerIntervention(InterventionDTO interventionDTO) {
        Intervention intervention = interventionMapper.toEntity(interventionDTO);
        Intervention saved = interventionDepot.save(intervention);
        return interventionMapper.toDto(saved);
    }

    public InterventionDTO mettreAJourIntervention(Long id, InterventionDTO dtoDetails) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));

        intervention.setDate(dtoDetails.getDate());
        intervention.setStatut(dtoDetails.getStatut());
        intervention.setCout(dtoDetails.getCout());

        if (dtoDetails.getEquipementId() != null) {
            Equipement eq = equipementDepot.findById(dtoDetails.getEquipementId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé"));
            intervention.setEquipement(eq);
        }
        
        if (dtoDetails.getTechnicienId() != null) {
            Technicien tech = technicienDepot.findById(dtoDetails.getTechnicienId())
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé"));
            intervention.setTechnicien(tech);
        }

        Intervention updated = interventionDepot.save(intervention);
        return interventionMapper.toDto(updated);
    }

    public void supprimerIntervention(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));
        interventionDepot.delete(intervention);
    }
}
"""
}

# 2. Controllers
controllers = {
    "EquipementControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.service.EquipementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des équipements.
 */
@RestController
@RequestMapping("/api/equipements")
public class EquipementControleur {

    private final EquipementService equipementService;

    public EquipementControleur(EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping
    public List<EquipementDTO> getAll() {
        return equipementService.obtenirTousLesEquipements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipementService.obtenirEquipementParId(id));
    }

    @PostMapping
    public EquipementDTO create(@RequestBody EquipementDTO dto) {
        return equipementService.creerEquipement(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipementDTO> update(@PathVariable Long id, @RequestBody EquipementDTO dtoDetails) {
        return ResponseEntity.ok(equipementService.mettreAJourEquipement(id, dtoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementService.supprimerEquipement(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "TechnicienControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.service.TechnicienService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des techniciens.
 */
@RestController
@RequestMapping("/api/techniciens")
public class TechnicienControleur {

    private final TechnicienService technicienService;

    public TechnicienControleur(TechnicienService technicienService) {
        this.technicienService = technicienService;
    }

    @GetMapping
    public List<TechnicienDTO> getAll() {
        return technicienService.obtenirTousLesTechniciens();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicienDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicienService.obtenirTechnicienParId(id));
    }

    @PostMapping
    public TechnicienDTO create(@RequestBody TechnicienDTO dto) {
        return technicienService.creerTechnicien(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicienDTO> update(@PathVariable Long id, @RequestBody TechnicienDTO dtoDetails) {
        return ResponseEntity.ok(technicienService.mettreAJourTechnicien(id, dtoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        technicienService.supprimerTechnicien(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "PanneControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.service.PanneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des pannes.
 */
@RestController
@RequestMapping("/api/pannes")
public class PanneControleur {

    private final PanneService panneService;

    public PanneControleur(PanneService panneService) {
        this.panneService = panneService;
    }

    @GetMapping
    public List<PanneDTO> getAll() {
        return panneService.obtenirToutesLesPannes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanneDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(panneService.obtenirPanneParId(id));
    }

    @PostMapping
    public PanneDTO create(@RequestBody PanneDTO dto) {
        return panneService.creerPanne(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PanneDTO> update(@PathVariable Long id, @RequestBody PanneDTO dtoDetails) {
        return ResponseEntity.ok(panneService.mettreAJourPanne(id, dtoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        panneService.supprimerPanne(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "InterventionControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.service.InterventionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des interventions.
 */
@RestController
@RequestMapping("/api/interventions")
public class InterventionControleur {

    private final InterventionService interventionService;

    public InterventionControleur(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @GetMapping
    public List<InterventionDTO> getAll() {
        return interventionService.obtenirToutesLesInterventions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.obtenirInterventionParId(id));
    }

    @PostMapping
    public InterventionDTO create(@RequestBody InterventionDTO dto) {
        return interventionService.creerIntervention(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> update(@PathVariable Long id, @RequestBody InterventionDTO dtoDetails) {
        return ResponseEntity.ok(interventionService.mettreAJourIntervention(id, dtoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interventionService.supprimerIntervention(id);
        return ResponseEntity.noContent().build();
    }
}
"""
}

for name, content in services.items():
    with open(os.path.join(base_pkg, "service", name), "w", encoding="utf-8") as f:
        f.write(content)

for name, content in controllers.items():
    with open(os.path.join(base_pkg, "controleur", name), "w", encoding="utf-8") as f:
        f.write(content)

print("Services and Controllers updated with DTOs and Exception handling.")
