-- Order Management Service: Orders table
-- Run after 01-create-schema.sql

SET search_path TO order_management;

-- Enum types
DO $$ BEGIN
    CREATE TYPE order_status AS ENUM (
        'pending', 'confirmed', 'processing', 'shipped',
        'delivered', 'cancelled', 'refunded', 'failed'
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE payment_status AS ENUM ('pending', 'paid', 'failed', 'refunded', 'partially_refunded');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Orders (order header)
CREATE TABLE IF NOT EXISTS orders (
    id                      BIGSERIAL       PRIMARY KEY,
    order_number            VARCHAR(50)     NOT NULL UNIQUE,  -- human-readable, e.g. ORD-20240101-0001
    user_id                 BIGINT          NOT NULL,          -- FK to user_management DB (cross-service)
    status                  order_status    NOT NULL DEFAULT 'pending',
    payment_status          payment_status  NOT NULL DEFAULT 'pending',

    -- Pricing breakdown
    subtotal                NUMERIC(14, 2)  NOT NULL CHECK (subtotal >= 0),
    discount_amount         NUMERIC(14, 2)  NOT NULL DEFAULT 0.00 CHECK (discount_amount >= 0),
    shipping_amount         NUMERIC(14, 2)  NOT NULL DEFAULT 0.00 CHECK (shipping_amount >= 0),
    tax_amount              NUMERIC(14, 2)  NOT NULL DEFAULT 0.00 CHECK (tax_amount >= 0),
    total_amount            NUMERIC(14, 2)  NOT NULL CHECK (total_amount >= 0),
    currency                CHAR(3)         NOT NULL DEFAULT 'USD',

    -- Addresses (snapshot at order time — do not FK to live address table)
    shipping_address        JSONB           NOT NULL,
    billing_address         JSONB           NOT NULL,

    -- Coupon / promotion
    coupon_code             VARCHAR(50),
    coupon_discount         NUMERIC(14, 2)  DEFAULT 0.00,

    -- Meta
    notes                   TEXT,
    ip_address              INET,
    user_agent              VARCHAR(500),

    -- Important timestamps
    placed_at               TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    confirmed_at            TIMESTAMPTZ,
    shipped_at              TIMESTAMPTZ,
    delivered_at            TIMESTAMPTZ,
    cancelled_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Shipments
CREATE TABLE IF NOT EXISTS shipments (
    id                  BIGSERIAL       PRIMARY KEY,
    order_id            BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    tracking_number     VARCHAR(100),
    carrier             VARCHAR(100),
    shipping_method     VARCHAR(100),
    estimated_delivery  DATE,
    shipped_at          TIMESTAMPTZ,
    delivered_at        TIMESTAMPTZ,
    status              VARCHAR(50)     NOT NULL DEFAULT 'pending',
    tracking_url        VARCHAR(2048),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Order status history / audit trail
CREATE TABLE IF NOT EXISTS order_status_history (
    id          BIGSERIAL       PRIMARY KEY,
    order_id    BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    old_status  order_status,
    new_status  order_status    NOT NULL,
    changed_by  BIGINT,                                    -- user_id of actor (NULL = system)
    notes       TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Trigger to record status changes automatically
CREATE OR REPLACE FUNCTION order_management.record_order_status_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO order_status_history (order_id, old_status, new_status)
        VALUES (NEW.id, OLD.status, NEW.status);
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_order_status_history
AFTER UPDATE OF status ON orders
FOR EACH ROW EXECUTE FUNCTION order_management.record_order_status_change();
