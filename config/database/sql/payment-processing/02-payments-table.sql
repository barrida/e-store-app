-- Payment Processing Service: Payments / transactions table
-- Run after 01-create-schema.sql

SET search_path TO payment_processing;

-- Enum types
DO $$ BEGIN
    CREATE TYPE payment_status AS ENUM (
        'pending', 'authorized', 'captured', 'failed',
        'cancelled', 'refunded', 'partially_refunded', 'disputed'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE payment_type AS ENUM ('charge', 'refund', 'chargeback', 'authorization');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Payment transactions
CREATE TABLE IF NOT EXISTS payment_transactions (
    id                      BIGSERIAL           PRIMARY KEY,
    transaction_reference   VARCHAR(100)        NOT NULL UNIQUE,  -- internal reference
    order_id                BIGINT              NOT NULL,          -- FK to order_management DB (cross-service)
    user_id                 BIGINT              NOT NULL,          -- FK to user_management DB (cross-service)
    payment_method_id       BIGINT,                                -- FK to payment_methods (nullable for guest)
    payment_type            payment_type        NOT NULL DEFAULT 'charge',
    status                  payment_status      NOT NULL DEFAULT 'pending',
    amount                  NUMERIC(14, 2)      NOT NULL CHECK (amount >= 0),
    currency                CHAR(3)             NOT NULL DEFAULT 'USD',

    -- Gateway details (stored encrypted or tokenized in production)
    gateway                 VARCHAR(50)         NOT NULL,          -- 'stripe', 'paypal', 'braintree'
    gateway_transaction_id  VARCHAR(255),                          -- gateway's own transaction ID
    gateway_response        JSONB,                                 -- full gateway response payload

    -- Authorization / capture
    authorized_at           TIMESTAMPTZ,
    captured_at             TIMESTAMPTZ,
    failed_at               TIMESTAMPTZ,
    refunded_at             TIMESTAMPTZ,
    failure_reason          TEXT,
    refunded_amount         NUMERIC(14, 2)      DEFAULT 0.00 CHECK (refunded_amount >= 0),

    -- Parent reference for refunds / chargebacks
    parent_transaction_id   BIGINT              REFERENCES payment_transactions(id),

    -- Audit
    ip_address              INET,
    user_agent              VARCHAR(500),
    metadata                JSONB,
    created_at              TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

-- Transaction status history / audit trail
CREATE TABLE IF NOT EXISTS transaction_status_history (
    id              BIGSERIAL           PRIMARY KEY,
    transaction_id  BIGINT              NOT NULL REFERENCES payment_transactions(id) ON DELETE CASCADE,
    old_status      payment_status,
    new_status      payment_status      NOT NULL,
    changed_by      BIGINT,
    notes           TEXT,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT NOW()
);

-- Trigger to record status changes
CREATE OR REPLACE FUNCTION payment_processing.record_transaction_status_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO transaction_status_history (transaction_id, old_status, new_status)
        VALUES (NEW.id, OLD.status, NEW.status);
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_payment_transaction_status_history
AFTER UPDATE OF status ON payment_transactions
FOR EACH ROW EXECUTE FUNCTION payment_processing.record_transaction_status_change();
