# API Gateway

The single entry point for all external client requests to the Patient Management System. It handles routing and cross-cutting concerns like security before requests reach the internal microservices.

## Stack
- **Java 21**
- **Spring Boot 3**
- **Spring Cloud Gateway**
- **Maven**

## Architecture & Responsibilities

In a distributed environment, having a central API Gateway simplifies client development and improves security. The clients only need to know one URL (`http://localhost:4004`), and the Gateway routes the traffic to the appropriate backend service based on the URL path.

### 1. Request Routing
The gateway uses `application.yml` configurations to inspect incoming HTTP requests and forward them to the correct microservice container. 
For example:
- `/auth/**` -> `auth-service`
- `/api/patients/**` -> `patient-service`
- `/api/slots/**` -> `scheduling-service`
- `/api/payments/**` -> `billing-service`

It also abstracts the internal network layout, so clients don't need to know the internal Docker container hostnames or ports.

### 2. Global Security Filtering (JWT)
Instead of implementing JWT validation logic inside every single microservice (which duplicates code and increases coupling), the API Gateway acts as a central security checkpoint.

It uses a custom Spring Cloud Gateway filter: **`JwtValidationGatewayFilterFactory`**.
- Intercepts requests destined for protected routes (e.g., `/api/patients`).
- Extracts the `Bearer` token from the `Authorization` header.
- Makes a synchronous web call (`WebClient`) to the `auth-service`'s `/validate` endpoint.
- If the token is valid, it forwards the request to the target service.
- If invalid or missing, it immediately rejects the request with a `401 Unauthorized` status.

### 3. Swagger UI Aggregation
The gateway orchestrates the OpenAPI documentation routes. It rewrites API paths (`/api-docs/*/swagger-ui`) so that developers can access the interactive Swagger documentation for any underlying microservice through a single entry point.

## Configuration
Requires the internal URL of the authentication service to validate tokens:
```yaml
AUTH_SERVICE_URL=http://auth-service:port
```
