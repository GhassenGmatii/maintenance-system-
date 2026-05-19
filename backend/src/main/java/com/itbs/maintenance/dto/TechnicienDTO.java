package com.itbs.maintenance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO pour l'entité Technicien.
 */
@Data
@Schema(description = "Représentation d'un technicien de maintenance")
public class TechnicienDTO {

    @Schema(description = "Identifiant unique du technicien", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nom complet du technicien", example = "Ali Ben Salah", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Schema(description = "Compétences techniques du technicien", example = "Électricité industrielle, Hydraulique")
    private String competences;

    @Schema(description = "Indique si le technicien est disponible pour une intervention", example = "true")
    private Boolean disponibilite;
}
