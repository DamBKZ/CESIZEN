#!/usr/bin/env bash

set -Eeuo pipefail

CONTAINER_NAME="${DB_CONTAINER:-cesizen-database}"
DATABASE_NAME="${DB_NAME:-cesizen}"
ROOT_USER="${DB_ROOT_USER:-root}"
TEST_DATABASE="${DB_TEST_NAME:-cesizen_restore_test}"

: "${DB_ROOT_PASSWORD:?La variable DB_ROOT_PASSWORD est obligatoire}"

PROJECT_ROOT="$(
  cd "$(dirname "${BASH_SOURCE[0]}")/../.."
  pwd
)"

BACKUP_DIRECTORY="${BACKUP_DIR:-${PROJECT_ROOT}/backups/database}"

if [ "$#" -gt 1 ]; then
  echo "Usage : $0 [sauvegarde.sql.gz]" >&2
  exit 1
fi

if [ "$#" -eq 1 ]; then
  BACKUP_FILE="$1"
else
  BACKUP_FILE="$(
    find "${BACKUP_DIRECTORY}" \
      -type f \
      -name "${DATABASE_NAME}_*.sql.gz" \
      -printf '%T@ %p\n' 2>/dev/null |
      sort -nr |
      head -n 1 |
      cut -d ' ' -f2-
  )"
fi

if [ -z "${BACKUP_FILE:-}" ] || [ ! -f "${BACKUP_FILE}" ]; then
  echo "Erreur : aucune sauvegarde disponible." >&2
  exit 1
fi

if ! docker inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  echo "Erreur : conteneur ${CONTAINER_NAME} introuvable." >&2
  exit 1
fi

if [ "$(docker inspect -f '{{.State.Running}}' "${CONTAINER_NAME}")" != "true" ]; then
  echo "Erreur : conteneur ${CONTAINER_NAME} arrêté." >&2
  exit 1
fi

echo "Vérification de l’intégrité du fichier..."

if ! gzip -t "${BACKUP_FILE}"; then
  echo "Erreur : l’archive est corrompue." >&2
  exit 1
fi

cleanup() {
  local exit_code=$?

  echo "Suppression de la base temporaire ${TEST_DATABASE}..."

  docker exec \
    "${CONTAINER_NAME}" \
    mariadb \
      --user="${ROOT_USER}" \
      --password="${DB_ROOT_PASSWORD}" \
      --execute="
        DROP DATABASE IF EXISTS \`${TEST_DATABASE}\`;
      " >/dev/null 2>&1 || true

  exit "${exit_code}"
}

trap cleanup EXIT INT TERM

echo "Création de la base temporaire ${TEST_DATABASE}..."

docker exec \
  "${CONTAINER_NAME}" \
  mariadb \
    --user="${ROOT_USER}" \
    --password="${DB_ROOT_PASSWORD}" \
    --execute="
      DROP DATABASE IF EXISTS \`${TEST_DATABASE}\`;

      CREATE DATABASE \`${TEST_DATABASE}\`
      CHARACTER SET utf8mb4
      COLLATE utf8mb4_unicode_ci;
    "

echo "Restauration de ${BACKUP_FILE}..."

gzip -dc "${BACKUP_FILE}" |
  docker exec -i \
    "${CONTAINER_NAME}" \
    mariadb \
      --user="${ROOT_USER}" \
      --password="${DB_ROOT_PASSWORD}" \
      "${TEST_DATABASE}"

TABLE_COUNT="$(
  docker exec \
    "${CONTAINER_NAME}" \
    mariadb \
      --batch \
      --skip-column-names \
      --user="${ROOT_USER}" \
      --password="${DB_ROOT_PASSWORD}" \
      --execute="
        SELECT COUNT(*)
        FROM information_schema.tables
        WHERE table_schema = '${TEST_DATABASE}';
      "
)"

if ! [[ "${TABLE_COUNT}" =~ ^[0-9]+$ ]]; then
  echo "Erreur : nombre de tables invalide : ${TABLE_COUNT}" >&2
  exit 1
fi

if [ "${TABLE_COUNT}" -eq 0 ]; then
  echo "Erreur : aucune table restaurée." >&2
  exit 1
fi

CHANGELOG_EXISTS="$(
  docker exec \
    "${CONTAINER_NAME}" \
    mariadb \
      --batch \
      --skip-column-names \
      --user="${ROOT_USER}" \
      --password="${DB_ROOT_PASSWORD}" \
      --execute="
        SELECT COUNT(*)
        FROM information_schema.tables
        WHERE table_schema = '${TEST_DATABASE}'
          AND table_name = 'DATABASECHANGELOG';
      "
)"

if [ "${CHANGELOG_EXISTS}" -ne 1 ]; then
  echo "Erreur : table DATABASECHANGELOG absente." >&2
  exit 1
fi

CHANGESET_COUNT="$(
  docker exec \
    "${CONTAINER_NAME}" \
    mariadb \
      --batch \
      --skip-column-names \
      --user="${ROOT_USER}" \
      --password="${DB_ROOT_PASSWORD}" \
      --database="${TEST_DATABASE}" \
      --execute="
        SELECT COUNT(*)
        FROM DATABASECHANGELOG;
      "
)"

if ! [[ "${CHANGESET_COUNT}" =~ ^[0-9]+$ ]]; then
  echo "Erreur : nombre de changesets invalide : ${CHANGESET_COUNT}" >&2
  exit 1
fi

if [ "${CHANGESET_COUNT}" -eq 0 ]; then
  echo "Erreur : aucun changeset Liquibase restauré." >&2
  exit 1
fi

echo
echo "Vérification réussie."
echo "Sauvegarde testée : ${BACKUP_FILE}"
echo "Nombre de tables restaurées : ${TABLE_COUNT}"
echo "Nombre de changesets Liquibase : ${CHANGESET_COUNT}"
