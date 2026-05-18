# Système de Gestion de Maintenance — Documentation Complète

## Table des Matières
1. [Présentation du Projet](#1-présentation-du-projet)
2. [Architecture Technique](#2-architecture-technique)
3. [Structure des Dossiers](#3-structure-des-dossiers)
4. [Modèle de Données (Base de Données)](#4-modèle-de-données-base-de-données)
5. [API REST — Endpoints](#5-api-rest--endpoints)
6. [Guide d'Exécution du Projet](#6-guide-dexécution-du-projet)

---

## 1. Présentation du Projet

**Nom du projet :** Système de Gestion de Maintenance  
**Version :** 0.0.1-SNAPSHOT  
**Auteur :** ITBS  
**Date :** Mai 2026  

### Objectif
Ce système est une application web complète permettant de gérer les opérations de maintenance industrielle. Il permet de :

- **Gérer les équipements** : enregistrer, suivre et modifier l'état des équipements.
- **Déclarer des pannes** : signaler une anomalie ou une défaillance sur un équipement.
- **Planifier des interventions** : créer des bons de travaux et les assigner à des techniciens.
- **Gérer les techniciens** : suivre leur disponibilité et leurs compétences.
- **Visualiser un tableau de bord** : consulter des statistiques en temps réel.

---

## 2. Architecture Technique

```
┌──────────────────────────────────────────────────────────┐
│                  FRONTEND (Angular 21)                   │
│           http://localhost:4200                          │
│  • Thème Dark Premium (Glassmorphism)                    │
│  • Tableau de bord (Analyses)                            │
│  • Liste DI (Pannes)                                     │
│  • Liste BT (Interventions)                              │
│  • Liste d'Équipements                                   │
└──────────────────┬───────────────────────────────────────┘
                   │  HTTP REST API (JSON) via Proxy
                   │  (proxy.conf.json cible 127.0.0.1:8081)
┌──────────────────▼───────────────────────────────────────┐
│               BACKEND (Spring Boot 3.3.5)                │
│           http://localhost:8081                          │
│  • Spring Web MVC (REST Controllers)                     │
│  • Spring Data JPA (Repositories)                        │
│  • Hibernate ORM                                         │
│  • Lombok                                                │
└──────────────────┬───────────────────────────────────────┘
                   │  JDBC / Hibernate
┌──────────────────▼───────────────────────────────────────┐
│              BASE DE DONNÉES (H2 In-Memory)              │
│           maintenance_db                                 │
│  • equipements                                           │
│  • techniciens                                           │
│  • pannes                                                │
│  • interventions                                         │
└──────────────────────────────────────────────────────────┘
```

### Stack Technologique

| Couche | Technologie | Version |
|--------|-------------|---------|
| Frontend | Angular | 21.x |
| Backend | Spring Boot | 3.3.5 |
| Langage | Java | 17 (Amazon Corretto) |
| ORM | Hibernate | 6.5.x |
| Base de données | H2 (In-Memory) | Latest |
| Build | Maven | 3.x |
| Mapping objet | Lombok | Latest |

---

## 3. Structure des Dossiers

```
maintenance_management_system/
│
├── backend/                          ← Code source Spring Boot
│   ├── src/main/java/com/itbs/maintenance/
│   │   ├── configuration/            ← Configurations (CORS, DataInitializer)
│   │   ├── controleur/               ← Contrôleurs REST (API)
│   │   ├── depot/                    ← Repositories (accès BDD)
│   │   ├── entite/                   ← Entités JPA (Modèles)
│   │   └── service/                  ← Logique métier
│   ├── src/main/resources/
│   │   ├── application.properties    ← Configuration H2, port, JPA
│   │   └── static/                   ← Ancienne interface HTML (legacy)
│   └── pom.xml                       ← Dépendances Maven
│
├── frontend/                         ← Application Angular
│   ├── src/app/
│   │   ├── layout/                   ← Header et mise en page principale
│   │   ├── pages/                    ← Pages de l'application
│   │   │   ├── dashboard/            ← Tableau de bord
│   │   │   ├── pannes/               ← Liste des DI (pannes)
│   │   │   ├── interventions/        ← Liste des BT (interventions)
│   │   │   ├── techniciens/          ← Liste des techniciens
│   │   │   └── equipements/          ← Liste des équipements
│   │   └── services/                 ← Service HTTP (ApiService)
│   ├── src/styles.css                ← Styles globaux (Thème Dark Premium)
│   ├── proxy.conf.json               ← Configuration du proxy vers le backend
│   └── package.json                  ← Dépendances npm
│
├── documentation/                    ← 📁 Documentation du projet (ce dossier)
│   ├── DOCUMENTATION.md              ← Ce fichier
│   ├── ARCHITECTURE_ET_REGLES_METIER.md ← Explication simple des règles métier
│   ├── BASE_DE_DONNEES.md            ← Schéma et détails de la BDD
│   ├── GUIDE_EXECUTION.md            ← Guide détaillé
│   └── LANCER_PROJET.md              ← Guide rapide pour lancer le projet
│
└── docker/                           ← Fichiers Docker (déploiement)
```

---

## 4. Modèle de Données (Base de Données)

> Voir le fichier détaillé : **[BASE_DE_DONNEES.md](./BASE_DE_DONNEES.md)**

### Diagramme relationnel simplifié

```
┌─────────────────┐         ┌──────────────────┐
│   EQUIPEMENTS   │         │   TECHNICIENS    │
│─────────────────│         │──────────────────│
│ id (PK)         │         │ id (PK)          │
│ nom             │         │ nom              │
│ etat            │         │ competences      │
│ date_acquisition│         │ disponibilite    │
└───────┬─────────┘         └────────┬─────────┘
        │ 1                           │ 1
        │                             │
        │ N                           │ N
┌───────▼─────────────────────────────▼─────────┐
│                  INTERVENTIONS                 │
│────────────────────────────────────────────────│
│ id (PK)                                        │
│ statut (PLANIFIEE / EN_COURS / TERMINEE)       │
│ date                                           │
│ cout                                           │
│ equipement_id (FK)                             │
│ technicien_id (FK)                             │
└────────────────────────────────────────────────┘

┌─────────────────────────────────┐
│             PANNES              │
│─────────────────────────────────│
│ id (PK)                         │
│ description                     │
│ categorie                       │
│ date_signalement                │
│ equipement_id (FK)              │
└─────────────────────────────────┘
```

---

## 5. API REST — Endpoints

### Base URL : `http://localhost:8081/api`

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/dashboard/stats` | Statistiques du tableau de bord |
| GET | `/equipements` | Liste de tous les équipements |
| GET | `/equipements/{id}` | Détail d'un équipement |
| POST | `/equipements` | Créer un équipement |
| PUT | `/equipements/{id}` | Modifier un équipement |
| DELETE | `/equipements/{id}` | Supprimer un équipement |
| GET | `/pannes` | Liste de toutes les pannes |
| POST | `/pannes` | Signaler une panne |
| PUT | `/pannes/{id}` | Modifier une panne |
| DELETE | `/pannes/{id}` | Supprimer une panne |
| GET | `/interventions` | Liste de toutes les interventions |
| POST | `/interventions` | Créer une intervention |
| PUT | `/interventions/{id}` | Modifier une intervention |
| DELETE | `/interventions/{id}` | Supprimer une intervention |
| GET | `/techniciens` | Liste de tous les techniciens |
| POST | `/techniciens` | Créer un technicien |
| PUT | `/techniciens/{id}` | Modifier un technicien |
| DELETE | `/techniciens/{id}` | Supprimer un technicien |

---

## 6. Guide d'Exécution du Projet

> Voir le fichier détaillé : **[GUIDE_EXECUTION.md](./GUIDE_EXECUTION.md)** ou le guide rapide **[LANCER_PROJET.md](./LANCER_PROJET.md)**.
