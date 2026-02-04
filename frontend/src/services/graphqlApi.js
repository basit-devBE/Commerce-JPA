import axios from 'axios';

const GRAPHQL_URL = process.env.REACT_APP_GRAPHQL_URL || 'http://localhost:8080/graphql';

const graphqlClient = axios.create({
  baseURL: GRAPHQL_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for auth
graphqlClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle errors
graphqlClient.interceptors.response.use(
  (response) => {
    if (response.data.errors) {
      console.error('GraphQL Errors:', response.data.errors);
      
      // Extract the most relevant error message
      const error = response.data.errors[0];
      let errorMessage = error.message;
      
      // Remove the UUID from INTERNAL_ERROR messages if present
      if (errorMessage.includes('INTERNAL_ERROR for')) {
        errorMessage = 'An internal error occurred. Please try again or contact support.';
      }
      
      // Create a custom error with the GraphQL error details
      const customError = new Error(errorMessage);
      customError.graphQLErrors = response.data.errors;
      customError.extensions = error.extensions;
      
      throw customError;
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

const executeQuery = async (query, variables = {}) => {
  try {
    const response = await graphqlClient.post('', {
      query,
      variables,
    });
    
    return response.data.data;
  } catch (error) {
    console.error('GraphQL Error:', error);
    throw error;
  }
};

// ==================== PRODUCT QUERIES ====================

export const getAllProducts = () => executeQuery(`
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
`);

export const getProductById = (id) => executeQuery(`
  query GetProduct($id: ID!) {
    productById(id: $id) {
      id
      name
      categoryName
      sku
      price
      quantity
    }
  }
`, { id });

export const addProduct = (input) => executeQuery(`
  mutation AddProduct($input: AddProductInput!) {
    addProduct(input: $input) {
      id
      name
      categoryName
      sku
      price
      quantity
    }
  }
`, { input });

export const deleteProduct = (id) => executeQuery(`
  mutation DeleteProduct($id: ID!) {
    deleteProduct(id: $id)
  }
`, { id });

// Paginated products query
export const getProductsPaginated = (page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC', categoryId = null, search = null) => executeQuery(`
  query ProductsPaginated($pagination: PaginationInput!, $categoryId: ID, $search: String) {
    productsPaginated(pagination: $pagination, categoryId: $categoryId, search: $search) {
      content {
        id
        name
        categoryName
        sku
        price
        quantity
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { pagination: { page, size, sortBy, sortDirection }, categoryId, search });

// ==================== CATEGORY QUERIES ====================

export const getAllCategories = () => executeQuery(`
  query {
    allCategories {
      id
      name
      description
    }
  }
`);

export const getCategoryById = (id) => executeQuery(`
  query GetCategory($id: ID!) {
    categoryById(id: $id) {
      id
      name
      description
    }
  }
`, { id });

export const addCategory = (input) => executeQuery(`
  mutation AddCategory($input: AddCategoryInput!) {
    addCategory(input: $input) {
      id
      name
      description
    }
  }
`, { input });

export const updateCategory = (id, input) => executeQuery(`
  mutation UpdateCategory($id: ID!, $input: UpdateCategoryInput!) {
    updateCategory(id: $id, input: $input) {
      id
      name
      description
    }
  }
`, { id, input });

export const deleteCategory = (id) => executeQuery(`
  mutation DeleteCategory($id: ID!) {
    deleteCategory(id: $id)
  }
`, { id });

// Paginated categories query
export const getCategoriesPaginated = (page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC') => executeQuery(`
  query CategoriesPaginated($pagination: PaginationInput!) {
    categoriesPaginated(pagination: $pagination) {
      content {
        id
        name
        description
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { pagination: { page, size, sortBy, sortDirection } });

// ==================== ORDER QUERIES ====================

// Basic orders list
export const getAllOrders = () => executeQuery(`
  query {
    allOrders {
      id
      userId
      userName
      userEmail
      totalAmount
      status
      createdAt
      items {
        id
        productId
        productName
        quantity
        totalPrice
      }
    }
  }
`);

// Full order with timestamps
export const getOrderById = (id) => executeQuery(`
  query GetOrder($id: ID!) {
    orderById(id: $id) {
      id
      userId
      userName
      userEmail
      totalAmount
      status
      createdAt
      updatedAt
      items {
        id
        productId
        productName
        quantity
        totalPrice
      }
    }
  }
`, { id });

// Order with user details - uses GraphQL nested relation
export const getOrderWithUser = (id) => executeQuery(`
  query GetOrderWithUser($id: ID!) {
    orderById(id: $id) {
      id
      totalAmount
      status
      createdAt
      user {
        id
        firstName
        lastName
        email
      }
      items {
        productName
        quantity
        totalPrice
      }
    }
  }
`, { id });

// Order with full product details - uses GraphQL nested relation
export const getOrderWithProducts = (id) => executeQuery(`
  query GetOrderWithProducts($id: ID!) {
    orderById(id: $id) {
      id
      userId
      totalAmount
      status
      items {
        id
        quantity
        totalPrice
        product {
          id
          name
          categoryName
          sku
          price
          isAvailable
        }
      }
    }
  }
`, { id });

// User's orders with basic info
export const getOrdersByUserId = (userId) => executeQuery(`
  query GetUserOrders($userId: ID!) {
    ordersByUserId(userId: $userId) {
      id
      userId
      userName
      userEmail
      totalAmount
      status
      createdAt
      items {
        productName
        quantity
        totalPrice
      }
    }
  }
`, { userId });

// Orders summary for dashboard
export const getOrdersSummary = () => executeQuery(`
  query {
    allOrders {
      id
      totalAmount
      status
      createdAt
    }
  }
`);

export const createOrder = (input) => executeQuery(`
  mutation CreateOrder($input: AddOrderInput!) {
    createOrder(input: $input) {
      id
      totalAmount
      status
      createdAt
      items {
        productName
        quantity
        totalPrice
      }
    }
  }
`, { input });

export const updateOrderStatus = (id, input) => executeQuery(`
  mutation UpdateOrderStatus($id: ID!, $input: UpdateOrderInput!) {
    updateOrderStatus(id: $id, input: $input) {
      id
      status
      updatedAt
      totalAmount
    }
  }
`, { id, input });

// Paginated orders query
export const getOrdersPaginated = (page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC', status = null, search = null) => executeQuery(`
  query OrdersPaginated($pagination: PaginationInput!, $status: OrderStatus, $search: String) {
    ordersPaginated(pagination: $pagination, status: $status, search: $search) {
      content {
        id
        userId
        userName
        userEmail
        totalAmount
        status
        createdAt
        updatedAt
        items {
          id
          productName
          quantity
          totalPrice
        }
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { pagination: { page, size, sortBy, sortDirection }, status, search });

// Paginated orders by user query
export const getOrdersByUserIdPaginated = (userId, page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC') => executeQuery(`
  query OrdersByUserIdPaginated($userId: ID!, $pagination: PaginationInput!) {
    ordersByUserIdPaginated(userId: $userId, pagination: $pagination) {
      content {
        id
        userId
        userName
        totalAmount
        status
        createdAt
        items {
          productName
          quantity
          totalPrice
        }
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { userId, pagination: { page, size, sortBy, sortDirection } });

// ==================== INVENTORY QUERIES ====================

export const getAllInventories = () => executeQuery(`
  query {
    allInventories {
      id
      productId
      productName
      quantity
      location
    }
  }
`);

export const getInventoryById = (id) => executeQuery(`
  query GetInventory($id: ID!) {
    inventoryById(id: $id) {
      id
      productId
      productName
      quantity
      location
    }
  }
`, { id });

export const getInventoryByProductId = (productId) => executeQuery(`
  query GetInventoryByProduct($productId: ID!) {
    inventoryByProductId(productId: $productId) {
      id
      productId
      productName
      quantity
      location
    }
  }
`, { productId });

export const addInventory = (input) => executeQuery(`
  mutation AddInventory($input: AddInventoryInput!) {
    addInventory(input: $input) {
      id
      productName
      quantity
      location
    }
  }
`, { input });

export const updateInventory = (id, input) => executeQuery(`
  mutation UpdateInventory($id: ID!, $input: UpdateInventoryInput!) {
    updateInventory(id: $id, input: $input) {
      id
      quantity
      location
    }
  }
`, { id, input });

export const deleteInventory = (id) => executeQuery(`
  mutation DeleteInventory($id: ID!) {
    deleteInventory(id: $id)
  }
`, { id });

// Paginated inventories query
export const getInventoriesPaginated = (page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC') => executeQuery(`
  query InventoriesPaginated($pagination: PaginationInput!) {
    inventoriesPaginated(pagination: $pagination) {
      content {
        id
        productId
        productName
        quantity
        location
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { pagination: { page, size, sortBy, sortDirection } });

// ==================== USER QUERIES ====================

export const getAllUsers = () => executeQuery(`
  query {
    getAllUsers {
      id
      firstName
      lastName
      email
      role
    }
  }
`);

export const getUserById = (id) => executeQuery(`
  query GetUser($id: ID!) {
    getUserById(id: $id) {
      id
      firstName
      lastName
      email
      role
    }
  }
`, { id });

// Paginated users query
export const getUsersPaginated = (page = 0, size = 10, sortBy = 'id', sortDirection = 'ASC') => executeQuery(`
  query UsersPaginated($pagination: PaginationInput!) {
    usersPaginated(pagination: $pagination) {
      content {
        id
        firstName
        lastName
        email
        role
      }
      pageInfo {
        currentPage
        totalItems
        totalPages
        isLast
        hasNext
        hasPrevious
      }
    }
  }
`, { pagination: { page, size, sortBy, sortDirection } });

// ==================== CART QUERIES ====================

// Basic cart query - only essential fields
export const getCart = (userId) => executeQuery(`
  query GetCart($userId: ID!) {
    cart(userId: $userId) {
      id
      userId
      totalAmount
      totalItems
      items {
        id
        productId
        productName
        productPrice
        productSku
        quantity
        subtotal
      }
    }
  }
`, { userId });

// Full cart with timestamps
export const getCartWithTimestamps = (userId) => executeQuery(`
  query GetCartWithTimestamps($userId: ID!) {
    cart(userId: $userId) {
      id
      userId
      totalAmount
      totalItems
      createdAt
      updatedAt
      items {
        id
        productId
        productName
        productPrice
        productSku
        quantity
        subtotal
        createdAt
        updatedAt
      }
    }
  }
`, { userId });

// Cart with user details - uses GraphQL nested relation
export const getCartWithUser = (userId) => executeQuery(`
  query GetCartWithUser($userId: ID!) {
    cart(userId: $userId) {
      id
      totalAmount
      totalItems
      user {
        id
        firstName
        lastName
        email
      }
      items {
        id
        productId
        productName
        productPrice
        quantity
        subtotal
      }
    }
  }
`, { userId });

// Cart with full product details - uses GraphQL nested relation
export const getCartWithProducts = (userId) => executeQuery(`
  query GetCartWithProducts($userId: ID!) {
    cart(userId: $userId) {
      id
      userId
      totalAmount
      totalItems
      items {
        id
        quantity
        subtotal
        product {
          id
          name
          categoryName
          sku
          price
          quantity
          isAvailable
        }
      }
    }
  }
`, { userId });

// Minimal cart for navbar badge
export const getCartSummary = (userId) => executeQuery(`
  query GetCartSummary($userId: ID!) {
    cart(userId: $userId) {
      id
      totalAmount
      totalItems
    }
  }
`, { userId });

// ==================== CART MUTATIONS ====================

export const addToCart = (userId, input) => executeQuery(`
  mutation AddToCart($userId: ID!, $input: AddToCartInput!) {
    addToCart(userId: $userId, input: $input) {
      id
      totalAmount
      totalItems
      items {
        id
        productId
        productName
        productPrice
        quantity
        subtotal
      }
    }
  }
`, { userId, input });

export const updateCartItem = (userId, productId, input) => executeQuery(`
  mutation UpdateCartItem($userId: ID!, $productId: ID!, $input: UpdateCartItemInput!) {
    updateCartItem(userId: $userId, productId: $productId, input: $input) {
      id
      totalAmount
      totalItems
      items {
        id
        productId
        productName
        productPrice
        quantity
        subtotal
      }
    }
  }
`, { userId, productId, input });

export const removeFromCart = (userId, productId) => executeQuery(`
  mutation RemoveFromCart($userId: ID!, $productId: ID!) {
    removeFromCart(userId: $userId, productId: $productId) {
      id
      totalAmount
      totalItems
      items {
        id
        productId
        productName
        productPrice
        quantity
        subtotal
      }
    }
  }
`, { userId, productId });

export const clearCart = (userId) => executeQuery(`
  mutation ClearCart($userId: ID!) {
    clearCart(userId: $userId)
  }
`, { userId });

// Export all as named object for easier imports
const graphqlApi = {
  // Products
  getAllProducts,
  getProductById,
  getProductsPaginated,
  addProduct,
  deleteProduct,
  
  // Categories
  getAllCategories,
  getCategoryById,
  getCategoriesPaginated,
  addCategory,
  updateCategory,
  deleteCategory,
  
  // Orders
  getAllOrders,
  getOrderById,
  getOrderWithUser,
  getOrderWithProducts,
  getOrdersByUserId,
  getOrdersSummary,
  getOrdersPaginated,
  getOrdersByUserIdPaginated,
  createOrder,
  updateOrderStatus,
  
  // Inventory
  getAllInventories,
  getInventoryById,
  getInventoryByProductId,
  getInventoriesPaginated,
  addInventory,
  updateInventory,
  deleteInventory,
  
  // Users
  getAllUsers,
  getUserById,
  getUsersPaginated,

  // Cart
  getCart,
  getCartWithTimestamps,
  getCartWithUser,
  getCartWithProducts,
  getCartSummary,
  addToCart,
  updateCartItem,
  removeFromCart,
  clearCart,
};

// ==================== REVIEW QUERIES ====================

export const getAllReviews = () => executeQuery(`
  query {
    allReviews {
      id
      productId
      productName
      userId
      userName
      userEmail
      rating
      comment
      createdAt
      updatedAt
    }
  }
`);

export const getReviewById = (id) => executeQuery(`
  query GetReview($id: ID!) {
    reviewById(id: $id) {
      id
      productId
      productName
      userId
      userName
      userEmail
      rating
      comment
      createdAt
      updatedAt
    }
  }
`, { id });

export const getReviewsByProductId = (productId) => executeQuery(`
  query GetReviewsByProduct($productId: ID!) {
    reviewsByProductId(productId: $productId) {
      id
      userId
      userName
      userEmail
      rating
      comment
      createdAt
      updatedAt
    }
  }
`, { productId });

export const getReviewsByUserId = (userId) => executeQuery(`
  query GetReviewsByUser($userId: ID!) {
    reviewsByUserId(userId: $userId) {
      id
      productId
      productName
      rating
      comment
      createdAt
      updatedAt
    }
  }
`, { userId });

export const getProductReviewStats = (productId) => executeQuery(`
  query GetProductReviewStats($productId: ID!) {
    productReviewStats(productId: $productId) {
      productId
      averageRating
      totalReviews
    }
  }
`, { productId });

export const addReview = (input) => executeQuery(`
  mutation AddReview($input: ReviewInput!) {
    addReview(input: $input) {
      id
      productId
      productName
      userId
      userName
      rating
      comment
      createdAt
    }
  }
`, { input });

export const updateReview = (id, rating, comment) => executeQuery(`
  mutation UpdateReview($id: ID!, $rating: Int, $comment: String) {
    updateReview(id: $id, rating: $rating, comment: $comment) {
      id
      rating
      comment
      updatedAt
    }
  }
`, { id, rating, comment });

export const deleteReview = (id) => executeQuery(`
  mutation DeleteReview($id: ID!) {
    deleteReview(id: $id)
  }
`, { id });

export default graphqlApi;
