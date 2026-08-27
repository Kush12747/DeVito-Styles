DROP DATABASE IF EXISTS barber_shop_test;
CREATE DATABASE barber_shop_test;
USE barber_shop_test;

CREATE TABLE categories (
	category_id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL UNIQUE,
	description VARCHAR(255)
);

CREATE TABLE products (
	product_id INT PRIMARY KEY AUTO_INCREMENT,
	category_id INT NOT NULL,
	
	name VARCHAR(255) NOT NULL,
	description TEXT,
	price DECIMAL(10,2) NOT NULL,
	stock_quantity INT NOT NULL DEFAULT 0,
	
	image_url VARCHAR(255),
	
	is_featured BOOLEAN NOT NULL DEFAULT FALSE,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_category
        FOREIGN KEY(category_id)
        REFERENCES categories(category_id)
);


CREATE TABLE product_specifications (
    product_id INT PRIMARY KEY,

    size VARCHAR(50) NOT NULL,
    scent VARCHAR(100),
    hair_type VARCHAR(100),
    hold_strength VARCHAR(50),
    finish VARCHAR(50),
    country_of_origin VARCHAR(100),
    weight VARCHAR(50),
    sku VARCHAR(50) UNIQUE,

    CONSTRAINT fk_product_specifications_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE product_benefits (
    benefit_id INT PRIMARY KEY AUTO_INCREMENT,

    product_id INT NOT NULL,

    benefit VARCHAR(150) NOT NULL,

    display_order INT DEFAULT 1,

    CONSTRAINT fk_product_benefits_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE product_ingredients (
    ingredient_id INT PRIMARY KEY AUTO_INCREMENT,

    product_id INT NOT NULL,

    ingredient VARCHAR(100) NOT NULL,

    display_order INT DEFAULT 1,

    CONSTRAINT fk_product_ingredients_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

CREATE TABLE product_usage_steps (
    step_id INT PRIMARY KEY AUTO_INCREMENT,

    product_id INT NOT NULL,

    step_number INT NOT NULL,

    instruction VARCHAR(255) NOT NULL,

    CONSTRAINT fk_product_usage_steps_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE CASCADE
);


CREATE TABLE users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(100) UNIQUE,
	username VARCHAR(50) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	address VARCHAR(100),
	phone VARCHAR(15),
	role VARCHAR(15) NOT NULL,
	profile_picture_url VARCHAR(500)
);

CREATE TABLE service (
	service_id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	duration_minutes INT NOT NULL,
	price DECIMAL(10,2) NOT NULL,
	description TEXT NOT NULL
);

CREATE TABLE barber (
	barber_id INT PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	availability_status VARCHAR(20) NOT NULL,
	specialization VARCHAR(100) NOT NULL,
	image_url VARCHAR(500),
	title VARCHAR(50),
	bio TEXT,
	start_year INT,
	instagram_url VARCHAR(500),
	display_order INT DEFAULT 0,
	is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE appointment (
	appointment_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	barber_id INT NOT NULL,
	service_id INT NOT NULL,
	appointment_datetime DATETIME NOT NULL,
	status VARCHAR(50) NOT NULL,
	google_event_id VARCHAR(255),

	CONSTRAINT fk_user_id
		FOREIGN KEY (user_id)
		REFERENCES users(user_id),

	CONSTRAINT fk_barber_id
		FOREIGN KEY (barber_id)
		REFERENCES barber(barber_id),

	CONSTRAINT fk_service_id
		FOREIGN KEY (service_id)
		REFERENCES service(service_id)
);

ALTER TABLE appointment
ADD CONSTRAINT unique_barber_time
UNIQUE (barber_id, appointment_datetime);

CREATE TABLE review (
	review_id INT PRIMARY KEY AUTO_INCREMENT,
	
	user_id INT NOT NULL,
	appointment_id INT NOT NULL UNIQUE,
	barber_id INT NOT NULL,
	
	rating TINYINT NOT NULL,	
	review_text TEXT,
	
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	
	CONSTRAINT fk_review_user
		FOREIGN KEY (user_id)
		REFERENCES users(user_id),
	
	CONSTRAINT fk_review_barber
		FOREIGN KEY	(barber_id)
		REFERENCES barber(barber_id),
		
	CONSTRAINT fk_review_appointment
		FOREIGN KEY (appointment_id)
		REFERENCES appointment(appointment_id)
);

CREATE TABLE carts (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

CREATE TABLE cart_items (
    cart_item_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,

    CONSTRAINT fk_cartitem_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts(cart_id),

    CONSTRAINT fk_cartitem_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
);

CREATE TABLE orders (

    order_id INT PRIMARY KEY AUTO_INCREMENT,

    order_number VARCHAR(50) NOT NULL UNIQUE,

    user_id INT NOT NULL,

    status ENUM(
        'Pending',
        'Paid',
        'Processing',
        'Completed',
        'Cancelled',
        'Refunded'
    ) DEFAULT 'Pending',

    subtotal DECIMAL(10,2) NOT NULL,

    tax_amount DECIMAL(10,2) DEFAULT 0.00,

    shipping_cost DECIMAL(10,2) DEFAULT 0.00,

    discount_amount DECIMAL(10,2) DEFAULT 0.00,

    total_amount DECIMAL(10,2) NOT NULL,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_user

        FOREIGN KEY(user_id)

        REFERENCES users(user_id)

);

CREATE TABLE order_items (

    order_item_id INT PRIMARY KEY AUTO_INCREMENT,

    order_id INT NOT NULL,

    product_id INT NOT NULL,

    product_name VARCHAR(255) NOT NULL,

    quantity INT NOT NULL,

    unit_price DECIMAL(10,2) NOT NULL,

    line_total DECIMAL(10,2) NOT NULL,


    CONSTRAINT fk_orderitems_order

        FOREIGN KEY(order_id)

        REFERENCES orders(order_id),


    CONSTRAINT fk_orderitems_product

        FOREIGN KEY(product_id)

        REFERENCES products(product_id)

);

CREATE TABLE payments (

    payment_id INT PRIMARY KEY AUTO_INCREMENT,

    order_id INT NOT NULL,

    payment_provider VARCHAR(50) DEFAULT 'Stripe',

    payment_intent_id VARCHAR(255),

    stripe_charge_id VARCHAR(255),

    payment_status ENUM(
        'Pending',
        'Succeeded',
        'Failed',
        'Refunded'
    ) DEFAULT 'Pending',

    amount DECIMAL(10,2) NOT NULL,

    currency CHAR(3) DEFAULT 'USD',

    paid_at DATETIME,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT fk_payment_order

        FOREIGN KEY(order_id)

        REFERENCES orders(order_id)

);
CREATE INDEX idx_payment_intent_id
ON payments(payment_intent_id);


DELIMITER //

CREATE PROCEDURE set_known_good_state()
BEGIN
	
	DELETE FROM payments;
	DELETE FROM order_items;
	DELETE FROM orders;
	
	DELETE FROM cart_items;
	DELETE FROM carts;

	DELETE FROM review;
	DELETE FROM appointment;
	
	DELETE FROM product_usage_steps;
	DELETE FROM product_ingredients;
	DELETE FROM product_benefits;
	DELETE FROM product_specifications;
	DELETE FROM products;
	
	DELETE FROM service;
	DELETE FROM barber;
	
	DELETE FROM users;
	DELETE FROM categories;
	
	ALTER TABLE orders AUTO_INCREMENT = 1;
	ALTER TABLE order_items AUTO_INCREMENT = 1;
	ALTER TABLE payments AUTO_INCREMENT = 1;
	ALTER TABLE carts AUTO_INCREMENT = 1;
	ALTER TABLE cart_items AUTO_INCREMENT = 1;
	ALTER TABLE users AUTO_INCREMENT = 1;
	ALTER TABLE categories AUTO_INCREMENT = 1;
	ALTER TABLE products AUTO_INCREMENT = 1;
	ALTER TABLE product_benefits AUTO_INCREMENT = 1;
	ALTER TABLE product_ingredients AUTO_INCREMENT = 1;
	ALTER TABLE product_usage_steps AUTO_INCREMENT = 1;
	ALTER TABLE service AUTO_INCREMENT = 1;
	ALTER TABLE barber AUTO_INCREMENT = 1;
	ALTER TABLE appointment AUTO_INCREMENT = 1;
	ALTER TABLE review AUTO_INCREMENT = 1;

	-- USERS
	INSERT INTO users (
    username,
    first_name,
    last_name,
    email,
    password,
    address,
    phone,
    role,
    profile_picture_url
)
VALUES
(
    'admin1',
    'Michael',
    'Stone',
    'admin@devitostyles.com',
    'admin123',
    '101 Main St',
    '3125551000',
    'ADMIN',
    NULL
),
(
    'jdoe',
    'John',
    'Doe',
    'johndoe@email.com',
    'password123',
    '22 Oak Ave',
    '3125551001',
    'CUSTOMER',
    NULL
),
(
    'asmith',
    'Anna',
    'Smith',
    'annasmith@email.com',
    'password123',
    '45 Pine Rd',
    '3125551002',
    'CUSTOMER',
    NULL
),
(
    'mjohnson',
    'Marcus',
    'Johnson',
    'mjohnson@email.com',
    'password123',
    '78 Cedar Ln',
    '3125551003',
    'CUSTOMER',
    NULL
);
	-- SERVICES
	INSERT INTO service (name, duration_minutes, price, description)
	VALUES
	('Classic Haircut',30,25.00,'Standard haircut service'),
	('Skin Fade',45,35.00,'Fade haircut with detailed blending'),
	('Beard Trim',20,15.00,'Professional beard shaping and trim'),
	('Haircut and Beard Combo',60,45.00,'Haircut combined with beard service');

	-- BARBERS
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
	2011,
	'Antonio is the owner of DeVito Styles and specializes in precision fades and beard grooming.',
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
	'Beard Styling, Tapers',
	2016,
	'Chris enjoys creating modern hairstyles and detailed beard work.',
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
	'Classic Cuts',
	2019,
	'David specializes in traditional barbering techniques and family haircuts.',
	NULL,
	NULL,
	3,
	TRUE
);

	-- APPOINTMENTS
	INSERT INTO appointment (user_id, barber_id, service_id, appointment_datetime, status)
	VALUES
	(2,1,2,'2026-05-12 10:00:00','COMPLETED'),
	(3,2,1,'2026-05-12 11:30:00','COMPLETED'),
	(4,1,4,'2026-05-13 14:00:00','COMPLETED'),
	(2,3,3,'2026-05-14 16:00:00','BOOKED');
	
	-- REVIEWS
	INSERT INTO review (user_id, barber_id, appointment_id, rating, review_text)
	VALUES
	(2,1,1,5,'Excellent haircut. Will definitely come back.'),
	(3,2,2,4,'Great service and very professional.'),
	(4,1,3,5,'Best fade I have ever had.');
	
	-- categoires
	INSERT INTO categories (name, description)
	VALUES
	('Pomades','Hair styling products'),
	('Hair Care','Shampoo and conditioner'),
	('Beard Care','Beard oils and balms'),
	('Accessories','Combs, brushes, razors');
	
	-- products
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
	    'https://your-cloudinary-url.com/pomade.jpg',
	    TRUE
	),
	(
	    2,
	    'Daily Shampoo',
	    'Hydrating shampoo for everyday use.',
	    14.99,
	    35,
	    'https://your-cloudinary-url.com/shampoo.jpg',
	    FALSE
	),
	(
	    3,
	    'Beard Oil',
	    'Nourishes and softens beard hair.',
	    17.99,
	    18,
	    'https://your-cloudinary-url.com/beardoil.jpg',
	    TRUE
	);
	
	INSERT INTO product_specifications
	(product_id, size, scent, hair_type, hold_strength, finish, country_of_origin, weight, sku)
	VALUES
	(1,'4 oz','Classic Barber','Short to Medium Hair','Medium Hold','Matte','USA','113 g','MP-001'),
	(2,'12 fl oz','Fresh Citrus','All Hair Types','N/A','N/A','USA','355 g','DS-001'),
	(3,'2 oz','Sandalwood','All Beard Types','N/A','Natural','USA','57 g','BO-001');
	
	INSERT INTO product_benefits
	(product_id, benefit, display_order)
	VALUES
	
	-- Matte Pomade
	(1,'Provides Medium Hold',1),
	(1,'Natural Matte Finish',2),
	(1,'Easy to Restyle Throughout the Day',3),
	(1,'Water-Based Formula',4),
	(1,'Washes Out Easily',5),
	
	-- Daily Shampoo
	(2,'Hydrates Dry Hair',1),
	(2,'Safe for Daily Use',2),
	(2,'Removes Dirt & Oil',3),
	(2,'Sulfate Free Formula',4),
	(2,'Leaves Hair Soft &Healthy',5),
	
	-- Beard Oil
	(3,'Hydrates Dry Beard',1),
	(3,'Softens Hair',2),
	(3,'Adds Natural Shine',3),
	(3,'Reduces Itching',4),
	(3,'Made With Natural Oils',5);
	
	INSERT INTO product_ingredients
	(product_id, ingredient, display_order)
	VALUES
	
	-- Matte Pomade
	(1,'Beeswax',1),
	(1,'Kaolin Clay',2),
	(1,'Castor Oil',3),
	(1,'Shea Butter',4),
	(1,'Vitamin E',5),
	
	-- Daily Shampoo
	(2,'Aloe Vera',1),
	(2,'Argan Oil',2),
	(2,'Vitamin E',3),
	(2,'Tea Tree Extract',4),
	(2,'Coconut Oil',5),
	
	-- Beard Oil
	(3,'Argan Oil',1),
	(3,'Jojoba Oil',2),
	(3,'Vitamin E',3),
	(3,'Tea Tree Oil',4);
	
	INSERT INTO product_usage_steps
	(product_id, step_number, instruction)
	VALUES
	
	-- Matte Pomade
	(1,1,'Scoop a small amount onto your fingertips.'),
	(1,2,'Rub between your palms until evenly distributed.'),
	(1,3,'Apply to dry or slightly damp hair.'),
	(1,4,'Style with your hands or a comb.'),
	
	-- Daily Shampoo
	(2,1,'Wet hair thoroughly with warm water.'),
	(2,2,'Apply a quarter-sized amount of shampoo.'),
	(2,3,'Massage gently into scalp and hair.'),
	(2,4,'Rinse thoroughly with water.'),
	(2,5,'Repeat if desired.'),
	
	-- Beard Oil
	(3,1,'Dispense 2–3 drops into your palm.'),
	(3,2,'Rub hands together evenly.'),
	(3,3,'Massage into beard and skin.'),
	(3,4,'Comb through evenly.');
	
	-- CARTS

	INSERT INTO carts (
	    user_id
	)
	VALUES
	(2),
	(3),
	(4);
	
	INSERT INTO cart_items (
    cart_id,
    product_id,
    quantity
	)
	VALUES
	(1, 1, 2),
	(1, 2, 1);
	
	-- ORDERS
	
	INSERT INTO orders
	(
	    order_number,
	    user_id,
	    status,
	    subtotal,
	    tax_amount,
	    shipping_cost,
	    discount_amount,
	    total_amount
	)
	
	VALUES
	(
	    'DEV-TEST-1001',
	    2,
	    'Pending',
	    54.97,
	    5.50,
	    0.00,
	    0.00,
	    60.47
	);
	
	INSERT INTO order_items
(
    order_id,
    product_id,
    product_name,
    quantity,
    unit_price,
    line_total
)

VALUES

(
    1,
    1,
    'Matte Pomade',
    2,
    19.99,
    39.98
),

(
    1,
    2,
    'Daily Shampoo',
    1,
    14.99,
    14.99
);

INSERT INTO payments
(
    order_id,
    payment_provider,
    payment_status,
    amount,
    currency
)

VALUES

(
    1,
    'Stripe',
    'Pending',
    60.47,
    'USD'
);

END //

DELIMITER ;