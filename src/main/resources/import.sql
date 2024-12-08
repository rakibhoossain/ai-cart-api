-- This file allow to write SQL commands that will be emitted in test and dev.

-- Inserting Country (United States, Spain)
INSERT INTO countries (id, code, name, tax_rate) VALUES (1, 'usa', 'United States', 20), (2, 'es', 'Spain', 22);

-- Inserting WarehouseLocation
INSERT INTO warehouse_locations (id, name, address_line1, city, postal_code, country_id, contact_number, is_active)
VALUES (1, 'EU First', 'Line 1', 'Berlin', 1216, 1, '0123456789', TRUE);

-- Inserting Currency (USD, EUR)
INSERT INTO currencies (id, code, name) VALUES (1, 'USD', 'US Dollar'), (2, 'EUR', 'Euro');

-- Inserting Language (English, Spanish)
INSERT INTO languages (id, code, name) VALUES (1, 'en', 'English'), (2, 'es', 'Spanish');


-- Inserting product category
-- Insert Root categories (Level 1)
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at)
VALUES
    (1, 'Electronics', NULL, NOW(), NOW()),    -- ID 1
    (2, 'Furniture', NULL, NOW(), NOW()),      -- ID 2
    (3, 'Books', NULL, NOW(), NOW());           -- ID 3

-- Insert Level 2 (Subcategories under root categories)
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at)
VALUES
    (4, 'Computers', (SELECT id FROM categories WHERE name = 'Electronics'), NOW(), NOW()), -- ID 4
    (5, 'Smartphones', (SELECT id FROM categories WHERE name = 'Electronics'), NOW(), NOW()), -- ID 5
    (6, 'Chairs', (SELECT id FROM categories WHERE name = 'Furniture'), NOW(), NOW()),        -- ID 6
    (7, 'Tables', (SELECT id FROM categories WHERE name = 'Furniture'), NOW(), NOW()),        -- ID 7
    (8, 'Novels', (SELECT id FROM categories WHERE name = 'Books'), NOW(), NOW()),           -- ID 8
    (9, 'Magazines', (SELECT id FROM categories WHERE name = 'Books'), NOW(), NOW());         -- ID 9

-- Insert Level 3 (Subcategories under the previous level)
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at)
VALUES
    (10, 'Gaming', (SELECT id FROM categories WHERE name = 'Computers'), NOW(), NOW()),     -- ID 10
    (11, 'Accessories', (SELECT id FROM categories WHERE name = 'Computers'), NOW(), NOW()), -- ID 11
    (12, 'Office', (SELECT id FROM categories WHERE name = 'Chairs'), NOW(), NOW()),        -- ID 12
    (13, 'Outdoor', (SELECT id FROM categories WHERE name = 'Tables'), NOW(), NOW());        -- ID 13

-- Insert Level 4 (Subcategories under the previous level)
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at)
VALUES
    (14, 'PCs', (SELECT id FROM categories WHERE name = 'Gaming'), NOW(), NOW()),  -- ID 14
    (15, 'Consoles', (SELECT id FROM categories WHERE name = 'Gaming'), NOW(), NOW()), -- ID 15
    (16, 'Gaming Accessories', (SELECT id FROM categories WHERE name = 'Accessories'), NOW(), NOW());  -- ID 16

-- Insert Level 5 (Subcategories under the previous level)
INSERT INTO categories (id, name, parent_category_id, created_at, updated_at)
VALUES
    (17, 'Desktop PCs', (SELECT id FROM categories WHERE name = 'PCs'), NOW(), NOW()), -- ID 17
    (18, 'Laptop Accessories', (SELECT id FROM categories WHERE name = 'Gaming Accessories'), NOW(), NOW());  -- ID 18


SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT MAX(id) FROM categories));

