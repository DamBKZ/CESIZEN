#!/usr/bin/env bash

set -Eeuo pipefail

CONTAINER_NAME="${DB_CONTAINER:-cesizen-database}"
DATABASE_NAME="${DB_NAME:-cesizen}"
DATABASE_USER="${DB_USER:-cesizen}"
BACKUP_DIRECTORY="${BACKUP_DIR:-backups/database}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"

PROJECT_ROOT="$(
  cd "$(dirname "${BASH_SOURCE[0]}")/../.."
  pwd
)"

BACKUP_PATH="${PROJECT_ROOT}/${BACKUP_DIRECTORY}"
TIMESTAMP="$(date -u +"%Y-%m-%dT%H-%M-%SZ")"
BACKUP_FILE="${BACKUP_PATH}/${DATABASE_NAME}_${TIMESTAMP}.sql.gz"
TEMP_FILE="${BACKUP_FILE}.tmp"

mkdir -p "${BACKUP_PATH}"

if ! docker inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  echo "Erreur : conteneur ${CONTAINER_NAME} introuvable." >&2
  exit 1
fi

if [ "$(docker inspect -f '{{.State.Running}}' "${CONTAINER_NAME}")" != "true" ]; then
  echo "Erreur : conteneur ${CONTAINER_NAME} arrêté." >&2
  exit 1
fi

echo "Sauvegarde de la base ${DATABASE_NAME}..."

trap 'rm -f "${TEMP_FILE}"' EXIT

docker exec \
  "${CONTAINER_NAME}" \
  mariadb-dump \
    --user="${DATABASE_USER}" \
    --password="${DB_PASSWORD:?La variable DB_PASSWORD est obligatoire}" \
    --single-transaction \
    --quick \
    --routines \
    --triggers \
    --events \
    --hex-blob \
    --default-character-set=utf8mb4 \
    "${DATABASE_NAME}" \
  | gzip -9 > "${TEMP_FILE}"

gzip -t "${TEMP_FILE}"

mv "${TEMP_FILE}" "${BACKUP_FILE}"
trap - EXIT

find "${BACKUP_PATH}" \
  -type f \
  -name "${DATABASE_NAME}_*.sql.gz" \
  -mtime "+${RETENTION_DAYS}" \
  -delete

echo "Sauvegarde créée : ${BACKUP_FILE}"
echo "Taille : $(du -h "${BACKUP_FILE}" | cut -f1)"
echo "Somme SHA-256 : $(sha256sum "${BACKUP_FILE}" | cut -d ' ' -f1)"
