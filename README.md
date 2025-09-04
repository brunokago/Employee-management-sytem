<img width="901" height="802" alt="image" src="https://github.com/user-attachments/assets/e8b37a6b-38f3-44f3-a196-45daa89b9d23" />

<img width="1885" height="609" alt="image" src="https://github.com/user-attachments/assets/a95288b6-b03e-4f1c-b6a5-ea420febaa83" />

<img width="1904" height="810" alt="image" src="https://github.com/user-attachments/assets/a533fddb-73dc-48b5-9fd0-7620131a2e70" />


# Employee-management-sytem
Employee management sytem
API Endpoints
Authentication - POST /api/auth/login - User login

Employee Management
GET /api/employees - Get all employees
GET /api/employees/{id} - Get employee by ID
POST /api/employees - Create new employee (Admin only)
PUT /api/employees/{id} - Update employee (Admin only)
DELETE /api/employees/{id} - Delete employee (Admin only)
GET /api/employees/profile - Get current user's profile
GET /api/employees/search?q={query} - Search employees

Getting Started

Prerequisites
Java 11 or higher
Node.js 18 or higher
Maven 3.6+
Docker (optional, for containerized deployment)

Development Setup

Backend Setup

1.Navigate to the backend directory:

2.Install dependencies:mvn clean install and run: mvn spring-boot:run


3.The backend will start on http://localhost:8080

4.Access Swagger documentation at http://localhost:8080/swagger-ui.html

Frontend Setup

1.Navigate to the frontend directory:

2.Install dependencies: npm install --force

3.Start the development server:pnpm run dev

4.The frontend will start on http://localhost:5173

Demo Credentials

Admin User
Username: admin
Password: admin123
Permissions: Full CRUD access to all employee data

Employee User
Username: john.doe
Password: password123
Permissions: View own profile, update personal info, browse employee directory

Deployment
Docker Deployment

1.Build and run with Docker Compose:

2.Access the application:

Frontend: http://localhost

Backend API: http://localhost:8080

Database: localhost:3306


Manual Deployment

Backend Deployment

1.
Build the JAR file:

2.
Run the JAR:

Frontend Deployment

1.
Build the React app:

2.
Serve the dist folder using any web server (nginx, Apache, etc.)

Configuration

Backend Configuration

Key configuration properties in application.properties:

Plain Text


# Server configuration
server.port=8080

# Database configuration (H2 for development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT configuration
app.jwtSecret=mySecretKey
app.jwtExpirationMs=86400000

# CORS configuration
app.cors.allowedOrigins=http://localhost:3000,http://localhost:5173


Frontend Configuration

API base URL configuration in src/lib/api.js:

JavaScript


const API_BASE_URL = 'http://localhost:8080/api';


Testing

Backend Tests

Run backend tests:

Bash


cd backend
mvn test


Frontend Tests

Run frontend tests:

Bash


cd frontend
pnpm test



