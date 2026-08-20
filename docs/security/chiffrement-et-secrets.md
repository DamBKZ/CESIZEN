# Chiffrement et gestion des secrets

## 1. Objectif

Ce document définit les mesures utilisées pour protéger les données et les secrets techniques de CESIZen :

- pendant leur transmission ;
- pendant leur stockage ;
- pendant leur sauvegarde ;
- pendant leur utilisation par l’application et la CI/CD.

## 2. Distinction entre chiffrement et hachage

Le **chiffrement** est réversible à l’aide d’une clé. Il protège les données qui doivent pouvoir être relues.

Le **hachage** est conçu pour être irréversible. Il est utilisé notamment pour vérifier les mots de passe sans conserver leur valeur originale.

L’encodage Base64 n’est pas un mécanisme de chiffrement et ne protège pas une donnée sensible.

## 3. Protection des mots de passe

Les mots de passe utilisateurs ne doivent jamais être enregistrés en clair ni chiffrés de manière réversible.

Ils doivent être hachés avec un algorithme adapté aux mots de passe, tel que :

- Argon2id ;
- bcrypt.

Dans le contexte de Spring Security, BCrypt peut être configuré avec un coût adapté aux capacités du serveur.

Les règles suivantes s’appliquent :

- salage automatique et unique ;
- coût régulièrement réévalué ;
- absence de mot de passe dans les logs ;
- absence de transmission par e-mail ;
- réinitialisation avec un token temporaire à usage unique ;
- invalidation du token après utilisation ou expiration.

## 4. Chiffrement des communications

En production, tous les échanges externes doivent utiliser HTTPS avec TLS.

Le certificat TLS est placé sur un reverse proxy, par exemple Nginx ou Traefik, situé devant le frontend et le backend.

Les mesures suivantes sont appliquées :

- redirection HTTP vers HTTPS ;
- protocoles et suites cryptographiques obsolètes désactivés ;
- certificat valide et renouvelé ;
- cookies marqués `Secure` en production ;
- attribut `HttpOnly` pour les cookies non accessibles à JavaScript ;
- politique `SameSite` adaptée ;
- en-tête HSTS activé après validation du fonctionnement HTTPS.

Les communications entre services doivent rester sur un réseau Docker privé et ne pas être exposées publiquement sans nécessité.

## 5. Protection des tokens

Les tokens d’accès et de renouvellement sont considérés comme des secrets temporaires.

Les mesures suivantes sont requises :

- clé de signature suffisamment longue et aléatoire ;
- durée de validité courte pour les access tokens ;
- durée limitée pour les refresh tokens ;
- mécanisme de révocation ;
- renouvellement après un événement de sécurité ;
- transmission uniquement par HTTPS en production ;
- absence de token dans les URL ;
- absence de token dans les logs ;
- stockage côté client limitant l’exposition aux scripts.

Une clé JWT ne doit jamais être codée en dur dans les sources.

## 6. Secrets de configuration

Les secrets techniques comprennent notamment :

- mot de passe MariaDB ;
- mot de passe administrateur Grafana ;
- mot de passe d’application Gmail ;
- clé de signature JWT ;
- identifiants SMTP ;
- éventuels tokens de déploiement ;
- clés ou certificats privés.

En développement local, ils sont stockés dans des fichiers `.env` exclus de Git.

En CI/CD, ils sont stockés dans GitHub Actions Secrets ou dans les variables protégées de l’environnement.

En production, ils doivent être injectés au démarrage depuis un mécanisme protégé, par exemple :

- variables d’environnement avec permissions limitées ;
- Docker Secrets ;
- fichiers montés en lecture seule avec permissions restrictives ;
- gestionnaire de secrets lorsque l’infrastructure le permet.

## 7. Règles de gestion des secrets

Les règles suivantes s’appliquent :

1. ne jamais commiter un secret ;
2. fournir uniquement des fichiers `.env.example` avec des valeurs factices ;
3. utiliser des secrets différents selon les environnements ;
4. limiter l’accès au strict besoin ;
5. éviter l’affichage des secrets dans les commandes et les logs ;
6. renouveler les secrets périodiquement et après un incident ;
7. supprimer les secrets devenus inutiles ;
8. documenter le propriétaire et l’usage de chaque secret ;
9. vérifier le dépôt avec les outils de détection de secrets ;
10. considérer tout secret publié comme compromis.

## 8. Rotation des secrets

Une rotation est réalisée :

- après une exposition réelle ou suspectée ;
- après le départ d’une personne autorisée ;
- après une compromission d’environnement ;
- lors du remplacement d’un prestataire ;
- à échéance périodique pour les secrets sensibles.

