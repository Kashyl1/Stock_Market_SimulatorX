# Użycie obrazu Node.js do budowania aplikacji
FROM node:18-alpine AS build

# Ustawienie katalogu roboczego
WORKDIR /app

# Kopiowanie plików package.json i package-lock.json
COPY package*.json ./

# Instalacja zależności
RUN npm install

# Kopiowanie pozostałych plików aplikacji
COPY . .

# Budowanie aplikacji React
RUN npm run build

# Użycie obrazu Nginx do serwowania statycznych plików frontendu
FROM nginx:alpine

# Kopiowanie wynikowych plików build do katalogu serwowania Nginx
COPY --from=build /app/build /usr/share/nginx/html

# Eksponowanie portu 80
EXPOSE 80

# Uruchomienie Nginx
CMD ["nginx", "-g", "daemon off;"]
