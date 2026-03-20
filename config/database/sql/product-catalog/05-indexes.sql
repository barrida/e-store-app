-- Product Catalog Service: Indexes for performance
-- Run after 04-reviews-table.sql

SET search_path TO product_catalog;

-- categories
CREATE INDEX IF NOT EXISTS idx_categories_parent_id   ON categories (parent_id);
CREATE INDEX IF NOT EXISTS idx_categories_slug        ON categories (slug);
CREATE INDEX IF NOT EXISTS idx_categories_is_active   ON categories (is_active) WHERE is_active = TRUE;

-- products
CREATE INDEX IF NOT EXISTS idx_products_category_id   ON products (category_id);
CREATE INDEX IF NOT EXISTS idx_products_sku           ON products (sku);
CREATE INDEX IF NOT EXISTS idx_products_slug          ON products (slug);
CREATE INDEX IF NOT EXISTS idx_products_is_active     ON products (is_active) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_products_is_featured   ON products (is_featured) WHERE is_featured = TRUE;
CREATE INDEX IF NOT EXISTS idx_products_price         ON products (price);
CREATE INDEX IF NOT EXISTS idx_products_name_fts      ON products USING GIN (to_tsvector('english', name || ' ' || COALESCE(description, '')));

-- product_images
CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images (product_id);

-- reviews
CREATE INDEX IF NOT EXISTS idx_reviews_product_id     ON reviews (product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id        ON reviews (user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating         ON reviews (rating);
CREATE INDEX IF NOT EXISTS idx_reviews_is_approved    ON reviews (is_approved) WHERE is_approved = TRUE;

-- category_attributes
CREATE INDEX IF NOT EXISTS idx_category_attributes_category_id ON category_attributes (category_id);

-- product_attribute_values
CREATE INDEX IF NOT EXISTS idx_product_attr_values_product_id   ON product_attribute_values (product_id);
CREATE INDEX IF NOT EXISTS idx_product_attr_values_attribute_id ON product_attribute_values (attribute_id);

-- Automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION product_catalog.set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION product_catalog.set_updated_at();

CREATE OR REPLACE TRIGGER trg_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION product_catalog.set_updated_at();

CREATE OR REPLACE TRIGGER trg_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION product_catalog.set_updated_at();
