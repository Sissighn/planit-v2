# PlanIT v2
[![CI](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml/badge.svg)](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml)

> **PlanIt** is a modern command-line task management application written in Java. It evolved from a simple to-do list into a robust, database-backed productivity tool with modular architecture and reliable persistence.


---

### Features
- **Persistent Storage:** Tasks are stored in an embedded **H2 database** (replacing the earlier JSON repository).  
- **Auto-Save & Shutdown Hook:** All active and archived tasks are automatically saved when the app exits.  
- **Task Management Core:** Add, edit, sort, mark as done, archive, and clear completed tasks.  
- **Layered Architecture:** Clean separation between the packages.  
- **Modular Repository Interface:** Easily switch between JSON, In-Memory, or Database backends.  
- **Multi-language UI Support (EN / DE):** Ready for further language extensions.  
- **Archive System:** Completed tasks can be safely moved to a persistent archive.


---

## Tech Stack

| Category | Technology |
|-----------|-------------|
| Language | Java 21 |
| Build Tool | Maven |
| Database | H2 (embedded) |
| ORM.     | JDBC
| Logging | SLF4J + SimpleLogger |
| Testing | JUnit 5 |

---

## Project Structure

```bash
planit-v2/
├── pom.xml
├── README.md
├── .gitignore
│
├── src/main/java/com/setayesh/planit/
│   ├── core/
│   ├── i18n/
│   ├── settings/
│   ├── storage/
│   ├── ui/
│   ├── util/
│   └── Main.java
│
├── src/main/resources/
│
└── src/test/java/com/setayesh/planit/
│   ├── core/
│   ├── settings/
│   ├── storage/
│   ├── ui/
│   └── util/
```

---

## Setup & Usage

### Prerequisites
- Java 21 or higher  
- Maven 3.9+  

### Installation
```bash
# Clone the repository
git clone https://github.com/Sissighn/planit-v2.git
cd planit-v2

# Build the project
mvn clean package

# Run the application
java -jar target/planit-1.0.0.jar
```
### Running Tests
```bash
mvn test
```
Tasks are stored in an embedded H2 database (planit_db.mv.db) in your project directory.

---

## Inspecting the Database
Access the H2 web console to view and query your data:
1. Run: 
```bash
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/h2-2.4.240.jar org.h2.tools.Server
```
Open http://localhost:8082 and connect with:

- JDBC URL: jdbc:h2:file:./planit_db
- Username: sa
- Password: (leave empty)

---

## Technical Highlights

- Embedded Database - H2 for zero-configuration data persistence

- Transactional Writes: conn.setAutoCommit(false) + conn.commit() ensures data integrity.

- Graceful Exit: Shutdown hook guarantees persistence even on unexpected termination.

- Clean Error Handling: Clear console feedback for I/O and SQL issues.

- Internationalization - i18n system ready for additional languages

---

## Roadmap
Phase 1: Enterprise Database

 Migrate from H2 to PostgreSQL
 Implement JPA/Hibernate instead of raw JDBC
 Add database migrations with Flyway
 Docker Compose for local PostgreSQL setup

Phase 2: REST API

 Spring Boot REST controllers
 OpenAPI/Swagger documentation
 DTO layer with validation

Phase 3: Modern Frontend

 React + TypeScript UI
 JWT Authentication
 Cloud deployment (AWS/Azure)
 
 ---

## 🪶 License
MIT License © 2025 Setayesh Golshan