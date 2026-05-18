import os
import re
import glob
import shutil

base_dir = r"C:\Users\Ghassen\eclipse-workspace\maintenance_management_system\backend\src\main\java\com\itbs\maintenance"

# 1. Rename directories
dirs_to_rename = {
    "controller": "controleur",
    "entity": "entite",
    "repository": "depot",
    "config": "configuration"
}

for old, new in dirs_to_rename.items():
    old_path = os.path.join(base_dir, old)
    new_path = os.path.join(base_dir, new)
    if os.path.exists(old_path):
        os.rename(old_path, new_path)

# 2. Rename classes mapping
class_renames = {
    "DashboardController": "TableauDeBordControleur",
    "EquipementController": "EquipementControleur",
    "InterventionController": "InterventionControleur",
    "PanneController": "PanneControleur",
    "TechnicienController": "TechnicienControleur",
    "EquipementRepository": "EquipementDepot",
    "InterventionRepository": "InterventionDepot",
    "PanneRepository": "PanneDepot",
    "TechnicienRepository": "TechnicienDepot",
    "DashboardStats": "StatistiquesTableauDeBord",
    "DataInitializer": "InitialiseurDeDonnees",
    "WebConfig": "ConfigurationWeb",
    "MaintenanceManagementSystemApplication": "SystemeGestionMaintenanceApplication"
}

# 3. Add Javadoc comments
comments_map = {
    "TableauDeBordControleur": "/**\n * Contrôleur REST gérant les requêtes pour le tableau de bord.\n */",
    "EquipementControleur": "/**\n * Contrôleur REST pour la gestion des équipements.\n */",
    "InterventionControleur": "/**\n * Contrôleur REST pour la gestion des interventions (Bons de travail).\n */",
    "PanneControleur": "/**\n * Contrôleur REST pour la gestion des pannes (Demandes d'intervention).\n */",
    "TechnicienControleur": "/**\n * Contrôleur REST pour la gestion des techniciens.\n */",
    "EquipementService": "/**\n * Service métier contenant la logique pour les équipements.\n */",
    "InterventionService": "/**\n * Service métier contenant la logique pour les interventions.\n */",
    "PanneService": "/**\n * Service métier contenant la logique pour les pannes.\n */",
    "TechnicienService": "/**\n * Service métier contenant la logique pour les techniciens.\n */",
    "Equipement": "/**\n * Entité représentant un équipement dans le système.\n */",
    "Intervention": "/**\n * Entité représentant une intervention de maintenance.\n */",
    "Panne": "/**\n * Entité représentant une panne ou une anomalie signalée.\n */",
    "Technicien": "/**\n * Entité représentant un technicien de maintenance.\n */",
    "EquipementDepot": "/**\n * Dépôt (Repository) pour l'accès aux données des équipements.\n */",
    "InterventionDepot": "/**\n * Dépôt (Repository) pour l'accès aux données des interventions.\n */",
    "PanneDepot": "/**\n * Dépôt (Repository) pour l'accès aux données des pannes.\n */",
    "TechnicienDepot": "/**\n * Dépôt (Repository) pour l'accès aux données des techniciens.\n */",
    "ConfigurationWeb": "/**\n * Configuration globale du serveur Web (CORS, etc.).\n */",
    "InitialiseurDeDonnees": "/**\n * Composant chargé d'initialiser la base de données avec des valeurs par défaut.\n */",
    "SystemeGestionMaintenanceApplication": "/**\n * Classe principale de l'application Spring Boot.\n */"
}

# 4. Process files
java_files = glob.glob(os.path.join(base_dir, "**", "*.java"), recursive=True)

for file_path in java_files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace packages
    content = content.replace('.controller', '.controleur')
    content = content.replace('.entity', '.entite')
    content = content.replace('.repository', '.depot')
    content = content.replace('.config', '.configuration')

    # Replace class names
    for old_cls, new_cls in class_renames.items():
        # Match word boundaries to avoid partial matches
        content = re.sub(rf'\b{old_cls}\b', new_cls, content)

    # Insert Javadoc
    # Find the public class/interface/enum declaration and insert comment before it
    for new_cls, comment in comments_map.items():
        if f"class {new_cls}" in content or f"interface {new_cls}" in content or f"enum {new_cls}" in content:
            # Check if it already has a doc comment just above
            if comment not in content:
                content = re.sub(rf'(public (class|interface|enum) {new_cls})', rf'{comment}\n\1', content)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

# 5. Rename file names
java_files = glob.glob(os.path.join(base_dir, "**", "*.java"), recursive=True)
for file_path in java_files:
    dir_name = os.path.dirname(file_path)
    base_name = os.path.basename(file_path)
    old_cls = base_name.replace('.java', '')
    if old_cls in class_renames:
        new_name = class_renames[old_cls] + '.java'
        os.rename(file_path, os.path.join(dir_name, new_name))

print("Refactoring terminee avec succes!")
