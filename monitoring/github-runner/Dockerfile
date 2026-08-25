FROM ghcr.io/actions/actions-runner:latest

USER root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        gnupg \
        jq \
        maven \
        wget \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key \
        | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo \
        "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_24.x nodistro main" \
        > /etc/apt/sources.list.d/nodesource.list \
    && wget -qO /tmp/temurin-keyring.deb \
        https://packages.adoptium.net/artifactory/api/gpg/key/public \
    && gpg --dearmor \
        < /tmp/temurin-keyring.deb \
        > /etc/apt/keyrings/adoptium.gpg \
    && echo \
        "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(. /etc/os-release && echo "$VERSION_CODENAME") main" \
        > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        nodejs \
        temurin-21-jdk \
    && rm -rf \
        /var/lib/apt/lists/* \
        /tmp/temurin-keyring.deb

ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --chown=runner:runner entrypoint.sh /home/runner/entrypoint.sh

RUN chmod +x /home/runner/entrypoint.sh \
    && java -version \
    && node --version \
    && npm --version \
    && mvn --version

USER runner

WORKDIR /home/runner

ENTRYPOINT ["/home/runner/entrypoint.sh"]
