# My Happy Plants 🌿

**My Happy Plants** is a web application where users can log in and manage their personal plant collection.  
The app combines a local database (your plants, tags, watering history) with plant information fetched from the **Perenual API**.
This school project and is a rework of an older school project also called My Happy Plants.

---

## Features

- User authentication (login)
- Plant catalog/search (plant data + details)
- Personal plant library (plants connected to a user)
- Tagging and watering history tracking
- Server-side rendered UI with Thymeleaf

---

## Installation & Setup
### 1. Clone the repo 
```bash
git clone https://github.com/ninijonsson/my-happy-plants.git
cd my-happy-plants
```

### 2. Environmental variables 
#### macOS/Linux:
```bash
export DB_PASSWORD="your_password"
export PERENUAL_API_KEY="your_api_key"
```

#### Windows (PowerShell):
```bash
$env:DB_PASSWORD="your_password"
$env:PERENUAL_API_KEY="your_api_key"
```

### 3. Build and Run the Application
```bash
# macOS/Linux
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

### 4. Open in Web Browser
Go to: http://localhost:8080

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot (parent version is defined in `pom.xml`)
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA (Hibernate)

**Database**
- PostgreSQL

**External API**
- Perenual API (base URL + API key via env var)

**Testing / Quality**
- JUnit (Spring Boot test starter)
- JaCoCo configured in Maven for coverage reporting

**CI**
- GitHub Actions workflow runs tests on push/PR to `main` and publishes JUnit test report artifacts

---

## Project Structure (high-level)

Source code is organised by domain/package:
- `user/` – authentication, user entity/service/controllers
- `plant/` – plant catalog + controller/service/repository
- `library/` – user library, tags, watering history
- `perenual/` – API client, DTOs and mapping
- `resources/templates/` – Thymeleaf pages
- `resources/static/` – CSS/JS/images

---

## Getting Started (Local Setup)

### Prerequisites
- **Java 21**
- **Maven** (or use the Maven wrapper in the repo)
- **PostgreSQL**
- A **Perenual API key**

### Environment variables
The application expects these environment variables (see `application.yaml`):

- `DB_PASSWORD` – password for the configured database user
- `PERENUAL_API_KEY` – API key for Perenual

Example (macOS/Linux):
```bash
export DB_PASSWORD="your_password"
export PERENUAL_API_KEY="your_perenual_api_key"
```

---

## Kanban Board
- https://da489-grupp1.youtrack.cloud/agiles/195-1/current