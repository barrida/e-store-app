-- Product Catalog Service Database Schema
-- Creates the dedicated schema and database role for the product catalog microservice

CREATE DATABASE product_catalog_db;

\connect product_catalog_db;

CREATE SCHEMA IF NOT EXISTS product_catalog;

-- Create a dedicated role for the product catalog service
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'product_catalog_user') THEN
        CREATE ROLE product_catalog_user WITH LOGIN PASSWORD 'change_me_in_production';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA product_catalog TO product_catalog_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA product_catalog TO product_catalog_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA product_catalog
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO product_catalog_user;

SET search_path TO product_catalog;
