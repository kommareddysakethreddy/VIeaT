-- PostgreSQL is persistent, so we guard the sample data with NOT EXISTS checks.
-- This keeps docker restarts from inserting the same demo orders again and again.

INSERT INTO orders (customer_id, customer_name, delivery_address, order_status, total_amount, created_at, updated_at)
SELECT 101, 'Rahul Sharma', '21 Park Street, Bangalore', 'PLACED', 19.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE customer_id = 101
      AND customer_name = 'Rahul Sharma'
      AND delivery_address = '21 Park Street, Bangalore'
);

INSERT INTO order_items (food_item_id, food_item_name, quantity, price, subtotal, order_id)
SELECT 1, 'Margherita Pizza', 1, 12.99, 12.99,
       (SELECT id
        FROM orders
        WHERE customer_id = 101
          AND customer_name = 'Rahul Sharma'
          AND delivery_address = '21 Park Street, Bangalore')
WHERE EXISTS (
    SELECT 1
    FROM orders
    WHERE customer_id = 101
      AND customer_name = 'Rahul Sharma'
      AND delivery_address = '21 Park Street, Bangalore'
)
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    JOIN orders o ON o.id = oi.order_id
    WHERE o.customer_id = 101
      AND o.customer_name = 'Rahul Sharma'
      AND oi.food_item_name = 'Margherita Pizza'
  );

INSERT INTO order_items (food_item_id, food_item_name, quantity, price, subtotal, order_id)
SELECT 4, 'Cold Coffee', 2, 3.50, 7.00,
       (SELECT id
        FROM orders
        WHERE customer_id = 101
          AND customer_name = 'Rahul Sharma'
          AND delivery_address = '21 Park Street, Bangalore')
WHERE EXISTS (
    SELECT 1
    FROM orders
    WHERE customer_id = 101
      AND customer_name = 'Rahul Sharma'
      AND delivery_address = '21 Park Street, Bangalore'
)
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    JOIN orders o ON o.id = oi.order_id
    WHERE o.customer_id = 101
      AND o.customer_name = 'Rahul Sharma'
      AND oi.food_item_name = 'Cold Coffee'
  );

INSERT INTO orders (customer_id, customer_name, delivery_address, order_status, total_amount, created_at, updated_at)
SELECT 102, 'Anita Verma', '9 Lake View Road, Chennai', 'PREPARING', 16.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM orders
    WHERE customer_id = 102
      AND customer_name = 'Anita Verma'
      AND delivery_address = '9 Lake View Road, Chennai'
);

INSERT INTO order_items (food_item_id, food_item_name, quantity, price, subtotal, order_id)
SELECT 2, 'Veggie Burger', 2, 8.49, 16.98,
       (SELECT id
        FROM orders
        WHERE customer_id = 102
          AND customer_name = 'Anita Verma'
          AND delivery_address = '9 Lake View Road, Chennai')
WHERE EXISTS (
    SELECT 1
    FROM orders
    WHERE customer_id = 102
      AND customer_name = 'Anita Verma'
      AND delivery_address = '9 Lake View Road, Chennai'
)
  AND NOT EXISTS (
    SELECT 1
    FROM order_items oi
    JOIN orders o ON o.id = oi.order_id
    WHERE o.customer_id = 102
      AND o.customer_name = 'Anita Verma'
      AND oi.food_item_name = 'Veggie Burger'
  );
