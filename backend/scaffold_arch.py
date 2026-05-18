import os

base_pkg = r"C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\backend\src\main\java\com\itbs\maintenance"

# =====================================================================
# 1. DTOs (aligned with actual entities)
# =====================================================================
dtos = {
    "EquipementDTO.java": """package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Equipement.
 */
@Data
public class EquipementDTO {
    private Long id;
    private String nom;
    private EtatEquipement etat;
    private LocalDate dateAcquisition;
}
""",
    "TechnicienDTO.java": """package com.itbs.maintenance.dto;

import lombok.Data;

/**
 * DTO pour l'entité Technicien.
 */
@Data
public class TechnicienDTO {
    private Long id;
    private String nom;
    private String competences;
    private Boolean disponibilite;
}
""",
    "PanneDTO.java": """package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Panne.CategoriePanne;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Panne.
 */
@Data
public class PanneDTO {
    private Long id;
    private String description;
    private CategoriePanne categorie;
    private LocalDate dateSignalement;
    private Long equipementId;
}
""",
    "InterventionDTO.java": """package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Intervention.
 */
@Data
public class InterventionDTO {
    private Long id;
    private StatutIntervention statut;
    private LocalDate date;
    private Double cout;
    private Long equipementId;
    private Long technicienId;
}
"""
}

# =====================================================================
# 2. Mappers (aligned with actual entities)
# =====================================================================
mappers = {
    "EquipementMapper.java": """package com.itbs.maintenance.mapper;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.entite.Equipement;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Equipement et EquipementDTO.
 */
@Component
public class EquipementMapper {

    public EquipementDTO toDto(Equipement entite) {
        if (entite == null) return null;
        EquipementDTO dto = new EquipementDTO();
        dto.setId(entite.getId());
        dto.setNom(entite.getNom());
        dto.setEtat(entite.getEtat());
        dto.setDateAcquisition(entite.getDateAcquisition());
        return dto;
    }

    public Equipement toEntity(EquipementDTO dto) {
        if (dto == null) return null;
        Equipement entite = new Equipement();
        entite.setId(dto.getId());
        entite.setNom(dto.getNom());
        entite.setEtat(dto.getEtat());
        entite.setDateAcquisition(dto.getDateAcquisition());
        return entite;
    }
}
""",
    "TechnicienMapper.java": """package com.itbs.maintenance.mapper;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.entite.Technicien;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Technicien et TechnicienDTO.
 */
@Component
public class TechnicienMapper {

    public TechnicienDTO toDto(Technicien entite) {
        if (entite == null) return null;
        TechnicienDTO dto = new TechnicienDTO();
        dto.setId(entite.getId());
        dto.setNom(entite.getNom());
        dto.setCompetences(entite.getCompetences());
        dto.setDisponibilite(entite.getDisponibilite());
        return dto;
    }

    public Technicien toEntity(TechnicienDTO dto) {
        if (dto == null) return null;
        Technicien entite = new Technicien();
        entite.setId(dto.getId());
        entite.setNom(dto.getNom());
        entite.setCompetences(dto.getCompetences());
        entite.setDisponibilite(dto.getDisponibilite());
        return entite;
    }
}
""",
    "PanneMapper.java": """package com.itbs.maintenance.mapper;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.entite.Panne;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Panne et PanneDTO.
 */
@Component
public class PanneMapper {

    private final EquipementDepot equipementDepot;

    public PanneMapper(EquipementDepot equipementDepot) {
        this.equipementDepot = equipementDepot;
    }

    public PanneDTO toDto(Panne entite) {
        if (entite == null) return null;
        PanneDTO dto = new PanneDTO();
        dto.setId(entite.getId());
        dto.setDescription(entite.getDescription());
        dto.setCategorie(entite.getCategorie());
        dto.setDateSignalement(entite.getDateSignalement());
        if (entite.getEquipement() != null) {
            dto.setEquipementId(entite.getEquipement().getId());
        }
        return dto;
    }

    public Panne toEntity(PanneDTO dto) {
        if (dto == null) return null;
        Panne entite = new Panne();
        entite.setId(dto.getId());
        entite.setDescription(dto.getDescription());
        entite.setCategorie(dto.getCategorie());
        entite.setDateSignalement(dto.getDateSignalement());
        if (dto.getEquipementId() != null) {
            entite.setEquipement(equipementDepot.findById(dto.getEquipementId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + dto.getEquipementId())));
        }
        return entite;
    }
}
""",
    "InterventionMapper.java": """package com.itbs.maintenance.mapper;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.entite.Intervention;
import com.itbs.maintenance.depot.EquipementDepot;
import com.itbs.maintenance.depot.TechnicienDepot;
import com.itbs.maintenance.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Intervention et InterventionDTO.
 */
@Component
public class InterventionMapper {

    private final EquipementDepot equipementDepot;
    private final TechnicienDepot technicienDepot;

    public InterventionMapper(EquipementDepot equipementDepot, TechnicienDepot technicienDepot) {
        this.equipementDepot = equipementDepot;
        this.technicienDepot = technicienDepot;
    }

    public InterventionDTO toDto(Intervention entite) {
        if (entite == null) return null;
        InterventionDTO dto = new InterventionDTO();
        dto.setId(entite.getId());
        dto.setStatut(entite.getStatut());
        dto.setDate(entite.getDate());
        dto.setCout(entite.getCout());
        if (entite.getEquipement() != null) {
            dto.setEquipementId(entite.getEquipement().getId());
        }
        if (entite.getTechnicien() != null) {
            dto.setTechnicienId(entite.getTechnicien().getId());
        }
        return dto;
    }

    public Intervention toEntity(InterventionDTO dto) {
        if (dto == null) return null;
        Intervention entite = new Intervention();
        entite.setId(dto.getId());
        entite.setStatut(dto.getStatut());
        entite.setDate(dto.getDate());
        entite.setCout(dto.getCout());
        if (dto.getEquipementId() != null) {
            entite.setEquipement(equipementDepot.findById(dto.getEquipementId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + dto.getEquipementId())));
        }
        if (dto.getTechnicienId() != null) {
            entite.setTechnicien(technicienDepot.findById(dto.getTechnicienId())
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + dto.getTechnicienId())));
        }
        return entite;
    }
}
"""
}

