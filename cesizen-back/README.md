# CESIZEN Backend

Backend Spring Boot 3.4 / Java 21 de l'application CESIZEN.

## Stack

- Spring Boot 3.4
- Java 21
- Spring Security
- Spring Data JPA
- Liquibase
- MariaDB
- JWT
- Spring Mail
- Springdoc OpenAPI
- Maven

---

## Pré-requis

- Java 21
- Maven ou Maven Wrapper
- Docker, si MariaDB est lancée en conteneur
- MariaDB 11
- Un frontend CESIZEN configuré pour appeler l'API backend

---

## Configuration

### Variables d'environnement production

Le backend attend au minimum :

DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
MAIL_USERNAME
MAIL_PASSWORD
FRONTEND_URL
Variables optionnelles :



SERVER_PORT
JWT_EXPIRATION_MS
JWT_REFRESH_EXPIRATION_DAYS
RESET_PASSWORD_EXPIRATION_MINUTES
APP_COOKIES_SECURE
MAIL_HOST
MAIL_PORT
Exemple :


DB_URL=jdbc:mariadb://mariadb:3306/cesizen
DB_USERNAME=cesizen_user
DB_PASSWORD=un_mot_de_passe_fort
JWT_SECRET=base64-secret
MAIL_USERNAME=example@gmail.com
MAIL_PASSWORD=app-password
FRONTEND_URL=https://cesizen.example.com
APP_COOKIES_SECURE=true
En production, APP_COOKIES_SECURE doit être à true si l'application est derrière HTTPS.

Configuration locale dev
Le profil dev utilise :

src/main/resources/application-dev.properties
Configuration dev recommandée :

properties


spring.datasource.url=jdbc:mariadb://localhost:3310/cesizen
spring.datasource.username=cesizen_user
spring.datasource.password=un_mot_de_passe_fort
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

server.port=8081

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true

jwt.secret=ta_clé_base64_de_dev

app.cookies.secure=false

spring.mail.username=dev@cesizen.local
spring.mail.password=dev-password

app.frontend-url=http://localhost:4200
Base de données locale avec Docker
Exemple de conteneur MariaDB attendu :

cesizen-db
mariadb:11
localhost:3310 -> container:3306
Pour entrer dans MariaDB :

bash


docker exec -it cesizen-db mariadb -u root -p
Créer ou réinitialiser la base :

sql


DROP DATABASE IF EXISTS cesizen;
CREATE DATABASE cesizen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'cesizen_user'@'%' IDENTIFIED BY 'un_mot_de_passe_fort';
GRANT ALL PRIVILEGES ON cesizen.* TO 'cesizen_user'@'%';
FLUSH PRIVILEGES;
Vérifier :

sql


USE cesizen;
SHOW TABLES;
Lancer le backend en local
Depuis le dossier backend :

bash


cd cesizen-back
mvn spring-boot:run -Dspring-boot.run.profiles=dev
L'API démarre en dev sur :

http://localhost:8081
OpenAPI / Swagger
En profil dev, Swagger est disponible ici :

http://localhost:8081/swagger-ui/index.html
ou :

http://localhost:8081/swagger-ui.html
En production, Swagger est désactivé par défaut.

Tests
Lancer tous les tests :

bash


cd cesizen-back
mvn test
Certains tests d'intégration ou tests avec mocks peuvent être désactivés temporairement selon l'état de l'infrastructure de test.

Pour un test ciblé :

bash


mvn -Dtest=JwtServiceTest test
Migrations Liquibase
Les migrations sont dans :

src/main/resources/db/changelog

Le fichier principal est :

src/main/resources/db/changelog/db.changelog-master.yaml
Liquibase est exécuté automatiquement au démarrage.

En développement, si les anciens changesets ont été modifiés, il est recommandé de recréer la base locale :

sql


DROP DATABASE IF EXISTS cesizen;
CREATE DATABASE cesizen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Création du premier administrateur
Le projet ne contient pas de seed utilisateur/admin.

Pour créer un administrateur proprement :

