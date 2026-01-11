CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    cart_state VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id),
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(shopping_cart_id) ON DELETE CASCADE
);