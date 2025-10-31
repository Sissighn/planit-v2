# 🌍 PlanIT v2
[![CI](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml/badge.svg)](https://github.com/Sissighn/planit-v2/actions/workflows/ci.yml)

> A modern, personal **task & time management CLI app** built with **Java 21**, designed for developers and productivity lovers.

---

## ✨ Overview

**PlanIT v2** is the next-generation version of my original *PlanITt* project.  
It’s a **clean, refactored Maven application** that combines simplicity with solid engineering principles.

### 🎯 Features
- ✅ Create, view, and manage tasks directly from your terminal  
- 💾 Persistent storage using **JSON files** (no database needed)  
- 🌍 Multi-language support (German / English via i18n system)  
- 🧩 Modular architecture (`core`, `ui`, `storage`, `util`)  
- 🧪 JUnit 5 tests & clean Maven build  

---

## 🧠 Tech Stack

| Category | Technology |
|-----------|-------------|
| Language | Java 21 |
| Build Tool | Maven |
| Data Format | JSON (Jackson) |
| Logging | SLF4J + SimpleLogger |
| Testing | JUnit 5 |
| IDE | VS Code / IntelliJ |

---

## 🧰 Project Structure

```bash
planit-v2/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/setayesh/planit/
│   │   │   ├── core/
│   │   │   ├── storage/
│   │   │   ├── ui/
│   │   │   ├── util/
│   │   │   └── Main.java
│   └── resources/tasks.json
├── test/java/com/setayesh/planit/
│   └── TaskServiceTest.java
└── README.md
```

---

## 🚀 Run Locally

Clone the project:

```bash
git clone git@github.com:setayesh/planit-v2.git
cd planit-v2

mvn clean package
java -jar target/planit-1.0.0.jar
```

---

## 🧪 Run all tests with:

```bash
mvn test
```

---

## 📈 Roadmap

Implement recurring tasks
Build a GUI version (JavaFX or React frontend)
Cloud sync (REST API or Firebase)
 
 ---
 
## 👩‍💻 Author
Setayesh Golshan

---

## 🪶 License
This project is open-source under the MIT License.