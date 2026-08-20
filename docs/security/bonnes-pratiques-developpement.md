# Structuration des développements et bonnes pratiques

## 1. Objectif

Ce document définit les règles appliquées au développement de CESIZen afin de maintenir un code lisible, testable, sécurisé et déployable.

Ces règles concernent :

- le backend Spring Boot ;
- le frontend Angular ;
- la base de données MariaDB ;
- les fichiers Docker ;
- les workflows GitHub Actions ;
- la documentation ;
- les composants de supervision et de sécurité.

## 2. Organisation du dépôt

Le dépôt est organisé par responsabilité :

```text
CESIZEN/
├── cesizen-back/          # API Spring Boot
├── cesizen-front/         # Interface Angular
├── docs/                  # Documentation technique et procédures
├── honeypot/              # Configuration OpenCanary
├── monitoring/            # Prometheus, Grafana, Loki, Alloy et Alertmanager
├── scripts/               # Sauvegarde, restauration et automatisation
├── .github/               # CI/CD, Dependabot et modèles de tickets
├── compose.yml            # Environnement applicatif local
├── compose.monitoring.yml # Supervision et journalisation
└── compose.honeypot.yml   # Honeypot isolé
```

Chaque composant doit rester dans son périmètre afin d’éviter le mélange des responsabilités.

## 3. Gestion des branches

Les branches principales sont :

- `main` : versions stables destinées à la production ;
- `develop` : intégration des développements validés.

Les branches de travail utilisent les conventions suivantes :

```text
feat/<ticket>-<description>
fix/<ticket>-<description>
security/<ticket>-<description>
docs/<ticket>-<description>
chore/<ticket>-<description>
```

Exemples :

```text
feat/57-export-diagnostic
fix/42-refresh-token-expire
security/61-harden-cookie-policy
docs/74-deployment-plan
```

Une branche est créée depuis `develop`, puis fusionnée au moyen d’une pull request après validation.

## 4. Convention de commits

Les commits suivent la convention Conventional Commits :

```text
<type>(<scope>): <description>
```

Types principaux :

| Type | Usage |
|---|---|
| `feat` | Nouvelle fonctionnalité |
| `fix` | Correction d’une anomalie |
| `security` | Renforcement ou correction de sécurité |
| `docs` | Documentation |
| `test` | Ajout ou modification de tests |
| `refactor` | Réorganisation sans changement fonctionnel |
| `build` | Construction et dépendances |
| `ci` | Intégration continue |
| `ops` | Déploiement, exploitation et supervision |
| `chore` | Maintenance technique |

Exemples :

```text
feat(diagnostic): add PDF export
fix(auth): reject expired refresh tokens
security(front): run Nginx as non-root user
ops(monitoring): add Loki log collection
```

Un commit doit être limité à un objectif cohérent et ne doit contenir aucun secret.

## 5. Pull requests

Toute pull request doit préciser :

- le besoin ou le problème traité ;
- le ticket associé ;
- les modifications principales ;
- la méthode de test ;
- les impacts sur la sécurité et les données personnelles ;
- les migrations éventuelles ;
- les instructions de déploiement ;
- la procédure de retour arrière si nécessaire.

La pull request est fusionnée uniquement lorsque :

- la CI est réussie ;
- les tests sont concluants ;
- les analyses de sécurité sont acceptables ;
- les commentaires de revue sont traités ;
- la documentation est mise à jour ;
- aucun secret ni fichier temporaire n’est présent.

## 6. Bonnes pratiques backend

Le backend respecte une séparation des responsabilités :

- contrôleurs pour les échanges HTTP ;
- services pour la logique métier ;
- repositories pour l’accès aux données ;
- DTO pour les entrées et sorties ;
- mappers pour les transformations ;
- entités pour la persistance ;
- composants de sécurité pour l’authentification et les autorisations.

Les règles principales sont :

- valider les entrées avec Jakarta Validation ;
- ne pas exposer directement les entités JPA ;
- effectuer les contrôles d’autorisation côté backend ;
- utiliser les repositories JPA et des requêtes paramétrées ;
- traiter les erreurs de manière homogène ;
- ne pas renvoyer de détails techniques sensibles ;
- journaliser les événements utiles sans inclure de secret ;
- utiliser les transactions lorsque plusieurs opérations doivent rester atomiques ;
- limiter les dépendances et supprimer celles qui ne sont plus utilisées.

## 7. Bonnes pratiques frontend

Le frontend Angular applique les règles suivantes :

- composants limités à une responsabilité claire ;
- logique d’accès aux API placée dans les services ;
- modèles TypeScript définis explicitement ;
- routes protégées par des guards lorsque nécessaire ;
- absence de confiance dans les contrôles réalisés uniquement côté client ;
- validation ergonomique des formulaires ;
- échappement Angular conservé par défaut ;
- absence d’injection HTML non maîtrisée ;
- messages d’erreur sans information technique sensible ;
- variables d’environnement utilisées pour les URL et paramètres publics ;
- dépendances mises à jour et analysées.

Le frontend améliore l’expérience utilisateur, mais ne remplace jamais les validations et autorisations du backend.

## 8. Validation des entrées

Toute donnée externe est considérée comme non fiable :

- corps de requête ;
- paramètres de route ;
- paramètres de requête ;
- en-têtes ;
- fichiers ;
- données provenant d’un service tiers.

Les contrôles portent notamment sur :

