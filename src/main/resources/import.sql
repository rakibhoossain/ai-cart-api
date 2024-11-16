-- This file allow to write SQL commands that will be emitted in test and dev.

-- Inserting Country (United States, Spain)
INSERT INTO countries (id, code, name) VALUES (1, 'usa', 'United States'), (2, 'es', 'Spain');

-- Inserting WarehouseLocation
INSERT INTO warehouse_locations (id, name, address_line1, city, postal_code, country_id, contact_number, is_active)
VALUES (1, 'EU First', 'Line 1', 'Berlin', 1216, 1, '0123456789', TRUE);

-- Inserting Currency (USD, EUR)
INSERT INTO currencies (id, code, name) VALUES (1, 'USD', 'US Dollar'), (2, 'EUR', 'Euro');

-- Inserting Language (English, Spanish)
INSERT INTO languages (id, code, name) VALUES (1, 'en', 'English'), (2, 'es', 'Spanish');

-- Inserting Category
INSERT INTO categories (id) VALUES (1), (2);
INSERT INTO categories (id, parent_category_id) VALUES (3, 1), (4, 3);

-- Inserting Category translations
INSERT INTO category_translations (id, category_id, language_id, name)
VALUES (1, 1, 1, 'First category'),
       (2, 2, 1, 'Second category'),
       (3, 3, 1, 'Third category'),
        (4, 4, 1, 'Fourth category');


-- Inserting Attribute. Defines an attribute type like size or color.
INSERT INTO attributes (id) VALUES (1), (2);

-- Inserting AttributeTranslation
INSERT INTO attribute_translations(id, attribute_id, language_id, name)
VALUES (1, 1, 1, 'Color'),
        (2, 2, 1, 'Size');

-- Inserting AttributeValue
INSERT INTO attribute_values(id, attribute_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 2);

-- Inserting AttributeValueTranslation
INSERT INTO attribute_value_translations(id, attribute_value_id, language_id, value)
VALUES (1, 1, 1, 'Blue'),
    (2, 2, 1, 'Red'),
    (3, 3, 1, 'X'),
    (4, 4, 1, 'XL'),
    (5, 5, 1, 'XXL');

-- Inserting Product
INSERT INTO products(id, sku) VALUES (1, 'SKU_1'), (2, 'SKU_2');

-- Inserting translations
INSERT INTO product_translations(id, product_id, language_id, name, description)
VALUES (1, 1, 1, 'Test product 1', 'Test product description 1'),
       (2, 2, 1, 'Test product 2', 'Test product description 2'),
       (3, 2, 2, 'Producto de prueba 2', 'Descripción del producto de prueba 2');


-- Inserting product category
INSERT INTO product_category(product_id, category_id)
VALUES (1, 1),
       (2, 1);

-- Inserting variants
INSERT INTO product_variants(id, product_id, sku)
VALUES (1, 1, 'SKU_1_V'),
       (2, 2, 'SKU_2_V');

-- Inserting product variant value
INSERT INTO product_variant_value(variant_id, attribute_value_id)
VALUES (1, 1),
       (1, 3),
       (2, 2),
       (2, 3);


-- Inserting VariantStock
INSERT INTO variant_stocks(id, variant_id, quantity, warehouse_id)
VALUES (1, 1, 100, 1),
       (2, 2, 150, 1);

-- Insert VariantPrice
INSERT INTO variant_prices(id, country_id, variant_id, currency_id, price, discount, tax_rate, is_active)
VALUES (1, 1, 1, 1, 1200, 0, 0, TRUE),
       (2, 1, 2, 1,1700, 0, 0, TRUE);


-- Insert VariantImage
INSERT INTO variant_images(id, variant_id, url)
VALUES (1, 1, 'https://laravel.pixelstrap.net/multikart/storage/49/fashion_173.jpg'),
       (2, 1, 'https://laravel.pixelstrap.net/multikart/storage/70/fashion_71.jpg'),
       (3, 2, 'https://laravel.pixelstrap.net/multikart/storage/13/fashion_311.jpg'),
        (4, 2, 'https://laravel.pixelstrap.net/multikart/storage/161/fashion_0122.jpg');