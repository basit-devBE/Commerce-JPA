# Commerce-JPA 🛍️

A modern, full-stack e-commerce platform built with Spring Boot and React. This application provides a complete shopping experience with product management, order processing, user authentication, and an admin dashboard.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)
![Java](https://img.shields.io/badge/Java-25-blue)
![React](https://img.shields.io/badge/React-18.2.0-61dafb)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![GraphQL](https://img.shields.io/badge/GraphQL-Enabled-E10098)

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Contributing](#-contributing)

## ✨ Features

### Customer Features
- 🔐 **User Authentication & Authorization** - Secure login/registration with role-based access control
- 🛍️ **Product Catalog** - Browse products with search, filter, and category navigation
- 🛒 **Shopping Cart** - Add, update, and remove items from cart
- 📦 **Order Management** - Place orders and track order history
- ⭐ **Product Reviews** - Read and write product reviews with ratings
- 👤 **User Profile** - Manage personal information and view order history

### Admin Features
- 📊 **Admin Dashboard** - Comprehensive dashboard with GraphQL support
- 📝 **Product Management** - Create, update, and delete products
- 📂 **Category Management** - Organize products into categories
- 📋 **Order Processing** - View and update order statuses
- 📦 **Inventory Management** - Track and manage product stock levels
- 👥 **User Management** - View and manage user accounts

### Technical Features
- 🚀 **RESTful API** - Well-structured REST endpoints
- 🎯 **GraphQL API** - Flexible data querying with GraphQL
- 💾 **Caching** - Redis-based caching for improved performance
- 📊 **Performance Monitoring** - Built-in performance tracking and metrics
- 🔍 **Logging** - Comprehensive logging with AOP-based aspects
- 🛡️ **Error Handling** - Centralized exception handling
- 📚 **API Documentation** - OpenAPI/Swagger documentation
- ✅ **Input Validation** - Request validation and constraint checking

## 🏗️ Architecture

This application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         React Frontend              │
│   (Components, Pages, Services)     │
└──────────────┬──────────────────────┘
               │ HTTP/GraphQL
┌──────────────┴──────────────────────┐
│       Spring Boot Backend           │
├─────────────────────────────────────┤
│  Controllers (REST & GraphQL)       │
│  ├─ Services (Business Logic)       │
│  ├─ Repositories (Data Access)      │
│  ├─ Entities (JPA Models)           │
│  └─ DTOs (Data Transfer Objects)    │
└──────────────┬──────────────────────┘
               │ JPA/Hibernate
┌──────────────┴──────────────────────┐
│       PostgreSQL Database           │
└─────────────────────────────────────┘
```

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 25
- **Database**: PostgreSQL
- **Cache**: Redis
- **ORM**: Hibernate/JPA
- **API Styles**: REST + GraphQL
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok (Reduce boilerplate)
  - MapStruct (Object mapping)
  - Spring Validation (Input validation)
  - Spring AOP (Aspect-oriented programming)

### Frontend
- **Framework**: React 18.2.0
- **Routing**: React Router v6
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **UI Components**: Headless UI, Heroicons
- **Date Handling**: date-fns
- **Build Tool**: Create React App

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 25 or higher
- **Maven** 3.6+ (or use the included Maven wrapper)
- **Node.js** 14+ and npm
- **PostgreSQL** 12+
- **Redis** (optional, for caching)
- **Git** (for cloning the repository)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/basit-devBE/Commerce-JPA.git
cd Commerce-JPA
```

### 2. Backend Setup

#### Set up PostgreSQL Database

```bash
# Create a new database
createdb commerce_db

# Or using psql
psql -U postgres
CREATE DATABASE commerce_db;
\q
```

#### Configure Environment Variables

Create a `.env` file in the root directory (or set environment variables):

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/commerce_db
DATABASE_USERNAME=your_postgres_username
DATABASE_PASSWORD=your_postgres_password
```

#### Install Dependencies

```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using Maven directly
mvn clean install
```

### 3. Frontend Setup

```bash
cd frontend
npm install
```

## ⚙️ Configuration

### Backend Configuration

The application uses Spring profiles for different environments:

- **Default Profile** (`application.properties`):
  ```properties
  spring.application.name=Commerce
  spring.jpa.show-sql=true
  spring.profiles.active=dev
  ```

- **Development Profile** (`application-dev.properties`):
  ```properties
  spring.jpa.hibernate.ddl-auto=update
  spring.datasource.url=${DATABASE_URL}
  spring.datasource.username=${DATABASE_USERNAME}
  spring.datasource.password=${DATABASE_PASSWORD}
  spring.graphql.graphiql.enabled=true
  spring.data.redis.host=localhost
  spring.data.redis.port=6379
  ```

### Frontend Configuration

The frontend is configured to proxy requests to the backend:

```json
"proxy": "http://localhost:8080"
```

## 🏃 Running the Application

### Start Backend Server

```bash
# From the project root directory
./mvnw spring-boot:run

# Or using Maven
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`

### Start Frontend Development Server

```bash
cd frontend
npm start
```

The frontend will start on `http://localhost:3000`

### Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **GraphQL Playground**: http://localhost:8080/graphiql
- **API Documentation**: http://localhost:8080/swagger-ui.html (if configured)

## 📚 API Documentation

### REST API Endpoints

#### Authentication
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

#### Products
- `GET /api/products` - Get all products (with pagination, sorting, filtering)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product (Admin)
- `PUT /api/products/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

#### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create category (Admin)
- `PUT /api/categories/{id}` - Update category (Admin)
- `DELETE /api/categories/{id}` - Delete category (Admin)

#### Cart
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update cart item
- `DELETE /api/cart/items/{id}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

#### Orders
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}/status` - Update order status (Admin)

#### Inventory
- `GET /api/inventory` - Get inventory items (Admin)
- `PUT /api/inventory/{id}` - Update inventory (Admin)

#### Performance
- `GET /api/performance/metrics` - Get performance metrics (Admin)

### GraphQL API

Access GraphQL Playground at `http://localhost:8080/graphiql`

Example queries:

```graphql
# Get all products
query {
  products {
    id
    name
    price
    description
    category {
      name
    }
  }
}

# Get product by ID
query {
  productById(id: 1) {
    id
    name
    price
    inventory {
      quantity
    }
  }
}

# Create product (Admin)
mutation {
  createProduct(input: {
    name: "New Product"
    price: 29.99
    description: "Product description"
    categoryId: 1
  }) {
    id
    name
  }
}
```

## 📁 Project Structure

```
Commerce-JPA/
├── src/
│   ├── main/
│   │   ├── java/com/example/commerce/
│   │   │   ├── CommerceApplication.java        # Main application class
│   │   │   ├── SeedData.java                   # Database seeding
│   │   │   ├── aspects/                        # AOP aspects (logging, performance)
│   │   │   ├── cache/                          # Cache configuration and management
│   │   │   ├── config/                         # Spring configuration classes
│   │   │   ├── controllers/                    # REST controllers
│   │   │   ├── dtos/                           # Data Transfer Objects
│   │   │   │   ├── requests/                   # Request DTOs
│   │   │   │   └── responses/                  # Response DTOs
│   │   │   ├── entities/                       # JPA entities
│   │   │   ├── enums/                          # Enumerations
│   │   │   ├── errorhandlers/                  # Exception handlers
│   │   │   ├── graphql/                        # GraphQL controllers and resolvers
│   │   │   ├── interfaces/                     # Service interfaces
│   │   │   ├── mappers/                        # MapStruct mappers
│   │   │   ├── repositories/                   # JPA repositories
│   │   │   ├── services/                       # Business logic services
│   │   │   └── utils/                          # Utility classes
│   │   └── resources/
│   │       ├── application.properties          # Main configuration
│   │       ├── application-dev.properties      # Development configuration
│   │       └── graphql/
│   │           └── schema.graphqls             # GraphQL schema
│   └── test/                                   # Unit and integration tests
├── frontend/
│   ├── public/                                 # Static files
│   ├── src/
│   │   ├── components/                         # Reusable React components
│   │   ├── context/                            # React context providers
│   │   ├── pages/                              # Page components
│   │   ├── services/                           # API service layer
│   │   ├── utils/                              # Utility functions
│   │   ├── App.js                              # Main App component
│   │   └── index.js                            # Entry point
│   ├── package.json                            # Frontend dependencies
│   └── tailwind.config.js                      # Tailwind CSS configuration
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

## 🧪 Testing

### Backend Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CommerceApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

### Frontend Tests

```bash
cd frontend
npm test
```

## 🔒 Security

- **Authentication**: Token-based authentication with JWT (if implemented)
- **Authorization**: Role-based access control (USER, ADMIN)
- **Password Security**: Passwords should be hashed (implement BCrypt)
- **CORS**: Configured for cross-origin requests
- **Input Validation**: Request validation using Spring Validation
- **SQL Injection Protection**: JPA/Hibernate prepared statements

## 🚀 Deployment

### Backend Deployment

1. **Build the application**:
   ```bash
   ./mvnw clean package
   ```

2. **Run the JAR file**:
   ```bash
   java -jar target/Commerce-0.0.1-SNAPSHOT.jar
   ```

3. **Environment Variables**: Set production environment variables for database and Redis

### Frontend Deployment

1. **Build the production bundle**:
   ```bash
   cd frontend
   npm run build
   ```

2. **Deploy the `build/` directory** to your hosting service (Netlify, Vercel, AWS S3, etc.)

## 🐛 Troubleshooting

### Common Issues

**Database Connection Error**:
- Verify PostgreSQL is running
- Check database credentials in environment variables
- Ensure database exists

**Port Already in Use**:
- Change the backend port: `server.port=8081` in application.properties
- Change the frontend port: Set `PORT=3001` environment variable

**Redis Connection Error**:
- If not using Redis, comment out Redis configuration
- Or install and start Redis: `redis-server`

## 📝 Additional Documentation

- [Frontend README](frontend/README.md) - Frontend-specific documentation
- [Architecture Diagram](ARCHITECTURE_DIAGRAM.md) - System architecture details
- [Error Handling](frontend/ERROR_HANDLING.md) - Error handling strategies
- [GraphQL Admin Guide](frontend/GRAPHQL_ADMIN_README.md) - GraphQL admin features

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style Guidelines

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Add unit tests for new features
- Update documentation as needed

## 📄 License

This project is available for educational and personal use.

## 👨‍💻 Authors

- **basit-devBE** - [GitHub Profile](https://github.com/basit-devBE)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful UI library
- All contributors and open-source projects used in this application

## 📞 Support

For questions or issues:
- Open an issue on [GitHub Issues](https://github.com/basit-devBE/Commerce-JPA/issues)
- Check existing documentation in the repository

---

**Happy Coding! 🚀**