# =====================================================================
# 3. Services (aligned with actual entities)
# =====================================================================
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

    public List<EquipementDTO> obtenirTous() {
        return equipementDepot.findAll().stream()
                .map(equipementMapper::toDto)
                .collect(Collectors.toList());
    }

    public EquipementDTO obtenirParId(Long id) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + id));
        return equipementMapper.toDto(equipement);
    }

    public EquipementDTO creer(EquipementDTO dto) {
        Equipement equipement = equipementMapper.toEntity(dto);
        return equipementMapper.toDto(equipementDepot.save(equipement));
    }

    public EquipementDTO mettreAJour(Long id, EquipementDTO dto) {
        Equipement equipement = equipementDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement non trouvé avec l'ID: " + id));
        equipement.setNom(dto.getNom());
        equipement.setEtat(dto.getEtat());
        equipement.setDateAcquisition(dto.getDateAcquisition());
        return equipementMapper.toDto(equipementDepot.save(equipement));
    }

    public void supprimer(Long id) {
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

    public List<TechnicienDTO> obtenirTous() {
        return technicienDepot.findAll().stream()
                .map(technicienMapper::toDto)
                .collect(Collectors.toList());
    }

    public TechnicienDTO obtenirParId(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));
        return technicienMapper.toDto(technicien);
    }

    public TechnicienDTO creer(TechnicienDTO dto) {
        Technicien technicien = technicienMapper.toEntity(dto);
        return technicienMapper.toDto(technicienDepot.save(technicien));
    }

    public TechnicienDTO mettreAJour(Long id, TechnicienDTO dto) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));
        technicien.setNom(dto.getNom());
        technicien.setCompetences(dto.getCompetences());
        technicien.setDisponibilite(dto.getDisponibilite());
        return technicienMapper.toDto(technicienDepot.save(technicien));
    }

    public void supprimer(Long id) {
        Technicien technicien = technicienDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé avec l'ID: " + id));
        technicienDepot.delete(technicien);
    }
}
""",
    "PanneService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.entite.Panne;
import com.itbs.maintenance.mapper.PanneMapper;
import com.itbs.maintenance.depot.PanneDepot;
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

    public PanneService(PanneDepot panneDepot, PanneMapper panneMapper) {
        this.panneDepot = panneDepot;
        this.panneMapper = panneMapper;
    }

    public List<PanneDTO> obtenirTous() {
        return panneDepot.findAll().stream()
                .map(panneMapper::toDto)
                .collect(Collectors.toList());
    }

    public PanneDTO obtenirParId(Long id) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));
        return panneMapper.toDto(panne);
    }

    public PanneDTO creer(PanneDTO dto) {
        Panne panne = panneMapper.toEntity(dto);
        return panneMapper.toDto(panneDepot.save(panne));
    }

    public PanneDTO mettreAJour(Long id, PanneDTO dto) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));
        panne.setDescription(dto.getDescription());
        panne.setCategorie(dto.getCategorie());
        panne.setDateSignalement(dto.getDateSignalement());
        return panneMapper.toDto(panneDepot.save(panne));
    }

    public void supprimer(Long id) {
        Panne panne = panneDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panne non trouvée avec l'ID: " + id));
        panneDepot.delete(panne);
    }
}
""",
    "InterventionService.java": """package com.itbs.maintenance.service;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.entite.Intervention;
import com.itbs.maintenance.mapper.InterventionMapper;
import com.itbs.maintenance.depot.InterventionDepot;
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

    public InterventionService(InterventionDepot interventionDepot, InterventionMapper interventionMapper) {
        this.interventionDepot = interventionDepot;
        this.interventionMapper = interventionMapper;
    }

    public List<InterventionDTO> obtenirTous() {
        return interventionDepot.findAll().stream()
                .map(interventionMapper::toDto)
                .collect(Collectors.toList());
    }

    public InterventionDTO obtenirParId(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));
        return interventionMapper.toDto(intervention);
    }

    public InterventionDTO creer(InterventionDTO dto) {
        Intervention intervention = interventionMapper.toEntity(dto);
        return interventionMapper.toDto(interventionDepot.save(intervention));
    }

    public InterventionDTO mettreAJour(Long id, InterventionDTO dto) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));
        intervention.setStatut(dto.getStatut());
        intervention.setDate(dto.getDate());
        intervention.setCout(dto.getCout());
        return interventionMapper.toDto(interventionDepot.save(intervention));
    }

    public void supprimer(Long id) {
        Intervention intervention = interventionDepot.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention non trouvée avec l'ID: " + id));
        interventionDepot.delete(intervention);
    }
}
"""
}

