# 📦 Description des Classes (Entités & DTO)

Ce document explique les modèles de données utilisés dans le code Java (Backend).

## 1. La différence entre "Entité" et "DTO"

Dans le dossier `com.itbs.maintenance`, tu verras deux dossiers importants : `entite` et `dto`.
* **Entité (`entite/`)** : C'est la classe qui représente la **table dans la base de données**. Elle contient toutes les annotations Hibernate (`@Entity`, `@Table`, `@Id`...).
* **DTO (`dto/`)** : C'est l'objet "simplifié" qu'on envoie au Frontend Angular via l'API. Ça évite d'envoyer toute la base de données (sécurité & performance).

---

## 2. Les Entités Principales

### `Equipement.java`
Représente une machine physique.
* **Champs clés** : `id`, `nom`, `etat`, `dateAcquisition`.
* **Relations** : Il est lié aux pannes (`@OneToMany`) et aux interventions (`@OneToMany`).

### `Technicien.java`
Représente un employé de la maintenance.
* **Champs clés** : `id`, `nom`, `competences`, `disponibilite`.
* **Relations** : Il peut être assigné à plusieurs interventions (`@OneToMany`).

### `Panne.java` (Demande d'Intervention - DI)
Représente un problème signalé.
* **Champs clés** : `id`, `description`, `categorie`, `dateSignalement`.
* **Relations** : Appartient à UN équipement (`@ManyToOne`).

### `Intervention.java` (Bon de Travail - BT)
Représente un travail de réparation planifié.
* **Champs clés** : `id`, `statut`, `date` (début), `dateFin`, `cout`.
* **Relations** : Concerne UN équipement (`@ManyToOne`) et est assignée à UN technicien (`@ManyToOne`).

---

## 3. Les Enumérations (États et Catégories)

Pour éviter les erreurs de saisie, les états sont définis avec des "Enum" (des listes de choix fixes) :

* **`EtatEquipement`** : `OPERATIONNEL`, `EN_PANNE`, `EN_MAINTENANCE`, `HORS_SERVICE`
* **`StatutIntervention`** : `PLANIFIEE`, `EN_COURS`, `TERMINEE`, `ANNULEE`

---

## 4. Les Mappers (`mapper/`)

Les Mappers (ex: `EquipementMapper.java`) sont de simples classes qui ont deux fonctions :
1. `toDto(Entite e)` : Transforme l'Entité de la base de données en DTO pour le renvoyer à Angular.
2. `toEntity(Dto d)` : Transforme le DTO reçu depuis Angular en Entité pour le sauvegarder dans la base.
