# Procédure de gestion des évolutions

## 1. Objectif

Cette procédure définit la méthode utilisée pour analyser, planifier, développer et déployer les évolutions fonctionnelles ou techniques de CESIZen.

Les demandes sont centralisées dans GitHub Issues avec le modèle « Évolution ».

## 2. Expression et qualification du besoin

Chaque demande doit préciser :

- le besoin utilisateur ;
- le comportement attendu ;
- les critères d’acceptation ;
- les composants concernés ;
- la priorité estimée ;
- les impacts techniques, réglementaires et de sécurité ;
- les solutions alternatives envisagées.

La demande est ensuite analysée afin d’évaluer sa valeur, sa complexité, ses risques et son coût de maintenance.

## 3. Étude d’impact

Avant le développement, les impacts suivants sont examinés :

- architecture frontend et backend ;
- modèle de données et migrations Liquibase ;
- authentification et autorisations ;
- données personnelles et conformité RGPD ;
- performances et disponibilité ;
- compatibilité avec les versions existantes ;
- tests automatisés ;
- documentation ;
- déploiement et retour arrière ;
- supervision et journalisation.

Une évolution présentant un risque important peut faire l’objet d’un prototype ou d’une étude technique préalable.

## 4. Priorisation

| Priorité | Description | Traitement cible |
|---|---|---|
| Critique | Évolution indispensable pour la sécurité, la conformité ou la continuité du service | Dès que possible |
| Haute | Forte valeur utilisateur ou contrainte technique importante | Prochaine itération |
| Moyenne | Amélioration utile sans caractère urgent | Planifiée dans le backlog |
| Faible | Optimisation ou amélioration de confort | Selon la capacité disponible |

## 5. Cycle de réalisation

1. Création de la demande dans GitHub Issues.
2. Analyse du besoin et des impacts.
3. Définition des critères d’acceptation.
4. Estimation et priorisation.
5. Passage du ticket à `status:ready`.
6. Création d’une branche dédiée.
7. Développement et ajout des tests.
8. Exécution de la CI et des analyses de sécurité.
9. Relecture par pull request.
10. Validation dans l’environnement de test.
11. Fusion et déploiement.
12. Vérification après déploiement et clôture du ticket.

## 6. Branches et commits

Une branche d’évolution est créée à partir de `develop` :

```text
feat/<numero-ticket>-<description-courte>
```

Exemple :

```text
feat/57-export-diagnostic-pdf
```

Les commits respectent la convention Conventional Commits :

```text
feat(diagnostic): add PDF export
```

## 7. Critères de validation

Une évolution est considérée comme terminée lorsque :

- les critères d’acceptation sont satisfaits ;
- les autorisations sont contrôlées côté backend ;
- les entrées utilisateur sont validées ;
- les tests nécessaires sont ajoutés et réussissent ;
- les migrations de données sont versionnées avec Liquibase ;
- les contrôles de sécurité ne signalent aucune nouvelle vulnérabilité bloquante ;
- la documentation est mise à jour ;
- le déploiement et le retour arrière ont été vérifiés ;
- le ticket et la pull request sont liés.

## 8. Mise en production

Le déploiement d’une évolution suit les étapes suivantes :

1. sauvegarder la base de données ;
2. identifier la version Docker actuellement déployée ;
3. déployer d’abord dans l’environnement de test ;
4. exécuter les tests de validation ;
5. planifier le déploiement en production ;
6. appliquer les migrations Liquibase ;
7. déployer les images versionnées ;
8. effectuer les contrôles fonctionnels et techniques ;
9. surveiller les métriques, les journaux et les alertes ;
10. revenir à la version précédente en cas d’échec.

## 9. Traçabilité

Chaque évolution conserve les liens entre :

- le ticket GitHub ;
- la branche ;
- les commits ;
- la pull request ;
- les résultats de CI ;
- la version déployée ;
- les éventuelles migrations Liquibase ;
- la documentation mise à jour.
