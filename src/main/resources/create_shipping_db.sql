CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID UNIQUE,
    street VARCHAR(128) NOT NULL,
    house_number VARCHAR(32),
    city VARCHAR(64) NOT NULL,
    zip_code VARCHAR(64) NOT NULL,
    shipping_state VARCHAR(16) NOT NULL,
    version INT NOT NULL
);