INSERT INTO category_closure (ancestor_id, descendant_id, depth) VALUES
    (1, 1, 0),   -- Electronics (itself)
    (2, 2, 0),   -- Furniture (itself)
    (3, 3, 0),   -- Books (itself)
    (4, 4, 0),   -- Computers (itself)
    (5, 5, 0),   -- Smartphones (itself)
    (6, 6, 0),   -- Chairs (itself)
    (7, 7, 0),   -- Tables (itself)
    (8, 8, 0),   -- Novels (itself)
    (9, 9, 0),   -- Magazines (itself)
    (10, 10, 0), -- Gaming (itself)
    (11, 11, 0), -- Accessories (itself)
    (12, 12, 0), -- Office (itself)
    (13, 13, 0), -- Outdoor (itself)
    (14, 14, 0), -- PCs (itself)
    (15, 15, 0), -- Consoles (itself)
    (16, 16, 0), -- Gaming Accessories (itself)
    (17, 17, 0), -- Desktop PCs (itself)
    (18, 18, 0); -- Laptop Accessories (itself)

-- Depth 1 (Direct Parent-Child Relationships)
INSERT INTO category_closure (ancestor_id, descendant_id, depth) VALUES
                                                                     (1, 4, 1),  -- Electronics -> Computers
                                                                     (1, 5, 1),  -- Electronics -> Smartphones
                                                                     (2, 6, 1),  -- Furniture -> Chairs
                                                                     (2, 7, 1),  -- Furniture -> Tables
                                                                     (3, 8, 1),  -- Books -> Novels
                                                                     (3, 9, 1),  -- Books -> Magazines
                                                                     (4, 10, 1), -- Computers -> Gaming
                                                                     (4, 11, 1), -- Computers -> Accessories
                                                                     (6, 12, 1), -- Chairs -> Office
                                                                     (7, 13, 1), -- Tables -> Outdoor
                                                                     (10, 14, 1), -- Gaming -> PCs
                                                                     (10, 15, 1), -- Gaming -> Consoles
                                                                     (11, 16, 1), -- Accessories -> Gaming Accessories
                                                                     (14, 17, 1), -- PCs -> Desktop PCs
                                                                     (16, 18, 1); -- Gaming Accessories -> Laptop Accessories

-- Depth 2 (Indirect relationships)
INSERT INTO category_closure (ancestor_id, descendant_id, depth) VALUES
                                                                     (1, 10, 2),  -- Electronics -> Gaming
                                                                     (1, 11, 2),  -- Electronics -> Accessories
                                                                     (2, 12, 2),  -- Furniture -> Office
                                                                     (2, 13, 2),  -- Furniture -> Outdoor
                                                                     (4, 14, 2),  -- Computers -> PCs
                                                                     (4, 15, 2),  -- Computers -> Consoles
                                                                     (6, 16, 2),  -- Chairs -> Gaming Accessories
                                                                     (10, 17, 2), -- Gaming -> Desktop PCs
                                                                     (15, 18, 2); -- Consoles -> Laptop Accessories

-- Depth 3 (Further indirect relationships)
INSERT INTO category_closure (ancestor_id, descendant_id, depth) VALUES
                                                                     (1, 14, 3),  -- Electronics -> PCs
                                                                     (1, 15, 3),  -- Electronics -> Consoles
                                                                     (2, 16, 3),  -- Furniture -> Gaming Accessories
                                                                     (4, 17, 3),  -- Computers -> Desktop PCs
                                                                     (10, 18, 3); -- Gaming -> Laptop Accessories

-- Depth 4 (Max depth from the root)
INSERT INTO category_closure (ancestor_id, descendant_id, depth) VALUES
                                                                     (1, 17, 4),  -- Electronics -> Desktop PCs
                                                                     (1, 18, 4); -- Electronics -> Laptop Accessories

-- Inserting Category translations
INSERT INTO category_translations (category_id, language_id, name)
VALUES (1, 1, 'First category'),
       (2, 1, 'Second category'),
       (3, 1, 'Third category'),
        (4, 1, 'Fourth category'),
       (5, 1, 'Fifth category'),
       (6, 1, 'Sixth category'),
       (7, 1, 'Seventh category'),
       (8, 1, 'Eight category'),
       (9, 1, 'Nine category'),
       (10, 1, 'Ten category');


