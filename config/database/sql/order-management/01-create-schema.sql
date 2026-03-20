-- Order Management Service Database Schema
-- Creates the dedicated schema and database role for the order management microservice

CREATE DATABASE order_management_db;

\connect order_management_db;

CREATE SCHEMA IF NOT EXISTS order_management;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'order_management_user') THEN
        CREATE ROLE order_management_user WITH LOGIN PASSWORD 'change_me_in_production';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA order_management TO order_management_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA order_management TO order_management_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_management
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO order_management_user;

SET search_path TO order_management;
