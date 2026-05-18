# 🗄️ Documentation de la Base de Données

Ce document présente la structure de la base de données utilisée par l'application de Maintenance.

## Informations Générales
- **Nom de la base :** `maintenance_db`
- **SGBD :** MySQL (8.x ou supérieur recommandé)
- **Création des tables :** Gérée automatiquement par Spring Boot (Hibernate `ddl-auto=update`).

---

## 1. Table `equipements`
Contient la liste des machines et équipements de l'usine.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique de l'équipement |
| `nom` | VARCHAR | Le nom de l'équipement (ex: "Compresseur A1") |
| `etat` | ENUM | L'état actuel (`OPERATIONNEL`, `EN_PANNE`, `EN_MAINTENANCE`, `HORS_SERVICE`) |
| `date_acquisition` | DATE | La date d'achat ou de mise en service |

---

## 2. Table `techniciens`
Contient la liste du personnel de maintenance.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique du technicien |
| `nom` | VARCHAR | Le prénom et nom (ex: "Ahmed Ben Ali") |
| `competences` | TEXT | Liste des compétences séparées par des virgules (ex: "Électricité, Soudure") |
| `disponibilite` | BOOLEAN | `true` = ✅ Disponible, `false` = 🔴 Occupé (en intervention) |

---

## 3. Table `pannes`
Représente les Demandes d'Intervention (DI) signalées par les opérateurs.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique de la panne |
| `description` | TEXT | Description du problème observé |
| `categorie` | VARCHAR | La catégorie de panne (`MECANIQUE`, `ELECTRIQUE`, `HYDRAULIQUE`...) |
| `date_signalement` | DATE | Date où la panne a été signalée |
| `equipement_id` | BIGINT (FK) | 🔗 Clé étrangère : L'équipement en panne |

---

## 4. Table `interventions`
Représente les Bons de Travaux (BT) planifiés pour réparer une panne.

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | BIGINT (PK) | Identifiant unique de l'intervention |
| `statut` | ENUM | État calculé (`PLANIFIEE`, `EN_COURS`, `TERMINEE`, `ANNULEE`) |
| `date` | DATE | Date de **début** prévue de l'intervention |
| `date_fin` | DATE | Date de **fin** prévue de l'intervention |
| `cout` | DOUBLE | Le coût estimé de l'intervention |
| `equipement_id` | BIGINT (FK) | 🔗 Clé étrangère : L'équipement à réparer |
| `technicien_id` | BIGINT (FK) | 🔗 Clé étrangère : Le technicien qui va faire le travail |

---

## 💡 Notes sur les Relations (FK)
- Un **équipement** peut avoir plusieurs **pannes** (1 ➔ N).
- Un **équipement** peut subir plusieurs **interventions** (1 ➔ N).
- Un **technicien** peut réaliser plusieurs **interventions** (1 ➔ N).
