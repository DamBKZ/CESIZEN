# Méthodologie de veille technologique

## 1. Objectif

La veille technologique vise à maintenir CESIZen sécurisé, compatible et maintenable face aux évolutions des technologies utilisées.

Elle permet notamment :

- d’identifier les vulnérabilités publiées ;
- d’anticiper les versions obsolètes ou en fin de support ;
- d’évaluer les nouvelles versions des dépendances ;
- de suivre les bonnes pratiques de développement et de déploiement ;
- de planifier les évolutions techniques sans dégrader la stabilité de l’application.

## 2. Périmètre de la veille

La veille couvre principalement :

- Java et Spring Boot ;
- Spring Security ;
- Angular, TypeScript et Node.js ;
- MariaDB et Liquibase ;
- Docker et Docker Compose ;
- GitHub Actions ;
- OWASP et les vulnérabilités web ;
- Trivy et OWASP ZAP ;
- Grafana, Prometheus, Alertmanager, Loki et Alloy ;
- OpenCanary ;
- la réglementation relative aux données personnelles et au RGPD.

## 3. Sources surveillées

Les sources privilégiées sont :

- les documentations officielles ;
- les notes de version des éditeurs ;
- les bulletins de sécurité des projets ;
- les avis GitHub Security Advisories ;
- la base CVE et la National Vulnerability Database ;
- les publications de l’ANSSI ;
- les recommandations de la CNIL ;
- les publications de l’OWASP ;
- les rapports générés par Dependabot et Trivy.

Les sources officielles et primaires sont prioritaires. Les articles et réseaux sociaux peuvent signaler une information, mais celle-ci doit être vérifiée avant toute décision technique.

## 4. Outils utilisés

| Outil | Utilisation |
|---|---|
| Dependabot | Détection et proposition de mise à jour des dépendances et des actions GitHub |
| Trivy | Analyse des dépendances, images Docker, secrets et mauvaises configurations |
| OWASP ZAP | Recherche dynamique de vulnérabilités web |
| GitHub Security Advisories | Suivi des vulnérabilités des composants utilisés |
| GitHub Issues | Enregistrement et suivi des actions issues de la veille |
| GitHub Projects | Priorisation et visualisation des actions |
| Flux RSS et abonnements officiels | Suivi régulier des annonces techniques et réglementaires |

## 5. Fréquence

| Activité | Fréquence |
|---|---|
| Consultation des alertes Dependabot et Trivy | À chaque exécution de la CI |
| Revue des pull requests de dépendances | Hebdomadaire |
| Consultation des bulletins de sécurité critiques | Hebdomadaire |
| Revue des versions des composants | Mensuelle |
| Revue des versions en fin de support | Trimestrielle |
| Revue des recommandations OWASP, ANSSI et CNIL | Trimestrielle |
| Bilan global de la veille | Semestriel |

Une alerte critique affectant directement un composant utilisé est analysée immédiatement.

## 6. Processus de traitement

Chaque information pertinente suit le processus suivant :

1. collecter l’information ;
2. vérifier la fiabilité et la date de publication ;
3. identifier les versions concernées ;
4. comparer avec les versions utilisées dans CESIZen ;
5. évaluer l’exploitabilité et les impacts ;
6. attribuer une criticité ;
7. créer un ticket GitHub si une action est nécessaire ;
8. tester la mise à jour ou la mesure corrective sur une branche dédiée ;
9. exécuter les tests et analyses de sécurité ;
10. valider dans l’environnement de test ;
11. déployer selon le processus normal ;
12. documenter la décision et clôturer le ticket.

## 7. Qualification des informations

Une information est évaluée selon :

- la fiabilité de la source ;
- la date de publication ;
- le composant et la version concernés ;
- l’existence d’un correctif ;
- la présence du composant dans l’application ;
- son exposition réelle ;
- la complexité de l’exploitation ;
- les impacts sur la confidentialité, l’intégrité et la disponibilité ;
- les conséquences possibles sur les données personnelles.

Les scores CVSS constituent un indicateur, mais la priorité finale tient également compte du contexte réel de CESIZen.

## 8. Mise à jour des dépendances

Les mises à jour sont classées en trois catégories :

### Correctif de sécurité

Traitement prioritaire lorsqu’une vulnérabilité affecte une version utilisée. La correction est testée et déployée selon la criticité.

### Mise à jour mineure

Traitement régulier après consultation des notes de version et validation des tests automatisés.

### Mise à jour majeure

Une étude d’impact est réalisée avant adoption :

- changements incompatibles ;
- migrations nécessaires ;
- compatibilité des bibliothèques ;
- impacts sur les tests ;
- impacts sur le déploiement ;
- stratégie de retour arrière.

Les mises à jour majeures ne sont pas fusionnées automatiquement.

## 9. Traçabilité

Chaque action issue de la veille peut être associée à :

- une source et une date de consultation ;
- un composant et une version ;
- un niveau de criticité ;
- un ticket GitHub ;
- une pull request ;
- des résultats de tests ;
- une décision d’adoption, de report ou de rejet ;
- une version de l’application déployée.

Une décision de ne pas appliquer immédiatement une mise à jour de sécurité doit être justifiée et accompagnée, si nécessaire, de mesures compensatoires.

## 10. Indicateurs de suivi

Les indicateurs proposés sont :

- nombre d’alertes de sécurité ouvertes ;
- délai moyen de qualification ;
- délai moyen de correction ;
- nombre de dépendances obsolètes ;
- nombre de mises à jour appliquées ;
- nombre de mises à jour reportées ;
- nombre de vulnérabilités critiques non corrigées ;
- pourcentage de contrôles CI réussis ;
- date de la dernière revue technologique.

## 11. Restitution

Un bilan périodique synthétise :

- les technologies surveillées ;
- les alertes importantes ;
- les mises à jour réalisées ;
- les risques acceptés ;
- les évolutions planifiées ;
- les composants approchant leur fin de support ;
- les actions à inscrire dans le backlog.

Ce bilan alimente le plan de maintenance et contribue à la pérennité de CESIZen.
