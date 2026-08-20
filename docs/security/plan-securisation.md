# Plan de sécurisation de CESIZen

## 1. Objectif

Le plan de sécurisation définit les mesures permettant de protéger CESIZen contre les accès non autorisés, les vulnérabilités, les pertes de données et les interruptions de service.

Il couvre :

- la prévention ;
- la détection ;
- la réponse aux incidents ;
- la protection des données personnelles ;
- la continuité et la restauration ;
- l’amélioration continue.

## 2. Architecture de sécurité

CESIZen repose sur plusieurs composants complémentaires :

```text
Utilisateur
    |
   HTTPS
    |
Reverse proxy
    |
    +---- Frontend Angular
    |
    +---- Backend Spring Boot
               |
               +---- MariaDB

Conteneurs Docker
    |
    +---- Alloy ---- Loki ---- Grafana ---- Notification Gmail
    |
    +---- cAdvisor ---- Prometheus ---- Alertmanager
    |
    +---- OpenCanary sur un réseau dédié
```

Les environnements de développement, de test et de production utilisent des configurations et secrets distincts.

## 3. Protection de l’application

### Backend

Le backend utilise :

- Spring Security ;
- authentification par JWT ;
- contrôle des rôles et autorisations ;
- validation des entrées avec Jakarta Validation ;
- accès aux données avec JPA ;
- requêtes paramétrées ;
- gestion centralisée des erreurs ;
- journalisation des événements importants.

Les contrôles d’autorisation sont réalisés côté serveur. Les restrictions du frontend ne constituent pas une mesure de sécurité suffisante.

### Frontend

Le frontend Angular applique :

- l’échappement automatique des contenus ;
- la validation des formulaires ;
- la protection des routes ;
- la limitation du HTML dynamique ;
- la séparation des composants et des services ;
- l’absence de secret dans le code distribué.

### Base de données

MariaDB utilise :

- un compte applicatif dédié ;
- des identifiants externalisés ;
- des permissions limitées ;
- des migrations versionnées avec Liquibase ;
- un volume persistant ;
- des sauvegardes et restaurations testées.

En production, la base ne doit pas être publiée sur Internet.

## 4. Authentification et sessions

Les mesures prévues comprennent :

- mots de passe hachés avec un algorithme adapté ;
- access tokens à durée de vie courte ;
- refresh tokens limités et révocables ;
- invalidation des tokens expirés ou utilisés ;
- cookies `Secure`, `HttpOnly` et `SameSite` en production lorsque les tokens sont transportés par cookie ;
- transmission uniquement par HTTPS ;
- révocation des sessions après compromission ou déconnexion ;
- journalisation des événements d’authentification pertinents.

Les clés JWT sont distinctes selon les environnements et exclues du dépôt.

## 5. Chiffrement et secrets

Les échanges externes utilisent TLS en production.

Les secrets sont :

- exclus de Git ;
- stockés dans les fichiers `.env` locaux ignorés ;
- injectés par les secrets GitHub dans la CI ;
- distincts selon les environnements ;
- renouvelés après une exposition ou un incident ;
- masqués dans les journaux et captures.

Les sauvegardes externalisées doivent être chiffrées. Les sommes SHA-256 servent uniquement au contrôle d’intégrité.

## 6. Sécurité des conteneurs

Les conteneurs appliquent, selon leurs contraintes :

- des images de base minimales ;
- des utilisateurs non privilégiés ;
- le principe du moindre privilège ;
- la suppression des capacités Linux inutiles ;
- un système de fichiers en lecture seule ;
- des répertoires temporaires contrôlés ;
- des limites CPU et mémoire ;
- des réseaux séparés ;
- des healthchecks ;
- une analyse Trivy.

OpenCanary dispose uniquement des capacités `SETUID` et `SETGID`, nécessaires à l’abandon de ses privilèges au démarrage.

## 7. Analyses automatisées

### Intégration continue

GitHub Actions vérifie :

- la compilation et les tests Maven ;
- la construction Angular ;
- les images Docker ;
- les dépendances ;
- les mauvaises configurations ;
- les vulnérabilités connues.

### Dependabot

Dependabot surveille :

- les dépendances Maven ;
- les dépendances npm ;
- les actions GitHub ;
- les images Docker, selon la configuration retenue.

### Trivy

Trivy analyse :

- le dépôt ;
- les dépendances ;
- les Dockerfiles ;
- les images backend ;
- l’image frontend ;
- l’image OpenCanary ;
- les vulnérabilités élevées et critiques ;
- les erreurs de configuration ;
- les secrets détectables.

La CI bloque les vulnérabilités élevées ou critiques corrigibles selon la politique définie.

### OWASP ZAP

OWASP ZAP réalise une analyse dynamique du backend exposé dans l’environnement de test.

Une réponse `401 Unauthorized` sur une route protégée est un comportement attendu et ne constitue pas nécessairement une erreur du scan.

## 8. Supervision et détection

### Métriques

Prometheus collecte les métriques disponibles. cAdvisor fournit les informations relatives aux conteneurs.

