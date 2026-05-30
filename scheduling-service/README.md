# Scheduling Service

A microservice for booking time slots (for a fee).

The main focus was on **Domain Driven Design (DDD)** and **Hexagonal Architecture (Ports and Adapters)** to write clean, testable, and maintainable code.
The service reliably handles domain events from a **billing service** (via Kafka) using the **Transactional Inbox pattern** to guarantee idempotent processing.

## Stack

- Java 21
- Spring Boot 3, Spring Data JPA
- PostgreSQL
- Kafka
- maven

## Architecture

#### 1. Pragmatic Domain Driven Design

The service was designed with a focus on **Rich Domain Model**:

- Encapsulation of business logic: The `Slot` aggregate manages its own state machine: `FREE → RESERVED → CONFIRMED`.

- Invariant protection: All validation happens inside the aggregate (constructor, state‑change methods). No public setters.

**Trade‑off**: The domain model and the persistence model are intentionally merged into a single `Slot` entity (JPA annotations are present). This avoids boilerplate code (mappers, DTOs) and still allows dirty checking and optimistic locking – a pragmatic choice for a microservice of this size.


#### 2. Hexagonal Architecture (Ports and Adapters)

The code is split into independent layers, with dependencies pointing **inward** toward the domain.

- **`domain/`** – core business logic (entities, value objects, domain services, exceptions).
- **`domain/port/`** – interfaces that the domain needs (e.g. `SlotRepository`).
- **`application/`** – use cases (application services) that orchestrate the domain and manage transactions.
- **`infrastructure/`** – implementations of the ports (JPA repositories, Kafka consumers, schedulers).
- **`api/`** – REST controllers (driving adapters).


#### 3. Reliable Event Processing with Inbox Pattern

To handle `PAYMENT_SUCCESS` events from the billing service, the service uses a **Transactional Inbox**:

- An `Inbox` table stores the id of every successfully processed event.
- The event consumer checks this table **before** processing – if the event id already exists, the event is skipped (idempotency).
- The business operation (`confirmBooking`) and the `Inbox` record are saved **in the same database transaction** (managed by the use case).  
  This guarantees exactly‑once processing even if the service crashes.

#### 4. Automatic Inbox Cleanup

A scheduled job runs every day at 3 AM and deletes `Inbox` records older than 30 days.  
The cleanup logic resides in an **application use case** (`InboxCleanupUseCase`), while the scheduling trigger (`@Scheduled`) is placed in the infrastructure layer – keeping the core free of framework annotations.


## Main Functionalities

- Create time slots (admin only).
- Reserve a slot
- Confirm a booking – upon receiving `PAYMENT_SUCCESS` from Kafka, the slot state changes to `CONFIRMED`.
- Idempotent event processing via Inbox pattern.

## Package Structure

```
src/main/java/com/pm/scheduling_service/
 ├── api/                          # Web layer (Driven Adapters)
 │    ├── controller/              # REST Controllers
 │    └── dto/                     # API Request/Response objects
 |── application/                  # Use cases (application layer)
 │    ├── scheduler/               # Cleanup use case
 │    └── service/                 # Other use cases (e.g. ConfirmBookingUseCase)
 ├── domain/                       # Core Business Logic
 │    ├── enums/                   # Ubiquitous Language enums (e.g., SlotStatus)
 │    ├── exception/               # Domain-specific exceptions
 │    ├── model/                   # Rich Entities / Aggregates
 │    ├── port/                    # Outbound interfaces (repositories, gateway)
 └── infrastructure/               # Implementation details (Driving Adapters)
      ├── client/                  # HTTP Clients (e.g., BillingServiceClient)
      ├── messaging/               # Kafka consumer, Inbox entity & repository
      ├── persistence/             # JPA repositories (adapters)
      └── scheduler/               # Scheduler trigger (cleanup)
```

## Key Design Decisions

- **Single database transaction** for `confirmBooking` + `Inbox` insert → avoids dual write problems.
- **Explicit use cases** in the `application` package – even for simple operations – to keep the transaction boundary visible and the architecture uniform.
