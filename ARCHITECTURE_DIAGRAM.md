# GraphQL Admin Dashboard Architecture

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                             │
│                     http://localhost:3000                            │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
          ┌─────────▼──────────┐   ┌─────────▼──────────┐
          │   REST API Client  │   │ GraphQL API Client │
          │   (api.js)         │   │  (graphqlApi.js)   │
          └─────────┬──────────┘   └─────────┬──────────┘
                    │                         │
                    │                         │
          ┌─────────▼──────────┐   ┌─────────▼──────────┐
          │  AdminDashboard    │   │AdminDashboardGraphQL│
          │      (REST)        │   │     (GraphQL)       │
          └────────────────────┘   └────────────────────┘
                    │                         │
                    └────────────┬────────────┘
                                 │
┌────────────────────────────────▼─────────────────────────────────────┐
│                        BACKEND (Spring Boot)                          │
│                     http://localhost:8080                             │
└───────────────────────────────────────────────────────────────────────┘
                    │                         │
          ┌─────────▼──────────┐   ┌─────────▼──────────┐
          │  REST Controllers  │   │ GraphQL Resolvers  │
          │   (/api/*)         │   │   (/graphql)       │
          └─────────┬──────────┘   └─────────┬──────────┘
                    │                         │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │      Services Layer      │
                    │  - ProductService        │
                    │  - CategoryService       │
                    │  - OrderService          │
                    │  - InventoryService      │
                    │  - UserService           │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Repository Layer       │
                    │   (JPA Repositories)     │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │      Database (H2)       │
                    │   - Products             │
                    │   - Categories           │
                    │   - Orders               │
                    │   - Inventory            │
                    │   - Users                │
                    └──────────────────────────┘
```

## Request Flow Comparison

### REST API Flow

```
User Action → AdminDashboard.js
              ↓
        REST API Call (api.js)
              ↓
        Multiple Endpoints:
        - GET /api/products?page=0&size=10
        - GET /api/categories
        - GET /api/products/{id}
              ↓
        REST Controllers
              ↓
        Service Layer
              ↓
        Database
              ↓
        Fixed Response Structure
              ↓
        UI Updates
```

### GraphQL Flow

```
User Action → AdminDashboardGraphQL.js
              ↓
        GraphQL Query (graphqlApi.js)
              ↓
        Single Endpoint:
        POST /graphql
        {
          query: "{ allProducts { id name price } }"
        }
              ↓
        GraphQL Resolvers
              ↓
        Service Layer
              ↓
        Database
              ↓
        Flexible Response (only requested fields)
              ↓
        UI Updates
```

## Component Hierarchy

```
App.js
├── Navbar (with Admin dropdown)
│   └── Admin Menu
│       ├── Dashboard (REST)
│       ├── Dashboard (GraphQL) ⚡
│       └── Compare Both
│
├── Route: /admin
│   └── AdminDashboard (REST version)
│       ├── Products Tab → REST API calls
│       ├── Categories Tab → REST API calls
│       ├── Orders Tab → REST API calls
│       ├── Inventory Tab → REST API calls
│       ├── Users Tab → REST API calls
│       └── Performance Tab → REST API calls
│
├── Route: /admin/graphql
│   └── AdminDashboardGraphQL (GraphQL version)
│       ├── Products Tab → GraphQL queries
│       ├── Categories Tab → GraphQL queries
│       ├── Orders Tab → GraphQL queries
│       ├── Inventory Tab → GraphQL queries
│       └── Users Tab → GraphQL queries
│
└── Route: /admin/compare
    └── AdminDashboardComparison
        ├── REST Features Card
        ├── GraphQL Features Card
        └── Comparison Table
```

## Data Flow Diagram

### Products Management

```
┌─────────────────────────────────────────────────────────────┐
│                    USER INTERACTION                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
          ┌────────────┴────────────┐
          │                         │
    REST Version            GraphQL Version
          │                         │
          │                         │
┌─────────▼──────────┐   ┌─────────▼──────────┐
│ productAPI.getAll()│   │getAllProducts()     │
│ → Multiple requests│   │→ Single request     │
│   per page         │   │  with exact fields  │
└─────────┬──────────┘   └─────────┬──────────┘
          │                         │
          │   POST /api/products    │   POST /graphql
          │   ?page=0&size=10       │   { query: "..." }
          │                         │
          └────────────┬────────────┘
                       │
          ┌────────────▼────────────┐
          │    ProductService        │
          │  - Caching (@Cacheable) │
          │  - Business Logic       │
          └────────────┬─────────────┘
                       │
          ┌────────────▼────────────┐
          │   ProductRepository      │
          │   (JPA/Hibernate)        │
          └────────────┬─────────────┘
                       │
          ┌────────────▼────────────┐
          │      Database            │
          └──────────────────────────┘
```

## Authentication Flow

```
┌──────────────┐
│  User Login  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ POST /login  │
└──────┬───────┘
       │
       ▼
┌────────────────────┐
│  JWT Token Created │
└──────┬─────────────┘
       │
       ▼
┌─────────────────────────────┐
│ Token stored in localStorage│
└──────┬──────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ Axios Interceptor adds token │
│ to all requests:              │
│ - REST: api.js                │
│ - GraphQL: graphqlApi.js      │
└──────┬───────────────────────┘
       │
       ▼
┌─────────────────────────┐
│ Backend verifies token   │
│ via @RequiresRole        │
└──────────────────────────┘
```

## GraphQL Query Structure

```graphql
# Simple Query
query {
  allProducts {
    id
    name
    price
  }
}

# Query with Variables
query GetProduct($id: ID!) {
  productById(id: $id) {
    id
    name
    price
    categoryName
  }
}

# Mutation
mutation AddProduct($input: AddProductInput!) {
  addProduct(input: $input) {
    id
    name
    price
  }
}

# Complex Query with Nested Data
query {
  allOrders {
    id
    userId
    totalAmount
    status
    items {
      productName
      quantity
      totalPrice
    }
  }
}
```

## Technology Stack

```
┌─────────────────────────────────────────────────────┐
│                    FRONTEND                          │
├─────────────────────────────────────────────────────┤
│ - React 18                                           │
│ - React Router (routing)                             │
│ - Axios (HTTP client)                                │
│ - Tailwind CSS (styling)                             │
│ - Heroicons (icons)                                  │
└─────────────────────────────────────────────────────┘
                       │
                       │ HTTP/HTTPS
                       │
┌─────────────────────────────────────────────────────┐
│                    BACKEND                           │
├─────────────────────────────────────────────────────┤
│ - Spring Boot 3.x                                    │
│ - Spring for GraphQL                                 │
│ - Spring Data JPA                                    │
│ - Spring Security (JWT)                              │
│ - Spring Cache (Redis)                               │
│ - H2 Database                                        │
│ - Lombok                                             │
│ - MapStruct                                          │
└─────────────────────────────────────────────────────┘
```

## Performance Considerations

### REST API
```
Request: GET /api/products?page=0&size=10
Response Size: ~5-10KB (full objects)
Requests per page: 1-3 (products, categories, users)
Caching: HTTP Cache-Control headers
```

### GraphQL
```
Request: POST /graphql { allProducts { id name price } }
Response Size: ~2-5KB (only requested fields)
Requests per page: 1 (all data in single request)
Caching: Query-based caching (future: Apollo Client)
```

## Security

```
┌──────────────────────────────────────┐
│       Authentication Layer            │
│  - JWT Token validation               │
│  - Role-based access (ADMIN required) │
└────────────┬──────────────────────────┘
             │
┌────────────▼──────────────────────────┐
│         Authorization                  │
│  - @RequiresRole annotation            │
│  - Method-level security               │
└────────────┬──────────────────────────┘
             │
┌────────────▼──────────────────────────┐
│      Data Access Layer                 │
│  - JPA Repository security             │
│  - Query-level filtering               │
└────────────────────────────────────────┘
```

## Deployment Architecture

```
┌───────────────────────────────────────────────┐
│              Production Setup                  │
└───────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌───────▼─────────┐   ┌─────────▼───────┐
│  Frontend       │   │  Backend        │
│  (nginx)        │   │  (Tomcat)       │
│  Port: 80/443   │   │  Port: 8080     │
│  - Static files │   │  - REST API     │
│  - React build  │   │  - GraphQL API  │
└─────────────────┘   └─────────┬───────┘
                                 │
                    ┌────────────▼────────────┐
                    │   PostgreSQL/MySQL      │
                    │   (Production DB)       │
                    └─────────────────────────┘
```

## File Structure Overview

```
Commerce-JPA/
│
├── backend/
│   ├── src/main/java/com/example/commerce/
│   │   ├── controllers/
│   │   │   ├── ProductController.java (REST)
│   │   │   └── ...
│   │   ├── graphql/
│   │   │   ├── ProductGraphQLController.java
│   │   │   ├── CategoryGraphQLController.java
│   │   │   ├── OrderGraphQLController.java
│   │   │   └── ...
│   │   ├── services/
│   │   │   ├── ProductService.java (shared by both)
│   │   │   └── ...
│   │   └── repositories/
│   │       └── ... (shared by both)
│   └── src/main/resources/
│       └── graphql/
│           └── schema.graphqls
│
└── frontend/
    ├── src/
    │   ├── services/
    │   │   ├── api.js (REST client)
    │   │   └── graphqlApi.js (GraphQL client)
    │   ├── pages/
    │   │   ├── AdminDashboard.js (REST version)
    │   │   ├── AdminDashboardGraphQL.js (GraphQL version)
    │   │   └── AdminDashboardComparison.js
    │   ├── components/
    │   │   └── Navbar.js (with dropdown)
    │   └── App.js (routing)
    └── GRAPHQL_ADMIN_README.md
```

---

This architecture provides flexibility, allowing you to:
- Use REST for traditional operations
- Use GraphQL for flexible data fetching
- Run both simultaneously
- Gradually migrate from REST to GraphQL
- Choose the best tool for each use case
