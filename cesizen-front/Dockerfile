# Étape 1 : compilation Angular
FROM node:24-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

RUN npm run build


# Étape 2 : serveur Nginx
FROM nginx:1.28-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/cesizen-front/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
