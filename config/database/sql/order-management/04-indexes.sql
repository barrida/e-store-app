-- Order Management Service: Indexes for performance
-- Run after 03-order-items-table.sql

SET search_path TO order_management;

-- orders
CREATE INDEX IF NOT EXISTS idx_orders_user_id        ON orders (user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status         ON orders (status);
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON orders (payment_status);
CREATE INDEX IF NOT EXISTS idx_orders_order_number   ON orders (order_number);
CREATE INDEX IF NOT EXISTS idx_orders_placed_at      ON orders (placed_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_coupon_code    ON orders (coupon_code) WHERE coupon_code IS NOT NULL;

-- shipments
CREATE INDEX IF NOT EXISTS idx_shipments_order_id        ON shipments (order_id);
CREATE INDEX IF NOT EXISTS idx_shipments_tracking_number ON shipments (tracking_number) WHERE tracking_number IS NOT NULL;

-- order_status_history
CREATE INDEX IF NOT EXISTS idx_order_status_history_order_id ON order_status_history (order_id);

-- order_items
CREATE INDEX IF NOT EXISTS idx_order_items_order_id   ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items (product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_sku        ON order_items (product_sku);

-- order_item_returns
CREATE INDEX IF NOT EXISTS idx_order_item_returns_order_item_id ON order_item_returns (order_item_id);
CREATE INDEX IF NOT EXISTS idx_order_item_returns_status        ON order_item_returns (status);

-- Automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION order_management.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION order_management.set_updated_at();

CREATE OR REPLACE TRIGGER trg_order_items_updated_at
    BEFORE UPDATE ON order_items
    FOR EACH ROW EXECUTE FUNCTION order_management.set_updated_at();

CREATE OR REPLACE TRIGGER trg_shipments_updated_at
    BEFORE UPDATE ON shipments
    FOR EACH ROW EXECUTE FUNCTION order_management.set_updated_at();
