package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Equipement.EtatEquipement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Equipement.
 */
@Data
@Schema(description = "Représentation d'un équipement industriel")
public class EquipementDTO {

    @Schema(description = "Identifiant unique de l'équipement", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nom de l'équipement", example = "Compresseur A", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Schema(description = "État actuel de l'équipement", example = "OPERATIONNEL",
            allowableValues = {"OPERATIONNEL", "EN_PANNE", "EN_MAINTENANCE"})
    private EtatEquipement etat;

    @Schema(description = "Date d'acquisition de l'équipement", example = "2022-03-15")
    private LocalDate dateAcquisition;
}