# =====================================================================
# 4. Controllers (clean, using Services + DTOs)
# =====================================================================
controllers = {
    "EquipementControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.EquipementDTO;
import com.itbs.maintenance.service.EquipementService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<EquipementDTO>> getAll() {
        return ResponseEntity.ok(equipementService.obtenirTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipementService.obtenirParId(id));
    }

    @PostMapping
    public ResponseEntity<EquipementDTO> create(@RequestBody EquipementDTO dto) {
        return new ResponseEntity<>(equipementService.creer(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipementDTO> update(@PathVariable Long id, @RequestBody EquipementDTO dto) {
        return ResponseEntity.ok(equipementService.mettreAJour(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "TechnicienControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.TechnicienDTO;
import com.itbs.maintenance.service.TechnicienService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<TechnicienDTO>> getAll() {
        return ResponseEntity.ok(technicienService.obtenirTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicienDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicienService.obtenirParId(id));
    }

    @PostMapping
    public ResponseEntity<TechnicienDTO> create(@RequestBody TechnicienDTO dto) {
        return new ResponseEntity<>(technicienService.creer(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicienDTO> update(@PathVariable Long id, @RequestBody TechnicienDTO dto) {
        return ResponseEntity.ok(technicienService.mettreAJour(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        technicienService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "PanneControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.PanneDTO;
import com.itbs.maintenance.service.PanneService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<PanneDTO>> getAll() {
        return ResponseEntity.ok(panneService.obtenirTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanneDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(panneService.obtenirParId(id));
    }

    @PostMapping
    public ResponseEntity<PanneDTO> create(@RequestBody PanneDTO dto) {
        return new ResponseEntity<>(panneService.creer(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PanneDTO> update(@PathVariable Long id, @RequestBody PanneDTO dto) {
        return ResponseEntity.ok(panneService.mettreAJour(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        panneService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
""",
    "InterventionControleur.java": """package com.itbs.maintenance.controleur;

import com.itbs.maintenance.dto.InterventionDTO;
import com.itbs.maintenance.service.InterventionService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<InterventionDTO>> getAll() {
        return ResponseEntity.ok(interventionService.obtenirTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.obtenirParId(id));
    }

    @PostMapping
    public ResponseEntity<InterventionDTO> create(@RequestBody InterventionDTO dto) {
        return new ResponseEntity<>(interventionService.creer(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionDTO> update(@PathVariable Long id, @RequestBody InterventionDTO dto) {
        return ResponseEntity.ok(interventionService.mettreAJour(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interventionService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
"""
}

# Write all files
for name, content in dtos.items():
    path = os.path.join(base_pkg, "dto", name)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

for name, content in mappers.items():
    path = os.path.join(base_pkg, "mapper", name)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

for name, content in services.items():
    path = os.path.join(base_pkg, "service", name)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

for name, content in controllers.items():
    path = os.path.join(base_pkg, "controleur", name)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("Tout est genere avec succes : DTOs, Mappers, Services, Controllers.")
