# Étape 1 : compilation Angular
FROM node:24-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

RUN npm run build


# Étape 2 : serveur Nginx non privilégié
FROM nginxinc/nginx-unprivileged:1.31.3-alpine3.24

COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf

COPY --chown=nginx:nginx \
    --from=build \
    /app/dist/cesizen-front/browser \
    /usr/share/nginx/html

USER nginx

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
