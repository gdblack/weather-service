Weather Service (Kotlin + Spring Boot)

## Overview

Weather Service is a real‑world learning project built with Kotlin and Spring Boot. It demonstrates common backend patterns and capabilities you’ll encounter in production systems:

- RESTful APIs with Spring MVC
- CRUD and database persistence with Spring Data JPA (PostgreSQL)
- API security foundations (Spring Security + JWT plumbing)
- Caching with Spring Cache (Caffeine)
- External API consumption via WebClient (WebFlux)
- Interactive API docs with SpringDoc OpenAPI (Swagger UI)

The codebase is intentionally straightforward, emphasizing clear patterns and Kotlin idioms to help you learn by example.


## Features at a Glance

- Health endpoint for liveness checks
- Test User API demonstrating basic CRUD patterns
- Weather cache domain showing caching and repository patterns
- Wiring for JWT support (keys and expiration configured) and permissive security for local development
- Swagger UI to explore the API


## Tech Stack

- Language: Kotlin (JVM)
- Runtime: Java 17
- Framework: Spring Boot 4.x
- Persistence: Spring Data JPA (Hibernate) + PostgreSQL
- Security: Spring Security, JWT (jjwt)
- HTTP Client: Spring WebFlux (WebClient)
- Caching: Spring Cache + Caffeine
- API Docs: SpringDoc OpenAPI
- Build: Gradle (Kotlin DSL)


## Project Layout

```
src/main/kotlin/org/example/weatherservice/
├── config/          # Configuration classes
├── controller/      # REST endpoints
├── domain/          # Entities and repositories
├── dto/             # Data transfer objects
├── security/        # JWT and security
└── service/         # Business logic
```
```
- src/main/kotlin/org/example/weatherservice/WeatherServiceApplication.kt — main application entrypoint
- src/main/kotlin/org/example/weatherservice/config/SecurityConfig.kt — security configuration (permitAll for dev)
- src/main/kotlin/org/example/weatherservice/controller/* — REST controllers (health, user test, weather test)
- src/main/kotlin/org/example/weatherservice/domain/model/* — JPA entities (User, WeatherCache)
- src/main/kotlin/org/example/weatherservice/domain/repository/* — Spring Data repositories
- src/main/resources/application.yml — application configuration (DB, cache, Swagger, JWT)
```

## Key Endpoints (dev/test)

- GET /actuator/health or GET /health (see HealthController) — basic health check
- Users (test controller)
  - GET  /api/test/users — list users
  - POST /api/test/users — create user (conflict on duplicate username/email)
  - GET  /api/test/users/{username} — get by username
  - GET  /api/test/users/email/{email} — get by email
  - GET  /api/test/users/count — count users
  - DELETE /api/test/users/{username} — delete user by username

## Swagger/OpenAPI

- OpenAPI JSON: GET /api-docs
- Swagger UI:   GET /swagger-ui.html


## Configuration

See src/main/resources/application.yml for full settings. Highlights:

- Server port: 8080
- PostgreSQL datasource (dev default):
  - url: jdbc:postgresql://localhost:5432/weatherdb
  - username: postgres
  - password: postgres
  - ddl-auto: update (dev only)
- Cache: Caffeine (maximumSize=500, expireAfterWrite=300s)
- Weather API config: env var WEATHER_API_KEY (fallback YOUR_API_KEY_HERE)
- JWT config: env var JWT_SECRET (fallback development value), expiration 24h

You can override any property via environment variables or an application-local.yml.


### Dependencies (from build.gradle.kts)

- Spring Boot Starters
  - spring-boot-starter-webmvc (REST)
  - spring-boot-starter-webflux (WebClient)
  - spring-boot-starter-data-jpa (JPA/Hibernate)
  - spring-boot-starter-security (Spring Security)
  - spring-boot-starter-validation (Jakarta Bean Validation)
  - spring-boot-starter-cache (Spring Cache abstraction)
- Kotlin
  - kotlin-reflect
  - kotlinx-coroutines-core, kotlinx-coroutines-reactor
  - jackson-module-kotlin
- Security/JWT
  - io.jsonwebtoken:jjwt-* (api, impl, jackson)
- Caching
  - com.github.ben-manes.caffeine:caffeine
- OpenAPI
  - org.springdoc:springdoc-openapi-starter-webmvc-ui
- Database driver
  - org.postgresql:postgresql (runtimeOnly)
- Test
  - JUnit Platform, kotlin-test-junit5
  - spring-boot-starter-*-test variants


## Getting Started (Developer Onboarding)

### Prerequisites

- Java 17 (JDK)
- Git
- Gradle wrapper (included) — no local Gradle install required
- PostgreSQL 14+ (local or Docker)

1) Clone the repo

```
git clone <your-fork-or-repo-url>
cd weather-service
```

2) Start PostgreSQL

Option A: Local installation — create a database and user matching application.yml defaults:

```
createuser postgres --superuser   # if not present
createdb weatherdb
# Ensure the 'postgres' user has password 'postgres' for local dev, or update application.yml/env.
```

Option B: Docker

```
docker run --name pg-weather \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=weatherdb \
  -p 5432:5432 -d postgres:16
```

3) Set environment variables

Create `src/main/resources/application-local.yml` with your secrets:
```yaml
weather:
  api:
    key: YOUR_API_KEY_HERE

jwt:
  secret: YOUR_JWT_SECRET_HERE
```

**Get OpenWeatherMap API Key:**
1. Sign up at https://openweathermap.org/api
2. Copy your API key
3. Paste into `application-local.yml`

4) Build and run

Using Gradle wrapper:

```
./gradlew clean build
./gradlew bootRun
```

The app will start on http://localhost:8080

5) Explore the API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

6) Run tests

```
./gradlew test
```


## Security

- Current configuration (SecurityConfig) permits all requests for ease of local development.
- JWT settings are wired (secret and expiration). Add authentication filters and method/security rules when you start enforcing auth.
- Do NOT use the example JWT secret in production. Supply a strong key via env or secrets manager.


## Caching

- Spring Cache with a Caffeine provider is enabled. Default policy: maximumSize=500, expireAfterWrite=300s.
- Add @EnableCaching on a configuration if not already active, then annotate service methods with @Cacheable/@CacheEvict as needed.


## Database & Migrations

- JPA/Hibernate is set to ddl-auto=update for development convenience.
- For real projects consider adding Flyway or Liquibase migrations and switching ddl-auto off in non-dev environments.


## Developer Workflow Tips

- Kotlin + Spring: favor data classes for DTOs and entities, and extension functions for helpers.
- Keep controllers thin; push logic to services; keep repositories focused on data access.
- Use WebClient (from WebFlux) to call external APIs (e.g., OpenWeatherMap).
- Validate request payloads with Bean Validation annotations.
- Prefer ResponseEntity builders for clear status codes.


## Troubleshooting

- Port 8080 already in use: change server.port in application.yml or free the port.
- DB connection errors: verify PostgreSQL is running and credentials match application.yml or env overrides.
- Swagger 404: ensure app is running and check /api-docs and /swagger-ui.html endpoints.


## License

This project is provided for learning purposes. Add your preferred license if you plan to distribute.
