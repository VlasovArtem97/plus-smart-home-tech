CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    productName VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(1024),
    quantity_state VARCHAR(15) NOT NULL,
    product_state VARCHAR(15) NOT NULL,
    product_category VARCHAR(15) NOT NULL,
    price NUMERIC(10,2) NOT NULL
);