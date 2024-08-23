ARG KEYCLOAK_VERSION=25.0

FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION} as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

ENV KC_DB=postgres

# Изменяет рабочий каталог для команд командного процессора
WORKDIR /opt/keycloak

RUN curl -sL https://github.com/aerogear/keycloak-metrics-spi/releases/download/6.0.0/keycloak-metrics-spi-6.0.0.jar
RUN /opt/keycloak/bin/kc.sh build

# ---
FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION}
COPY --from=builder /opt/keycloak/ /opt/keycloak/

WORKDIR /opt/keycloak

# Программа, которая будет запущена после сборки контейнера
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]