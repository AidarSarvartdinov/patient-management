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

### 2. Token Validation (`/validate`)
Provides an internal endpoint used primarily by the **API Gateway**.
- Parses the incoming `Bearer` token.
- Cryptographically verifies the signature using a shared `JWT_SECRET`.
- Validates the token's expiration date.
- Returns `200 OK` if valid, or `401 Unauthorized` if invalid/expired.

### 3. Test Credentials
User registration is currently not supported. To test the API, you can use the following pre-configured test user:

- **Email**: `testuser@test.com`
- **Password**: `password123`

## API Endpoints

| Method | Path | Description | Access |
|---|---|---|---|
| `POST` | `/login` | Authenticate user and receive JWT | Public |
| `GET` | `/validate` | Verify the validity of a JWT | Internal (Gateway) |

*(Detailed API documentation is available via Swagger UI at `/swagger-ui.html` or `/api-docs/auth/swagger-ui` through the Gateway)*

## Configuration

Requires the following environment variables to run securely:

```yaml
JWT_SECRET=your_base64_encoded_secret_key
```
