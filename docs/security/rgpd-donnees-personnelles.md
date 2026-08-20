# Protection des données personnelles et conformité RGPD

## 1. Objectif

CESIZen traite des données liées aux comptes utilisateurs et aux diagnostics. Ces données doivent être protégées pendant leur collecte, leur utilisation, leur conservation et leur suppression.

La démarche repose sur les principes suivants :

- licéité, loyauté et transparence ;
- finalité déterminée ;
- minimisation des données ;
- exactitude ;
- limitation de la conservation ;
- intégrité et confidentialité ;
- responsabilité du responsable de traitement ;
- protection des données dès la conception et par défaut.

## 2. Catégories de données

| Catégorie | Exemples | Finalité |
|---|---|---|
| Identification | Adresse e-mail, pseudonyme, identifiant technique | Création et gestion du compte |
| Authentification | Empreinte du mot de passe, tokens et dates d’expiration | Sécurisation de l’accès |
| Diagnostics | Réponses, résultats et dates de réalisation | Fourniture du service CESIZen |
| Contenus | Informations créées ou administrées par un utilisateur autorisé | Mise à disposition de contenus |
| Données techniques | Adresses IP, événements de sécurité et journaux | Sécurité, diagnostic et supervision |
| Communications | Demandes de réinitialisation et notifications | Assistance et sécurité du compte |

CESIZen ne doit collecter aucune donnée sans finalité identifiée.

## 3. Base légale

La base légale doit être déterminée pour chaque traitement.

| Traitement | Base légale envisagée |
|---|---|
| Création et gestion du compte | Exécution du service demandé |
| Authentification et sécurité | Exécution du service et intérêt légitime de sécurisation |
| Diagnostics demandés par l’utilisateur | Exécution du service |
| Envoi d’un message de réinitialisation | Exécution du service et sécurité |
| Journalisation de sécurité | Intérêt légitime de protection du système |
| Communication facultative ou commerciale | Consentement, lorsqu’elle est mise en œuvre |

Cette qualification doit être validée par le responsable de traitement avant la mise en production.

## 4. Minimisation

Seules les données nécessaires sont collectées.

Les mesures suivantes sont appliquées :

- pseudonyme utilisé lorsque l’identité civile n’est pas nécessaire ;
- absence de stockage du mot de passe en clair ;
- absence de secrets et de tokens dans les journaux ;
- absence de données personnelles réelles dans les tickets ;
- absence de données réelles dans OpenCanary ;
- limitation des données retournées par les API ;
- contrôle des champs acceptés par les DTO ;
- suppression des informations devenues inutiles.

## 5. Information des utilisateurs

Une notice de confidentialité doit présenter clairement :

- l’identité du responsable de traitement ;
- les finalités ;
- les bases légales ;
- les données collectées ;
- les destinataires ;
- les durées de conservation ;
- les mesures générales de sécurité ;
- les droits des personnes ;
- le moyen d’exercer ces droits ;
- le droit d’introduire une réclamation auprès de la CNIL ;
- l’existence éventuelle de transferts hors de l’Union européenne.

L’information doit être accessible avant ou au moment de la collecte.

## 6. Durées de conservation

Les durées doivent être validées selon les besoins réels du projet.

| Donnée | Durée proposée |
|---|---|
| Compte actif | Pendant l’utilisation du service |
| Compte inactif | Suppression ou anonymisation après 24 mois d’inactivité |
| Diagnostic | Jusqu’à suppression par l’utilisateur ou suppression du compte |
| Token d’accès | Durée technique courte définie dans la configuration |
| Refresh token | Jusqu’à expiration, révocation ou déconnexion |
| Token de réinitialisation | Durée courte, usage unique |
| Journaux applicatifs | 30 jours |
| Journaux de sécurité | 90 jours, sauf conservation nécessaire pour un incident |
| Sauvegardes | Selon la politique de rotation, par exemple 30 jours |
| Tickets et preuves d’incident | Durée nécessaire au traitement et aux obligations applicables |

Une tâche périodique doit supprimer ou anonymiser les données arrivées à échéance.

## 7. Droits des personnes

Les utilisateurs peuvent exercer, selon leur situation :

