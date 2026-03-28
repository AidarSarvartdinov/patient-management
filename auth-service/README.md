# Auth Service

A dedicated microservice responsible for user authentication and authorization token management. 

## Stack
- **Java 21**
- **Spring Boot 3**
- **Spring Security**
- **JWT** (io.jsonwebtoken)
- **Spring Data JPA**
- **PostgreSQL 17**
- **Maven**

## Architecture & Responsibilities

In a microservices architecture, managing sessions globally is complex. This service solves that by issuing stateless JWTs that can be cryptographically verified by any other service (or the API Gateway) without needing to query the database.

### 1. Authentication (`/login`)
- Accepts user credentials (email/password).
- Validates them against the local `authdb` PostgreSQL database.
- Generates a signed **JWT (JSON Web Token)** containing user claims.
- Returns the token to the client.

### 2. Registration (`/register`)
- Creates a new user with provided credentials and PATIENT role.
- Generates a signed **JWT (JSON Web Token)** containing user claims.
- Returns the token to the client.

### 3. Test Credentials
You can use the following pre-configured test user with ADMIN role:

- **Email**: `testuser@test.com`
- **Password**: `password123`

## API Endpoints

| Method | Path | Description | Access |
|---|---|---|---|
| `POST` | `/login` | Authenticate user and receive JWT | Public |
| `POST` | `/register` | Creates a new user and receive JWT | Public |
| `GET` | `/.well-known/jwks.json` | Provides a public RSA key | Internal

*(Detailed API documentation is available via Swagger UI at `/swagger-ui.html` or `/api-docs/auth/swagger-ui` through the Gateway)*

