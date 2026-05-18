package com.itbs.maintenance.mapper;

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
