# Plan de déploiement de CESIZen

## 1. Objectif

Ce document décrit l’architecture, les environnements, les ressources et les procédures nécessaires au déploiement externalisé de CESIZen.

Il doit permettre à une personne extérieure au développement de :

- préparer l’infrastructure ;
- configurer les secrets ;
- déployer l’application ;
- vérifier son fonctionnement ;
- superviser les services ;
- sauvegarder et restaurer les données ;
- revenir à une version stable en cas d’échec.

## 2. Présentation de la solution

CESIZen est composé des éléments suivants :

| Composant | Technologie | Fonction |
|---|---|---|
| Frontend | Angular | Interface utilisateur |
| Backend | Spring Boot et Java 21 | API REST et logique métier |
| Base de données | MariaDB | Persistance des données |
| Migrations | Liquibase | Versioning du schéma |
| Conteneurisation | Docker et Docker Compose | Construction et exécution |
| CI/CD | GitHub Actions | Tests, construction et contrôles |
| Métriques | Prometheus et cAdvisor | Supervision technique |
| Visualisation | Grafana OSS | Tableaux de bord et alertes |
| Logs | Alloy et Loki | Centralisation des journaux |
| Alertes | Alertmanager et Grafana Alerting | Détection et notification |
| Honeypot | OpenCanary | Détection d’interactions suspectes |
| Sécurité | Trivy et OWASP ZAP | Analyses statiques, images et application |

## 3. Architecture générale

```text
                        Internet
                            |
                         HTTPS
                            |
                    Reverse proxy TLS
                            |
                +-----------+-----------+
                |                       |
          Frontend Angular         Backend Spring Boot
                                        |
                                  Réseau privé Docker
                                        |
                                     MariaDB

Supervision :
cAdvisor ------> Prometheus ------> Grafana ------> Alertmanager
Docker logs ---> Alloy -----------> Loki ---------> Grafana

Détection :
OpenCanary ---> Docker logs ------> Alloy ---> Loki ---> Grafana ---> Gmail
```

En production :

- seul le reverse proxy est exposé publiquement ;
- MariaDB reste sur un réseau privé ;
- Prometheus, Loki, Grafana et Alertmanager ne sont pas exposés directement à Internet ;
- OpenCanary utilise un réseau distinct et ne contient aucune donnée réelle ;
- les secrets sont injectés au démarrage.

## 4. Environnement de développement

### 4.1 Objectif

L’environnement de développement permet de modifier, construire et tester l’application sur le poste du développeur.

### 4.2 Ressources proposées

| Ressource | Minimum |
|---|---|
| Processeur | 4 cœurs |
| Mémoire | 8 Go |
| Stockage disponible | 20 Go |
| Système | Windows, Linux ou macOS |
| Conteneurisation | Docker Desktop ou Docker Engine |
| Versioning | Git |
| Backend | Java 21 et Maven |
| Frontend | Node.js et npm |
| Navigateur | Version récente |

Seuls les composants nécessaires sont démarrés afin de limiter la consommation de ressources.

### 4.3 Configuration

Le développeur utilise :

- des données fictives ;
- des secrets locaux ;
- des mots de passe sans valeur en production ;
- des ports accessibles uniquement depuis le poste ;
- les fichiers `.env` exclus du dépôt.

Exemples de ports locaux :

| Service | Port hôte |
|---|---:|
| Frontend | 4200 |
| Backend | 8081 |
| MariaDB | 3308 |
| Grafana | 3000 |
| Prometheus | 9090 |
| Alertmanager | 9093 |
| Loki | 3100 |
| cAdvisor | 8082 |
| OpenCanary HTTP | 8088 |
| OpenCanary SSH | 2222 |
| OpenCanary FTP | 2121 |

Ces ports peuvent être adaptés s’ils sont déjà utilisés.

### 4.4 Démarrage

L’application et les services de supervision sont séparés afin de pouvoir les démarrer indépendamment.

```bash
docker compose up -d --build
```

