package com.itbs.maintenance.mapper;

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
        dto.setDateFin(entite.getDateFin());
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
        entite.setDateFin(dto.getDateFin());
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