La procédure comprend :

1. créer un nouveau secret ;
2. le déployer sans l’exposer ;
3. vérifier le fonctionnement ;
4. révoquer l’ancien secret ;
5. surveiller les erreurs et comportements inhabituels ;
6. documenter la date et le responsable de la rotation.

Pour une clé de signature JWT, la rotation doit tenir compte des tokens déjà émis. En cas de compromission, les sessions existantes doivent être révoquées.

## 9. Données stockées

MariaDB ne doit pas être exposée publiquement en production.

La protection des données stockées repose sur :

- contrôle d’accès avec un compte applicatif dédié ;
- mot de passe fort et distinct ;
- permissions minimales ;
- volume de données protégé sur l’hôte ;
- accès administrateur limité ;
- journalisation des opérations sensibles ;
- sauvegardes protégées ;
- chiffrement du disque ou du volume de l’hôte lorsque disponible.

Un chiffrement applicatif par champ peut être ajouté pour une donnée particulièrement sensible, à condition que les clés soient gérées séparément des données.

## 10. Sauvegardes

Les sauvegardes de base de données peuvent contenir des données personnelles et doivent donc bénéficier du même niveau de protection que la base active.

Les mesures suivantes sont prévues :

- stockage hors du répertoire public de l’application ;
- accès limité ;
- transmission chiffrée lors d’une externalisation ;
- chiffrement avant stockage sur un support externe ;
- somme SHA-256 pour vérifier l’intégrité ;
- rotation et suppression des sauvegardes expirées ;
- tests réguliers de restauration ;
- absence de sauvegarde dans Git.

La somme SHA-256 vérifie l’intégrité, mais ne chiffre pas le contenu.

## 11. Chiffrement des sauvegardes

Pour une externalisation, une sauvegarde peut être chiffrée avec un outil reconnu tel que GPG.

Exemple de principe :

```text
sauvegarde SQL compressée
        ↓
chiffrement avec une clé ou une phrase secrète
        ↓
stockage sur un support distinct
```

La clé de déchiffrement doit être conservée séparément de la sauvegarde et accessible uniquement aux personnes autorisées.

La procédure de restauration doit inclure le déchiffrement, le contrôle d’intégrité et la restauration dans une base temporaire avant toute utilisation en production.

## 12. Messagerie SMTP

Grafana utilise un mot de passe d’application Gmail dédié.

Les mesures suivantes s’appliquent :

- validation en deux étapes activée sur le compte Google ;
- mot de passe d’application distinct du mot de passe principal ;
- secret stocké uniquement dans `.env.monitoring` local ou dans le mécanisme de secrets de production ;
- connexion SMTP avec StartTLS ;
- absence du secret dans les sources et captures ;
- révocation du mot de passe d’application lorsqu’il n’est plus utilisé.

Le compte utilisé pour les alertes doit être dédié si l’environnement passe en production.

## 13. Détection d’une fuite de secret

La détection repose sur :

- revue des modifications avant commit ;
- fichiers `.gitignore` et `.gitattributes` ;
- analyse Trivy du dépôt ;
- contrôle des journaux GitHub Actions ;
- revue des tickets et captures ;
- alertes fournies par la plateforme de versioning.

Si un secret est publié :

1. le révoquer immédiatement ;
2. générer un nouveau secret ;
3. rechercher son utilisation dans les logs et systèmes ;
4. supprimer la valeur des sources et artefacts accessibles ;
5. nettoyer l’historique Git si nécessaire ;
6. ouvrir un incident de sécurité ;
7. documenter les mesures prises.

La suppression du fichier dans le dernier commit ne suffit pas si le secret reste présent dans l’historique Git.

## 14. Responsabilités

Le responsable du déploiement :

- crée et injecte les secrets ;
- limite les accès ;
- organise leur rotation ;
- vérifie leur absence du dépôt ;
- maintient la procédure de récupération.

Le développeur :

- ne code aucun secret en dur ;
- utilise les variables de configuration ;
- masque les données sensibles dans les logs ;
- signale immédiatement toute exposition ;
- ajoute des valeurs factices dans les fichiers d’exemple.

## 15. Revue périodique

La gestion du chiffrement et des secrets est revue :

- avant chaque mise en production ;
- après un incident ;
- lors de l’ajout d’un service externe ;
- lors d’un changement d’algorithme ou de bibliothèque ;
- au minimum tous les six mois.

Cette revue vérifie notamment les versions TLS, les durées de validité, les permissions, les secrets inutilisés et l’efficacité des procédures de rotation.