```bash
docker compose \
  --env-file .env.monitoring \
  -f compose.monitoring.yml \
  up -d
```

```bash
docker compose \
  -f compose.honeypot.yml \
  up -d --build
```

### 4.5 Validation

Le développeur vérifie :

- l’état des conteneurs ;
- le healthcheck du backend et de MariaDB ;
- l’accès au frontend ;
- l’accès à l’API ;
- l’application des migrations Liquibase ;
- la disponibilité des cibles Prometheus ;
- la consultation des logs dans Grafana ;
- la détection d’une interaction OpenCanary.

## 5. Environnement de test ou préproduction

### 5.1 Objectif

L’environnement de test reproduit autant que possible les caractéristiques de la production sans contenir de données personnelles réelles.

Il permet de valider :

- les fonctionnalités ;
- les migrations ;
- les images Docker ;
- les performances élémentaires ;
- les contrôles de sécurité ;
- les procédures de sauvegarde ;
- le retour arrière ;
- la supervision.

### 5.2 Ressources proposées

Pour un projet de la taille de CESIZen :

| Ressource | Proposition |
|---|---|
| Serveur | Une machine virtuelle Linux |
| Processeur | 2 à 4 vCPU |
| Mémoire | 8 Go |
| Stockage | 40 Go SSD |
| Système | Distribution Linux stable |
| Conteneurisation | Docker Engine et Docker Compose |
| Accès | SSH par clé, limité aux personnes autorisées |
| DNS | Sous-domaine de test |
| TLS | Certificat valide |

Pour une démonstration pédagogique, l’environnement local Docker actuellement configuré tient lieu d’environnement déployé et démontrable.

### 5.3 Données

L’environnement utilise :

- des utilisateurs fictifs ;
- des adresses de test ;
- des diagnostics fictifs ;
- aucun export de production non anonymisé ;
- des secrets distincts du développement et de la production.

### 5.4 Déploiement

Le déploiement est déclenché après réussite de la CI sur `develop`.

Étapes :

1. récupérer la version validée ;
2. charger ou construire les images Docker ;
3. injecter les secrets de test ;
4. sauvegarder la base existante ;
5. démarrer MariaDB ;
6. démarrer le backend et appliquer Liquibase ;
7. vérifier le healthcheck ;
8. démarrer le frontend ;
9. démarrer la supervision ;
10. exécuter les tests fonctionnels ;
11. exécuter OWASP ZAP ;
12. vérifier Grafana, Loki et les alertes ;
13. enregistrer le résultat du déploiement.

## 6. Environnement de production

### 6.1 Objectif

La production fournit le service réel avec des mesures renforcées de sécurité, de disponibilité et de sauvegarde.

### 6.2 Ressources initiales proposées

Pour une charge limitée et un lancement progressif :

| Ressource | Proposition initiale |
|---|---|
| Serveur applicatif | 4 vCPU, 8 Go de RAM |
| Stockage système | 30 Go SSD |
| Stockage MariaDB | 30 à 50 Go SSD extensibles |
| Stockage supervision | 20 à 40 Go selon la rétention |
| Sauvegardes | Support distinct et chiffré |
| Système | Linux stable et maintenu |
| Accès | SSH par clé et pare-feu |
| Réseau | Adresse publique uniquement pour le reverse proxy |
| DNS | Nom de domaine dédié |
| TLS | Certificat automatisé et renouvelé |

Ces ressources constituent un dimensionnement initial. Elles sont réévaluées à partir des métriques réelles :

- CPU ;
- mémoire ;
- espace disque ;
- taille de la base ;
- temps de réponse ;
- nombre de requêtes ;
- volume des logs ;
- nombre d’utilisateurs simultanés.

### 6.3 Séparation logique

Même sur un serveur unique, les composants utilisent des réseaux Docker séparés :

- réseau public du reverse proxy ;
- réseau applicatif frontend/backend ;
- réseau privé backend/MariaDB ;
- réseau de supervision ;
- réseau dédié au honeypot.

