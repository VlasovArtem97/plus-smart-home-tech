CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID NOT NULL,
    total_payment DECIMAL(12, 2),
    delivery_total DECIMAL(12, 2),
    fee_total DECIMAL(12, 2),
    payment_state VARCHAR(20) NOT NULL,
    product_price DECIMAL(12, 2),

    CONSTRAINT check_payment_state CHECK (payment_state IN ('PENDING', 'SUCCESS', 'FAILED'))
    );