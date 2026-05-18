package com.itbs.maintenance.entite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "interventions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entité représentant une intervention de maintenance.
 */
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technicien_id", nullable = false)
    private Technicien technicien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIntervention statut;

    /** Date de début de l'intervention */
    @Column(nullable = false)
    private LocalDate date;

    /** Date de fin prévue de l'intervention (période) */
    @Column(name = "date_fin")
    private LocalDate dateFin;

    private Double cout;

    public enum StatutIntervention {
        PLANIFIEE, EN_COURS, TERMINEE, ANNULEE
    }
}
