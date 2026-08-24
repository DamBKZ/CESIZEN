FROM python:3.14-slim

RUN apt-get update \
    && apt-get upgrade -y \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system opencanary \
    && useradd \
        --system \
        --gid opencanary \
        --home-dir /opt/opencanary \
        opencanary \
    && python -m pip install \
        --no-cache-dir \
        --upgrade \
        pip \
        "setuptools==78.1.1" \
        "wheel>=0.46.2" \
    && python -m pip install \
        --no-cache-dir \
        opencanary \
    && python -m pip install \
        --no-cache-dir \
        --upgrade \
        "cryptography>=50.0.0" \
        "jaraco.context>=6.1.0" \
        "msgpack>=1.2.1" \
        "setuptools==78.1.1" \
        "wheel>=0.46.2" \
    && find /usr/local/lib/python3.13/site-packages/setuptools/_vendor \
        -type d \
        -name 'jaraco.context-*.dist-info' \
        -exec rm -rf {} + \
    && find /usr/local/lib/python3.13/site-packages/setuptools/_vendor \
        -type d \
        -name 'wheel-*.dist-info' \
        -exec rm -rf {} + \
    && rm -rf \
        /usr/local/lib/python3.13/site-packages/pkg_resources/tests \
        /usr/local/lib/python3.13/site-packages/setuptools/tests \
        /usr/local/lib/python3.13/site-packages/pip/_vendor/bom.cdx.json

WORKDIR /opt/opencanary

COPY opencanary.conf /etc/opencanaryd/opencanary.conf

RUN mkdir -p /var/log/opencanary \
    && chown -R opencanary:opencanary \
        /opt/opencanary \
        /var/log/opencanary \
        /etc/opencanaryd

EXPOSE 2121 2222 8088

CMD ["opencanaryd", "--dev", "--uid=opencanary", "--gid=opencanary"]
