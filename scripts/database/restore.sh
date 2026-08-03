#!/usr/bin/env bash

set -Eeuo pipefail

CONTAINER_NAME="${DB_CONTAINER:-cesizen-database}"
DATABASE_NAME="${DB_NAME:-cesizen}"
DATABASE_USER="${DB_USER:-cesizen}"

if [ "$#" -ne 1 ]; then
  echo "Usage : $0 <sauvegarde.sql.gz>" >&2
  exit 1
fi

BACKUP_FILE="$1"

if [ ! -f "${BACKUP_FILE}" ]; then
  echo "Erreur : sauvegarde introuvable : ${BACKUP_FILE}" >&2
  exit 1
fi

if ! docker inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  echo "Erreur : conteneur ${CONTAINER_NAME} introuvable." >&2
  exit 1
fi

gzip -t "${BACKUP_FILE}"

echo "ATTENTION : la base ${DATABASE_NAME} sera remplacée."
read -r -p "Saisissez RESTAURER pour confirmer : " confirmation

if [ "${confirmation}" != "RESTAURER" ]; then
  echo "Restauration annulée."
  exit 0
fi

echo "Recréation de la base ${DATABASE_NAME}..."

docker exec \
  "${CONTAINER_NAME}" \
  mariadb \
    --user="${DATABASE_USER}" \
    --password="${DB_PASSWORD:?La variable DB_PASSWORD est obligatoire}" \
    --execute="
      DROP DATABASE IF EXISTS \`${DATABASE_NAME}\`;
      CREATE DATABASE \`${DATABASE_NAME}\`
      CHARACTER SET utf8mb4
      COLLATE utf8mb4_unicode_ci;
    "

echo "Import de la sauvegarde..."

gzip -dc "${BACKUP_FILE}" |
  docker exec -i \
    "${CONTAINER_NAME}" \
    mariadb \
      --user="${DATABASE_USER}" \
      --password="${DB_PASSWORD}" \
      "${DATABASE_NAME}"

echo "Restauration terminée."

docker exec \
  "${CONTAINER_NAME}" \
  mariadb \
    --user="${DATABASE_USER}" \
    --password="${DB_PASSWORD}" \
    --database="${DATABASE_NAME}" \
    --execute="
      SELECT COUNT(*) AS tables_count
      FROM information_schema.tables
      WHERE table_schema = '${DATABASE_NAME}';
    "
