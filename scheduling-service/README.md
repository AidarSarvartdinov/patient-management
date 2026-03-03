# Scheduling Service

A microservice for managing doctors' schedules and booking time slots (for a fee).

The main focus was on Domain Driven Design (DDD) and Hexagonal Architecture (Ports and Adapters) to write clean, testable, and maintainable code.

## Stack

- Java 21
- Spring Boot 3, Spring Data JPA
- Spring RestClient (for synchronous requests)
- PostgreSQL
- maven

## Architecture

#### 1. Pragmatic Domain Driven Design

The service was designed with a focus on Rich Domain Model:

- Encapsulation of business logic: Slot entity manages its state by itself. State machine was implemented. Example: FREE -> RESERVED -> CONFIRMED.

- Invariant protection: Validation happens during the aggregate construction and status changes, setters absent.


#### 2. Hexagonal Architecture (Ports and Adapters)

The service is divided into layers. The dependencies are directed inside the domain core.

- domain/ - service core (business logic, models, exceptions).
- domain/port - contracts for working with infrastructure (e.g PaymentGateway).
- infrastructure - ports implementations for working with external services (e.g. BillingServiceClient for integration with billing microservice via RestClient).
- api - HTTP controllers and DTO.


## Architectural Trade-offs

In pure DDD domain model cannot have external dependencies including JPA annotations. However, in this service it was decided to combine Domain Model and Persistence Model into one Slot class. Reasons:

- Avoiding boiler plate: additional model and mapper would increase the code size without any profit for the microservice with such size
- Dirty Checking and Optimistic Locking support

## Solution of distributed transaction problem

Slot booking requires writing to the database and synchronous HTTP call of Billing Service. Local transactions with compensating action:

1. Transaction: Slot status is set to RESERVED in database.
2. HTTP call for Billing service out of transaction.
3. If the call throws an error, compensating transaction cancelReservation is called to free the slot.


## Main Functionalities

- Time slots creation 
- Slots booking with automatic request for Stripe session creation to billing-service
- Slot's state machine


## Package Structure

```
src/main/java/com/pm/scheduling_service/
 ├── api/                          # Web layer (Driven Adapters)
 │    ├── controller/              # REST Controllers
 │    └── dto/                     # API Request/Response objects
 ├── domain/                       # Core Business Logic
 │    ├── enums/                   # Ubiquitous Language enums (e.g., SlotStatus)
 │    ├── exception/               # Domain-specific exceptions
 │    ├── model/                   # Rich Entities / Aggregates
 │    ├── port/                    # Outbound interfaces (Driving Ports)
 │    ├── service/                 # Domain Services
 └── infrastructure/               # Implementation details (Driving Adapters)
      ├── client/                  # HTTP Clients (e.g., BillingServiceClient)
      └── config/                  # Framework configurations
```
