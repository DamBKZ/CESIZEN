# Registre des risques de sécurité

## 1. Objectif

Ce registre identifie les principaux risques susceptibles d’affecter CESIZen et décrit les mesures permettant de les prévenir, de les détecter et d’en limiter les conséquences.

Il doit être réévalué après une évolution importante, un incident de sécurité ou l’identification d’une nouvelle menace.

## 2. Méthode d’évaluation

Chaque risque est évalué selon deux critères :

### Probabilité

| Valeur | Niveau | Description |
|---:|---|---|
| 1 | Faible | Événement peu probable |
| 2 | Moyenne | Événement possible |
| 3 | Élevée | Événement probable |

### Impact

| Valeur | Niveau | Description |
|---:|---|---|
| 1 | Faible | Effet limité et rapidement réversible |
| 2 | Moyen | Dégradation significative ou exposition limitée |
| 3 | Élevé | Indisponibilité, compromission ou violation de données |

La criticité est calculée avec la formule :

```text
Criticité = Probabilité × Impact
```

| Score | Criticité |
|---:|---|
| 1 à 2 | Faible |
| 3 à 4 | Moyenne |
| 6 | Haute |
| 9 | Critique |

## 3. Risques identifiés

| ID | Risque | Actif concerné | P | I | Score | Mesures principales |
|---|---|---|---:|---:|---:|---|
| R01 | Compromission d’un compte | Comptes utilisateurs et administrateurs | 2 | 3 | 6 | Hachage des mots de passe, contrôle d’accès, JWT, révocation des tokens, journalisation |
| R02 | Contournement des autorisations | API et fonctions d’administration | 2 | 3 | 6 | Spring Security, contrôle côté backend, tests de sécurité, principe du moindre privilège |
| R03 | Injection dans les entrées utilisateur | API et base MariaDB | 2 | 3 | 6 | Validation des DTO, requêtes JPA paramétrées, tests et analyse ZAP |
| R04 | Vol ou réutilisation d’un token | Sessions utilisateur | 2 | 3 | 6 | Durée de validité limitée, révocation, cookies sécurisés en production, TLS |
| R05 | Exposition d’un secret | Dépôt Git, CI et configuration | 2 | 3 | 6 | Fichiers `.env` ignorés, secrets GitHub, scan Trivy, rotation des secrets |
| R06 | Vulnérabilité d’une dépendance | Backend, frontend et images Docker | 3 | 3 | 9 | Dependabot, Trivy, versions contrôlées, veille et mises à jour |
| R07 | Attaque XSS | Interface Angular et contenus affichés | 2 | 3 | 6 | Échappement Angular, validation, limitation du HTML dynamique, ZAP |
| R08 | Attaque CSRF | Actions authentifiées | 1 | 3 | 3 | Politique adaptée aux JWT/cookies, attribut SameSite, contrôle des origines |
| R09 | Indisponibilité de l’application | Frontend, backend ou base de données | 2 | 3 | 6 | Healthchecks, supervision, alertes, sauvegardes et procédure de restauration |
| R10 | Perte ou corruption de données | Base MariaDB | 2 | 3 | 6 | Sauvegardes compressées, sommes SHA-256, tests de restauration, Liquibase |
| R11 | Accès direct à la base de données | MariaDB | 2 | 3 | 6 | Réseau privé en production, compte dédié, mot de passe fort, absence d’exposition publique |
| R12 | Mauvaise configuration Docker | Conteneurs et hôte | 2 | 3 | 6 | Utilisateurs non privilégiés, système de fichiers en lecture seule, capacités limitées, Trivy |
| R13 | Absence de détection d’une attaque | Ensemble du système | 2 | 3 | 6 | Logs centralisés, Loki, Grafana, Alertmanager, OpenCanary et notifications |
| R14 | Compromission du honeypot | OpenCanary et infrastructure | 2 | 2 | 4 | Réseau dédié, aucune donnée réelle, limites CPU/RAM, capacités minimales, lecture seule |
| R15 | Fuite de données personnelles | Comptes et diagnostics | 2 | 3 | 6 | Minimisation, contrôle d’accès, chiffrement en transit, durées de conservation |
| R16 | Erreur de déploiement ou de migration | Application et base de données | 2 | 3 | 6 | CI, environnement de test, sauvegarde préalable, Liquibase et retour arrière |
| R17 | Compromission de la chaîne CI/CD | GitHub Actions et dépôt | 1 | 3 | 3 | Permissions minimales, actions versionnées, protection des branches, revue des pull requests |
| R18 | Journaux contenant des données sensibles | Loki, Grafana et logs applicatifs | 2 | 3 | 6 | Interdiction des secrets dans les logs, accès restreint, rétention limitée et revue |

## 4. Traitement des risques prioritaires

Les risques critiques ou élevés sont traités en priorité.

### Dépendances vulnérables

- surveillance par Dependabot ;
- analyse Trivy du dépôt et des images ;
- blocage de la CI pour les vulnérabilités corrigibles de niveau élevé ou critique ;
- création d’un ticket et mise à jour prioritaire.

### Compromission de compte ou de token

- révocation des sessions ;
- désactivation temporaire du compte ;
- renouvellement des secrets concernés ;
- analyse des journaux ;
- correction et test de non-régression.

### Perte ou corruption de données

- arrêt des écritures si nécessaire ;
- sauvegarde des éléments disponibles ;
- restauration d’une sauvegarde vérifiée ;
- contrôle des changesets Liquibase ;
- validation de l’intégrité avant remise en service.

### Fuite de données personnelles

- confinement immédiat ;
- identification des données et personnes concernées ;
- information du responsable de traitement ;
- évaluation de la notification à la CNIL et aux personnes concernées ;
- documentation de la décision et des mesures prises.

## 5. Risque résiduel

La mise en place des mesures réduit les risques sans les supprimer totalement.

Un risque résiduel est accepté uniquement lorsque :

- les mesures raisonnables ont été appliquées ;
- l’impact restant est connu ;
- la décision est documentée ;
- des mesures compensatoires existent si nécessaire ;
- une échéance de réévaluation est fixée.

## 6. Révision du registre

Le registre est revu :

- chaque trimestre ;
- après une évolution majeure ;
- après un incident de sécurité ;
- après une nouvelle vulnérabilité importante ;
- avant un déploiement significatif en production.

Chaque révision précise la date, les changements, le responsable et les actions ouvertes.
