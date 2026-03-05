# Billing Service

A microservice responsible for processing payments and handling transactions via **Stripe**. It manages the creation of payment sessions, tracks transaction statuses, and processes asynchronous webhooks from Stripe to keep the system state synchronized.

## Stack
- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL 17**
- **Stripe API** (Stripe Java SDK)
- **Maven**

## Architecture & Responsibilities

The primary role of the `billing-service` is to abstract external payment processing from the rest of the system (such as `scheduling-service`). It serves as an internal adapter to the Stripe ecosystem.

### 1. Payment Initiation (Synchronous)
When another service needs to process a payment (e.g., booking a slot), it makes a synchronous HTTP `POST` request to `/payments`.
- The service creates a `PENDING` payment record in the database.
- Uses the Stripe SDK to generate a **Stripe Checkout Session**.
- Returns the generated Checkout URL back to the caller service, which then redirects the client to the Stripe-hosted payment page.

### 2. Stripe Webhooks Handling (Asynchronous)
Once the user completes the payment (or fails/abandons it), Stripe sends an asynchronous HTTP POST to the `/stripe/webhook` endpoint.
- Validates the cryptographic `Stripe-Signature` to ensure the request is genuinely from Stripe.
- Extracts the payload to update the internal Payment status (`PAID`, `FAILED`, `EXPIRED`).
- Handled events:
  - `checkout.session.completed`
  - `payment_intent.payment_failed`
  - `checkout.session.expired`

### 3. Automated Reconciliation Job
To gracefully handle scenarios where a Stripe webhook might be missed or delayed (e.g., network timeout), a scheduled task (`@Scheduled`) runs every 15 minutes.
- It finds `PENDING` payments older than 20 minutes in the database.
- Actively polls the Stripe API (`Session.retrieve`) to get the actual status of the Session.
- Synchronizes the local database with the true status from Stripe.

## API Endpoints

| Method | Path | Description | Access | 
|---|---|---|---|
| `POST` | `/payments` | Creates a new Stripe checkout session | Internal Microservices (Scheduling) |
| `POST` | `/stripe/webhook` | Receives async payment events from Stripe | External (Stripe servers only) |

*(Detailed API documentation is available via Swagger UI at `/swagger-ui.html` when the service is running)*

## Package Structure

```
src/main/java/com/bs/billing_service/
 ├── controller/                   # REST Controllers (Payment, Webhooks)
 ├── dto/                          # DTOs for internal service communication
 ├── enums/                        # Payment statuses and failure reasons
 ├── model/                        # JPA Entities (e.g., Payment)
 ├── repository/                   # Spring Data JPA repositories
 ├── service/                      # Business logic (PaymentService, WebhookService)
 └── util/                         # Stripe Client abstraction
```

## Setup & Configuration

This service requires active Stripe API keys to function. Ensure these environment variables are set (typically via the root `.env` and `docker-compose.yml`):

```yaml
STRIPE_API_KEY=sk_test_...             # Secret API key for creating sessions
STRIPE_API_PUBLIC_KEY=pk_test_...      # Public API key
STRIPE_WEBHOOK_SECRET=whsec_...        # Webhook signing secret for signature validation
```
