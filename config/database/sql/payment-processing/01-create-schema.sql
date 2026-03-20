-- Payment Processing Service Database Schema
-- Creates the dedicated schema and database role for the payment processing microservice

CREATE DATABASE payment_processing_db;

\connect payment_processing_db;

CREATE SCHEMA IF NOT EXISTS payment_processing;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'payment_processing_user') THEN
        CREATE ROLE payment_processing_user WITH LOGIN PASSWORD 'change_me_in_production';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA payment_processing TO payment_processing_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA payment_processing TO payment_processing_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA payment_processing
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO payment_processing_user;

SET search_path TO payment_processing;
