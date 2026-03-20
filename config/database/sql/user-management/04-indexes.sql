-- User Management Service: Indexes for performance
-- Run after 03-addresses-table.sql

SET search_path TO user_management;

-- users
CREATE INDEX IF NOT EXISTS idx_users_email         ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_status        ON users (status);
CREATE INDEX IF NOT EXISTS idx_users_provider      ON users (auth_provider, provider_user_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at    ON users (created_at);

-- refresh_tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id    ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);

-- user_profiles
CREATE INDEX IF NOT EXISTS idx_user_profiles_name  ON user_profiles (last_name, first_name);

-- addresses
CREATE INDEX IF NOT EXISTS idx_addresses_user_id       ON addresses (user_id);
CREATE INDEX IF NOT EXISTS idx_addresses_country_code  ON addresses (country_code);

-- Automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION user_management.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION user_management.set_updated_at();

CREATE OR REPLACE TRIGGER trg_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION user_management.set_updated_at();

CREATE OR REPLACE TRIGGER trg_addresses_updated_at
    BEFORE UPDATE ON addresses
    FOR EACH ROW EXECUTE FUNCTION user_management.set_updated_at();

CREATE OR REPLACE TRIGGER trg_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW EXECUTE FUNCTION user_management.set_updated_at();
