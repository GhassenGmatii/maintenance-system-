package com.itbs.maintenance.dto;

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
    /** Date de début */
    private LocalDate date;
    /** Date de fin prévue (période de l'intervention) */
    private LocalDate dateFin;
    private Double cout;
    private Long equipementId;
    private Long technicienId;
}
