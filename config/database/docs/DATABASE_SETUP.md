# Database Setup Guide — E-Store Application

This document describes how to initialise, migrate, and maintain the PostgreSQL databases for the five E-Store microservices.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Database Inventory](#database-inventory)
4. [Initialisation Procedure](#initialisation-procedure)
5. [Migration Strategy](#migration-strategy)
6. [Connection Configuration](#connection-configuration)
7. [Connection Pooling](#connection-pooling)
8. [Seed / Test Data](#seed--test-data)
9. [Backup and Recovery](#backup-and-recovery)
10. [Troubleshooting](#troubleshooting)

---

## Architecture Overview

The E-Store application follows the **Database-per-Service** pattern. Each microservice owns its PostgreSQL schema and no service directly queries another service's tables. Cross-service data needs are fulfilled through APIs or asynchronous events.

```
┌──────────────────────┐   ┌─────────────────────┐   ┌────────────────────┐
│ product_catalog_db   │   │ user_management_db   │   │ order_management_db│
│  schema: product_    │   │  schema: user_        │   │  schema: order_    │
│  catalog             │   │  management           │   │  management        │
└──────────────────────┘   └─────────────────────┘   └────────────────────┘

┌─────────────────────────┐   ┌──────────────────────────┐
│ payment_processing_db   │   │ notification_service_db   │
│  schema: payment_       │   │  schema: notification_    │
│  processing             │   │  service                  │
└─────────────────────────┘   └──────────────────────────┘
```

---

## Prerequisites

| Requirement | Minimum version |
|-------------|----------------|
| PostgreSQL  | 14             |
| psql client | 14             |
| Flyway CLI  | 9.x (optional) |
| Docker      | 24.x (optional, for local dev) |

---

## Database Inventory

| Database                  | Schema                | Service Role                 |
|---------------------------|-----------------------|------------------------------|
| `product_catalog_db`      | `product_catalog`     | `product_catalog_user`       |
| `user_management_db`      | `user_management`     | `user_management_user`       |
| `order_management_db`     | `order_management`    | `order_management_user`      |
| `payment_processing_db`   | `payment_processing`  | `payment_processing_user`    |
| `notification_service_db` | `notification_service`| `notification_service_user`  |

---

## Initialisation Procedure

### 1. Start PostgreSQL

**Local (Docker Compose — recommended for development):**

```bash
# Start a local PostgreSQL container
docker run -d \
  --name estore-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

### 2. Run the SQL scripts in order

Execute scripts for **each service** in numerical order using psql:

```bash
# Example: Product Catalog Service
PGPASSWORD=postgres psql -h localhost -U postgres \
  -f config/database/sql/product-catalog/01-create-schema.sql \
  -f config/database/sql/product-catalog/02-products-table.sql \
  -f config/database/sql/product-catalog/03-categories-table.sql \
  -f config/database/sql/product-catalog/04-reviews-table.sql \
  -f config/database/sql/product-catalog/05-indexes.sql

# Example: User Management Service
PGPASSWORD=postgres psql -h localhost -U postgres \
  -f config/database/sql/user-management/01-create-schema.sql \
  -f config/database/sql/user-management/02-users-table.sql \
  -f config/database/sql/user-management/03-addresses-table.sql \
  -f config/database/sql/user-management/04-indexes.sql

# Example: Order Management Service
PGPASSWORD=postgres psql -h localhost -U postgres \
  -f config/database/sql/order-management/01-create-schema.sql \
  -f config/database/sql/order-management/02-orders-table.sql \
  -f config/database/sql/order-management/03-order-items-table.sql \
  -f config/database/sql/order-management/04-indexes.sql

# Example: Payment Processing Service
PGPASSWORD=postgres psql -h localhost -U postgres \
  -f config/database/sql/payment-processing/01-create-schema.sql \
  -f config/database/sql/payment-processing/02-payments-table.sql \
  -f config/database/sql/payment-processing/03-payment-methods-table.sql \
  -f config/database/sql/payment-processing/04-indexes.sql

# Example: Notification Service
PGPASSWORD=postgres psql -h localhost -U postgres \
  -f config/database/sql/notification-service/01-create-schema.sql \
  -f config/database/sql/notification-service/02-templates-table.sql \
  -f config/database/sql/notification-service/03-notifications-table.sql
```

### 3. Verify the setup

```sql
-- Connect to any service database, for example:
\c product_catalog_db
SET search_path TO product_catalog;
\dt          -- lists all tables in the schema
\di          -- lists all indexes
SELECT * FROM categories;
```

---

## Migration Strategy

### Flyway (recommended)

Place migration scripts in each service's `src/main/resources/db/migration/` directory following the Flyway naming convention:

```
V1__create_schema.sql
V2__products_table.sql
V3__categories_table.sql
V4__reviews_table.sql
V5__indexes.sql
```

Flyway configuration (Spring Boot):

```properties
spring.flyway.enabled=true
spring.flyway.schemas=product_catalog
spring.flyway.locations=classpath:db/migration/product-catalog
spring.flyway.baseline-on-migrate=true
```

### Liquibase (alternative)

Use `changelog.xml` as the master change log and reference individual change sets:

```xml
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.0.xsd">

    <include file="db/changelog/product-catalog/01-create-schema.xml"/>
    <include file="db/changelog/product-catalog/02-products-table.xml"/>
</databaseChangeLog>
```

---

## Connection Configuration

Copy `config/database/config/application-db.properties` into each microservice and supply the required environment variables:

| Environment Variable                 | Description                     | Default                    |
|--------------------------------------|---------------------------------|----------------------------|
| `DB_HOST`                            | PostgreSQL hostname             | `localhost`                |
| `DB_PORT`                            | PostgreSQL port                 | `5432`                     |
| `DB_SSL_MODE`                        | SSL mode (`prefer`/`require`)   | `prefer`                   |
| `PRODUCT_CATALOG_DB_USER`            | Product catalog DB username     | `product_catalog_user`     |
| `PRODUCT_CATALOG_DB_PASSWORD`        | Product catalog DB password     | *(must be set)*            |
| `USER_MANAGEMENT_DB_USER`            | User management DB username     | `user_management_user`     |
| `USER_MANAGEMENT_DB_PASSWORD`        | User management DB password     | *(must be set)*            |
| `ORDER_MANAGEMENT_DB_USER`           | Order management DB username    | `order_management_user`    |
| `ORDER_MANAGEMENT_DB_PASSWORD`       | Order management DB password    | *(must be set)*            |
| `PAYMENT_PROCESSING_DB_USER`         | Payment processing DB username  | `payment_processing_user`  |
| `PAYMENT_PROCESSING_DB_PASSWORD`     | Payment processing DB password  | *(must be set)*            |
| `NOTIFICATION_SERVICE_DB_USER`       | Notification service DB username| `notification_service_user`|
| `NOTIFICATION_SERVICE_DB_PASSWORD`   | Notification service DB password| *(must be set)*            |

> **Security note:** Always supply passwords via environment variables or a secrets manager (AWS Secrets Manager, HashiCorp Vault, Kubernetes Secrets). Never hard-code credentials.

---

## Connection Pooling

See `config/database/config/db-connection-pooling.yaml` for HikariCP settings. Key guidelines:

- **Development:** use defaults (5–10 connections per service).
- **Staging/Production:** tune `maximum-pool-size` based on observed load and the formula `(2 × CPU cores) + 1`.
- **High concurrency:** put [PgBouncer](https://www.pgbouncer.org/) in front of PostgreSQL in `transaction` mode.

---

## Seed / Test Data

Each SQL script includes `INSERT … ON CONFLICT DO NOTHING` seed statements for basic reference data:

| Service            | Seed data included                                              |
|--------------------|-----------------------------------------------------------------|
| Product Catalog    | Root and child categories                                       |
| User Management    | Example admin user (`admin@estore.local`)                       |
| Notification       | Common notification templates (order confirmed, shipped, reset) |

> **Warning:** The admin user seed uses a placeholder password hash. Update the password immediately after the first login in any non-development environment.

---

## Backup and Recovery

### Ad-hoc backup

```bash
# Dump a single service database
pg_dump -h localhost -U postgres -Fc product_catalog_db \
  -f backups/product_catalog_db_$(date +%Y%m%d_%H%M%S).dump

# Restore
pg_restore -h localhost -U postgres -d product_catalog_db \
  backups/product_catalog_db_20240101_120000.dump
```

### Automated backups

In production, configure your managed PostgreSQL service (AWS RDS, Google Cloud SQL, etc.) to take automated daily snapshots with a 7-day retention period.

---

## Troubleshooting

| Symptom | Likely cause | Resolution |
|---------|-------------|------------|
| `role "x" does not exist` | Script run out of order | Run `01-create-schema.sql` first |
| `database "x" does not exist` | Schema scripts run before DB creation | Run `01-create-schema.sql` as superuser first |
| `permission denied for schema` | Service role missing USAGE grant | Re-run `01-create-schema.sql` |
| `too many connections` | Pool size too large for PostgreSQL `max_connections` | Reduce `maximum-pool-size` or enable PgBouncer |
| Flyway checksum mismatch | SQL file modified after migration was applied | Create a new versioned migration instead of editing existing ones |
