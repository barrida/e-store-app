-- Notification Service Database Schema
-- Creates the dedicated schema and database role for the notification microservice

CREATE DATABASE notification_service_db;

\connect notification_service_db;

CREATE SCHEMA IF NOT EXISTS notification_service;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'notification_service_user') THEN
        CREATE ROLE notification_service_user WITH LOGIN PASSWORD 'change_me_in_production';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA notification_service TO notification_service_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA notification_service TO notification_service_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA notification_service
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO notification_service_user;

SET search_path TO notification_service;
