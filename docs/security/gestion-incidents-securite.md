# Procédure de gestion des incidents de sécurité

## 1. Objectif

Cette procédure définit la méthode utilisée pour détecter, qualifier, contenir, traiter et clôturer un incident de sécurité affectant CESIZen.

Elle couvre notamment :

- les tentatives d’accès non autorisé ;
- les vulnérabilités applicatives ou techniques ;
- la compromission d’un compte ;
- l’exposition d’un secret ;
- la fuite ou l’altération de données personnelles ;
- l’indisponibilité provoquée par une attaque ;
- les alertes générées par OpenCanary, Grafana, Trivy ou ZAP.

## 2. Canaux de signalement

Les incidents non sensibles peuvent être déclarés avec le modèle GitHub Issues « Incident de sécurité ».

Une vulnérabilité exploitable, un secret ou une preuve contenant des données sensibles ne doit jamais être publié dans un ticket public. Ces informations doivent être transmises au moyen de GitHub Private Vulnerability Reporting ou d’un canal privé approprié.

## 3. Détection

La détection repose sur :

- les journaux du backend et des conteneurs ;
- la centralisation des logs avec Alloy et Loki ;
- les tableaux de bord et alertes Grafana ;
- les alertes OpenCanary ;
- les scans Trivy des dépendances, configurations et images ;
- les analyses OWASP ZAP ;
- les échecs d’authentification et comportements inhabituels ;
- les signalements des utilisateurs.

## 4. Niveaux de criticité

| Niveau | Description | Exemple | Prise en charge |
|---|---|---|---|
| Critique | Compromission avérée, fuite de données ou service essentiel indisponible | Extraction de données personnelles | Immédiate |
| Haute | Vulnérabilité exploitable ou attaque active sans fuite confirmée | Contournement d’autorisation | Moins de 4 heures ouvrées |
| Moyenne | Risque limité ou tentative bloquée nécessitant une analyse | Interactions répétées avec le honeypot | Moins d’un jour ouvré |
| Faible | Événement isolé sans impact démontré | Scan automatisé sans exploitation | Prochaine revue de sécurité |

Ces délais sont des objectifs de prise en charge et non des garanties de résolution.

## 5. Procédure de réponse

### 5.1 Détection et qualification

1. enregistrer la date et l’heure de détection ;
2. identifier la source de l’alerte ;
3. déterminer les environnements et composants concernés ;
4. évaluer la confidentialité, l’intégrité et la disponibilité ;
5. attribuer un niveau de criticité ;
6. ouvrir un ticket non sensible et désigner un responsable.

### 5.2 Confinement

Selon la nature de l’incident :

- désactiver le compte compromis ;
- révoquer les access tokens et refresh tokens ;
- renouveler les secrets exposés ;
- bloquer temporairement une adresse ou un flux malveillant ;
- isoler un conteneur ou un service ;
- interrompre un déploiement ;
- limiter l’accès au composant concerné ;
- préserver les journaux et preuves nécessaires à l’analyse.

Le honeypot ne doit jamais être connecté au réseau applicatif interne ni contenir de données réelles.

### 5.3 Éradication

1. identifier la cause racine ;
2. corriger la vulnérabilité ou la mauvaise configuration ;
3. mettre à jour les dépendances concernées ;
4. ajouter un test de non-régression ;
5. exécuter les tests automatisés ;
6. relancer les analyses Trivy et ZAP ;
7. vérifier l’absence d’autres composants affectés.

### 5.4 Rétablissement

1. sauvegarder les éléments nécessaires avant intervention ;
2. déployer la correction dans l’environnement de test ;
3. valider le comportement fonctionnel et la sécurité ;
4. restaurer progressivement le service ;
5. surveiller les métriques, les logs et les alertes ;
6. confirmer le retour à un fonctionnement normal.

### 5.5 Clôture et retour d’expérience

L’incident est clôturé lorsque :

- la menace est supprimée ;
- les services sont rétablis ;
- les secrets concernés sont renouvelés ;
- les contrôles de sécurité sont concluants ;
- les utilisateurs ou responsables concernés ont été informés ;
- les preuves et décisions sont documentées ;
- un retour d’expérience est réalisé ;
- des actions préventives sont ajoutées au backlog.

## 6. Communication et escalade

| Criticité | Communication |
|---|---|
| Critique | Information immédiate du responsable du projet, suspension éventuelle du service et mises à jour régulières |
| Haute | Information rapide du responsable et suivi jusqu’à résolution |
| Moyenne | Suivi dans le ticket et revue lors du prochain point projet |
| Faible | Enregistrement et traitement dans le cycle normal de maintenance |

La communication doit rester factuelle et préciser :

- ce qui est connu ;
- ce qui reste à confirmer ;
- les impacts observés ;
- les mesures de confinement ;
- la prochaine mise à jour prévue.

Aucun secret, détail d’exploitation ou donnée personnelle ne doit être diffusé publiquement.

## 7. Gestion des données personnelles

En cas de violation de données personnelles :

1. identifier les catégories de données et les personnes concernées ;
2. déterminer la nature, la durée et l’étendue de l’exposition ;
3. évaluer les conséquences possibles ;
4. documenter les faits et les mesures prises ;
5. informer le responsable de traitement ;
6. déterminer si une notification à la CNIL est nécessaire ;
7. déterminer si les personnes concernées doivent être informées ;
8. conserver la traçabilité de la décision.

Lorsqu’une notification à la CNIL est requise, elle doit être effectuée dans les meilleurs délais et, si possible, dans les 72 heures suivant la prise de connaissance de la violation.

## 8. Conservation des preuves

Les éléments suivants peuvent être conservés pour l’analyse :

- journaux applicatifs ;
- événements OpenCanary ;
- alertes Grafana ;
- résultats Trivy et ZAP ;
- identifiants de versions et d’images Docker ;
- horodatages ;
- actions réalisées pendant l’incident.

Les preuves doivent être protégées contre la modification, accessibles uniquement aux personnes autorisées et purgées selon la durée de conservation définie.

## 9. Retour d’expérience

Après un incident significatif, un compte rendu précise :

- la chronologie ;
- la cause racine ;
- les impacts ;
- les actions de confinement ;
- la correction appliquée ;
- les difficultés rencontrées ;
- les actions préventives ;
- le responsable et l’échéance de chaque action.
