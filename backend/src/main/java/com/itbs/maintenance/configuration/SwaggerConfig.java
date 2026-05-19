package com.itbs.maintenance.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ============================================================
 * CONFIGURATION SWAGGER / OPENAPI 3
 * ============================================================
 * Accessible via : http://localhost:8081/swagger-ui.html
 * JSON spec     : http://localhost:8081/v3/api-docs
 * ============================================================
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI maintenanceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Système de Gestion de Maintenance Industrielle")
                        .description("""
                                API REST pour la gestion des opérations de maintenance industrielle.
                                
                                ## Ressources disponibles
                                - **Équipements** : gestion du parc matériel (état, acquisition)
                                - **Pannes** : signalement et suivi des pannes par catégorie
                                - **Interventions** : planification et suivi des interventions de maintenance
                                - **Techniciens** : gestion des techniciens et de leur disponibilité
                                - **Tableau de bord** : statistiques globales et indicateurs clés
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ITBS Maintenance Team")
                                .email("contact@itbs.com"))
                        .license(new License()
                                .name("Propriétaire")
                                .url("https://www.itbs.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Serveur de développement local")
                ));
    }
}
