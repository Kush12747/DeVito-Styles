# DeVito-Styles

DeVito-Styles is a full-stack barber shop management application that allows customers to book appointments, browse services, and manage accounts while giving barbers administrative control over their schedules and services. 

# Features
- User authentication and account management
- Customer appointment booking system
- Admin dashboard
- Database persistence
- Secure backend API
- Responsive frontend UI
- CRUD operations for services and appointments
- Customer profile customization
- Barber schedule management
- Email notifications for appointments

# Tech Stack

## Frontend
- React
- React Router
- Booststrap

## Backend
- Java
- Spring Boot
- REST APIs
- JDBC databse

## Database
- MySQL

## Wireframes
![Phase 1](/images/image-1.png)
![Phase 2](/images/image-2.png)
![Phase 3](/images/image-3.png)

# Database Schema
![Database Schema](/images/image.png)

# API Endpoints

# User Stories
User Stories - Barbershop Appointment System (DeVito Styles)
- Login
    As a user, I want to register an account that I can access the booking system.
    As a user, I want to login so that I can access my dashboard.
    As a user, I want to logout of my account.

- Appointment Management
    As a user, I want to view available appointment slots so that I can choose a time.
    As a user, I want to book an appointment so that I can schedule a haircut or other services.
    As a user, I want to edit an appointment so that I can change the date/time if needed.
    As a user, I want to cancel an appointment.
    As a user, I want to view my booked appointment information about the visit.

- Profile Management
    As a user, I want to view profiles so I can see my personal information.
    As a user, I want to edit my profile so that I can update my details or preferred barber. 

- Admin Dashboard
    As an admin, I want to view all users/customers.
    As an admin, I want to view all appointments so that I can monitor bookings.
    As an admin, I want to view barbers/services so that I can manage availability.

- Email Notification
    As a user, I want to receive an email when I book an appointment so that I have confirmation.
    As a user, I want to receive an email when I cancel an appointment.

- Role-Based Access
    As a system, I want to restrict admin pages so that only admins can access them.
    As a system, I want to restrict user pages so that only logged in users can book appointments.

# Installation
1. Clone the repository: `git clone <repo_url>`
2. Navigate to the project directory: `cd DeVito-Styles`
3. Install backend dependencies: `cd backend && mvn install`
4. Install frontend dependencies: `cd ../frontend && npm install`
5. Start the backend server: `cd ../backend && mvn spring-boot:run`
6. Start the frontend server: `cd ../frontend && npm start`

# Envionment Variables
- `DB_URL`: The URL of the MySQL database
- `DB_USERNAME`: The username for the MySQL database
- `DB_PASSWORD`: The password for the MySQL database
- `MAIL_HOST`: The SMTP host for sending email notifications
- `MAIL_PORT`: The SMTP port for sending email notifications
- `MAIL_USERNAME`: The username for the SMTP server
- `MAIL_PASSWORD`: The password for the SMTP server

# Project Structure
- `Client/`: Contains the React frontend code
- `Server/`: Contains the Java Spring Boot backend code
- `Database/`: Contains SQL scripts for setting up the MySQL database

# Challanges Faced
- Managing protected routes in React Router
- Desiging relational databse relatiosnships for users, appointments, and services
- Handling asynchronous operations and API calls in React
- State management across frontend components

# Future Enhancements
- Implementing a mobile app version of the application
- Implment JWT authentication for enhanced security
- Adding support for multiple languages
- Integrating payment processing for online bookings
- Implementing a loyalty program for customers
- Adding a review and rating system for services and barbers
- Implementing a chatbot for customer support
- Adding a calendar view for barbers to manage their schedules more efficiently
- Integrating with third-party services for marketing and promotions

# Author
- Created by Kush Gandhi
- GitHub: [Kush Gandhi](https://github.com/Kush12747)
- Email: kushgandhi2099@gmail.com
- LinkedIn: [Kush Gandhi](https://www.linkedin.com/in/kush-gandhi32/)