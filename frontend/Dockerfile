# ================================
# Étape 1 : Construction (Build)
# ================================
FROM node:20-alpine AS build

# Répertoire de travail
WORKDIR /app

# Copier package.json et package-lock.json pour le cache npm
COPY package*.json ./

# Installer les dépendances npm
RUN npm install

# Copier tout le code source Angular
COPY . .

# Construire l'application en mode production
RUN npm run build -- --configuration production

# ================================
# Étape 2 : Serveur Web (Nginx)
# ================================
FROM nginx:alpine

# Copier le build Angular vers le dossier de Nginx
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copier la configuration personnalisée de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Port exposé
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
