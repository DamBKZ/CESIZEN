# CESIZEN Frontend

Frontend Angular 21 de l'application CESIZEN.

## Stack

- Angular 21
- Angular Material
- RxJS
- Cypress pour les tests e2e

## Pré-requis

- Node.js 20+ recommandé
- npm 11+

## Lancer en local

```bash
cd cesizen-front
npm install
npm start
```

L’application est ensuite disponible sur `http://localhost:4200/`.

## Build

```bash
cd cesizen-front
npm run build
```

## Tests unitaires

```bash
cd cesizen-front
npm test
```

## Tests e2e

```bash
cd cesizen-front
npm run cypress:open
```

ou :

```bash
cd cesizen-front
npm run cypress:run
```

## Configuration API

En développement, le frontend passe par le proxy configuré pour joindre le backend.

- Authentification : access token en mémoire
- Refresh token : cookie HttpOnly
- Protection CSRF : cookie `XSRF-TOKEN` + en-tête `X-XSRF-TOKEN`

## Scripts utiles

- `npm start` : serveur de dev
- `npm run build` : build de production
- `npm test` : tests unitaires
- `npm run cypress:open` : interface Cypress
- `npm run cypress:run` : exécution headless Cypress

## Structure rapide

- `src/app/auth` : login, register, reset password
- `src/app/profile` : profil utilisateur et mot de passe
- `src/app/admin` : administration
- `src/app/shared` : composants et services réutilisables

## Bonnes pratiques

- Ne pas stocker de tokens sensibles en `localStorage`.
- Garder les secrets hors du dépôt.
- Vérifier les cookies et l’API backend avant un déploiement public.