MariaDB, Prometheus, Loki et Alertmanager ne publient aucun port public.

### 6.4 Durcissement

La production impose :

- HTTPS ;
- secrets robustes et distincts ;
- cookies sécurisés ;
- pare-feu ;
- accès SSH par clé ;
- compte administrateur nominatif ;
- principe du moindre privilège ;
- conteneurs non privilégiés ;
- images analysées ;
- sauvegardes chiffrées ;
- mises à jour de sécurité ;
- accès restreint à Grafana ;
- durées de conservation configurées ;
- notifications d’alerte vérifiées.

## 7. Ressources logicielles

| Outil | Usage | Licence ou coût |
|---|---|---|
| Git | Versioning | Gratuit |
| GitHub | Dépôt et tickets | Offre gratuite adaptée à la démonstration |
| GitHub Actions | CI/CD | Quota gratuit selon le dépôt |
| Docker | Conteneurisation | Gratuit selon les conditions applicables |
| Java 21 | Backend | OpenJDK |
| Maven | Construction backend | Gratuit |
| Node.js et npm | Construction frontend | Gratuit |
| MariaDB | Base de données | Open source |
| Grafana OSS | Visualisation | Open source |
| Prometheus | Métriques | Open source |
| Alertmanager | Alertes | Open source |
| Loki | Logs | Open source |
| Alloy | Collecte | Open source |
| cAdvisor | Métriques Docker | Open source |
| OpenCanary | Honeypot | Open source |
| Trivy | Analyse | Open source |
| OWASP ZAP | Test dynamique | Open source |

Le plan privilégie les solutions gratuites et auto-hébergées.

## 8. Versioning des sources et de la documentation

### 8.1 Branches

```text
main       versions stables
develop    intégration
feat/*     évolutions
fix/*      corrections
security/* sécurité
docs/*     documentation
```

### 8.2 Pull requests

Les modifications sont intégrées par pull request avec :

- ticket associé ;
- description ;
- procédure de test ;
- impact sécurité et RGPD ;
- résultat de la CI ;
- procédure de retour arrière si nécessaire.

### 8.3 Documentation

La documentation est versionnée dans `docs/` avec les sources.

Une modification nécessitant une évolution documentaire inclut cette évolution dans la même pull request ou dans un ticket lié.

### 8.4 Versions

Les versions stables utilisent le versionnement sémantique :

```text
MAJEURE.MINEURE.CORRECTIF
```

Exemples :

```text
1.0.0
1.1.0
1.1.1
```

- `MAJEURE` : rupture de compatibilité ;
- `MINEURE` : fonctionnalité compatible ;
- `CORRECTIF` : correction compatible.

Chaque production est associée à un tag Git :

```text
v1.0.0
```

Les images Docker reçoivent le même tag et ne reposent pas uniquement sur `latest`.

## 9. Intégration continue

GitHub Actions s’exécute lors :

- d’un push sur `develop` ou `main` ;
- d’une pull request vers ces branches ;
- d’un déclenchement manuel lorsque prévu.

La CI réalise notamment :

1. récupération des sources ;
2. installation de Java 21 ;
3. compilation et tests Maven ;
4. installation reproductible avec `npm ci` ;
5. construction Angular ;
6. analyse Trivy du dépôt ;
7. construction et analyse des images ;
8. analyse de sécurité dynamique ZAP selon le workflow configuré ;
9. publication du résultat dans GitHub Actions.

Une pull request n’est pas fusionnée lorsqu’un contrôle obligatoire échoue sans analyse et validation explicites.

## 10. Déploiement continu

Pour la démonstration, la construction, les tests et les analyses sont automatisés. Le déploiement final reste manuel afin de conserver un contrôle avant modification de l’environnement.

Une évolution future peut automatiser le déploiement après :

- réussite de la CI ;
- validation manuelle d’un environnement GitHub ;
- sauvegarde de la base ;
- disponibilité de la version précédente ;
- validation du responsable.

Cette approche correspond à une livraison continue avec approbation avant production.

