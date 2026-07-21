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

DELIMITER //

CREATE PROCEDURE set_known_good_state()
BEGIN

	DELETE FROM review;
	DELETE FROM appointment;
	DELETE FROM products;
	
	DELETE FROM service;
	DELETE FROM barber;
	DELETE FROM users;
	DELETE FROM categories;
	
	ALTER TABLE categories AUTO_INCREMENT = 1;
	ALTER TABLE products AUTO_INCREMENT = 1;
	ALTER TABLE users AUTO_INCREMENT = 1;
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

END //

DELIMITER ;