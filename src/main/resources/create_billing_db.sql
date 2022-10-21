CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID PRIMARY KEY,
    reference_id UUID UNIQUE,
    total NUMERIC NOT NULL,
    currency VARCHAR(8) NOT NULL,
    request_time TIMESTAMP NOT NULL,
    collection_result BOOLEAN,
    version INT NOT NULL
);
