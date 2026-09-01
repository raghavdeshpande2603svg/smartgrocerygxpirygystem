CREATE TYPE bill_status AS ENUM ('uploaded', 'processing', 'processed', 'failed');
CREATE TYPE inventory_status AS ENUM ('fresh', 'expiring_soon', 'expired');

CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    default_shelf_life_days INTEGER NOT NULL CHECK (default_shelf_life_days > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_products_category
        FOREIGN KEY (category) REFERENCES categories(name)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT uq_products_name_category UNIQUE (name, category)
);

CREATE TABLE IF NOT EXISTS bills (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ocr_raw_text TEXT,
    status bill_status NOT NULL DEFAULT 'uploaded',
    CONSTRAINT fk_bills_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bill_id BIGINT,
    product_id BIGINT NOT NULL,
    quantity NUMERIC(10,2) NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit VARCHAR(30) NOT NULL DEFAULT 'pcs',
    cost NUMERIC(10,2) NOT NULL DEFAULT 0,
    purchase_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    status inventory_status NOT NULL DEFAULT 'fresh',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_inventory_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_inventory_bill
        FOREIGN KEY (bill_id) REFERENCES bills(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_category
        FOREIGN KEY (category) REFERENCES categories(name)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_bills_user_id ON bills(user_id);
CREATE INDEX IF NOT EXISTS idx_inventory_user_id ON inventory_items(user_id);
CREATE INDEX IF NOT EXISTS idx_inventory_expiry_date ON inventory_items(expiry_date);
CREATE INDEX IF NOT EXISTS idx_inventory_status ON inventory_items(status);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

INSERT INTO categories (name)
VALUES
    ('Vegetables'),
    ('Dairy'),
    ('Grains'),
    ('Fruits'),
    ('Beverages'),
    ('Snacks'),
    ('Frozen'),
    ('Other')
ON CONFLICT (name) DO NOTHING;
