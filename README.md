# PlanIT

[![CI](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml/badge.svg)](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=20232A)](https://react.dev/)
[![Node.js 22](https://img.shields.io/badge/Node.js-22-5FA04E?logo=nodedotjs&logoColor=white)](https://nodejs.org/)
[![Docker Compose](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)

PlanIT is a full-stack task-planning application for organizing one-time and recurring work. It combines a responsive React interface with a Spring Boot REST API and local H2 persistence in a single, independently testable monorepo.

## Features

- Create, edit, complete, archive, and delete tasks
- Assign deadlines, priorities, times, and categories
- Schedule daily, weekly, monthly, yearly, and custom recurrence
- Complete or remove individual occurrences without affecting an entire series
- Delete future occurrences or entire recurring series
- Manage task groups and category assignments
- Explore tasks through dashboard and calendar views
- Switch between persistent light and dark themes
- Inspect and test the REST API through Swagger UI
- Store data locally without requiring an external database service

## Architecture

```text
Browser
   |
   | HTTP / JSON
   v
React + Vite frontend
   |
   | /api/*
   v
Spring Boot REST API
   |
   | JDBC
   v
Local H2 database
```

The frontend and backend are independently buildable applications. Their only integration boundary is the HTTP API under `/api`; neither application imports source code from the other.

See [Architecture](docs/architecture.md) for component boundaries and implementation details.

## Technology stack

| Area | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3, Maven, JDBC |
| API | REST, JSON, Spring Web, OpenAPI / Swagger UI |
| Persistence | Embedded H2 database |
| Frontend | React 19, Vite 7, Tailwind CSS 4, DaisyUI |
| Calendar and UI | FullCalendar, Framer Motion, Lucide React |
| Testing | JUnit 5, Jest, React Testing Library |
| Automation | Make, GitHub Actions |

## Repository structure

```text
planit-v2/
|-- backend/              Spring Boot API, domain logic, and persistence
|-- frontend/             React application and browser-side API client
|-- docs/                 Architecture and engineering documentation
|-- scripts/              Cross-application development scripts
|-- .github/workflows/    Backend and frontend CI pipelines
|-- Makefile              Shared development and verification commands
`-- README.md             Project overview
```

Application-specific documentation is available in the [backend](backend/README.md) and [frontend](frontend/README.md) directories.

## Requirements

- Java 21 or newer
- Maven 3.9 or newer
- Node.js 22
- npm 10 or newer
- Make and Bash for the shared root commands

## Quick start

Clone the repository and install the frontend dependencies:

```bash
git clone https://github.com/Sissighn/planit-v2.git
cd planit-v2
make install
```

Start the frontend and backend together:

```bash
make dev
```

The development environment exposes:

| Service | URL |
| --- | --- |
| Web application | http://localhost:5173 |
| REST API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI document | http://localhost:8080/v3/api-docs |
| H2 console | http://localhost:8080/h2-console |

Press `Ctrl+C` to stop both processes. To run each application in a separate terminal, use `make backend` and `make frontend`.

## Run with Docker

Docker Compose builds and runs the complete application with persistent data, health checks, bounded container logs, and automatic restart policies:

```bash
make docker-up
```

Open the application at http://localhost:3000. The backend remains available from the host at http://localhost:8080 for Swagger UI and local diagnostics.

Useful lifecycle commands:

```bash
make docker-ps       # Show container and health status
make docker-logs     # Follow application logs
make docker-down     # Stop the stack without deleting data
```

The named `planit-data` volume keeps the H2 database across container rebuilds, replacements, and regular `docker compose down` operations. Both containers use `restart: unless-stopped`, so Docker restarts them after failures and Docker Engine restarts. On macOS, Docker Desktop must be running; enable **Start Docker Desktop when you sign in** if PlanIT should return automatically after a computer restart.

To customize the published ports, copy the provided environment template before starting the stack:

```bash
cp .env.example .env
```

Do not run `docker compose down --volumes` unless you intentionally want to delete all persisted PlanIT data.

## Development commands

Run commands from the repository root:

| Command | Description |
| --- | --- |
| `make install` | Install frontend dependencies from the lockfile |
| `make dev` | Start the backend and frontend together |
| `make backend` | Start only the Spring Boot API |
| `make frontend` | Start only the Vite development server |
| `make test` | Run all backend and frontend tests |
| `make check` | Run backend verification, frontend linting, tests, and build |
| `make build` | Create backend and frontend production artifacts |
| `make docker-build` | Build both production container images |
| `make docker-up` | Build and start the complete stack in the background |
| `make docker-down` | Stop the stack while preserving application data |
| `make docker-logs` | Follow logs from both containers |
| `make docker-ps` | Show container and health status |

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `PLANIT_DATABASE_PATH` | `data/planit_db` through root commands | Override the local H2 database path |
| `VITE_API_BASE_URL` | Empty | Set the backend origin when frontend and backend are deployed separately |

During local development, Vite proxies `/api` requests to `http://localhost:8080`. Runtime data under `data/` and generated build output are excluded from version control.

## API

The REST API is grouped into two resources:

- `/api/tasks` manages tasks, archives, recurring occurrences, and series operations.
- `/api/groups` manages task categories and their assignments.

With the backend running, use [Swagger UI](http://localhost:8080/swagger-ui.html) for the complete interactive API contract or retrieve the OpenAPI document from [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

## Testing and quality checks

Run the complete local CI equivalent before opening a pull request:

```bash
make check
```

The GitHub Actions workflow runs the following checks for every push and pull request:

- Maven compilation and JUnit tests for the backend
- ESLint for the frontend
- Jest and React Testing Library tests
- Vite production build
- Docker Compose configuration and production image builds

## Production builds

Create both application artifacts with:

```bash
make build
```

Build output:

- Backend: `backend/target/planit-backend-1.0.0.jar`
- Frontend: `frontend/dist/`

## Contributing

Keep frontend and backend changes within their respective directory boundaries. Before committing, run `make check` and update the relevant documentation when behavior, configuration, or public API contracts change.

## License

PlanIT is available under the [MIT License](LICENSE). Copyright © 2025–2026 Setayesh Golshan.
