package com.itbs.maintenance.entite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pannes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entité représentant une panne ou une anomalie signalée.
 */
public class Panne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private CategoriePanne categorie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @Column(name = "date_signalement")
    private LocalDate dateSignalement;

    public enum CategoriePanne {
        MECANIQUE, ELECTRIQUE, ELECTRONIQUE, HYDRAULIQUE, PNEUMATIQUE, LOGICIEL, AUTRE
    }
}
