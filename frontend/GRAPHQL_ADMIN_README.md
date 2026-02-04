# GraphQL Admin Dashboard

This admin dashboard leverages GraphQL for efficient data fetching and management operations.

## 🚀 Features

- **Product Management**: Add, view, and delete products using GraphQL mutations and queries
- **Category Management**: Full CRUD operations for categories
- **Order Management**: View, create, update status, and delete orders
- **Inventory Management**: Track and update product inventory levels
- **User Management**: View all users with their roles

## 📍 Access the Dashboard

### REST API Version (Original)
```
http://localhost:3000/admin
```

### GraphQL Version (New)
```
http://localhost:3000/admin/graphql
```

## 🔧 Setup

### Backend (Spring Boot)

1. **Ensure GraphQL Dependencies** are in your `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
```

2. **GraphQL Schema** is located at:
```
src/main/resources/graphql/schema.graphqls
```

3. **GraphQL Endpoint**:
```
http://localhost:8080/graphql
```

4. **GraphiQL Interface** (for testing):
```
http://localhost:8080/graphiql
```

Enable GraphiQL in `application-dev.properties`:
```properties
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql
```

### Frontend (React)

1. **GraphQL API Service**: `/src/services/graphqlApi.js`
   - All GraphQL queries and mutations
   - Automatic token injection for authentication
   - Error handling

2. **GraphQL Admin Dashboard**: `/src/pages/AdminDashboardGraphQL.js`
   - Complete admin interface using GraphQL
   - Real-time data fetching
   - Optimistic UI updates

3. **Environment Variables** (optional):
```env
REACT_APP_GRAPHQL_URL=http://localhost:8080/graphql
```

## 📊 Available Operations

### Products
```graphql
# Query all products
query {
  allProducts {
    id
    name
    categoryName
    sku
    price
    quantity
  }
}

# Add a product
mutation {
  addProduct(input: {
    name: "New Product"
    categoryId: "1"
    sku: "SKU-001"
    price: 29.99
  }) {
    id
    name
    price
  }
}

# Delete a product
mutation {
  deleteProduct(id: "1")
}
```

### Categories
```graphql
# Query all categories
query {
  allCategories {
    id
    name
    description
  }
}

# Add a category
mutation {
  addCategory(input: {
    name: "Electronics"
    description: "Electronic devices"
  }) {
    id
    name
  }
}

# Update a category
mutation {
  updateCategory(id: "1", input: {
    name: "Updated Name"
    description: "Updated description"
  }) {
    id
    name
    description
  }
}
```

### Orders
```graphql
# Query all orders
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

# Create an order
mutation {
  createOrder(input: {
    userId: "1"
    items: [
      { productId: "1", quantity: 2 }
      { productId: "2", quantity: 1 }
    ]
  }) {
    id
    totalAmount
    status
  }
}

# Update order status
mutation {
  updateOrderStatus(id: "1", input: {
    status: SHIPPED
  }) {
    id
    status
  }
}
```

### Inventory
```graphql
# Query all inventory
query {
  allInventories {
    id
    productName
    quantity
    location
  }
}

# Add inventory
mutation {
  addInventory(input: {
    productId: "1"
    quantity: 100
    location: "Warehouse A"
  }) {
    id
    quantity
    location
  }
}

# Update inventory
mutation {
  updateInventory(id: "1", input: {
    quantity: 150
    location: "Warehouse B"
  }) {
    id
    quantity
    location
  }
}
```

### Users
```graphql
# Query all users
query {
  getAllUsers {
    id
    firstName
    lastName
    email
    role
  }
}
```

## 🔐 Authentication

The GraphQL API requires authentication. The token is automatically included in requests:

```javascript
// In graphqlApi.js
graphqlClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## 🎨 UI Components

The dashboard includes:
- **Tab Navigation**: Easy switching between different data sections
- **Data Tables**: Clean display of products, categories, orders, inventory, and users
- **Modal Forms**: Add new items with validation
- **Inline Editing**: Quick updates for categories and inventory
- **Status Badges**: Visual indicators for order status and user roles
- **Action Buttons**: Delete, edit, and manage items

## 🔄 Comparison: REST vs GraphQL

### REST API (Original)
- Multiple endpoints for different resources
- Pagination support
- Over-fetching of data
- Multiple round trips for related data

### GraphQL API (New)
- Single endpoint: `/graphql`
- Request exactly what you need
- No over-fetching or under-fetching
- Fetch related data in one request
- Strongly typed schema
- Self-documenting API

## 🧪 Testing GraphQL Queries

### Using GraphiQL (Browser)
1. Start your Spring Boot application
2. Navigate to: `http://localhost:8080/graphiql`
3. Write and test queries interactively

### Using cURL
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"query":"{ allProducts { id name price } }"}'
```

### Using Postman
1. Create a new POST request to `http://localhost:8080/graphql`
2. Set Header: `Content-Type: application/json`
3. Set Header: `Authorization: Bearer YOUR_TOKEN`
4. Body (raw JSON):
```json
{
  "query": "{ allProducts { id name price } }"
}
```

## 🐛 Error Handling

GraphQL errors are automatically handled:

```javascript
// Successful response
{
  "data": {
    "allProducts": [...]
  }
}

// Error response
{
  "errors": [
    {
      "message": "Product not found",
      "path": ["productById"],
      "extensions": {...}
    }
  ]
}
```

The frontend displays errors using the `ErrorAlert` component.

## 🚦 Getting Started

1. **Start the Backend**:
```bash
cd /home/abdul/Desktop/github_projects/Commerce-JPA
./mvnw spring-boot:run
```

2. **Start the Frontend**:
```bash
cd frontend
npm install
npm start
```

3. **Login as Admin**:
   - Navigate to `http://localhost:3000/login`
   - Use admin credentials
   - Go to `http://localhost:3000/admin/graphql`

## 📚 Additional Resources

- [GraphQL Official Documentation](https://graphql.org/)
- [Spring for GraphQL](https://spring.io/projects/spring-graphql)
- [Apollo Client (Alternative)](https://www.apollographql.com/docs/react/)

## 🎯 Benefits of GraphQL Implementation

1. **Performance**: Fetch only required fields
2. **Flexibility**: Clients control data shape
3. **Discoverability**: Schema introspection
4. **Type Safety**: Strong typing prevents errors
5. **Developer Experience**: Single endpoint, powerful queries
6. **Real-time Potential**: Easy to add subscriptions later

## 🔮 Future Enhancements

- [ ] Add GraphQL Subscriptions for real-time updates
- [ ] Implement DataLoader for N+1 query optimization
- [ ] Add query complexity analysis
- [ ] Implement rate limiting
- [ ] Add GraphQL caching with Apollo Client
- [ ] Create GraphQL fragments for reusable queries
- [ ] Add persisted queries for security

---

**Need Help?** Check the backend GraphQL controllers and schema definition for available operations.
