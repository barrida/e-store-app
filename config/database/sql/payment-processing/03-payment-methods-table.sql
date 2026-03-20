-- Payment Processing Service: Payment methods (tokenized storage)
-- Run after 02-payments-table.sql

SET search_path TO payment_processing;

DO $$ BEGIN
    CREATE TYPE payment_method_type AS ENUM (
        'credit_card', 'debit_card', 'paypal', 'apple_pay',
        'google_pay', 'bank_transfer', 'cryptocurrency'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Stored/tokenized payment methods
-- NOTE: Never store raw card numbers (PAN). Use gateway tokens only.
CREATE TABLE IF NOT EXISTS payment_methods (
    id                  BIGSERIAL               PRIMARY KEY,
    user_id             BIGINT                  NOT NULL,      -- FK to user_management DB (cross-service)
    method_type         payment_method_type     NOT NULL,
    is_default          BOOLEAN                 NOT NULL DEFAULT FALSE,

    -- Gateway token (replaces raw card data — PCI DSS compliant)
    gateway             VARCHAR(50)             NOT NULL,      -- 'stripe', 'paypal', ...
    gateway_token       VARCHAR(255)            NOT NULL,      -- e.g. Stripe PaymentMethod ID
    gateway_customer_id VARCHAR(255),                          -- e.g. Stripe Customer ID

    -- Display info (non-sensitive)
    display_name        VARCHAR(100),                          -- e.g. "Visa ending 4242"
    card_last4          CHAR(4),
    card_brand          VARCHAR(30),                           -- 'visa', 'mastercard', 'amex'
    card_exp_month      SMALLINT                CHECK (card_exp_month BETWEEN 1 AND 12),
    card_exp_year       SMALLINT                CHECK (card_exp_year >= 2024),
    billing_address     JSONB,

    is_active           BOOLEAN                 NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

-- Ensure only one default payment method per user
CREATE UNIQUE INDEX IF NOT EXISTS idx_payment_methods_default_per_user
    ON payment_methods (user_id)
    WHERE is_default = TRUE AND is_active = TRUE;
