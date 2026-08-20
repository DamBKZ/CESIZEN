# Procédure de gestion des anomalies

## 1. Objectif

Cette procédure définit la méthode utilisée pour déclarer, qualifier, corriger et clôturer les anomalies de CESIZen.

Les demandes sont centralisées dans GitHub Issues à l’aide du modèle « Anomalie ».

## 2. Cycle de traitement

1. Déclaration de l’anomalie dans GitHub Issues.
2. Vérification des informations et reproduction du défaut.
3. Qualification de la criticité et de la priorité.
4. Affectation au développeur responsable.
5. Création d’une branche corrective.
6. Développement de la correction.
7. Exécution des tests automatisés et des contrôles de sécurité.
8. Relecture par pull request.
9. Validation dans l’environnement de test.
10. Fusion, déploiement et clôture du ticket.

## 3. Niveaux de criticité

| Niveau | Description | Exemple | Prise en charge cible |
|---|---|---|---|
| Critique | Application indisponible, violation de données ou faille exploitable | Contournement de l’authentification | Immédiate |
| Haute | Fonction essentielle inutilisable sans contournement acceptable | Connexion impossible | Moins de 4 heures ouvrées |
| Moyenne | Dysfonctionnement limité avec solution de contournement | Erreur sur une fonction secondaire | Moins de 2 jours ouvrés |
| Faible | Défaut mineur sans impact fonctionnel important | Problème d’affichage | Prochaine itération |

Ces délais correspondent à des objectifs de prise en charge et non à une garantie de résolution.

## 4. Workflow du ticket

Les statuts utilisés sont :

- `status:triage` : demande à analyser ;
- `status:ready` : demande qualifiée et prête ;
- `status:in-progress` : correction en cours ;
- `status:review` : pull request ou validation en cours ;
- `status:blocked` : traitement bloqué ;
- `status:done` : correction validée et déployée.

## 5. Branches et commits

Une branche corrective est créée à partir de `develop` :

```text
fix/<numero-ticket>-<description-courte>
```

Exemple :

```text
fix/42-refresh-token-expire
```

Les commits utilisent la convention Conventional Commits :

```text
fix(auth): reject expired refresh tokens
```

## 6. Validation

Une correction est considérée comme terminée lorsque :

- le défaut est reproduit avant correction ;
- un test de non-régression est ajouté lorsque cela est pertinent ;
- les tests backend et frontend passent ;
- les contrôles Trivy et ZAP ne révèlent pas de nouvelle vulnérabilité bloquante ;
- la pull request est validée ;
- la correction est vérifiée dans l’environnement de test ;
- la documentation est mise à jour si nécessaire ;
- le ticket est lié à la pull request puis clôturé.

## 7. Retour arrière

En cas de régression après déploiement :

1. arrêter le déploiement ;
2. restaurer la version Docker précédemment validée ;
3. vérifier l’état de la base de données ;
4. ouvrir ou rouvrir un ticket prioritaire ;
5. analyser la cause ;
6. produire une nouvelle correction ;
7. documenter l’incident et les actions préventives.
