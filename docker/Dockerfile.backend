# ================================
# Étape 1 : Construction (Build)
# ================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers de dépendances Maven en premier (cache optimisé)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et créer le JAR (sans exécuter les tests)
RUN mvn clean package -DskipTests

# ================================
# Étape 2 : Exécution (Runtime)
# ================================
FROM eclipse-temurin:17-jre-alpine

# Métadonnées
LABEL maintainer="ITBS"
LABEL description="Backend Spring Boot - Système de Gestion de Maintenance"

# Répertoire de travail
WORKDIR /app

# Copier uniquement le JAR compilé depuis l'étape de build
COPY --from=build /app/target/maintenance_management_system-0.0.1-SNAPSHOT.jar app.jar

# Port exposé
EXPOSE 8081

# Point d'entrée pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
