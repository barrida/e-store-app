-- Payment Processing Service: Indexes for performance
-- Run after 03-payment-methods-table.sql

SET search_path TO payment_processing;

-- payment_transactions
CREATE INDEX IF NOT EXISTS idx_payment_txn_order_id           ON payment_transactions (order_id);
CREATE INDEX IF NOT EXISTS idx_payment_txn_user_id            ON payment_transactions (user_id);
CREATE INDEX IF NOT EXISTS idx_payment_txn_status             ON payment_transactions (status);
CREATE INDEX IF NOT EXISTS idx_payment_txn_gateway_txn_id     ON payment_transactions (gateway_transaction_id) WHERE gateway_transaction_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_payment_txn_reference          ON payment_transactions (transaction_reference);
CREATE INDEX IF NOT EXISTS idx_payment_txn_created_at         ON payment_transactions (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_payment_txn_parent_id          ON payment_transactions (parent_transaction_id) WHERE parent_transaction_id IS NOT NULL;

-- transaction_status_history
CREATE INDEX IF NOT EXISTS idx_txn_status_history_txn_id      ON transaction_status_history (transaction_id);

-- payment_methods
CREATE INDEX IF NOT EXISTS idx_payment_methods_user_id        ON payment_methods (user_id);
CREATE INDEX IF NOT EXISTS idx_payment_methods_gateway_token  ON payment_methods (gateway_token);
CREATE INDEX IF NOT EXISTS idx_payment_methods_is_active      ON payment_methods (is_active) WHERE is_active = TRUE;

-- Automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION payment_processing.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_payment_transactions_updated_at
    BEFORE UPDATE ON payment_transactions
    FOR EACH ROW EXECUTE FUNCTION payment_processing.set_updated_at();

CREATE OR REPLACE TRIGGER trg_payment_methods_updated_at
    BEFORE UPDATE ON payment_methods
    FOR EACH ROW EXECUTE FUNCTION payment_processing.set_updated_at();
