package com.itbs.maintenance.mapper;

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
