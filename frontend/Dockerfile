# --- Etapa 1: Build con Node ---
FROM node:18 AS build
WORKDIR /app

# Copiamos package.json y package-lock.json primero (cacheo de dependencias)
COPY package*.json ./

# Instalamos dependencias (usa ci para entorno prod)
RUN npm ci

# Copiamos el resto del código
COPY . .

# Compilamos la app (para Vue/React con Vite)
RUN npm run build

# --- Etapa 2: Servir con Nginx ---
FROM nginx:stable-alpine

# Copiamos los archivos compilados al directorio web de nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copiamos la configuración de Nginx para SPA + proxy
COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

