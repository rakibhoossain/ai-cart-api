-- Insert sample orders for dashboard analytics
INSERT INTO orders (
    id, shop_id, customer_id, order_number, currency, 
    subtotal, shipping_cost, total_tax, total_price, 
    status, payment_status, payment_type, created_at, updated_at
) VALUES 
-- Recent orders (last 30 days)
(1, 1, 1, 'ORD-001', 'USD', 2500, 500, 250, 3250, 11, 1, 0, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(2, 1, 1, 'ORD-002', 'USD', 1800, 300, 180, 2280, 11, 1, 0, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
(3, 1, 1, 'ORD-003', 'USD', 3200, 400, 320, 3920, 11, 1, 0, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
(4, 1, 1, 'ORD-004', 'USD', 1500, 200, 150, 1850, 11, 1, 0, NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
(5, 1, 1, 'ORD-005', 'USD', 2800, 350, 280, 3430, 11, 1, 0, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'),

-- Older orders (for growth comparison)
(6, 1, 1, 'ORD-006', 'USD', 2000, 300, 200, 2500, 11, 1, 0, NOW() - INTERVAL '35 days', NOW() - INTERVAL '35 days'),
(7, 1, 1, 'ORD-007', 'USD', 1200, 200, 120, 1520, 11, 1, 0, NOW() - INTERVAL '40 days', NOW() - INTERVAL '40 days'),
(8, 1, 1, 'ORD-008', 'USD', 1800, 250, 180, 2230, 11, 1, 0, NOW() - INTERVAL '45 days', NOW() - INTERVAL '45 days'),

-- Some pending orders
(9, 1, 1, 'ORD-009', 'USD', 1600, 200, 160, 1960, 0, 0, 0, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(10, 1, 1, 'ORD-010', 'USD', 2200, 300, 220, 2720, 1, 0, 0, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');

-- Update sequence
SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));

-- Insert order items for the orders
INSERT INTO order_items (
    id, order_id, product_id, variant_id, quantity, price, total_price
) VALUES 
-- Order 1 items
(1, 1, 1, 1, 2, 1250, 2500),
-- Order 2 items  
(2, 2, 2, 2, 1, 1800, 1800),
-- Order 3 items
(3, 3, 1, 1, 1, 1250, 1250),
(4, 3, 2, 2, 1, 1950, 1950),
-- Order 4 items
(5, 4, 1, 3, 1, 1500, 1500),
-- Order 5 items
(6, 5, 2, 2, 1, 1800, 1800),
(7, 5, 1, 1, 1, 1000, 1000),
-- Order 6 items
(8, 6, 1, 1, 2, 1000, 2000),
-- Order 7 items
(9, 7, 2, 2, 1, 1200, 1200),
-- Order 8 items
(10, 8, 1, 3, 1, 1800, 1800),
-- Order 9 items (pending)
(11, 9, 1, 1, 1, 1600, 1600),
-- Order 10 items (confirmed)
(12, 10, 2, 2, 1, 2200, 2200);

-- Update sequence
SELECT setval(pg_get_serial_sequence('order_items', 'id'), (SELECT MAX(id) FROM order_items));

-- Insert order billing information
INSERT INTO order_billing (
    id, order_id, full_name, email, phone, address_line1, city, postal_code, country_id
) VALUES 
(1, 1, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(2, 2, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(3, 3, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(4, 4, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(5, 5, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(6, 6, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(7, 7, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(8, 8, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(9, 9, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(10, 10, 'Admin Customer', 'admin@aicart.store', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1);

-- Update sequence
SELECT setval(pg_get_serial_sequence('order_billing', 'id'), (SELECT MAX(id) FROM order_billing));

-- Insert order shipping information
INSERT INTO order_shipping (
    id, order_id, full_name, phone, address_line1, city, postal_code, country_id
) VALUES 
(1, 1, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(2, 2, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(3, 3, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(4, 4, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(5, 5, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(6, 6, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(7, 7, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(8, 8, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(9, 9, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1),
(10, 10, 'Admin Customer', '+1 (555) 987-6543', '123 Main St', 'New York', '10001', 1);

-- Update sequence
SELECT setval(pg_get_serial_sequence('order_shipping', 'id'), (SELECT MAX(id) FROM order_shipping));