- le droit d’accès ;
- le droit de rectification ;
- le droit à l’effacement ;
- le droit à la limitation ;
- le droit d’opposition ;
- le droit à la portabilité ;
- le droit de retirer un consentement.

La procédure de traitement est la suivante :

1. recevoir la demande par un canal identifié ;
2. vérifier l’identité de façon proportionnée ;
3. enregistrer la date et la nature de la demande ;
4. identifier les données concernées ;
5. répondre dans le délai réglementaire ;
6. appliquer l’action demandée ou justifier le refus ;
7. conserver une preuve minimale du traitement.

La réponse doit normalement intervenir dans un délai d’un mois, avec prolongation possible dans les conditions prévues par le RGPD.

## 8. Suppression d’un compte

La suppression d’un compte doit entraîner :

- la désactivation de l’accès ;
- la révocation des tokens ;
- la suppression ou l’anonymisation des données du compte ;
- le traitement des diagnostics associés ;
- la suppression des données devenues inutiles ;
- la prise en compte des copies présentes dans les sauvegardes selon leur cycle de rotation.

Les données soumises à une obligation légale de conservation sont isolées et accessibles uniquement aux personnes autorisées.

## 9. Mesures de sécurité

Les mesures mises en œuvre ou prévues comprennent :

- hachage robuste des mots de passe ;
- contrôle d’accès avec Spring Security ;
- séparation des rôles utilisateur et administrateur ;
- durée de vie limitée et révocation des tokens ;
- validation des entrées ;
- chiffrement TLS en production ;
- secrets exclus du dépôt ;
- accès limité à MariaDB ;
- sauvegardes contrôlées et restaurations testées ;
- journalisation des événements importants ;
- surveillance avec Grafana, Prometheus et Loki ;
- analyses Trivy et OWASP ZAP ;
- mise à jour régulière des dépendances.

## 10. Environnements hors production

Les environnements de développement et de test ne doivent pas utiliser de données personnelles réelles.

Les jeux de données sont :

- fictifs ;
- générés ;
- anonymisés de manière irréversible si une copie est nécessaire ;
- limités au strict besoin de test.

Les mots de passe et secrets sont distincts entre les environnements.

## 11. Sous-traitants et services externes

Avant l’utilisation d’un service externe, les éléments suivants sont examinés :

- rôle du prestataire ;
- localisation des données ;
- garanties contractuelles ;
- mesures de sécurité ;
- durée de conservation ;
- possibilité de suppression ou d’export ;
- transferts éventuels hors de l’Union européenne ;
- dépendance au prestataire.

La solution CESIZen privilégie des outils gratuits et auto-hébergés pour limiter l’exposition à des tiers.

L’utilisation de GitHub et de Gmail pour certaines fonctions doit être identifiée dans la documentation du traitement et évaluée par le responsable de traitement.

## 12. Violation de données

Toute violation suspectée suit la procédure de gestion des incidents.

Il faut notamment :

1. contenir l’incident ;
2. déterminer les données et personnes concernées ;
3. évaluer les conséquences ;
4. documenter les faits ;
5. informer le responsable de traitement ;
6. déterminer si la CNIL doit être notifiée ;
7. déterminer si les personnes concernées doivent être informées ;
8. conserver la justification de la décision.

Lorsqu’elle est nécessaire, la notification à la CNIL doit être réalisée dans les meilleurs délais et, si possible, dans les 72 heures suivant la prise de connaissance.

## 13. Registre des traitements

Le responsable de traitement maintient un registre précisant :

- le nom du traitement ;
- sa finalité ;
- sa base légale ;
- les catégories de personnes ;
- les catégories de données ;
- les destinataires ;
- les durées de conservation ;
- les mesures de sécurité ;
- les transferts éventuels ;
- les sous-traitants ;
- la date de révision.

## 14. Revue de conformité

La conformité est réévaluée :

- avant la mise en production ;
- lors de l’ajout d’une nouvelle donnée ;
- lors d’un changement de finalité ;
- lors de l’ajout d’un prestataire ;
- après un incident ;
- au minimum une fois par an.

Une analyse d’impact relative à la protection des données doit être envisagée lorsqu’un traitement est susceptible d’engendrer un risque élevé pour les droits et libertés des personnes.