## 11. Préparation d’un serveur externe

La personne responsable prépare une machine Linux :

1. appliquer les mises à jour ;
2. créer un compte de déploiement non-root ;
3. configurer l’accès SSH par clé ;
4. désactiver l’authentification SSH par mot de passe si possible ;
5. configurer le pare-feu ;
6. installer Docker Engine ;
7. installer Docker Compose ;
8. créer les répertoires applicatifs ;
9. configurer le DNS ;
10. installer le reverse proxy ;
11. obtenir un certificat TLS ;
12. préparer les volumes de données ;
13. préparer l’emplacement des sauvegardes ;
14. injecter les secrets avec des permissions restrictives ;
15. vérifier l’espace disque et la synchronisation horaire.

## 12. Ports de production

Les ports publics recommandés sont :

| Port | Usage |
|---:|---|
| 80 | Redirection vers HTTPS |
| 443 | Application HTTPS |
| 22 | Administration SSH, limitée par pare-feu |

Les ports suivants restent internes :

- MariaDB `3306` ;
- backend `8080` ;
- Prometheus `9090` ;
- Loki `3100` ;
- Alertmanager `9093` ;
- cAdvisor `8080` ;
- Grafana `3000`, sauf accès administratif protégé.

## 13. Secrets de production

Les valeurs suivantes doivent être définies avant le déploiement :

- mot de passe MariaDB ;
- mot de passe root MariaDB ;
- clé JWT ;
- identifiants SMTP ;
- mot de passe d’application SMTP ;
- mot de passe administrateur Grafana ;
- paramètres de cookies ;
- URL publique du frontend ;
- URL interne de la base ;
- paramètres de rétention ;
- éventuels identifiants du registre Docker.

Les secrets :

- ne sont pas présents dans Git ;
- sont différents des secrets locaux ;
- utilisent des valeurs aléatoires ;
- sont accessibles uniquement au service concerné ;
- sont sauvegardés séparément si leur récupération est nécessaire ;
- disposent d’une procédure de rotation.

## 14. Procédure de déploiement

### 14.1 Avant le déploiement

1. vérifier que la CI est réussie ;
2. vérifier les résultats Trivy et ZAP ;
3. identifier le tag à déployer ;
4. lire les notes de version ;
5. vérifier les migrations Liquibase ;
6. annoncer l’intervention si elle peut avoir un impact ;
7. vérifier les ressources du serveur ;
8. créer une sauvegarde ;
9. vérifier l’intégrité de la sauvegarde ;
10. confirmer la disponibilité de la version précédente.

### 14.2 Déploiement

1. récupérer les fichiers de déploiement ;
2. récupérer les images portant le tag validé ;
3. vérifier la configuration Compose ;
4. charger les secrets de l’environnement ;
5. démarrer ou vérifier MariaDB ;
6. démarrer le backend ;
7. surveiller l’exécution de Liquibase ;
8. attendre le healthcheck du backend ;
9. démarrer le frontend ;
10. démarrer les composants de supervision ;
11. vérifier le reverse proxy et le certificat ;
12. enregistrer l’heure et la version déployée.

### 14.3 Contrôles après déploiement

Vérifier :

- l’accès HTTPS ;
- l’absence d’erreur de certificat ;
- le chargement du frontend ;
- la disponibilité de l’API ;
- l’inscription et la connexion avec un compte de test ;
- les autorisations utilisateur et administrateur ;
- le fonctionnement d’un diagnostic ;
- la connexion MariaDB ;
- l’état Liquibase ;
- les healthchecks ;
- les cibles Prometheus ;
- les tableaux de bord Grafana ;
- les logs Loki ;
- les alertes ;
- l’espace disque ;
- l’absence d’erreurs répétées.

## 15. Sauvegarde

La base est sauvegardée :

- avant chaque migration importante ;
- automatiquement selon une planification ;
- avec un nom horodaté ;
- sous forme compressée ;
- avec une somme SHA-256 ;
- sur un emplacement distinct ;
- avec chiffrement lors d’une externalisation ;
- avec une politique de rotation.

