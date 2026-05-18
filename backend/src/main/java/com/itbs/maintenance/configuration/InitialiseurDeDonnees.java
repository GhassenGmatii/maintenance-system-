package com.itbs.maintenance.configuration;

import com.itbs.maintenance.entite.*;
import com.itbs.maintenance.depot.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
/**
 * Composant chargé d'initialiser la base de données avec des valeurs par défaut.
 */
public class InitialiseurDeDonnees implements CommandLineRunner {

    private final EquipementDepot equipementRepository;
    private final TechnicienDepot technicienRepository;
    private final PanneDepot panneRepository;
    private final InterventionDepot interventionRepository;

    @Override
    public void run(String... args) {
        if (equipementRepository.count() > 0) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Initializing sample data...");

        // Create Equipements
        Equipement e1 = equipementRepository.save(Equipement.builder()
                .nom("Compresseur A1").etat(Equipement.EtatEquipement.OPERATIONNEL)
                .dateAcquisition(LocalDate.of(2020, 3, 15)).build());
        Equipement e2 = equipementRepository.save(Equipement.builder()
                .nom("Pompe Hydraulique B2").etat(Equipement.EtatEquipement.EN_PANNE)
                .dateAcquisition(LocalDate.of(2019, 7, 22)).build());
        Equipement e3 = equipementRepository.save(Equipement.builder()
                .nom("Générateur C3").etat(Equipement.EtatEquipement.EN_MAINTENANCE)
                .dateAcquisition(LocalDate.of(2021, 1, 10)).build());
        Equipement e4 = equipementRepository.save(Equipement.builder()
                .nom("Convoyeur D4").etat(Equipement.EtatEquipement.OPERATIONNEL)
                .dateAcquisition(LocalDate.of(2022, 5, 5)).build());
        Equipement e5 = equipementRepository.save(Equipement.builder()
                .nom("Robot Industriel E5").etat(Equipement.EtatEquipement.OPERATIONNEL)
                .dateAcquisition(LocalDate.of(2023, 11, 20)).build());

        // Create Techniciens
        Technicien t1 = technicienRepository.save(Technicien.builder()
                .nom("Ahmed Ben Ali").competences("Électricité, Automatisme, PLC")
                .disponibilite(true).build());
        Technicien t2 = technicienRepository.save(Technicien.builder()
                .nom("Fatma Trabelsi").competences("Hydraulique, Pneumatique, Mécanique")
                .disponibilite(true).build());
        Technicien t3 = technicienRepository.save(Technicien.builder()
                .nom("Mohamed Chaabane").competences("Électronique, Informatique industrielle")
                .disponibilite(false).build());
        Technicien t4 = technicienRepository.save(Technicien.builder()
                .nom("Sonia Gharbi").competences("Mécanique générale, Soudure")
                .disponibilite(true).build());

        // Create Pannes
        panneRepository.save(Panne.builder()
                .description("Fuite d'huile importante sur le joint principal")
                .categorie(Panne.CategoriePanne.HYDRAULIQUE)
                .equipement(e2)
                .dateSignalement(LocalDate.now().minusDays(5)).build());
        panneRepository.save(Panne.builder()
                .description("Court-circuit dans le tableau électrique")
                .categorie(Panne.CategoriePanne.ELECTRIQUE)
                .equipement(e3)
                .dateSignalement(LocalDate.now().minusDays(2)).build());
        panneRepository.save(Panne.builder()
                .description("Bruit anormal dans les roulements")
                .categorie(Panne.CategoriePanne.MECANIQUE)
                .equipement(e1)
                .dateSignalement(LocalDate.now().minusDays(10)).build());
        panneRepository.save(Panne.builder()
                .description("Erreur de communication réseau")
                .categorie(Panne.CategoriePanne.LOGICIEL)
                .equipement(e5)
                .dateSignalement(LocalDate.now().minusDays(1)).build());

        // Create Interventions
        interventionRepository.save(Intervention.builder()
                .equipement(e2).technicien(t2)
                .statut(Intervention.StatutIntervention.EN_COURS)
                .date(LocalDate.now()).cout(350.0).build());
        interventionRepository.save(Intervention.builder()
                .equipement(e3).technicien(t1)
                .statut(Intervention.StatutIntervention.PLANIFIEE)
                .date(LocalDate.now().plusDays(2)).cout(500.0).build());
        interventionRepository.save(Intervention.builder()
                .equipement(e1).technicien(t4)
                .statut(Intervention.StatutIntervention.TERMINEE)
                .date(LocalDate.now().minusDays(8)).cout(120.0).build());
        interventionRepository.save(Intervention.builder()
                .equipement(e5).technicien(t3)
                .statut(Intervention.StatutIntervention.PLANIFIEE)
                .date(LocalDate.now().plusDays(1)).cout(200.0).build());

        log.info("Sample data initialized successfully!");
    }
}
