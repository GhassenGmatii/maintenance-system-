# 📖 Architecture & Règles Métier du Backend

Ce document explique de manière **très simple** comment fonctionne le code backend (Spring Boot). 
L'objectif est que n'importe qui puisse ouvrir le projet et comprendre exactement ce qu'il se passe sans se perdre.

---

## 🏛️ 1. Architecture Globale (DTO, Mapper, Service, Controller)

Le code est organisé en **couches**. Chaque couche a un rôle précis :

1. **Entités (`entite`)** : Les tables de la base de données (Equipement, Panne...).
2. **DTO (`dto`)** : L'objet qu'on envoie au Frontend Angular (pour ne pas envoyer toute la base de données).
3. **Mappers (`mapper`)** : Le code qui convertit une Entité en DTO (et inversement).
4. **Depot (`depot`)** : Les requêtes SQL vers la base de données (ex: `findByEtat()`).
5. **Services (`service`)** : **Le cœur du système**. C'est ici qu'on met les règles métier (les "si... alors...").
6. **Controleurs (`controleur`)** : La porte d'entrée pour le Frontend (les API `GET`, `POST`, `PUT`, `DELETE`).

👉 **Où faire les modifications ?** 
Si tu veux changer la logique (ex: un équipement change d'état), **c'est TOUJOURS dans les Services**.

---

## ⚙️ 2. Les Règles Métier (Automatisations)

Plutôt que d'avoir un "scheduler" compliqué qui tourne en arrière-plan, nous avons mis en place une logique **action/réaction** simple et claire dans les services.

### A. Gestion des Équipements & Pannes (`PanneService.java`)
- **Action** : L'utilisateur signale une panne.
  - **Réaction automatique** : L'équipement concerné passe immédiatement à l'état `🔴 EN_PANNE`.
- **Action** : L'utilisateur supprime la seule panne de l'équipement.
  - **Réaction automatique** : L'équipement n'a plus de problème, il redevient `✅ OPERATIONNEL`.

### B. Gestion des Interventions (`InterventionService.java`)
- **Action** : L'utilisateur planifie une intervention.
  - **Réaction automatique 1** : L'équipement passe en `🔧 EN_MAINTENANCE`.
  - **Réaction automatique 2** : Le technicien assigné passe en `🔴 OCCUPÉ`.
- **Action** : L'utilisateur marque l'intervention comme terminée.
  - **Réaction automatique 1** : L'équipement est réparé, il redevient `✅ OPERATIONNEL`.
  - **Réaction automatique 2** : Le technicien a fini son travail, il redevient `✅ DISPONIBLE`.
- **Action** : L'utilisateur annule l'intervention.
  - **Réaction automatique 1** : L'équipement n'a pas été réparé, il retourne à `🔴 EN_PANNE`.
  - **Réaction automatique 2** : Le technicien est libéré, il redevient `✅ DISPONIBLE`.

### C. Calcul automatique des statuts d'intervention
Le statut d'une intervention n'est plus choisi manuellement à la création. Il est **calculé** selon la date d'aujourd'hui :
- Si la date de début est **dans le futur** → `📅 PLANIFIEE`
- Si la date d'aujourd'hui est **comprise dans la période** → `⚙️ EN_COURS`
- Si la date de fin est **dépassée** → `✅ TERMINEE`

---

## 🛠️ 3. Pourquoi ce code est facile à comprendre ?

Nous avons retravaillé les fichiers de service (ex: `PanneService.java`, `InterventionService.java`) pour qu'ils soient lisibles comme un livre :
- Chaque méthode a un **commentaire en français** qui explique ce qu'elle fait.
- La logique métier est **numérotée** (1. Créer entité, 2. Calculer statut, 3. Sauvegarder...).
- Les méthodes complexes sont découpées en **petites fonctions** (ex: `changerEtatEquipement()`).

Si tu dois relire le code dans 6 mois, il te suffira de lire les commentaires pour tout comprendre !
