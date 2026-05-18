package com.itbs.maintenance.dto;

import lombok.Data;

/**
 * DTO pour l'entité Technicien.
 */
@Data
public class TechnicienDTO {
    private Long id;
    private String nom;
    private String competences;
    private Boolean disponibilite;
}
