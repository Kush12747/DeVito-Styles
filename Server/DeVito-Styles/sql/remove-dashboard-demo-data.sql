-- =====================================================================
-- REMOVE DASHBOARD DEMO DATA
--
-- Deletes everything added by sql/dashboard-demo-data.sql: the demo
-- customers (emails ending in @demo.devitostyles.com) and every
-- appointment, review, order, order item, payment and cart that
-- belongs to them. Real customers and their data are not touched.
-- =====================================================================

USE devitostyles;

-- BEGIN CLEANUP
SET @demo_email = '%@demo.devitostyles.com';

DELETE r FROM review r
    JOIN users u ON u.user_id = r.user_id
    WHERE u.email LIKE @demo_email;

DELETE a FROM appointment a
    JOIN users u ON u.user_id = a.user_id
    WHERE u.email LIKE @demo_email;

DELETE p FROM payments p
    JOIN orders o ON o.order_id = p.order_id
    JOIN users u ON u.user_id = o.user_id
    WHERE u.email LIKE @demo_email;

DELETE oi FROM order_items oi
    JOIN orders o ON o.order_id = oi.order_id
    JOIN users u ON u.user_id = o.user_id
    WHERE u.email LIKE @demo_email;

DELETE o FROM orders o
    JOIN users u ON u.user_id = o.user_id
    WHERE u.email LIKE @demo_email;

DELETE ci FROM cart_items ci
    JOIN carts c ON c.cart_id = ci.cart_id
    JOIN users u ON u.user_id = c.user_id
    WHERE u.email LIKE @demo_email;

DELETE c FROM carts c
    JOIN users u ON u.user_id = c.user_id
    WHERE u.email LIKE @demo_email;

DELETE FROM users WHERE email LIKE @demo_email;
-- END CLEANUP
