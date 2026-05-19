# MaintenanceMedis — Système de Gestion de Maintenance

**MaintenanceMedis** est une application full-stack moderne permettant de gérer les opérations de maintenance industrielle. Ce système facilite le suivi des équipements, la déclaration des pannes, la planification des interventions, et la gestion des techniciens, le tout complété par un tableau de bord analytique en temps réel.

---

## 🎯 Fonctionnalités Principales

- **📊 Tableau de bord** : Statistiques globales en temps réel (équipements opérationnels, pannes récentes, charge des techniciens).
- **🏭 Équipements** : Gestion du parc matériel (ajout, modification, suivi de l'état).
- **⚠️ Pannes (DI)** : Déclaration et suivi des Demandes d'Intervention.
- **🔧 Interventions (BT)** : Planification des Bons de Travail, affectation aux techniciens, suivi des coûts et du statut.
- **👷 Techniciens** : Gestion du personnel de maintenance, suivi de leurs compétences et de leur disponibilité.

---

## 🏗 Architecture Technique

Le projet est divisé en deux parties principales :

1. **Frontend (Angular)**
   - Interface utilisateur moderne et réactive (Thème Dark Premium, Glassmorphism).
   - Construit avec Angular 18+.
   - Situé dans le dossier `frontend/`.

2. **Backend (Spring Boot)**
   - API RESTful robuste.
   - Construit avec Spring Boot 3.3.5, Java 17, et Hibernate.
   - Base de données : **MySQL** (migrée pour la persistance des données).
   - Situé dans le dossier `backend/`.

---

## 🚀 Démarrage Rapide (Développement Local)

### Prérequis

- **Node.js** et **npm** (pour le frontend)
- **Java 17** (pour le backend)
- **Serveur MySQL** (doit être installé et en cours d'exécution localement)
- **Maven** (inclus via le wrapper `mvnw` dans le dossier backend)

### 1. Lancer le Backend

**Option A : Via le Terminal**
Ouvrez un terminal et placez-vous dans le dossier `backend` :

```bash
cd backend
# Sur Windows
.\mvnw.cmd spring-boot:run

# Sur Linux / macOS
./mvnw spring-boot:run
```

**Option B : Via Eclipse**
1. Ouvrez Eclipse et faites **File > Import > Maven > Existing Maven Projects**.
2. Sélectionnez le dossier `backend`.
3. Patientez pendant l'importation.
4. Lancez `MaintenanceApplication.java` (dans `src/main/java`) via **Run As > Java Application**.

Le backend démarrera sur `http://localhost:8081` et se connectera à votre base de données MySQL locale.

### 2. Lancer le Frontend

Ouvrez un nouveau terminal et placez-vous dans le dossier `frontend` :

```bash
cd frontend
npm install
npm start
```

Le frontend sera accessible à l'adresse **http://localhost:4200/**.

---

## 🐳 Déploiement avec Docker

Le projet inclut une configuration Docker complète pour un déploiement facile, incluant une base de données MySQL.

1. Placez-vous à la racine du projet :
   ```bash
   cd maintenance-system-
   ```
2. Lancez les conteneurs :
   ```bash
   docker-compose up --build -d
   ```

Le fichier `docker-compose.yml` se trouve à la racine du projet.

---


---

## 🛠 Structure du Dépôt

```text
maintenance-system-/
├── backend/                # Code source du backend Spring Boot
├── frontend/               # Code source du frontend Angular
├── docker-compose.yml      # Configuration pour le déploiement conteneurisé

```
