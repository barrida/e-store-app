-- User Management Service: Addresses
-- Run after 02-users-table.sql

SET search_path TO user_management;

DO $$ BEGIN
    CREATE TYPE address_type AS ENUM ('billing', 'shipping', 'both');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Addresses (billing and shipping)
CREATE TABLE IF NOT EXISTS addresses (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    address_type    address_type    NOT NULL DEFAULT 'shipping',
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    label           VARCHAR(100),                           -- e.g. "Home", "Office"
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100)    NOT NULL,
    company         VARCHAR(150),
    address_line1   VARCHAR(255)    NOT NULL,
    address_line2   VARCHAR(255),
    city            VARCHAR(100)    NOT NULL,
    state_province  VARCHAR(100),
    postal_code     VARCHAR(20)     NOT NULL,
    country_code    CHAR(2)         NOT NULL,               -- ISO 3166-1 alpha-2
    phone_number    VARCHAR(30),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Ensure only one default address per user per type
CREATE UNIQUE INDEX IF NOT EXISTS idx_addresses_default_per_user_type
    ON addresses (user_id, address_type)
    WHERE is_default = TRUE;
