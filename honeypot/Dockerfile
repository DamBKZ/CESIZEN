FROM python:3.13-slim

RUN groupadd --system opencanary \
    && useradd --system --gid opencanary --home-dir /opt/opencanary opencanary \
    && pip install --no-cache-dir opencanary

WORKDIR /opt/opencanary

COPY opencanary.conf /etc/opencanaryd/opencanary.conf

RUN mkdir -p /var/log/opencanary \
    && chown -R opencanary:opencanary \
        /opt/opencanary \
        /var/log/opencanary \
        /etc/opencanaryd

EXPOSE 2222 8088 2121

CMD ["opencanaryd", "--dev", "--uid=opencanary", "--gid=opencanary"]

