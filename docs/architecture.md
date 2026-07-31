# Architecture

PlanIT is organized as a monorepo containing two independently buildable applications. The repository root owns cross-project automation and documentation; application-specific configuration remains inside its application directory.

## Components

```text
Browser
  │
  │ HTTP / JSON
  ▼
frontend/ (React + Vite)
  │
  │ /api/*
  ▼
backend/ (Spring Boot)
  │
  │ JDBC
  ▼
data/planit_db.mv.db (local H2, not versioned)
```

## Repository boundaries

- `backend/` owns the REST API, domain model, recurrence behavior, persistence and the legacy CLI entry point.
- `frontend/` owns presentation, browser state, user interaction and API client code.
- `scripts/` contains cross-application development automation.
- `.github/workflows/` validates both applications independently.
- `data/` contains local runtime state and is excluded from Git.

Neither application imports source files from the other. Their integration boundary is the HTTP API under `/api`.

## Backend packages

- `api`: HTTP controllers and API mapping
- `core`: domain models and application services
- `storage`: persistence implementations
- `config`: Spring web and OpenAPI configuration
- `ui`, `i18n`, `settings`, `util`: legacy console application

The REST application starts through `PlanitApplication`. `Main` remains available only as the legacy console entry point.

## Frontend structure

- `components/common`: reusable UI primitives
- `components/layout`: application shell and page orchestration
- `components/tasks`: task-specific UI
- `components/groups`: category management UI
- `components/view`: calendar and dashboard views
- `services`: HTTP API access

During development, Vite proxies `/api` to the backend on port 8080. Deployments on separate origins can set `VITE_API_BASE_URL`.

## Local data

The `PLANIT_DATABASE_PATH` environment variable controls the H2 database location. Root development commands set it to `data/planit_db`, keeping runtime state outside both application source trees.
