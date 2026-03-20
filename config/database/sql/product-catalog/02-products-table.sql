-- Product Catalog Service: Categories and Products tables
-- Run after 01-create-schema.sql

SET search_path TO product_catalog;

-- Categories table (must exist before products for FK reference)
CREATE TABLE IF NOT EXISTS categories (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL,
    slug            VARCHAR(150)    NOT NULL UNIQUE,
    description     TEXT,
    parent_id       BIGINT          REFERENCES categories(id) ON DELETE SET NULL,
    image_url       VARCHAR(2048),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    sort_order      INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id                  BIGSERIAL       PRIMARY KEY,
    sku                 VARCHAR(100)    NOT NULL UNIQUE,
    name                VARCHAR(255)    NOT NULL,
    slug                VARCHAR(255)    NOT NULL UNIQUE,
    description         TEXT,
    short_description   VARCHAR(500),
    category_id         BIGINT          REFERENCES categories(id) ON DELETE SET NULL,
    brand               VARCHAR(150),
    price               NUMERIC(12, 2)  NOT NULL CHECK (price >= 0),
    sale_price          NUMERIC(12, 2)           CHECK (sale_price >= 0),
    cost_price          NUMERIC(12, 2)           CHECK (cost_price >= 0),
    currency            CHAR(3)         NOT NULL DEFAULT 'USD',
    stock_quantity      INT             NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT             NOT NULL DEFAULT 10,
    weight_kg           NUMERIC(8, 3),
    dimensions_cm       JSONB,                   -- {"length": 10, "width": 5, "height": 3}
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    is_featured         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_digital          BOOLEAN         NOT NULL DEFAULT FALSE,
    metadata            JSONB,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Product images / attachments
CREATE TABLE IF NOT EXISTS product_images (
    id          BIGSERIAL       PRIMARY KEY,
    product_id  BIGINT          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url         VARCHAR(2048)   NOT NULL,
    alt_text    VARCHAR(255),
    is_primary  BOOLEAN         NOT NULL DEFAULT FALSE,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Seed data: root categories
INSERT INTO categories (name, slug, description, sort_order)
VALUES
    ('Electronics',    'electronics',    'Electronic devices and accessories',  1),
    ('Clothing',       'clothing',       'Apparel and fashion items',           2),
    ('Home & Garden',  'home-garden',    'Home furnishings and garden tools',   3),
    ('Sports',         'sports',         'Sports equipment and activewear',     4),
    ('Books',          'books',          'Books, e-books and audiobooks',       5)
ON CONFLICT (slug) DO NOTHING;
