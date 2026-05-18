# 🚀 Guide d'Exécution du Projet

Ce guide explique étape par étape comment lancer l'application de Maintenance (Backend et Frontend).

## ⚠️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

| Logiciel | Version requise | Vérification |
|----------|----------------|--------------|
| **Java JDK** | 17 | `javac -version` |
| **Node.js** | 18+ | `node -v` |
| **npm** | 9+ | `npm -v` |
| **MySQL** | 8.x+ | Serveur MySQL doit être démarré sur le port 3306 avec l'utilisateur `root` (sans mot de passe). |

> **Note sur la base de données :** Spring Boot se chargera automatiquement de créer la base de données `maintenance_db` et les tables au démarrage.

---

## 🟢 Étape 1 — Lancer le Backend (Spring Boot) depuis Eclipse

### Importer le projet (une seule fois)
1. Ouvrez **Eclipse IDE**
2. Allez dans **File > Import > Maven > Existing Maven Projects**
3. Sélectionnez le dossier : `C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\backend`
4. Cliquez sur **Finish** et attendez le chargement complet des dépendances en bas à droite de l'écran.

### Lancer l'application
1. Dans l'explorateur de projet (Project Explorer), naviguez vers :
   `src/main/java` → `com.itbs.maintenance` → `SystemeGestionMaintenanceApplication.java`
2. Faites un clic droit sur ce fichier
3. Choisissez **Run As > Java Application**
4. Regardez la console (en bas), attendez de voir le message `"Started SystemeGestionMaintenanceApplication"`.

✅ Le serveur backend démarre et écoute sur : **`http://localhost:8081`**

*(Vous verrez dans la console que Spring Boot crée les tables dans MySQL et insère les données de test !)*

---

## 🔵 Étape 2 — Lancer le Frontend (Angular)

Ouvrez **un nouveau terminal** et tapez :

```powershell
# 1. Se placer dans le dossier du frontend
cd C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\frontend

# 2. Installer les dépendances (nécessaire uniquement la première fois)
npm install

# 3. Démarrer le serveur Angular
npm start
```

✅ L'interface utilisateur démarre sur : **`http://localhost:4200`**

---

## 🌐 Résumé des URLs de l'Application

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend Angular** | http://localhost:4200 | C'est ici que vous utilisez l'application ! |
| **Backend API** | http://localhost:8081/api/... | L'adresse de l'API invisible (ex: /api/equipements) |
| **Base de données** | `localhost:3306` | Accessible via PhpMyAdmin ou MySQL Workbench |

---

## 🔧 Résolution des Problèmes Courants

| Problème | Cause probable | Solution |
|----------|-------|----------|
| **Erreur de connexion MySQL** (Access denied) | Mot de passe incorrect ou MySQL éteint | Vérifier que MySQL (XAMPP, WAMP...) est bien allumé. Vérifier `application.properties` (mot de passe root). |
| **Le port 8081 ou 4200 est déjà utilisé** | Un ancien serveur tourne encore en arrière-plan | Fermer le terminal ou tuer le processus dans le Gestionnaire des tâches. |
| **Page bloquée sur "Chargement..."** | Le Backend n'est pas allumé | Lancer d'abord le Backend Spring Boot ! |
