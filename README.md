# To-Do List App

A RESTful API To-Do List application built with Java and Spring Boot. This project is a professional demonstration of best practices in modern backend development: scalable REST APIs, security, clean code, database integration, and automated testing.

---

## Table of Contents

- [Project Purpose](#project-purpose)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [Authentication](#authentication)
- [Testing](#testing)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)
- [Acknowledgments](#acknowledgments)
- [Developer Highlights](#developer-highlights)

---

## Project Purpose

Demonstrates:
- Building secure, maintainable RESTful APIs with Java and Spring Boot
- Implementing authentication with Spring Security
- Clean architecture: controllers, services, repositories, models
- Database interaction using JDBC and HSQLDB (with SQL scripts)
- Test-driven development with isolated config and coverage
- Professional use of version control (Git), build tools, and documentation

---

## Features

- Register new users (`POST /api/users/register`)
- User authentication (Spring Security, HTTP Basic)
- CRUD operations for tasks:
  - Create, read, update, delete tasks
- Relational database integration (JDBC + HSQLDB)
- Modular, scalable package structure
- Full automated test suite (JUnit, AssertJ, Spring Security test)
- Designed for API clients (Postman, curl, CLI)

---

## Technology Stack

- Java 17+
- Spring Boot 3.4
- Spring Security
- JDBC
- HSQLDB (database used for SQL scripts and testing)
- Maven (with Maven Wrapper)
- JUnit, AssertJ, Spring Security Test
- Git

---

## Project Structure

```text
To-Do-List-App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── todo/
│   │   │       ├── controller/    # REST controllers (API endpoints, including registration)
│   │   │       ├── service/       # Business logic
│   │   │       ├── repository/    # JDBC data access
│   │   │       ├── model/         # Domain models/entities
│   │   │       └── ToDoListApplication.java # Entry point
│   │   └── resources/
│   │       └── application.properties
│   ├── test/
│   │   ├── java/
│   │   │   └── todo/
│   │   │       └── app/
│   │   │           ├── ToDoListApplicationTests.java
│   │   │           ├── config/
│   │   │           ├── repository/
│   │   │           ├── service/
│   │   │           └── web/
│   │   └── resources/
│   │       └── application-test.properties
├── pom.xml
├── .gitignore
├── LICENSE (Apache License 2.0)
└── README.md
```

**Test Classes & Packages:**
- `todo/app/ToDoListApplicationTests.java`: General app test
- `todo/app/config/`: Test configuration classes
- `todo/app/repository/`: Repository (DAO) tests
- `todo/app/service/`: Service layer tests
- `todo/app/web/`: Web layer (controller, API) tests

---

## Installation

### Prerequisites

- Java 17 or higher
- Maven (or use included Maven Wrapper)
- No external DB setup needed (uses HSQLDB in-memory for dev/test)

### Clone the Repository

```bash
git clone https://github.com/MPfria02/To-Do-List-App.git
cd To-Do-List-App
```

### Build the Project

Using Maven Wrapper (recommended):

```bash
./mvnw clean install
```
Or with Maven:
```bash
mvn clean install
```

---

## Usage

### Run the Application

```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/`.

### API Endpoints

- `POST /api/users/register` — Register a new user (see Authentication section)
- `POST /api/tasks` — Create a new task
- `GET /api/tasks` — Get all tasks
- `GET /api/tasks/{id}` — Get a task by ID
- `PUT /api/tasks/{id}` — Update a task
- `DELETE /api/tasks/{id}` — Delete a task

(See source for full request/response details.)

#### Example: Register a New User

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"newpass"}' \
  http://localhost:8080/api/users/register
```

#### Example: List Tasks (after login)

```bash
curl -u username:password http://localhost:8080/api/tasks
```

---

## Authentication

- HTTP Basic Authentication (Spring Security)
- Register new users at `POST /api/users/register`
- Default/test users can be set up in `application.properties`
- All API endpoints are protected

---

## Testing

- Test classes in `src/test/java/todo/app/` and subpackages
- Isolated test config in `src/test/resources/application-test.properties`
- Uses HSQLDB for reliable, in-memory tests
- Run all tests:

```bash
./mvnw test
# or
mvn test
```

---

## Configuration

- Main app config: `src/main/resources/application.properties`
- Test config: `src/test/resources/application-test.properties`
- Database: HSQLDB (see `pom.xml` dependency and SQL scripts)
- All DB schema and data setup is handled by Spring/JDBC scripts

---

## Contributing

Contributions are welcome! Please:
1. Fork the repo and create a feature branch.
2. Write clear, maintainable, and well-documented code.
3. Ensure all tests pass (`mvn test`).
4. Open a pull request with a clear description of your changes.

---

## License

Distributed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

---

## Contact

Created by [MPfria02](https://github.com/MPfria02)

---

## Acknowledgments

- Spring Boot, Spring Security, and HSQLDB documentation
- Community guides on test-driven Java backend development

---

## Developer Highlights

This project demonstrates:
- Modern Java REST API development with Spring Boot and Spring Security
- Secure user management and authentication
- Clean, scalable, maintainable multi-layered architecture
- Real database integration via JDBC and HSQLDB (SQL proficiency)
- Comprehensive automated tests: app, web, service, repository, and config layers
- Professional use of Maven, Git, and industry-standard tools
- Isolated test environments for reliable, reproducible results
