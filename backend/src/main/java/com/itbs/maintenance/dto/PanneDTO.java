package com.itbs.maintenance.dto;

import com.itbs.maintenance.entite.Panne.CategoriePanne;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO pour l'entité Panne.
 */
@Data
public class PanneDTO {
    private Long id;
    private String description;
    private CategoriePanne categorie;
    private LocalDate dateSignalement;
    private Long equipementId;
}
