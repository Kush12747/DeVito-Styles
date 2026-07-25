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
    'mstone1',
    'Michael',
    'Stone',
    'admin@devitostyles.com',
    '$2a$10$Dz8qUnqfWvO6h0gFDy49LeUp1dfjE9eXuL1R4r8NiRGFQoQrqIhrO',
    '101 Main St',
    '3125551000',
    'ADMIN'
),
(
	'Kush12',
	'Kush',
	'Gandhi',
	'kushgandhi2099@gmail.com',
	'$2a$10$uEqOMnF9aH3ehIWirAnVresZFyKQgK63o9.x6GFmf32Bu8xmdnQCS',
	'22 Oak Ave',
	'2264534565',
	'CUSTOMER'),
(
    'jdoe',
    'John',
    'Doe',
    'johndoe@gmail.com',
    '$2a$10$yfcaphpV5EtBieqi8XsTD.BEvatjKxttE4mWXJ8Aq8aM.XWYlgov.',
    '22 Oak Ave',
    '3125551001',
    'CUSTOMER'
),
(
    'asmith',
    'Anna',
    'Smith',
    'annasmith@gmail.com',
    '$2a$10$5JRisApt30sLAMS4YX8HIu6bDjLwGEn33vF4e/6njX4Tis4xxJlmC',
    '45 Pine Rd',
    '3125551002',
    'CUSTOMER'
),
(
    'mjohn',
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
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1783295366/Antonio_f4qzoa.jpg',
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
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1783295366/Chris_txfdzo.jpg',
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
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1783295366/David_uy4kz4.jpg',
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
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1783295366/Marcus_peyqlw.jpg',
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
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1783295366/Luis_ufn8ux.jpg',
    NULL,
    5,
    TRUE
);


INSERT INTO categories (name, description)
VALUES
('Pomades','Hair styling products'),
('Hair Care','Shampoo and conditioner'),
('Beard Care','Beard oils and balms'),
('Accessories','Combs, brushes, razors');


INSERT INTO products
(
    category_id,
    name,
    description,
    price,
    stock_quantity,
    image_url,
    is_featured
)
VALUES
(
    1,
    'Matte Pomade',
    'Medium hold with matte finish.',
    19.99,
    20,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1784913960/MattePomade_ltb5cp.jpg',
    TRUE
),
(
    2,
    'Daily Shampoo',
    'Hydrating shampoo for everyday use.',
    14.99,
    35,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1784913965/DilyShampoo_yhzb3g.jpg',
    FALSE
),
(
    3,
    'Beard Oil',
    'Nourishes and softens beard hair.',
    17.99,
    18,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1784913976/BeardOil_nkkytn.jpg',
    TRUE
);

-- Beard Oil
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(1,
 '2 oz',
 'Sandalwood',
 'All Beard Types',
 'N/A',
 'Natural',
 'USA',
 '57 g',
 'BO-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(1,'Hydrates Dry Beard',1),
(1,'Softens Hair',2),
(1,'Adds Natural Shine',3),
(1,'Reduces Itching',4),
(1,'Made With Natural Oils',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(1,'Argan Oil',1),
(1,'Jojoba Oil',2),
(1,'Vitamin E',3),
(1,'Tea Tree Oil',4);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(1,1,'Dispense 2–3 drops into your palm.'),
(1,2,'Rub hands together evenly.'),
(1,3,'Massage into beard and skin.'),
(1,4,'Comb through for even distribution.');


-- Shampoo
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(
    2,
    '12 fl oz',
    'Fresh Citrus',
    'All Hair Types',
    'N/A',
    'N/A',
    'USA',
    '355 g',
    'DS-001'
);

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(2, 'Hydrates Dry Hair', 1),
(2, 'Safe for Daily Use', 2),
(2, 'Removes Dirt & Oil', 3),
(2, 'Sulfate Free Formula', 4),
(2, 'Leaves Hair Soft & Healthy', 5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(2, 'Aloe Vera', 1),
(2, 'Argan Oil', 2),
(2, 'Vitamin E', 3),
(2, 'Tea Tree Extract', 4),
(2, 'Coconut Oil', 5);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(2, 1, 'Wet hair thoroughly with warm water.'),
(2, 2, 'Apply a quarter-sized amount of shampoo.'),
(2, 3, 'Massage gently into the scalp and hair.'),
(2, 4, 'Rinse thoroughly with water.'),
(2, 5, 'Repeat if necessary for a deeper clean.');


-- Matte Pomade
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(
    3,
    '4 oz',
    'Classic Barber',
    'Short to Medium Hair',
    'Medium Hold',
    'Matte',
    'USA',
    '113 g',
    'MP-001'
);

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(3, 'Provides Medium Hold', 1),
(3, 'Natural Matte Finish', 2),
(3, 'Easy to Restyle Throughout the Day', 3),
(3, 'Water-Based Formula', 4),
(3, 'Washes Out Easily', 5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(3, 'Beeswax', 1),
(3, 'Kaolin Clay', 2),
(3, 'Castor Oil', 3),
(3, 'Shea Butter', 4),
(3, 'Vitamin E', 5);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(3, 1, 'Scoop a small amount onto your fingertips.'),
(3, 2, 'Rub between your palms until evenly distributed.'),
(3, 3, 'Apply to dry or slightly damp hair.'),
(3, 4, 'Style using your hands or a comb for your desired look.');