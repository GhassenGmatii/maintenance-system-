# 🚀 Lancer le Projet — Guide Rapide

## Prérequis installés sur la machine

| Logiciel | Requis |
|----------|--------|
| Java JDK | ✅ JDK 17 (ex: Amazon Corretto) |
| MySQL | ✅ Serveur MySQL (WAMP, XAMPP, etc.) sur port 3306 |
| Node.js | ✅ Version 18+ |

> **Note :** La base de données MySQL est désormais utilisée. Spring Boot se chargera de créer la base `maintenance_db` et les tables tout seul, vous n'avez rien à configurer.

---

## ⚡ ÉTAPE 1 — Allumer MySQL
1. Lancez votre serveur MySQL (par exemple via XAMPP ou WAMP).
2. Assurez-vous qu'il tourne sur le port `3306` (port par défaut).

---

## ⚡ ÉTAPE 2 — Lancer le Backend (Spring Boot) depuis Eclipse

### Importer le projet (une seule fois) :
1. Ouvrir **Eclipse**
2. Aller dans **File → Import → Maven → Existing Maven Projects → Next**
3. **Browse** → Sélectionner le dossier : `C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\backend`
4. Cocher le `pom.xml` détecté → **Finish**
5. Attendre le chargement des dépendances en bas à droite.

### Lancer l'application :
1. Dans le *Project Explorer*, ouvrir :
   `src/main/java` → `com.itbs.maintenance` → `SystemeGestionMaintenanceApplication.java`
2. Faire un **Clic droit** sur ce fichier → **Run As → Java Application**
3. Attendre dans la console le message : `"Started SystemeGestionMaintenanceApplication"`

✅ **Backend actif sur :** `http://localhost:8081`

---

## ⚡ ÉTAPE 3 — Lancer le Frontend (Angular)

Ouvrez **un nouveau terminal** :

```powershell
# 1. Allez dans le dossier frontend
cd C:\Users\Ghassen\OneDrive\Desktop\projet_spring\maintenance-_system\frontend

# 2. Installez les paquets (première fois seulement)
npm install

# 3. Lancez Angular
npm start
```

✅ **Frontend actif sur :** `http://localhost:4200`

---

## ⚡ ÉTAPE 4 — Ouvrir l'application

Ouvrez votre navigateur et accédez à :

```
http://localhost:4200
```

---

## 📋 Résumé

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│   1️⃣  MySQL         →  S'assurer qu'il est allumé   │
│           ↓                                         │
│   2️⃣  Backend       →  .\mvnw.cmd spring-boot:run   │
│           ↓                                         │
│   3️⃣  Frontend      →  npm start                    │
│           ↓                                         │
│   4️⃣  Navigateur    →  http://localhost:4200        │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## ❗ Problèmes fréquents et solutions rapides

| Symptôme | Cause | Solution |
|----------|-------|----------|
| `Port 8081 already in use` | Une autre instance tourne | Tuer le processus Java dans le gestionnaire de tâches |
| `Access denied for user 'root'` | Mot de passe MySQL manquant | Ajuster le mot de passe dans `backend/src/main/resources/application.properties` |
| Page bloquée sur `Chargement...` | Backend non démarré | Lancer d'abord le Backend |
| `npm : command not found` | Node.js non installé | Installer Node.js depuis https://nodejs.org |
| Données non affichées | Erreur de compilation | Vérifier la console du navigateur (F12) et celle du backend |