-- Inserting Attribute. Defines an attribute type like size or color.
INSERT INTO attributes (id, name) VALUES (1, 'Color'), (2, 'Size'), (3, 'Material');

-- Inserting AttributeTranslation
INSERT INTO attribute_translations(id, attribute_id, language_id, name)
VALUES (1, 1, 1, 'Color'),
        (2, 2, 1, 'Size'),
        (3, 3, 1, 'Material');

-- Inserting AttributeValue
INSERT INTO attribute_values(id, attribute_id, value)
VALUES (1, 1, 'Blue'),
       (2, 1, 'Red'),
       (3, 2, 'X'),
       (4, 2, 'XL'),
       (5, 2, 'XXL'),
       (6, 3, 'Fiber'),
       (7, 3, 'Cotton'),
        (8, 1, 'Green');

-- Inserting AttributeValueTranslation
INSERT INTO attribute_value_translations(id, attribute_value_id, language_id, value)
VALUES (1, 1, 1, 'Blue'),
    (2, 2, 1, 'Red'),
    (3, 3, 1, 'X'),
    (4, 4, 1, 'XL'),
    (5, 5, 1, 'XXL'),
    (6, 6, 1, 'Fiber'),
    (7, 7, 1, 'Cotton'),
       (8, 8, 1, 'Green');

-- Inserting Product
INSERT INTO products(id, sku, name, slug, created_at, updated_at)
VALUES (1, 'SKU_1', 'Test product 1', 'test-product-1', NOW(), NOW()),
       (2, 'SKU_2', 'Test product 2', 'test-product-2', NOW(), NOW());

-- Inserting translations
INSERT INTO product_translations(id, product_id, language_id, name, description)
VALUES (1, 1, 1, 'Test product 1', 'Test product description 1'),
       (2, 2, 1, 'Test product 2', 'Test product description 2'),
       (3, 2, 2, 'Producto de prueba 2', 'Descripción del producto de prueba 2');


-- Inserting product category
INSERT INTO product_category(product_id, category_id)
VALUES (1, 10),
       (2, 12),
       (2, 14);

-- Inserting variants
INSERT INTO product_variants(id, product_id, sku)
VALUES (1, 1, 'SKU_1_V'),
       (2, 2, 'SKU_2_V'),
       (3, 1, 'SKU_1_2V');

-- Inserting product variant value
INSERT INTO product_variant_value(variant_id, attribute_value_id)
VALUES (1, 1),
       (1, 3),
       (2, 2),
       (2, 3),
       (3, 2),
       (3, 4),
        (3, 5),
        (3, 6),
       (1, 7),
       (1, 8);


-- Inserting VariantStock
INSERT INTO variant_stocks(id, variant_id, quantity, warehouse_id)
VALUES (1, 1, 100, 1),
       (2, 2, 150, 1),
       (3, 3, 200, 1);

-- Insert VariantPrice
INSERT INTO variant_prices(id, country_id, variant_id, currency_id, price, discount, tax_rate, is_active)
VALUES (1, 1, 1, 1, 1200, 0, 0, TRUE),
       (2, 1, 2, 1,1700, 0, 0, TRUE),
       (3, 1, 3, 1,1700, 0, 0, TRUE);


-- Insert VariantImage
INSERT INTO variant_images(id, variant_id, url)
VALUES (1, 1, 'https://laravel.pixelstrap.net/multikart/storage/49/fashion_173.jpg'),
       (2, 1, 'https://laravel.pixelstrap.net/multikart/storage/70/fashion_71.jpg'),
       (3, 2, 'https://laravel.pixelstrap.net/multikart/storage/13/fashion_311.jpg'),
        (4, 2, 'https://laravel.pixelstrap.net/multikart/storage/161/fashion_0122.jpg'),
       (5, 3, 'https://laravel.pixelstrap.net/multikart/storage/161/fashion_0122.jpg');