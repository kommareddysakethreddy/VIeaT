-- PostgreSQL keeps data after restart, so these inserts are written to be idempotent.
-- Each sample row is inserted only if a row with the same name is not already present.

INSERT INTO food_items (name, description, category, price, quantity_available, is_available, created_at, updated_at)
SELECT 'Margherita Pizza', 'Classic pizza with tomato, mozzarella, and basil', 'Pizza', 12.99, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Margherita Pizza');

INSERT INTO food_items (name, description, category, price, quantity_available, is_available, created_at, updated_at)
SELECT 'Veggie Burger', 'Burger with grilled vegetable patty and lettuce', 'Burger', 8.49, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Veggie Burger');

INSERT INTO food_items (name, description, category, price, quantity_available, is_available, created_at, updated_at)
SELECT 'Chocolate Brownie', 'Soft brownie served as dessert', 'Dessert', 4.25, 7, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Chocolate Brownie');

INSERT INTO food_items (name, description, category, price, quantity_available, is_available, created_at, updated_at)
SELECT 'Cold Coffee', 'Chilled coffee drink with milk', 'Drinks', 3.99, 20, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Cold Coffee');

INSERT INTO food_items (name, description, category, price, quantity_available, is_available, created_at, updated_at)
SELECT 'Pepperoni Pizza', 'Pizza topped with pepperoni and cheese', 'Pizza', 14.50, 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Pepperoni Pizza');
