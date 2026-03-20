-- Product Catalog Service: Sub-categories seed data and category attributes
-- Run after 02-products-table.sql

SET search_path TO product_catalog;

-- Sub-categories (parent_id references inserted in previous script)
INSERT INTO categories (name, slug, description, parent_id, sort_order)
SELECT 'Smartphones',  'smartphones',  'Mobile phones and smartphones',
       (SELECT id FROM categories WHERE slug = 'electronics'), 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug = 'smartphones');

INSERT INTO categories (name, slug, description, parent_id, sort_order)
SELECT 'Laptops',  'laptops',  'Laptops and notebook computers',
       (SELECT id FROM categories WHERE slug = 'electronics'), 2
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug = 'laptops');

INSERT INTO categories (name, slug, description, parent_id, sort_order)
SELECT 'T-Shirts',  't-shirts',  'Casual and formal t-shirts',
       (SELECT id FROM categories WHERE slug = 'clothing'), 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug = 't-shirts');

INSERT INTO categories (name, slug, description, parent_id, sort_order)
SELECT 'Running Shoes',  'running-shoes',  'Athletic and running footwear',
       (SELECT id FROM categories WHERE slug = 'sports'), 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE slug = 'running-shoes');

-- Category attributes table (flexible EAV-style extension per category)
CREATE TABLE IF NOT EXISTS category_attributes (
    id            BIGSERIAL     PRIMARY KEY,
    category_id   BIGINT        NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    attribute_key VARCHAR(100)  NOT NULL,
    attribute_label VARCHAR(150) NOT NULL,
    input_type    VARCHAR(50)   NOT NULL DEFAULT 'text',   -- text, number, select, boolean
    options       JSONB,                                   -- for select type: ["Red","Blue","Green"]
    is_required   BOOLEAN       NOT NULL DEFAULT FALSE,
    sort_order    INT           NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (category_id, attribute_key)
);

-- Product attribute values (stores the per-product values of category attributes)
CREATE TABLE IF NOT EXISTS product_attribute_values (
    id              BIGSERIAL     PRIMARY KEY,
    product_id      BIGINT        NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    attribute_id    BIGINT        NOT NULL REFERENCES category_attributes(id) ON DELETE CASCADE,
    value           TEXT,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, attribute_id)
);
