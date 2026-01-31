CREATE TABLE IF NOT EXISTS orders (
    order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    payment_id UUID,
    delivery_id UUID,
    order_state VARCHAR(15) NOT NULL,
    delivery_weight DECIMAL(12,2) NOT NULL,
    delivery_volume DECIMAL(12,2) NOT NULL,
    fragile BOOLEAN,
    total_price DECIMAL(12,2),
    delivery_price DECIMAL(12,2),
    product_price DECIMAL(12,2),
    fee_total DECIMAL(12,2),
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(255) NOT NULL,
    flat VARCHAR(255) NOT NULL,

    CONSTRAINT chk_order_state CHECK (
            order_state IN (
                'NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED',
                'ASSEMBLED', 'PAID', 'COMPLETED', 'DELIVERY_FAILED',
                'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED'
            )
        )
);

CREATE TABLE IF NOT EXISTS orders_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);