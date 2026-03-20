-- Notification Service: Sent notifications history and preferences
-- Run after 02-templates-table.sql

SET search_path TO notification_service;

DO $$ BEGIN
    CREATE TYPE notification_status AS ENUM ('queued', 'sending', 'sent', 'delivered', 'failed', 'bounced');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Sent notifications history
CREATE TABLE IF NOT EXISTS notifications (
    id                  BIGSERIAL               PRIMARY KEY,
    template_id         BIGINT                  REFERENCES notification_templates(id) ON DELETE SET NULL,
    channel             notification_channel    NOT NULL,
    status              notification_status     NOT NULL DEFAULT 'queued',

    -- Recipient (user_id may be NULL for guest/system notifications)
    user_id             BIGINT,
    recipient_address   VARCHAR(500)            NOT NULL,       -- email address, phone number, device token, etc.

    -- Content (rendered at send time)
    subject             VARCHAR(500),
    body                TEXT                    NOT NULL,

    -- Correlation
    reference_type      VARCHAR(100),                           -- e.g. 'order', 'payment', 'user'
    reference_id        BIGINT,                                 -- e.g. order ID

    -- Delivery metadata
    provider            VARCHAR(100),                           -- e.g. 'SendGrid', 'Twilio', 'Firebase'
    provider_message_id VARCHAR(255),
    provider_response   JSONB,
    error_message       TEXT,
    retry_count         SMALLINT                NOT NULL DEFAULT 0,
    next_retry_at       TIMESTAMPTZ,

    queued_at           TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    sent_at             TIMESTAMPTZ,
    delivered_at        TIMESTAMPTZ,
    failed_at           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);

-- Per-user notification preferences per channel
CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id              BIGSERIAL               PRIMARY KEY,
    user_id         BIGINT                  NOT NULL,
    channel         notification_channel    NOT NULL,
    template_name   VARCHAR(100)            NOT NULL REFERENCES notification_templates(name) ON DELETE CASCADE,
    is_enabled      BOOLEAN                 NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ             NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, channel, template_name)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_id        ON notifications (user_id) WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_notifications_status         ON notifications (status);
CREATE INDEX IF NOT EXISTS idx_notifications_channel        ON notifications (channel);
CREATE INDEX IF NOT EXISTS idx_notifications_reference      ON notifications (reference_type, reference_id) WHERE reference_type IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_notifications_queued_at      ON notifications (queued_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_next_retry     ON notifications (next_retry_at) WHERE next_retry_at IS NOT NULL AND status = 'failed';
CREATE INDEX IF NOT EXISTS idx_notification_templates_name  ON notification_templates (name);
CREATE INDEX IF NOT EXISTS idx_notification_templates_ch    ON notification_templates (channel);
CREATE INDEX IF NOT EXISTS idx_user_notif_prefs_user_id     ON user_notification_preferences (user_id);

-- Automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION notification_service.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW EXECUTE FUNCTION notification_service.set_updated_at();

CREATE OR REPLACE TRIGGER trg_notification_templates_updated_at
    BEFORE UPDATE ON notification_templates
    FOR EACH ROW EXECUTE FUNCTION notification_service.set_updated_at();

CREATE OR REPLACE TRIGGER trg_user_notif_prefs_updated_at
    BEFORE UPDATE ON user_notification_preferences
    FOR EACH ROW EXECUTE FUNCTION notification_service.set_updated_at();
