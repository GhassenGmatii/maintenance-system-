package com.itbs.maintenance.dto;

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
