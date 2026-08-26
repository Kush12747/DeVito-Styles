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
-- Styling
(
    1,
    'Matte Pomade',
    'Medium hold pomade with a natural matte finish for everyday styling.',
    19.99,
    20,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1784913960/MattePomade_ltb5cp.jpg',
    TRUE
),
(
    1,
    'Hair Clay',
    'Strong hold styling clay with a matte finish and added texture.',
    21.99,
    18,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337829/ea088377-307a-4a0b-80a3-9c4d94f3c986.png',
    TRUE
),
(
    1,
    'Texture Powder',
    'Lightweight styling powder that adds instant volume and texture.',
    16.99,
    24,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785338597/29c2c573-66c1-446a-9db4-e19a74f4f835.png',
    FALSE
),

-- Hair Care
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
    2,
    'Daily Conditioner',
    'Moisturizing conditioner that leaves hair soft and healthy.',
    15.99,
    28,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337549/77ba2597-bca8-4b19-a3f2-4b4fd4571a3d.png',
    FALSE
),
(
    2,
    'Tea Tree Shampoo',
    'Refreshing shampoo formulated with tea tree oil for a clean scalp.',
    16.99,
    22,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337923/737ef7ae-9abe-4ac6-9c58-8dd151451063.png',
    TRUE
),

-- Beard Care
(
    3,
    'Beard Oil',
    'Nourishes and softens beard hair.',
    17.99,
    18,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1784913976/BeardOil_nkkytn.jpg',
    TRUE
),
(
    3,
    'Beard Balm',
    'Conditions beard while providing light styling control.',
    18.99,
    20,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785336653/db704deb-5dbe-430f-8985-1b23dac36c7d.png',
    FALSE
),
(
    3,
    'Beard Wash',
    'Gentle beard cleanser that removes dirt without drying hair.',
    15.99,
    25,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337461/ad8b1506-ccc7-4be3-bc0c-ffeb0e96997d.png',
    FALSE
),

