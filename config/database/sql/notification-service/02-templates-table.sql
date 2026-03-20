-- Notification Service: Templates table
-- Run after 01-create-schema.sql

SET search_path TO notification_service;

-- Enum types
DO $$ BEGIN
    CREATE TYPE notification_channel AS ENUM ('email', 'sms', 'push', 'in_app', 'webhook');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Notification templates
CREATE TABLE IF NOT EXISTS notification_templates (
    id              BIGSERIAL               PRIMARY KEY,
    name            VARCHAR(100)            NOT NULL UNIQUE,    -- e.g. 'order_confirmed'
    channel         notification_channel    NOT NULL,
    subject         VARCHAR(255),                               -- for email
    body_html       TEXT,                                       -- HTML body (email)
    body_text       TEXT            NOT NULL,                   -- plain-text / SMS body
    variables       JSONB,                                      -- documented placeholder vars: ["{{user_name}}", "{{order_id}}"]
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    version         INT             NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Seed: common notification templates
INSERT INTO notification_templates (name, channel, subject, body_html, body_text, variables)
VALUES
(
    'order_confirmed', 'email',
    'Your order #{{order_number}} has been confirmed',
    '<p>Hi {{user_name}},</p><p>Your order <strong>#{{order_number}}</strong> has been confirmed. Total: <strong>{{total_amount}} {{currency}}</strong>.</p>',
    'Hi {{user_name}}, your order #{{order_number}} has been confirmed. Total: {{total_amount}} {{currency}}.',
    '["user_name", "order_number", "total_amount", "currency"]'::jsonb
),
(
    'order_shipped', 'email',
    'Your order #{{order_number}} has been shipped',
    '<p>Hi {{user_name}},</p><p>Your order <strong>#{{order_number}}</strong> is on its way! Tracking: <a href="{{tracking_url}}">{{tracking_number}}</a>.</p>',
    'Hi {{user_name}}, your order #{{order_number}} has been shipped. Tracking: {{tracking_number}}.',
    '["user_name", "order_number", "tracking_number", "tracking_url"]'::jsonb
),
(
    'password_reset', 'email',
    'Reset your E-Store password',
    '<p>Hi {{user_name}},</p><p>Click <a href="{{reset_url}}">here</a> to reset your password. This link expires in 30 minutes.</p>',
    'Hi {{user_name}}, use this link to reset your password (expires in 30 min): {{reset_url}}',
    '["user_name", "reset_url"]'::jsonb
),
(
    'payment_failed', 'email',
    'Action required: Payment failed for order #{{order_number}}',
    '<p>Hi {{user_name}},</p><p>We were unable to process your payment for order <strong>#{{order_number}}</strong>. Please update your payment details.</p>',
    'Hi {{user_name}}, payment failed for order #{{order_number}}. Please update your payment details.',
    '["user_name", "order_number"]'::jsonb
),
(
    'welcome_sms', 'sms',
    NULL,
    NULL,
    'Welcome to E-Store, {{user_name}}! Start shopping at {{shop_url}}',
    '["user_name", "shop_url"]'::jsonb
),
(
    'order_shipped_sms', 'sms',
    NULL,
    NULL,
    'E-Store: Order #{{order_number}} shipped. Track: {{tracking_url}}',
    '["order_number", "tracking_url"]'::jsonb
)
ON CONFLICT (name) DO NOTHING;
