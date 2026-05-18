package com.itbs.maintenance.mapper;

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
