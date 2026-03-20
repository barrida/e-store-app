-- Order Management Service: Order items (line items)
-- Run after 02-orders-table.sql

SET search_path TO order_management;

-- Order line items
CREATE TABLE IF NOT EXISTS order_items (
    id                  BIGSERIAL       PRIMARY KEY,
    order_id            BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id          BIGINT          NOT NULL,               -- FK to product_catalog DB (cross-service)
    product_sku         VARCHAR(100)    NOT NULL,               -- snapshot at order time
    product_name        VARCHAR(255)    NOT NULL,               -- snapshot at order time
    product_image_url   VARCHAR(2048),                          -- snapshot at order time
    quantity            INT             NOT NULL CHECK (quantity > 0),
    unit_price          NUMERIC(12, 2)  NOT NULL CHECK (unit_price >= 0),
    discount_amount     NUMERIC(12, 2)  NOT NULL DEFAULT 0.00 CHECK (discount_amount >= 0),
    tax_amount          NUMERIC(12, 2)  NOT NULL DEFAULT 0.00 CHECK (tax_amount >= 0),
    total_price         NUMERIC(14, 2)  NOT NULL CHECK (total_price >= 0),
    currency            CHAR(3)         NOT NULL DEFAULT 'USD',
    product_options     JSONB,                                  -- {"color": "Red", "size": "M"}
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Returns / refunds for individual items
CREATE TABLE IF NOT EXISTS order_item_returns (
    id              BIGSERIAL       PRIMARY KEY,
    order_item_id   BIGINT          NOT NULL REFERENCES order_items(id) ON DELETE RESTRICT,
    quantity        INT             NOT NULL CHECK (quantity > 0),
    reason          VARCHAR(255),
    notes           TEXT,
    refund_amount   NUMERIC(12, 2)  NOT NULL CHECK (refund_amount >= 0),
    status          VARCHAR(50)     NOT NULL DEFAULT 'requested',  -- requested, approved, rejected, completed
    requested_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Seed: sequence function to generate readable order numbers
CREATE OR REPLACE FUNCTION order_management.generate_order_number()
RETURNS TEXT LANGUAGE plpgsql AS $$
DECLARE
    v_seq BIGINT;
BEGIN
    SELECT NEXTVAL('orders_id_seq') INTO v_seq;
    RETURN 'ORD-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(v_seq::TEXT, 6, '0');
END;
$$;
