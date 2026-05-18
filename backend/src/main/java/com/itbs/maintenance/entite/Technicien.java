package com.itbs.maintenance.entite;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "techniciens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entité représentant un technicien de maintenance.
 */
public class Technicien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String competences;

    @Column(nullable = false)
    private Boolean disponibilite;

    @OneToMany(mappedBy = "technicien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Intervention> interventions;
}
