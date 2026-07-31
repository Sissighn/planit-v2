# PlanIT

[![CI](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml/badge.svg)](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml)

PlanIT is a task-planning application with a Spring Boot REST API and a React frontend. Both applications live in this repository and are developed and tested together.

## Technology

- Backend: Java 21, Spring Boot, Maven, JDBC and H2
- Frontend: React 19, Vite, Tailwind CSS and FullCalendar
- Tests: JUnit 5, Jest and React Testing Library

## Repository structure

```text
planit-v2/
├── backend/                  Spring Boot application and Maven build
├── frontend/                 React application and npm build
├── docs/                     Architecture documentation
├── scripts/                  Cross-project development scripts
├── .github/workflows/        Backend and frontend CI
├── Makefile                  Common project commands
└── README.md                 Project entry point
```

See [Architecture](docs/architecture.md) for component boundaries and design decisions.

## Requirements

- Java 21 or newer
- Maven 3.9 or newer
- Node.js 22
- npm 10 or newer

## Setup

Install the frontend dependencies once:

```bash
make install
```

Start backend and frontend together:

```bash
make dev
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console

Press `Ctrl+C` to stop both development processes.

You can also start them separately in two terminals:

```bash
make backend
make frontend
```

## Verification

Run the complete backend and frontend quality checks:

```bash
make check
```

Run only the tests:

```bash
make test
```

Create both production builds:

```bash
make build
```

The backend JAR is written to `backend/target/planit-backend-1.0.0.jar`; the frontend output is written to `frontend/dist/`.

## Local data

Root development commands store the embedded H2 database under `data/planit_db.mv.db`. Override the location with `PLANIT_DATABASE_PATH`. Local data and generated build output are ignored by Git.

## License

[MIT License](LICENSE) © 2025–2026 Setayesh Golshan
