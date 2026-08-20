# Procédure opératoire de déploiement

## 1. Objet

Cette procédure résume les commandes et vérifications nécessaires pour déployer CESIZen dans un environnement Docker.

Elle complète le plan de déploiement détaillé.

## 2. Prérequis

Le serveur doit disposer de :

- Git ;
- Docker Engine ;
- Docker Compose ;
- suffisamment d’espace disque ;
- un accès au dépôt ou aux images ;
- des secrets propres à l’environnement ;
- un emplacement protégé pour les sauvegardes.

Vérifications :

```bash
git --version
docker --version
docker compose version
docker info
```

## 3. Récupération de la version

Récupérer le dépôt puis sélectionner une version validée :

```bash
git clone <URL_DU_DEPOT>
cd CESIZEN
git fetch --tags
git checkout v1.0.0
```

En environnement de test, une branche validée peut être utilisée. En production, un tag immuable est privilégié.

## 4. Configuration

Créer les fichiers d’environnement à partir des exemples :

```bash
cp .env.example .env
cp .env.monitoring.example .env.monitoring
```

Renseigner les valeurs réelles sans les afficher dans les journaux :

- mots de passe MariaDB ;
- clé JWT ;
- URL du frontend ;
- paramètres des cookies ;
- identifiants SMTP ;
- mot de passe Grafana ;
- paramètres de conservation.

Protéger les fichiers :

```bash
chmod 600 .env .env.monitoring
```

Vérifier qu’ils sont ignorés par Git :

```bash
git check-ignore -v .env
git check-ignore -v .env.monitoring
```

## 5. Vérification des configurations

```bash
docker compose config --quiet
```

```bash
docker compose \
  --env-file .env.monitoring \
  -f compose.monitoring.yml \
  config --quiet
```

Le honeypot est facultatif en production et ne doit être activé qu’après validation de son isolation :

```bash
docker compose \
  -f compose.honeypot.yml \
  config --quiet
```

## 6. Sauvegarde préalable

Avant une mise à jour, créer une sauvegarde :

```bash
export DB_PASSWORD='<mot-de-passe-applicatif>'
./scripts/database/backup.sh
unset DB_PASSWORD
```

Vérifier l’intégrité du fichier :

```bash
gzip -t backups/database/*.sql.gz
```

La restauration de contrôle utilise le mot de passe administrateur MariaDB :

```bash
export DB_ROOT_PASSWORD='<mot-de-passe-root>'
./scripts/database/verify-backup.sh
unset DB_ROOT_PASSWORD
```

Les mots de passe ne doivent pas rester dans l’historique du terminal sur un environnement réel. Un mécanisme de secrets ou une saisie interactive est préférable.

## 7. Construction ou récupération des images

Pour un déploiement local ou de test :

```bash
docker compose build --pull
```

En production, utiliser de préférence des images construites par la CI et portant un tag correspondant à la version :

```text
cesizen-back:1.0.0
cesizen-front:1.0.0
```

Ne pas utiliser uniquement `latest`.

## 8. Démarrage de l’application

```bash
docker compose up -d
```

Vérifier :

```bash
docker compose ps
```

Consulter le backend pendant le démarrage :

```bash
docker logs cesizen-back --tail 100
```

Le backend doit devenir `healthy` et Liquibase doit terminer sans erreur.

## 9. Démarrage de la supervision

```bash
docker compose \
  --env-file .env.monitoring \
  -f compose.monitoring.yml \
  up -d
```

Vérifier :

```bash
docker compose \
  --env-file .env.monitoring \
  -f compose.monitoring.yml \
  ps
```

Services attendus :

- Prometheus ;
- Grafana ;
- Alertmanager ;
- cAdvisor ;
- Loki ;
- Alloy.

## 10. Démarrage facultatif du honeypot

Le honeypot n’est démarré qu’après vérification :

- du réseau dédié ;
- de l’absence de données réelles ;
- des limites de ressources ;
- des ports publiés ;
- de la collecte des journaux.

```bash
docker compose \
  -f compose.honeypot.yml \
  up -d --build
```

Vérifier :

```bash
docker compose \
  -f compose.honeypot.yml \
  ps opencanary
```

## 11. Contrôles applicatifs

Vérifier au minimum :

1. chargement du frontend ;
2. disponibilité de l’API ;
3. authentification ;
4. autorisations utilisateur ;
5. autorisations administrateur ;
6. création ou lecture d’un diagnostic ;
7. accès à MariaDB depuis le backend ;
8. absence d’erreurs répétées dans les logs.

Exemples locaux :

```bash
curl -I http://127.0.0.1:4200
curl -i http://127.0.0.1:8081
```

Une réponse `401` sur une route protégée peut être normale.

## 12. Contrôles de supervision

Vérifier dans Prometheus que les cibles sont disponibles.

Dans Grafana, vérifier :

- la datasource Prometheus ;
- la datasource Loki ;
- les métriques ;
- les journaux ;
- les règles d’alerte ;
- le point de contact Gmail.

Exemples LogQL :

```logql
{container="cesizen-back"}
```

```logql
{container="cesizen-honeypot"}
```

## 13. Validation du honeypot

Uniquement dans un environnement autorisé :

```bash
curl -i http://127.0.0.1:8088/
```

Vérifier ensuite l’événement dans Loki et le déclenchement de l’alerte configurée.

Aucun scan ou test offensif ne doit être effectué sur un système tiers sans autorisation.

## 14. Contrôles après déploiement

Enregistrer :

- la version Git ;
- les tags des images ;
- la date et l’heure ;
- le résultat des healthchecks ;
- l’état Liquibase ;
- le résultat des tests fonctionnels ;
- l’état des cibles Prometheus ;
- l’état des alertes ;
- le nom de la sauvegarde préalable ;
- les anomalies rencontrées.

Commande de traçabilité :

```bash
git rev-parse HEAD
git describe --tags --always
docker compose images
```

## 15. Retour arrière

Si le déploiement échoue :

1. conserver les logs ;
2. arrêter les services défectueux ;
3. restaurer les images précédentes ;
4. redémarrer la version stable ;
5. restaurer la base uniquement si nécessaire ;
6. vérifier les fonctions essentielles ;
7. ouvrir un ticket d’incident.

Exemple d’arrêt contrôlé :

```bash
docker compose down
```

La commande ne doit pas utiliser `-v`, car cette option supprimerait les volumes persistants.

## 16. Arrêt des services complémentaires

Honeypot :

```bash
docker compose -f compose.honeypot.yml down
```

Supervision :

```bash
docker compose \
  --env-file .env.monitoring \
  -f compose.monitoring.yml \
  down
```

Ne pas utiliser `--remove-orphans` lorsque les différents fichiers Compose partagent le même nom de projet, car cela pourrait supprimer des conteneurs gérés par un autre fichier.

## 17. Critères de validation finale

Le déploiement est validé lorsque :

- l’application est accessible ;
- le backend est sain ;
- MariaDB est disponible ;
- Liquibase est à jour ;
- les fonctions essentielles sont vérifiées ;
- les métriques sont collectées ;
- les logs sont consultables ;
- les alertes sont opérationnelles ;
- une sauvegarde vérifiée existe ;
- aucun secret n’est exposé ;
- la version déployée est tracée.