-- Accessories
(
    4,
    'Carbon Fiber Styling Comb',
    'Professional anti-static carbon fiber styling comb.',
    9.99,
    40,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337502/04a9c6e2-9464-4b85-a4c4-f42174815294.png',
    FALSE
),
(
    4,
    'Wooden Beard Comb',
    'Pocket-sized wooden beard comb with smooth rounded teeth.',
    12.99,
    35,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785338706/96b8214b-5d9e-4cb2-b34a-45a56f4623a2.png',
    FALSE
),
(
    4,
    'Vent Hair Brush',
    'Professional vent brush for fast drying and styling.',
    18.99,
    18,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785338660/d243df24-54d0-4480-8b56-5b72cc38594b.png',
    FALSE
),
(
    4,
    'Barber Neck Duster',
    'Soft neck duster for removing loose hair after haircuts.',
    14.99,
    15,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785336609/5573ef26-44ee-4d3e-a724-43552994cb2f.png',
    FALSE
),
(
    4,
    'Straight Razor Holder',
    'Professional stainless steel straight razor holder.',
    24.99,
    12,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785337892/93ddbe4c-55d8-4f90-8313-d68618525a82.png',
    TRUE
),
(
    4,
    'Barber Cape',
    'Water-resistant barber cape with adjustable neck closure.',
    29.99,
    10,
    'https://res.cloudinary.com/dc0awmexj/image/upload/v1785336531/4c2caa89-0894-4a0b-8585-1a57f44e0290.png',
    FALSE
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

-- Daily Conditioner
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(4,'12 fl oz','Fresh Citrus','All Hair Types','N/A','N/A','USA','355 g','DC-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(4,'Moisturizes Hair',1),
(4,'Reduces Frizz',2),
(4,'Improves Manageability',3),
(4,'Adds Natural Shine',4),
(4,'Safe for Daily Use',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(4,'Shea Butter',1),
(4,'Argan Oil',2),
(4,'Aloe Vera',3),
(4,'Vitamin E',4);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(4,1,'Apply after shampooing.'),
(4,2,'Massage through hair.'),
(4,3,'Leave for 2 minutes.'),
(4,4,'Rinse thoroughly.');


-- Tea Tree Shampoo
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(5,'12 fl oz','Tea Tree','Oily Hair','N/A','N/A','USA','355 g','TS-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(5,'Deep Cleans Scalp',1),
(5,'Removes Oil Buildup',2),
(5,'Refreshes Hair',3),
(5,'Cooling Sensation',4),
(5,'Daily Formula',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(5,'Tea Tree Oil',1),
(5,'Peppermint Oil',2),
(5,'Vitamin E',3),
(5,'Aloe Vera',4);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(5,1,'Wet hair.'),
(5,2,'Massage into scalp.'),
(5,3,'Lather well.'),
(5,4,'Rinse completely.');

-- Beard Balm
INSERT INTO product_specifications
(product_id,size,scent,hair_type,hold_strength,finish,country_of_origin,weight,sku)
VALUES
(6,'2 oz','Cedarwood','All Beard Types','Light Hold','Natural','USA','57 g','BB-001');

INSERT INTO product_benefits
(product_id,benefit,display_order)
VALUES
(6,'Controls Flyaways',1),
(6,'Conditions Beard',2),
(6,'Light Styling Hold',3),
(6,'Reduces Dryness',4),
(6,'Natural Ingredients',5);

INSERT INTO product_ingredients
(product_id,ingredient,display_order)
VALUES
(6,'Beeswax',1),
(6,'Shea Butter',2),
(6,'Jojoba Oil',3),
(6,'Argan Oil',4);

INSERT INTO product_usage_steps
(product_id,step_number,instruction)
VALUES
(6,1,'Scrape a small amount.'),
(6,2,'Warm between palms.'),
(6,3,'Work evenly through beard.'),
(6,4,'Shape with a beard comb.');

-- Beard Wash
INSERT INTO product_specifications
(product_id,size,scent,hair_type,hold_strength,finish,country_of_origin,weight,sku)
VALUES
(7,'8 fl oz','Eucalyptus','All Beard Types','N/A','Natural','USA','240 g','BW-001');

INSERT INTO product_benefits
(product_id,benefit,display_order)
VALUES
(7,'Cleans Beard',1),
(7,'Hydrates Skin',2),
(7,'Prevents Dryness',3),
(7,'Removes Dirt',4),
(7,'Daily Formula',5);

INSERT INTO product_ingredients
(product_id,ingredient,display_order)
VALUES
(7,'Tea Tree Oil',1),
(7,'Aloe Vera',2),
(7,'Argan Oil',3),
(7,'Vitamin E',4);

INSERT INTO product_usage_steps
(product_id,step_number,instruction)
VALUES
(7,1,'Wet beard.'),
(7,2,'Apply beard wash.'),
(7,3,'Massage thoroughly.'),
(7,4,'Rinse well.');

-- Hair Clay
INSERT INTO product_specifications
(product_id,size,scent,hair_type,hold_strength,finish,country_of_origin,weight,sku)
VALUES
(8,'4 oz','Fresh Mint','Short to Medium Hair','Strong Hold','Matte','USA','113 g','HC-001');

INSERT INTO product_benefits
(product_id,benefit,display_order)
VALUES
(8,'Strong Hold',1),
(8,'Matte Finish',2),
(8,'Adds Texture',3),
(8,'Humidity Resistant',4),
(8,'Easy to Wash Out',5);

INSERT INTO product_ingredients
(product_id,ingredient,display_order)
VALUES
(8,'Kaolin Clay',1),
(8,'Beeswax',2),
(8,'Castor Oil',3),
(8,'Vitamin E',4);

INSERT INTO product_usage_steps
(product_id,step_number,instruction)
VALUES
(8,1,'Rub a small amount between palms.'),
(8,2,'Apply to dry hair.'),
(8,3,'Style with fingers.'),
(8,4,'Add more if needed.');

-- Texture Powder
INSERT INTO product_specifications
(product_id,size,scent,hair_type,hold_strength,finish,country_of_origin,weight,sku)
VALUES
(9,'0.5 oz','Unscented','Fine Hair','Light Hold','Matte','USA','15 g','TP-001');

INSERT INTO product_benefits
(product_id,benefit,display_order)
VALUES
(9,'Instant Volume',1),
(9,'Adds Texture',2),
(9,'Oil Control',3),
(9,'Lightweight Formula',4),
(9,'Invisible Finish',5);

INSERT INTO product_ingredients
(product_id,ingredient,display_order)
VALUES
(9,'Silica Silylate',1),
(9,'Rice Powder',2),
(9,'Magnesium Carbonate',3);

INSERT INTO product_usage_steps
(product_id,step_number,instruction)
VALUES
(9,1,'Sprinkle lightly onto dry hair.'),
(9,2,'Massage into roots.'),
(9,3,'Style as desired.');

-- Carbon Fiber Styling Comb
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(10,'7.5 in','N/A','All Hair Types','N/A','Matte Black','USA','35 g','CFC-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(10,'Heat Resistant',1),
(10,'Anti-Static Material',2),
(10,'Smooth Glide Through Hair',3),
(10,'Lightweight Design',4),
(10,'Professional Grade',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(10,'Carbon Fiber Composite',1);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(10,1,'Use on dry or damp hair.'),
(10,2,'Comb from roots to ends.'),
(10,3,'Clean after each use.');

-- Wooden Beard Comb
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(11,'4 in','N/A','All Beard Types','N/A','Natural Wood','USA','22 g','WBC-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(11,'Reduces Static',1),
(11,'Detangles Beard',2),
(11,'Pocket Sized',3),
(11,'Smooth Rounded Teeth',4),
(11,'Natural Wood Finish',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(11,'Sandalwood',1);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(11,1,'Comb beard from top to bottom.'),
(11,2,'Shape as desired.');

-- Vent Hair Brush
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(12,'9 in','N/A','All Hair Types','N/A','Matte','USA','95 g','VHB-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(12,'Speeds Up Blow Drying',1),
(12,'Reduces Tangles',2),
(12,'Comfort Grip Handle',3),
(12,'Flexible Bristles',4),
(12,'Lightweight',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(12,'ABS Plastic',1),
(12,'Nylon Bristles',2);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(12,1,'Brush through damp or dry hair.'),
(12,2,'Use while blow drying if desired.');

-- Barber Neck Duster
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(13,'6 in','N/A','N/A','N/A','Black','USA','120 g','ND-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(13,'Soft Synthetic Bristles',1),
(13,'Comfortable Grip',2),
(13,'Removes Loose Hair',3),
(13,'Professional Quality',4),
(13,'Easy to Clean',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(13,'Synthetic Fiber Bristles',1),
(13,'Wood Handle',2);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(13,1,'Brush loose hair from neck and shoulders.'),
(13,2,'Clean bristles regularly.');

-- Straight Razor Holder
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(14,'5.5 in','N/A','N/A','N/A','Stainless Steel','USA','65 g','SRH-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(14,'Professional Barber Tool',1),
(14,'Comfort Grip',2),
(14,'Replaceable Blade Design',3),
(14,'Rust Resistant',4),
(14,'Precision Shaving',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(14,'Stainless Steel',1);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(14,1,'Insert a fresh blade.'),
(14,2,'Lock blade securely.'),
(14,3,'Use with proper shaving technique.');

-- Barber Cape
INSERT INTO product_specifications
(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
VALUES
(15,'Universal','N/A','N/A','N/A','Water Resistant','USA','250 g','BC-001');

INSERT INTO product_benefits
(product_id, benefit, display_order)
VALUES
(15,'Water Resistant Fabric',1),
(15,'Adjustable Neck Closure',2),
(15,'Lightweight Material',3),
(15,'Professional Appearance',4),
(15,'Machine Washable',5);

INSERT INTO product_ingredients
(product_id, ingredient, display_order)
VALUES
(15,'Polyester Fabric',1);

INSERT INTO product_usage_steps
(product_id, step_number, instruction)
VALUES
(15,1,'Place cape around customer.'),
(15,2,'Adjust neck closure.'),
(15,3,'Shake off loose hair after use.');

INSERT INTO cart_items
(cart_id, product_id, quantity)
VALUES
(1, 5, 2);