- présence des champs obligatoires ;
- type et format ;
- longueur minimale et maximale ;
- valeurs autorisées ;
- cohérence métier ;
- droits de l’utilisateur ;
- taille des requêtes et fichiers.

Les listes d’autorisation sont privilégiées aux listes d’interdiction.

## 9. Authentification et autorisation

Les règles de sécurité sont :

- mots de passe hachés avec un algorithme adapté ;
- tokens à durée limitée ;
- refresh tokens révocables ;
- autorisations contrôlées côté backend ;
- principe du moindre privilège ;
- séparation des rôles ;
- absence de secret dans le token ;
- déconnexion entraînant la révocation appropriée ;
- refus par défaut lorsqu’aucune autorisation explicite n’est définie ;
- journalisation des événements d’authentification importants.

Une route masquée dans l’interface n’est pas considérée comme protégée tant que le backend ne contrôle pas son accès.

## 10. Gestion des erreurs et des logs

Les réponses d’erreur destinées au client doivent rester compréhensibles sans révéler :

- la pile d’exécution ;
- les requêtes SQL ;
- les chemins internes ;
- les secrets ;
- les détails de configuration ;
- les données d’autres utilisateurs.

Les journaux doivent contenir :

- un horodatage ;
- un niveau adapté ;
- le composant concerné ;
- un message exploitable ;
- un identifiant technique de corrélation si disponible.

Les mots de passe, tokens, secrets SMTP, données médicales éventuelles et informations personnelles non nécessaires sont exclus des logs.

## 11. Base de données et migrations

Toute évolution du schéma est versionnée avec Liquibase.

Les règles suivantes s’appliquent :

- un changeset correspond à une évolution cohérente ;
- un changeset déjà appliqué n’est pas modifié ;
- une nouvelle correction utilise un nouveau changeset ;
- les migrations sont testées dans l’environnement de test ;
- une sauvegarde précède une migration de production ;
- les contraintes d’intégrité sont définies dans la base ;
- le compte applicatif possède uniquement les permissions nécessaires ;
- les scripts contenant des données réelles ne sont pas versionnés.

## 12. Dépendances

Avant d’ajouter une dépendance, il faut vérifier :

- son utilité réelle ;
- son activité et sa maintenance ;
- sa licence ;
- ses vulnérabilités connues ;
- son impact sur l’image Docker ;
- l’existence d’une solution native plus simple.

Les dépendances sont surveillées avec Dependabot et Trivy. Une mise à jour majeure nécessite une étude d’impact et ne doit pas être fusionnée automatiquement.

## 13. Conteneurs Docker

Les images doivent appliquer les mesures suivantes lorsque cela est compatible :

- image de base minimale et maintenue ;
- construction multi-stage ;
- utilisateur non privilégié ;
- capacités Linux supprimées ou limitées ;
- système de fichiers en lecture seule ;
- répertoires temporaires explicitement autorisés ;
- secrets injectés au démarrage et non copiés dans l’image ;
- fichier `.dockerignore` ;
- healthcheck adapté ;
- limites de ressources ;
- versions d’images contrôlées ;
- analyse Trivy.

Les ports des services internes ne doivent pas être exposés publiquement sans nécessité.

## 14. Configuration et secrets

Les valeurs variant selon l’environnement sont externalisées :

- URL de base de données ;
- identifiants ;
- clé JWT ;
- configuration SMTP ;
- URL du frontend ;
- options de cookies ;
- ports ;
- niveau de journalisation.

Les fichiers secrets locaux sont ignorés par Git. Les fichiers d’exemple contiennent uniquement des valeurs factices.

## 15. Tests

La stratégie de tests comprend selon le besoin :

- tests unitaires ;
- tests de services ;
- tests de contrôleurs ;
- tests d’intégration ;
- tests de repositories ;
- tests frontend ;
- tests de non-régression ;
- tests des migrations ;
- tests de construction Docker ;
- analyse dynamique OWASP ZAP.

Un correctif doit inclure un test reproduisant l’anomalie lorsque cela est techniquement pertinent.

## 16. Intégration continue

La CI vérifie notamment :

- la compilation du backend ;
- les tests Maven ;
- l’installation reproductible des dépendances frontend ;
- la construction Angular ;
- les dépendances vulnérables ;
- les mauvaises configurations ;
- les images Docker ;
- les analyses de sécurité prévues.

Une modification ne doit pas être fusionnée si un contrôle obligatoire échoue sans justification et validation explicites.

## 17. Revue de sécurité

Une revue spécifique est réalisée pour les changements touchant :

- l’authentification ;
- les autorisations ;
- les tokens ;
- les données personnelles ;
- les fichiers téléversés ;
- les dépendances ;
- la configuration réseau ;
- les secrets ;
- les migrations ;
- les journaux ;
- les outils de supervision.

La revue vérifie les risques, les mesures de protection, les tests et le retour arrière.

## 18. Définition de terminé

Une tâche est terminée lorsque :

- le besoin et les critères d’acceptation sont satisfaits ;
- le code est lisible et structuré ;
- les tests nécessaires existent et réussissent ;
- la CI est réussie ;
- les contrôles de sécurité sont acceptables ;
- les impacts RGPD sont traités ;
- les migrations sont versionnées ;
- la documentation est à jour ;
- le déploiement est vérifié ;
- le ticket et la pull request sont liés ;
- aucun secret ni fichier temporaire n’est ajouté au dépôt.
