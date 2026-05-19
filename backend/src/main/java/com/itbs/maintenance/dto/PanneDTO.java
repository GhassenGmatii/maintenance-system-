package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Panne.CategoriePanne;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Panne.
 */
@Data
@Schema(description = "Représentation d'une panne signalée sur un équipement")
public class PanneDTO {

    @Schema(description = "Identifiant unique de la panne", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Description détaillée de la panne", example = "Surchauffe du moteur principal")
    private String description;

    @Schema(description = "Catégorie de la panne", example = "MECANIQUE",
            allowableValues = {"MECANIQUE", "ELECTRIQUE", "HYDRAULIQUE", "PNEUMATIQUE", "AUTRE"})
    private CategoriePanne categorie;

    @Schema(description = "Date à laquelle la panne a été signalée", example = "2024-05-20")
    private LocalDate dateSignalement;

    @Schema(description = "ID de l'équipement en panne", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long equipementId;
}
