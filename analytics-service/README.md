# Analytics Service (Placeholder)

A lightweight placeholder service demonstrating asynchronous event consumption from Kafka using Protocol Buffers.

## Stack
- **Java 21**
- **Spring Boot 3**
- **Kafka** (Kafka Listener)
- **Protobuf**
- **Maven**

## Current Status & Purpose 

At present, this service acts as a **stub/demo** to showcase Event-Driven Architecture within the Patient Management System. It does not yet contain complex analytical business logic or a dedicated database. 

Its primary purpose is to prove that events emitted by the `patient-service` (such as `PatientCreated`) can be successfully and asynchronously consumed by another microservice.

### What it does right now:
1. **Listens to Kafka:** Continuously polls the `patient` topic.
2. **Deserializes Protobuf:** Receives the binary payload from Kafka and safely parses it back into a strongly-typed `PatientEvent` Java object using Google Protobuf.
3. **Logs the Event:** Prints the extracted patient ID, name, and email to the application logs to prove successful receipt.

### Future Scope
In a fully-fledged production environment, this service would be expanded to:
- Persist events into an analytical database (like ClickHouse or Elasticsearch).
- Generate reports (e.g., "new registrations per month").
- Expose a GraphQL or REST API for dashboards to visualize the data.

## Configuration
Requires the Kafka broker address:
```yaml
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:port
```

*(This service does not expose a REST API; it operates entirely in the background).*