Un test de restauration périodique utilise une base temporaire et contrôle :

- la validité du fichier compressé ;
- la présence des tables ;
- l’historique Liquibase ;
- la cohérence générale.

## 16. Retour arrière

Le retour arrière est déclenché si :

- le backend ne devient pas sain ;
- une migration échoue ;
- une fonction essentielle est indisponible ;
- une vulnérabilité critique est introduite ;
- les erreurs augmentent fortement ;
- l’intégrité des données est menacée.

Procédure :

1. arrêter le déploiement ;
2. préserver les logs ;
3. arrêter les composants défectueux ;
4. restaurer les images précédentes ;
5. restaurer la base uniquement si la migration l’exige ;
6. redémarrer les services ;
7. exécuter les contrôles fonctionnels ;
8. vérifier la supervision ;
9. ouvrir un ticket d’incident ;
10. documenter les actions réalisées.

Une restauration de base ne doit pas être réalisée automatiquement sans analyse, car elle peut supprimer les données créées depuis la sauvegarde.

## 17. Supervision après déploiement

La surveillance renforcée dure au minimum pendant la période qui suit immédiatement le déploiement.

Les éléments suivis sont :

- disponibilité ;
- consommation CPU ;
- mémoire ;
- espace disque ;
- redémarrages de conteneurs ;
- erreurs backend ;
- temps de réponse ;
- connexions à la base ;
- volume des logs ;
- alertes de sécurité ;
- interactions OpenCanary.

Les alertes critiques sont transmises par le point de contact configuré.

## 18. Maintenance

### Quotidienne ou automatisée

- healthchecks ;
- métriques ;
- alertes ;
- erreurs applicatives ;
- état des sauvegardes.

### Hebdomadaire

- tickets critiques ;
- Dependabot ;
- Trivy ;
- capacité disque ;
- journaux de sécurité.

### Mensuelle

- mises à jour ;
- test de restauration ;
- revue des comptes ;
- revue des alertes ;
- état des certificats ;
- dépendances obsolètes.

### Trimestrielle

- registre des risques ;
- droits d’accès ;
- durées de conservation ;
- plan de reprise ;
- veille technologique ;
- revue RGPD.

## 19. Responsabilités

| Rôle | Responsabilités |
|---|---|
| Développeur | Code, tests, migrations et documentation |
| Responsable du déploiement | Préparation, secrets, sauvegarde, déploiement et retour arrière |
| Responsable de sécurité | Qualification des risques et suivi des incidents |
| Responsable de traitement | Décisions relatives aux données personnelles |
| Exploitant | Supervision, sauvegardes et maintien opérationnel |

Dans le cadre individuel du projet, une même personne peut assumer plusieurs rôles, mais les responsabilités restent documentées séparément.

## 20. Preuves de démonstration

Les éléments démontrables comprennent :

- historique Git ;
- branches et commits conventionnels ;
- modèles GitHub Issues ;
- tableau GitHub Projects ;
- workflows GitHub Actions ;
- rapports Trivy ;
- analyse ZAP ;
- construction Docker ;
- sauvegarde MariaDB ;
- restauration dans une base temporaire ;
- métriques Prometheus ;
- dashboard Grafana ;
- alertes Alertmanager ;
- logs Loki ;
- collecte Alloy ;
- événement OpenCanary ;
- alerte Grafana ;
- réception d’une notification Gmail.

## 21. Critères de réussite

Le déploiement est considéré comme réussi lorsque :

- tous les services requis sont démarrés ;
- les healthchecks sont au vert ;
- les migrations sont appliquées ;
- les tests fonctionnels sont concluants ;
- le HTTPS fonctionne ;
- aucun secret n’est exposé ;
- les métriques et logs sont disponibles ;
- les alertes sont opérationnelles ;
- une sauvegarde exploitable existe ;
- la version déployée est tracée ;
- la procédure de retour arrière est connue.
