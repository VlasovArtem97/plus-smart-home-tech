CREATE TABLE IF NOT EXISTS delivery (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_country VARCHAR(255) NOT NULL,
    from_city VARCHAR(255) NOT NULL,
    from_street VARCHAR(255) NOT NULL,
    from_house VARCHAR(255) NOT NULL,
    from_flat VARCHAR(255) NOT NULL,
    to_country VARCHAR(255) NOT NULL,
    to_city VARCHAR(255) NOT NULL,
    to_street VARCHAR(255) NOT NULL,
    to_house VARCHAR(255) NOT NULL,
    to_flat VARCHAR(255) NOT NULL,
    order_id UUID UNIQUE,
    delivery_states VARCHAR(15) NOT NULL,
    delivery_volume DECIMAL(12,3),
    delivery_weight DECIMAL(12,3),
    fragile BOOLEAN,

    CONSTRAINT chk_order_state CHECK (
                delivery_states IN (
                    'CREATED',
                    'IN_PROGRESS',
                    'DELIVERED',
                    'FAILED',
                    'CANCELLED'
                )
            )
);