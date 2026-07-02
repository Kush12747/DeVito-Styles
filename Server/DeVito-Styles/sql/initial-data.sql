USE barber_shop;

-- =========================
-- USERS
-- =========================
INSERT INTO users (
    username,
    first_name,
    last_name,
    email,
    password,
    address,
    phone,
    role
)
VALUES
(
    '$2a$10$x7elzfxHWqjvKuKjZuGNbOl0FB1zTw5xOn/mQegoCC1sLRsFHDV1S',
    'Michael',
    'Stone',
    'admin@devitostyles.com',
    '$2a$10$Dz8qUnqfWvO6h0gFDy49LeUp1dfjE9eXuL1R4r8NiRGFQoQrqIhrO',
    '101 Main St',
    '3125551000',
    'ADMIN'
),
(
	'$2a$10$PGg4/bcA7P2kM3MgYutfeefBlFyPqng0E8ZrwwhwDjJotPvLL1WQu',
	'Kush',
	'Gandhi',
	'kushgandhi2099@gmail.com',
	'$2a$10$uEqOMnF9aH3ehIWirAnVresZFyKQgK63o9.x6GFmf32Bu8xmdnQCS',
	'22 Oak Ave',
	'2264534565',
	'CUSTOMER'),
(
    '$2a$10$qetS3gxvTdZ45Dxsm2zoyugPx6C2GOkXlyPXAWJnC0qrR.EGEQMZ2',
    'John',
    'Doe',
    'johndoe@gmail.com',
    '$2a$10$yfcaphpV5EtBieqi8XsTD.BEvatjKxttE4mWXJ8Aq8aM.XWYlgov.',
    '22 Oak Ave',
    '3125551001',
    'CUSTOMER'
),
(
    '$2a$10$WBC/De9K4CwN4vC5rm9OK.3yRqs6bjhGtVvjK1EFqveQfp5WvSsXO',
    'Anna',
    'Smith',
    'annasmith@gmail.com',
    '$2a$10$5JRisApt30sLAMS4YX8HIu6bDjLwGEn33vF4e/6njX4Tis4xxJlmC',
    '45 Pine Rd',
    '3125551002',
    'CUSTOMER'
),
(
    '$2a$10$1rVcb4ikPjA6or80oB8INeNeg5XEm.3k2Aa5PQ9coxSM4e2RTotTi',
    'Marcus',
    'Johnson',
    'mjohnson@gmail.com',
    '$2a$10$ED2fpIUFfyE7CDDkv3bCceZAguenXDrhoBrxGRYq/rzYUcNYZoYau',
    '78 Cedar Ln',
    '3125551003',
    'CUSTOMER'
);

INSERT INTO service (
    name,
    duration_minutes,
    price,
    description
)
VALUES
(
    'DeVito Style',
    1,
    1000.00,
    'Danny DeVito Style Service'
);

-- =========================
-- SERVICES
-- =========================
INSERT INTO service (
    name,
    duration_minutes,
    price,
    description
)
VALUES
('Classic Haircut', 30, 25.00, 'Standard haircut service'),
('Skin Fade', 45, 35.00, 'Fade haircut with detailed blending'),
('Beard Trim', 20, 15.00, 'Professional beard shaping and trim'),
('Haircut and Beard Combo', 60, 45.00, 'Haircut combined with beard service');

-- =========================
-- BARBERS
-- =========================
INSERT INTO barber (
    first_name,
    last_name,
    title,
    availability_status,
    specialization,
    start_year,
    bio,
    image_url,
    instagram_url,
    display_order,
    is_active
)
VALUES
(
    'Antonio',
    'DeVito',
    'Owner & Master Barber',
    'AVAILABLE',
    'Fades, Beard Styling, Razor Shaves',
    2005,
    'Antonio is the owner of DeVito Styles with over 15 years of experience. He specializes in modern fades, beard grooming, and precision razor work while delivering a premium client experience.',
    NULL,
    NULL,
    1,
    TRUE
),
(
    'Chris',
    'Lopez',
    'Senior Barber',
    'AVAILABLE',
    'Beard Styling, Tapers, Hair Design',
    2016,
    'Chris is known for clean tapers, beard shaping, and detailed hair designs. He enjoys helping clients find styles that fit their personality.',
    NULL,
    NULL,
    2,
    TRUE
),
(
    'David',
    'Miller',
    'Barber',
    'UNAVAILABLE',
    'Classic Cuts, Scissor Cuts, Kids Haircuts',
    2021,
    'David specializes in classic barbering techniques, scissor cuts, and family-friendly services for clients of all ages.',
    NULL,
    NULL,
    3,
    TRUE
),
(
    'Marcus',
    'Johnson',
    'Senior Barber',
    'AVAILABLE',
    'Skin Fades, Lineups, Beard Styling',
    2014,
    'Marcus has built a reputation for sharp fades, crisp lineups, and consistent attention to detail.',
    NULL,
    NULL,
    4,
    TRUE
),
(
    'Luis',
    'Garcia',
    'Barber',
    'AVAILABLE',
    'Textured Cuts, Modern Styles, Color Enhancements',
    2026,
    'Luis enjoys creating modern hairstyles and helping clients experiment with new looks while maintaining healthy hair.',
    NULL,
    NULL,
    5,
    TRUE
);