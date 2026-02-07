CREATE TABLE
    IF NOT EXISTS payments (
        id UUID PRIMARY KEY,
        user_id UUID NOT NULL,
        order_id UUID NOT NULL UNIQUE,
        amount BIGINT NOT NULL,
        currency VARCHAR(50) NOT NULL,
        status VARCHAR(255) NOT NULL,
        stripe_session_id TEXT UNIQUE,
        stripe_session_url TEXT,
        failure_reason VARCHAR(255),
        create_at TIMESTAMP NOT NULL,
        version BIGINT
    );
