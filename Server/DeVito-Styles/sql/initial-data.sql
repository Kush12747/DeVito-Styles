-- USE barber_shop;
--
-- -- =========================
-- -- USERS
-- -- =========================
-- INSERT INTO users (
--     username,
--     first_name,
--     last_name,
--     email,
--     password,
--     address,
--     phone,
--     role
-- )
-- VALUES
-- (
--     'admin1',
--     'Michael',
--     'Stone',
--     'admin@devitostyles.com',
--     'admin123',
--     '101 Main St',
--     '3125551000',
--     'ADMIN'
-- ),
-- ('kush12', 'Kush', 'Gandhi', 'kushgandhi2099@gmail.com', 'password123', '22 Oak Ave', '2264534565', 'CUSTOMER'),
-- (
--     'jdoe',
--     'John',
--     'Doe',
--     'johndoe@gmail.com',
--     'password123',
--     '22 Oak Ave',
--     '3125551001',
--     'CUSTOMER'
-- ),
-- (
--     'asmith',
--     'Anna',
--     'Smith',
--     'annasmith@gmail.com',
--     'password123',
--     '45 Pine Rd',
--     '3125551002',
--     'CUSTOMER'
-- ),
-- (
--     'mjohnson',
--     'Marcus',
--     'Johnson',
--     'mjohnson@gmail.com',
--     'password123',
--     '78 Cedar Ln',
--     '3125551003',
--     'CUSTOMER'
-- );
--
-- INSERT INTO service (
--     name,
--     duration_minutes,
--     price,
--     description
-- )
-- VALUES
-- (
--     'DeVito Style',
--     1,
--     1000.00,
--     'Danny DeVito Style Service'
-- );
--
-- -- =========================
-- -- SERVICES
-- -- =========================
-- INSERT INTO service (
--     name,
--     duration_minutes,
--     price,
--     description
-- )
-- VALUES
-- ('Classic Haircut', 30, 25.00, 'Standard haircut service'),
-- ('Skin Fade', 45, 35.00, 'Fade haircut with detailed blending'),
-- ('Beard Trim', 20, 15.00, 'Professional beard shaping and trim'),
-- ('Haircut and Beard Combo', 60, 45.00, 'Haircut combined with beard service');
--
-- -- =========================
-- -- BARBERS
-- -- =========================
-- INSERT INTO barber (
--     first_name,
--     last_name,
--     availability_status,
--     specialization
-- )
-- VALUES
-- ('Antonio', 'DeVito', 'AVAILABLE', 'Fades'),
-- ('Chris', 'Lopez', 'AVAILABLE', 'Beard Styling'),
-- ('David', 'Miller', 'UNAVAILABLE', 'Classic Cuts');
--
-- -- =========================
-- -- APPOINTMENTS
-- -- =========================
-- INSERT INTO appointment (
--     user_id,
--     barber_id,
--     service_id,
--     appointment_datetime,
--     status
-- )
-- VALUES
-- (2, 1, 2, '2026-05-23 10:00:00', 'BOOKED'),
-- (3, 2, 3, '2026-05-23 11:30:00', 'BOOKED'),
-- (3, 2, 2, '2026-05-23 09:00:00', 'BOOKED'),
-- (4, 1, 4, '2026-05-21 14:00:00', 'COMPLETED'),
-- (2, 3, 3, '2026-05-22 16:00:00','CANCELLED'),
-- (4, 2, 3, '2026-05-23 14:00:00', 'BOOKED' ),
-- (5, 3, 4, '2026-05-31 10:00:00', 'BOOKED');
--
-- INSERT INTO appointment (user_id, barber_id, service_id, appointment_datetime, status)
-- VALUES
-- (3, 1, 1, '2025-05-10 10:00:00', 'COMPLETED'),
-- (3, 2, 2, '2025-05-12 11:00:00', 'CANCELLED'),
-- (3, 1, 3, '2025-05-14 09:00:00', 'COMPLETED');

-- USERS
INSERT INTO users (username, first_name, last_name, email, password, address, phone, role)
VALUES
    ('admin1','Michael','Stone','admin@devitostyles.com','admin123','101 Main St','3125551000','ADMIN'),
    ('kush12','Kush','Gandhi','kushgandhi2099@gmail.com','password123','22 Oak Ave','2264534565','CUSTOMER'),
    ('jdoe','John','Doe','johndoe@gmail.com','password123','22 Oak Ave','3125551001','CUSTOMER'),
    ('asmith','Anna','Smith','annasmith@gmail.com','password123','45 Pine Rd','3125551002','CUSTOMER'),
    ('mjohnson','Marcus','Johnson','mjohnson@gmail.com','password123','78 Cedar Ln','3125551003','CUSTOMER');

-- SERVICES
INSERT INTO service (name, duration_minutes, price, description)
VALUES
    ('DeVito Style', 60, 1000.00, 'Danny DeVito Style Service'),
    ('Classic Haircut', 30, 25.00, 'Standard haircut service'),
    ('Skin Fade', 45, 35.00, 'Fade haircut with detailed blending'),
    ('Beard Trim', 20, 15.00, 'Professional beard shaping and trim'),
    ('Haircut and Beard Combo', 60, 45.00, 'Haircut combined with beard service');

-- BARBERS
INSERT INTO barber (first_name, last_name, availability_status, specialization)
VALUES
    ('Antonio', 'DeVito', 'AVAILABLE', 'Fades'),
    ('Chris', 'Lopez', 'AVAILABLE', 'Beard Styling'),
    ('David', 'Miller', 'UNAVAILABLE', 'Classic Cuts');

-- APPOINTMENTS
INSERT INTO appointment (user_id, barber_id, service_id, appointment_datetime, status)
VALUES
    (2, 1, 2, '2026-05-23 10:00:00', 'BOOKED'),
    (3, 2, 3, '2026-05-23 11:30:00', 'BOOKED'),
    (3, 2, 2, '2026-05-23 09:00:00', 'BOOKED'),
    (4, 1, 4, '2026-05-21 14:00:00', 'COMPLETED'),
    (2, 3, 3, '2026-05-22 16:00:00', 'CANCELLED'),
    (4, 2, 3, '2026-05-23 14:00:00', 'BOOKED'),
    (5, 3, 4, '2026-05-31 10:00:00', 'BOOKED');