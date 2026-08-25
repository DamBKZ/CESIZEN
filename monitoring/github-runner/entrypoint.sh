#!/usr/bin/env bash
set -euo pipefail

RUNNER_HOME="/home/runner"
RUNNER_DATA="${RUNNER_HOME}/runner-data"

if [[ -z "${REPO_URL:-}" ]]; then
  echo "REPO_URL est obligatoire."
  exit 1
fi

mkdir -p "${RUNNER_DATA}/_work"

cd "${RUNNER_HOME}"

if [[ -f "${RUNNER_DATA}/.runner" ]]; then
  cp "${RUNNER_DATA}/.runner" .runner
  cp "${RUNNER_DATA}/.credentials" .credentials

  if [[ -f "${RUNNER_DATA}/.credentials_rsaparams" ]]; then
    cp "${RUNNER_DATA}/.credentials_rsaparams" .credentials_rsaparams
  fi

  echo "Configuration persistante du runner restaurée."
else
  if [[ -z "${RUNNER_TOKEN:-}" ]]; then
    echo "RUNNER_TOKEN est obligatoire pour la première configuration."
    exit 1
  fi

  ./config.sh \
    --unattended \
    --url "${REPO_URL}" \
    --token "${RUNNER_TOKEN}" \
    --name "${RUNNER_NAME:-cesizen-sonar-runner}" \
    --labels "${RUNNER_LABELS:-sonar,cesizen,docker}" \
    --work "${RUNNER_DATA}/_work" \
    --replace

  cp .runner "${RUNNER_DATA}/.runner"
  cp .credentials "${RUNNER_DATA}/.credentials"

  if [[ -f .credentials_rsaparams ]]; then
    cp .credentials_rsaparams "${RUNNER_DATA}/.credentials_rsaparams"
  fi

  echo "Configuration du runner sauvegardée."
fi

exec ./run.sh
