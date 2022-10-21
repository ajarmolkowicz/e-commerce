CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY,
    title VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    price NUMERIC NOT NULL,
    currency VARCHAR(8) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(8) NOT NULL,
    version INT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    submission_time TIMESTAMP NOT NULL,
    shipping_time TIMESTAMP,
    version INT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    money NUMERIC NOT NULL,
    currency VARCHAR(8),
    quantity INTEGER NOT NULL,
    CONSTRAINT pk_order_item PRIMARY KEY (product_id,order_id)
);

CREATE TABLE IF NOT EXISTS carts (
    cart_id UUID PRIMARY KEY,
    version INT NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL
);


