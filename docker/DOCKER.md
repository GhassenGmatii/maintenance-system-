# Guide de Dockerisation

## Présentation

La dockerisation du projet permet de lancer l'ensemble des 3 services avec **une seule commande**, sans avoir besoin d'installer Java, MySQL ou Node.js sur la machine hôte.

| Service | Technologie | Port | Accès |
|---------|------------|------|-------|
| Base de données | MySQL 8.0 | 3306 | Interne |
| Backend | Spring Boot + JRE 17 | 8081 | http://localhost:8081 |
| Frontend | Angular + Nginx | 80 | http://localhost |

---

## Structure des fichiers Docker

```
docker/
├── Dockerfile.backend    ← Image Docker du backend Spring Boot
├── Dockerfile.frontend   ← Image Docker du frontend Angular (via Nginx)
├── nginx.conf            ← Configuration du serveur web Nginx
├── docker-compose.yml    ← Orchestration des 3 services
└── DOCKER.md             ← Ce guide
```

---

## Prérequis

- **Docker Desktop** installé et démarré
- Téléchargement : https://www.docker.com/products/docker-desktop

Vérification :
```powershell
docker --version
docker compose version
```

---

## Lancement du projet (1 seule commande)

```powershell
# Se placer dans le dossier docker
cd C:\Users\Ghassen\eclipse-workspace\maintenance_management_system\docker

# Construire et lancer tous les services
docker compose up --build
```

> ⏳ La première exécution prend ~5 minutes (téléchargement des images et build)

---

## URLs après le lancement

| Service | URL |
|---------|-----|
| **Application Web** | http://localhost |
| **API Backend** | http://localhost:8081/api/equipements |
| **Dashboard API** | http://localhost:8081/api/dashboard/stats |

---

## Commandes Docker utiles

```powershell
# Lancer en arrière-plan (mode détaché)
docker compose up -d --build

# Vérifier l'état des conteneurs
docker compose ps

# Voir les logs en temps réel
docker compose logs -f

# Voir les logs d'un service spécifique
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# Arrêter tous les services
docker compose down

# Arrêter et supprimer les volumes (efface la base de données)
docker compose down -v

# Redémarrer un seul service
docker compose restart backend

# Accéder à la console MySQL
docker exec -it maintenance_mysql mysql -u root -proot maintenance_db
```

---

## Architecture Docker

```
┌─────────────────── Docker Network : maintenance_network ────────────────┐
│                                                                          │
│  ┌───────────────┐     ┌──────────────────┐     ┌──────────────────┐  │
│  │     mysql     │     │     backend      │     │    frontend      │  │
│  │  MySQL 8.0    │◄────│  Spring Boot 17  │◄────│  Angular/Nginx   │  │
│  │  Port: 3306   │     │  Port: 8081      │     │  Port: 80        │  │
│  └───────┬───────┘     └──────────────────┘     └────────┬─────────┘  │
│          │                                                 │             │
│    mysql_data                                              │             │
│    (volume)                                        ┌───────▼──────┐    │
│                                                    │   HOST:80    │    │
└────────────────────────────────────────────────────┤ localhost    ├────┘
                                                     └──────────────┘
                                                     Navigateur utilisateur
```

---

## Résolution des problèmes

| Problème | Solution |
|----------|----------|
| `Port 80 already in use` | Changer `"80:80"` en `"8080:80"` dans `docker-compose.yml` |
| `Port 3306 already in use` | Arrêter MySQL local ou changer le port en `"3307:3306"` |
| Backend ne démarre pas | Attendre que MySQL soit prêt (le `healthcheck` gère cela) |
| `docker: command not found` | Installer Docker Desktop et redémarrer le terminal |
