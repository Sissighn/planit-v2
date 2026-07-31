# PlanIT Backend

Spring Boot REST API and H2 persistence layer for PlanIT.

## Run locally

From the repository root, use the shared commands:

```bash
make backend
make check
```

To work inside this directory directly:

```bash
mvn spring-boot:run
mvn verify
```

The standalone command uses `./data/planit_db` relative to this directory. The root `make backend` and `make dev` commands use the repository-level `data/` directory instead. Set `PLANIT_DATABASE_PATH` to choose another location.

## Interfaces

- REST API: http://localhost:8080/api
- OpenAPI document: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console

## Source layout

```text
src/main/java/com/setayesh/planit/
├── api/          HTTP controllers
├── config/       Spring and OpenAPI configuration
├── core/         Domain model and application services
├── storage/      Repository interfaces and persistence
├── i18n/         Translations
├── settings/     Application settings
├── ui/           Legacy command-line interface
└── util/         Shared utilities
```

The web application starts from `PlanitApplication`. `Main` remains the entry point for the legacy command-line interface.
