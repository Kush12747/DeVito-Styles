DROP DATABASE IF EXISTS barber_shop;
CREATE DATABASE barber_shop;
USE barber_shop;

CREATE TABLE users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(100) UNIQUE,
	username VARCHAR(500) UNIQUE NOT NULL,
	password VARCHAR(500) NOT NULL,
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
	
	CONSTRAINT fk_user_id
		FOREIGN KEY (user_id)
		REFERENCES users(user_id),
	
	CONSTRAINT fk_barber_id
		FOREIGN KEY	(barber_id)
		REFERENCES barber(barber_id),
		
	CONSTRAINT fk_service_id
		FOREIGN KEY (service_id)
		REFERENCES service(service_id)
);

ALTER TABLE appointment
ADD CONSTRAINT unique_barber_time
UNIQUE (barber_id, appointment_datetime);

ALTER TABLE appointment
ADD COLUMN google_event_id VARCHAR(255);

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