Créer un utilisateur via l'inscription normale du frontend ou via l'API.
Passer cet utilisateur en admin directement en base.
Exemple :

sql


USE cesizen;

UPDATE users
SET roleID = (SELECT roleID FROM role WHERE roleName = 'ADMIN')
WHERE email = 'admin@cesizen.local';

SELECT u.userID, u.email, u.pseudo, r.roleName, u.active
FROM users u
JOIN role r ON r.roleID = u.roleID
WHERE u.email = 'admin@cesizen.local';
Cette méthode permet de laisser le backend générer le hash du mot de passe lors de l'inscription.

Authentification
Le système d'authentification utilise :

un access token JWT retourné dans le corps de la réponse
un refresh token stocké dans un cookie HttpOnly
un cookie XSRF-TOKEN
un header X-XSRF-TOKEN pour les actions liées au refresh/logout
Endpoints :

POST /auth/login
POST /auth/refresh
POST /auth/logout
POST /auth/reset-password/request
POST /auth/reset-password/confirm
La demande de reset password retourne toujours une réponse générique afin d'éviter l'énumération d'emails.

Endpoints utilisateur

POST   /api/users/register
GET    /api/users/me
PUT    /api/users/me
PUT    /api/users/me/password
DELETE /api/users/me
Un utilisateur peut supprimer son propre compte avec :

DELETE /api/users/me
Informations
Les informations peuvent être de trois types :

ARTICLE
VIDEO
PDF
Chaque type possède une validation métier :

ARTICLE nécessite content
VIDEO nécessite videoUrl
PDF nécessite pdfUrl
Routes publiques de lecture :

GET /api/information
GET /api/information/search
GET /api/information/{id}
Routes authentifiées :

POST   /api/information
PUT    /api/information/{id}
DELETE /api/information/{id}
Règles métier :

un utilisateur connecté peut créer une information
un utilisateur peut modifier ou supprimer uniquement ses propres informations
un administrateur peut modifier ou supprimer toutes les informations
Routes admin :

GET    /api/admin/information
GET    /api/admin/information/filter
GET    /api/admin/information/search
GET    /api/admin/information/{id}
POST   /api/admin/information
PUT    /api/admin/information/{id}
DELETE /api/admin/information/{id}
Catégories
Lecture publique :

GET /api/category
GET /api/category/{id}
Administration :

POST   /api/admin/category
PUT    /api/admin/category/{id}
DELETE /api/admin/category/{id}
Une catégorie utilisée par une information ne peut pas être supprimée.

Diagnostic
Les événements de diagnostic sont publics en lecture :

GET /api/diagnostic/events
Les actions utilisateur nécessitent une authentification :

POST /api/diagnostic/submit
GET  /api/diagnostic/history/me
Le userId n'est pas envoyé par le frontend.
Il est récupéré depuis le JWT de l'utilisateur connecté.

Conseils
Lecture publique :

GET /api/advice/{level}
Administration :

GET    /api/admin/advice
GET    /api/admin/advice/{id}
POST   /api/admin/advice
PUT    /api/admin/advice/{id}
DELETE /api/admin/advice/{id}
Logs
Les logs sont réservés aux administrateurs.

GET /api/admin/logs
GET /api/admin/logs/user/{userId}
La création de logs se fait côté backend, par exemple lors d'un diagnostic complété.

Sécurité
Principes appliqués :

API stateless
authentification JWT
refresh token en cookie HttpOnly
protection CSRF sur refresh/logout via XSRF-TOKEN
routes admin protégées par rôle ADMIN
propriétaire vérifié pour les informations utilisateur
historique diagnostic limité à l'utilisateur connecté
suppression de compte limitée à l'utilisateur connecté
reset password sans énumération d'emails
Bonnes pratiques
Ne pas committer de fichier .env
Ne pas stocker de mot de passe en clair
Garder JWT_SECRET, MAIL_PASSWORD et les mots de passe DB hors du dépôt
Utiliser APP_COOKIES_SECURE=true en HTTPS
Ne pas modifier un changeset Liquibase déjà appliqué en environnement partagé
Créer un nouveau changeset pour toute évolution de schéma