package com.itbs.maintenance.entite;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "equipements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entité représentant un équipement dans le système.
 */
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatEquipement etat;

    @Column(name = "date_acquisition")
    private LocalDate dateAcquisition;

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Panne> pannes;

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Intervention> interventions;

    public enum EtatEquipement {
        OPERATIONNEL, EN_PANNE, EN_MAINTENANCE, HORS_SERVICE
    }
}
