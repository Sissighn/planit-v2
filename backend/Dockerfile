# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn --batch-mode dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn --batch-mode package

FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL org.opencontainers.image.title="PlanIT Backend" \
      org.opencontainers.image.description="Spring Boot API for PlanIT" \
      org.opencontainers.image.source="https://github.com/Sissighn/planit-v2" \
      org.opencontainers.image.licenses="MIT"

RUN addgroup -S planit \
    && adduser -S planit -G planit \
    && mkdir -p /app/data \
    && chown -R planit:planit /app

WORKDIR /app

COPY --from=build --chown=planit:planit \
    /workspace/target/planit-backend-1.0.0.jar ./planit-backend.jar

ENV PLANIT_DATABASE_PATH=/app/data/planit_db

USER planit

EXPOSE 8080
VOLUME ["/app/data"]

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --quiet --spider http://127.0.0.1:8080/api/tasks || exit 1

ENTRYPOINT ["java", "-jar", "/app/planit-backend.jar"]
