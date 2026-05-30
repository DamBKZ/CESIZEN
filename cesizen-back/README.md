# CESIZEN Backend

Backend Spring Boot 3.4 / Java 21 de l'application CESIZEN.

## Stack

- Spring Boot
- Spring Security
- Spring Data JPA
- Liquibase
- MariaDB
- JWT
- Mail
- Springdoc OpenAPI

## Pré-requis

- Java 21
- Maven Wrapper (`./mvnw`)
- MariaDB
- Variables d'environnement définies avant lancement

## Variables d'environnement

Le backend attend au minimum :

- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_ROOT_PASSWORD`
- `JWT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `FRONTEND_URL`

En production, activer les cookies sécurisés avec :

- `app.cookies.secure=true`

## Lancer en local

```bash
cd cesizen-back
./mvnw spring-boot:run
```

Avec une base MariaDB déjà disponible, tu peux aussi lancer le jar :

```bash
cd cesizen-back
./mvnw clean package -DskipTests
java -Dspring.datasource.url="jdbc:mariadb://localhost:3306/cesizen" \
     -DDB_USERNAME=cesizen_user -DDB_PASSWORD=un_mot_de_passe_fort \
     -DMAIL_USERNAME=test@local -DMAIL_PASSWORD=testpass \
     -Djwt.secret="ta-cle-jwt" \
     -jar target/cesizen-back-0.0.1-SNAPSHOT.jar
```

## Tests

```bash
cd cesizen-back
./mvnw test
```

Pour un test ciblé :

```bash
cd cesizen-back
./mvnw -Dtest=AuthControllerUnitTest test
```

## Authentification

- L’access token est renvoyé dans le corps de la réponse.
- Le refresh token est stocké dans un cookie HttpOnly.
- Le client envoie un cookie `XSRF-TOKEN` et l’en-tête `X-XSRF-TOKEN` pour les actions sensibles.

## Endpoints principaux

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `POST /auth/reset-password/request`
- `POST /auth/reset-password/confirm`
- `GET /api/users/me`
- `PUT /api/users/me`
- `PUT /api/users/me/password`
- `DELETE /api/users/me`

## Base de données

Les migrations sont gérées par Liquibase dans `src/main/resources/db/changelog`.

## OpenAPI

Quand l’application est lancée, la documentation Swagger est disponible via Springdoc.

## Bonnes pratiques

- Ne pas committer de fichier `.env`.
- Garder `JWT_SECRET`, `MAIL_PASSWORD` et les mots de passe DB hors du dépôt.
- Activer `app.cookies.secure=true` dès que l’application passe derrière HTTPS.
