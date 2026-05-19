package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Intervention.StatutIntervention;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Intervention.
 */
@Data
@Schema(description = "Représentation d'une intervention de maintenance")
public class InterventionDTO {

    @Schema(description = "Identifiant unique de l'intervention", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Statut de l'intervention", example = "PLANIFIEE",
            allowableValues = {"PLANIFIEE", "EN_COURS", "TERMINEE"})
    private StatutIntervention statut;

    @Schema(description = "Date de début de l'intervention", example = "2024-06-01")
    private LocalDate date;

    @Schema(description = "Date de fin prévue de l'intervention", example = "2024-06-03")
    private LocalDate dateFin;

    @Schema(description = "Coût de l'intervention en dinars", example = "1500.00")
    private Double cout;

    @Schema(description = "ID de l'équipement concerné", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long equipementId;

    @Schema(description = "ID du technicien assigné", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long technicienId;
}
