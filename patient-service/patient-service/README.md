# Patient Service

A core microservice responsible for managing patient records (CRUD operations) and publishing domain events to the messaging broker.

## Stack
- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL 17**
- **Kafka** (Event Publisher)
- **Maven**

## Architecture & Responsibilities

This service acts as the source of truth for all patient-related data. It provides a RESTful API for client interactions and an asynchronous event-driven interface for internal microservices.

### 1. Patient Management (REST API)
Handles standard CRUD operations:
- Creating new patients (with email uniqueness validation).
- Updating patient details.
- Retrieving patient profiles.
- Deleting records.

The API is secured; incoming requests are expected to have their JWT tokens pre-validated by the API Gateway before reaching this service.

### 2. Event-Driven Architecture (Kafka)
To maintain loose coupling with other services, `patient-service` relies on an Event-Driven Architecture.
- When a new patient is successfully created in the database, the service serializes a `PatientEvent`.
- Instead of using standard JSON, the event is serialized into a binary payload using **Google Protobuf** (`patient.events.PatientEvent`).
- The binary payload is published directly to the `patient` Kafka topic using `KafkaTemplate`.
- Other services (like `analytics-service`) can consume these events asynchronously without synchronous HTTP dependencies.

*Why Protobuf?*: Protobuf offers faster serialization, smaller payload sizes, and strict schema enforcement compared to traditional JSON, which is crucial for high-throughput streaming via Kafka.

## API Endpoints

All endpoints are prefixed with `/api/patients` when accessed via the API Gateway.

| Method | Path | Description | Access |
|---|---|---|---|
| `GET` | `/patients` | Retrieve a list of patients | Authenticated (JWT) |
| `POST` | `/patients` | Register a new patient | Authenticated (JWT) |
| `PUT` | `/patients/{id}` | Update patient details | Authenticated (JWT) |
| `DELETE` | `/patients/{id}` | Delete a patient | Authenticated (JWT) |

*(Detailed API documentation is available via Swagger UI at `/swagger-ui.html` or `/api-docs/patients/swagger-ui` through the Gateway)*

## Package Structure

```
src/main/java/com/pm/patient_service/
 ├── controller/                   # REST Controllers
 ├── dto/                          # Request/Response objects
 ├── exception/                    # Global exception handler & custom exceptions
 ├── kafka/                        # Kafka Producer configuration
 ├── mapper/                       # DTO to Entity mappers
 ├── model/                        # JPA Entities
 ├── repository/                   # Spring Data JPA repositories
 └── service/                      # Business logic
```
