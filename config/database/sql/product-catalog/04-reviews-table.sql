-- Product Catalog Service: Reviews and ratings
-- Run after 03-categories-table.sql

SET search_path TO product_catalog;

-- Reviews and ratings
CREATE TABLE IF NOT EXISTS reviews (
    id              BIGSERIAL       PRIMARY KEY,
    product_id      BIGINT          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id         BIGINT          NOT NULL,              -- FK to user_management DB (cross-service)
    rating          SMALLINT        NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title           VARCHAR(255),
    body            TEXT,
    is_verified_purchase BOOLEAN    NOT NULL DEFAULT FALSE,
    is_approved     BOOLEAN         NOT NULL DEFAULT FALSE,
    helpful_votes   INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, user_id)   -- one review per user per product
);

-- Aggregate rating cache (updated via trigger or application logic)
CREATE TABLE IF NOT EXISTS product_rating_summary (
    product_id      BIGINT          PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    average_rating  NUMERIC(3, 2)   NOT NULL DEFAULT 0.00,
    total_reviews   INT             NOT NULL DEFAULT 0,
    rating_1_count  INT             NOT NULL DEFAULT 0,
    rating_2_count  INT             NOT NULL DEFAULT 0,
    rating_3_count  INT             NOT NULL DEFAULT 0,
    rating_4_count  INT             NOT NULL DEFAULT 0,
    rating_5_count  INT             NOT NULL DEFAULT 0,
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Trigger function to keep rating summary up to date
CREATE OR REPLACE FUNCTION product_catalog.update_rating_summary()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    v_product_id BIGINT;
BEGIN
    v_product_id := COALESCE(NEW.product_id, OLD.product_id);

    INSERT INTO product_rating_summary (
        product_id, average_rating, total_reviews,
        rating_1_count, rating_2_count, rating_3_count,
        rating_4_count, rating_5_count, updated_at
    )
    SELECT
        v_product_id,
        ROUND(AVG(rating)::NUMERIC, 2),
        COUNT(*),
        COUNT(*) FILTER (WHERE rating = 1),
        COUNT(*) FILTER (WHERE rating = 2),
        COUNT(*) FILTER (WHERE rating = 3),
        COUNT(*) FILTER (WHERE rating = 4),
        COUNT(*) FILTER (WHERE rating = 5),
        NOW()
    FROM reviews
    WHERE product_id = v_product_id
      AND is_approved = TRUE
    ON CONFLICT (product_id) DO UPDATE SET
        average_rating  = EXCLUDED.average_rating,
        total_reviews   = EXCLUDED.total_reviews,
        rating_1_count  = EXCLUDED.rating_1_count,
        rating_2_count  = EXCLUDED.rating_2_count,
        rating_3_count  = EXCLUDED.rating_3_count,
        rating_4_count  = EXCLUDED.rating_4_count,
        rating_5_count  = EXCLUDED.rating_5_count,
        updated_at      = EXCLUDED.updated_at;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE TRIGGER trg_reviews_rating_summary
AFTER INSERT OR UPDATE OR DELETE ON reviews
FOR EACH ROW EXECUTE FUNCTION product_catalog.update_rating_summary();