Grafana présente les tableaux de bord et Alertmanager traite les alertes techniques.

### Journaux

Alloy collecte les journaux Docker et les transmet à Loki.

Grafana permet de rechercher les événements, notamment :

```logql
{container="cesizen-back"}
```

```logql
{container="cesizen-honeypot"}
```

La durée de conservation doit être limitée selon les besoins techniques et réglementaires.

### Honeypot

OpenCanary simule des services HTTP, FTP et SSH.

Il respecte les principes suivants :

- réseau Docker dédié ;
- aucune donnée réelle ;
- aucun accès au réseau applicatif interne ;
- publication locale uniquement pour la démonstration ;
- ressources limitées ;
- système de fichiers en lecture seule ;
- exécution finale avec un utilisateur non privilégié ;
- journaux transmis à Loki.

Une interaction génère une alerte Grafana et une notification Gmail.

## 9. Sauvegarde et restauration

MariaDB fait l’objet de sauvegardes :

- automatisables ;
- compressées ;
- datées ;
- accompagnées d’une somme SHA-256 ;
- exclues du dépôt ;
- soumises à une rotation ;
- vérifiées avec une restauration dans une base temporaire.

Une sauvegarde n’est considérée comme fiable que lorsqu’une restauration a été testée.

Avant une migration ou un déploiement important :

1. créer une sauvegarde ;
2. vérifier son intégrité ;
3. identifier la version actuellement déployée ;
4. prévoir le retour arrière ;
5. tester les migrations dans un environnement distinct.

## 10. Protection des données personnelles

Les mesures RGPD comprennent :

- la minimisation ;
- la limitation des finalités ;
- l’information des utilisateurs ;
- la gestion des droits ;
- les durées de conservation ;
- la suppression ou l’anonymisation ;
- l’absence de données réelles hors production ;
- la protection des sauvegardes ;
- la traçabilité des violations ;
- la revue des sous-traitants.

Les mots de passe, tokens et secrets ne sont jamais enregistrés dans les tickets ou les journaux.

## 11. Gestion des incidents

La procédure comprend :

1. détection ;
2. qualification ;
3. attribution d’une criticité ;
4. confinement ;
5. préservation des preuves ;
6. éradication ;
7. restauration ;
8. communication ;
9. clôture ;
10. retour d’expérience.

Les incidents sont suivis avec GitHub Issues. Les vulnérabilités sensibles utilisent un canal privé.

En cas de violation de données personnelles, le responsable de traitement détermine si une notification à la CNIL et aux personnes concernées est nécessaire.

## 12. Matrice de criticité

| Probabilité / Impact | Faible | Moyen | Élevé |
|---|---:|---:|---:|
| Faible | Faible | Faible | Moyenne |
| Moyenne | Faible | Moyenne | Haute |
| Élevée | Moyenne | Haute | Critique |

Les risques élevés et critiques font l’objet d’un traitement prioritaire.

## 13. Continuité et retour arrière

Le retour arrière repose sur :

- des images Docker versionnées ;
- la conservation de la version précédemment validée ;
- une sauvegarde avant migration ;
- des changesets Liquibase tracés ;
- une procédure de restauration ;
- une vérification après redéploiement ;
- la surveillance des métriques et des journaux.

En cas d’échec, le déploiement est interrompu et la version stable précédente est restaurée.

## 14. Mesures déjà démontrables

Les éléments suivants sont configurés et peuvent être présentés :

- dépôt Git et branches ;
- GitHub Actions ;
- modèles de tickets ;
- GitHub Projects ;
- Dependabot ;
- Trivy ;
- OWASP ZAP ;
- images Docker durcies ;
- sauvegarde MariaDB ;
- test de restauration ;
- Prometheus ;
- cAdvisor ;
- Grafana ;
- Alertmanager ;
- Loki ;
- Alloy ;
- OpenCanary ;
- alerte Grafana ;
- notification Gmail.

## 15. Actions restant à appliquer en production

Avant une mise en production réelle, il faudra notamment :

- déployer un reverse proxy HTTPS ;
- installer et renouveler un certificat TLS ;
- ne plus publier MariaDB sur l’hôte ;
- utiliser des secrets de production robustes ;
- activer les cookies sécurisés ;
- limiter l’accès aux interfaces de supervision ;
- protéger Grafana avec un compte distinct et un mot de passe fort ;
- chiffrer les sauvegardes externalisées ;
- tester le plan de restauration ;
- définir les durées de conservation définitives ;
- valider les mentions RGPD ;
- appliquer la protection des branches ;
- créer une version stable et un tag Git ;
- vérifier les alertes et contacts de production.

## 16. Amélioration continue

Le plan est réévalué :

- après un incident ;
- après une évolution majeure ;
- après la découverte d’une vulnérabilité ;
- avant une mise en production ;
- lors de la revue trimestrielle des risques ;
- lors de la veille technologique.

Chaque action est suivie par un ticket avec un responsable, une priorité et une échéance.
