-- User Management Service Database Schema
-- Creates the dedicated schema and database role for the user management microservice

CREATE DATABASE user_management_db;

\connect user_management_db;

CREATE SCHEMA IF NOT EXISTS user_management;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'user_management_user') THEN
        CREATE ROLE user_management_user WITH LOGIN PASSWORD 'change_me_in_production';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA user_management TO user_management_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA user_management TO user_management_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA user_management
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO user_management_user;

SET search_path TO user_management;
