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
      throw new Error(response.data.errors[0].message);
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

// ==================== ORDER QUERIES ====================

export const getAllOrders = () => executeQuery(`
  query {
    allOrders {
      id
      userId
      totalAmount
      status
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

export const getOrderById = (id) => executeQuery(`
  query GetOrder($id: ID!) {
    orderById(id: $id) {
      id
      userId
      totalAmount
      status
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

export const getOrdersByUserId = (userId) => executeQuery(`
  query GetUserOrders($userId: ID!) {
    ordersByUserId(userId: $userId) {
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
`, { userId });

export const createOrder = (input) => executeQuery(`
  mutation CreateOrder($input: AddOrderInput!) {
    createOrder(input: $input) {
      id
      totalAmount
      status
    }
  }
`, { input });

export const updateOrderStatus = (id, input) => executeQuery(`
  mutation UpdateOrderStatus($id: ID!, $input: UpdateOrderInput!) {
    updateOrderStatus(id: $id, input: $input) {
      id
      status
      totalAmount
    }
  }
`, { id, input });

export const deleteOrder = (id) => executeQuery(`
  mutation DeleteOrder($id: ID!) {
    deleteOrder(id: $id)
  }
`, { id });

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

// Export all as default object for easier imports
export default {
  // Products
  getAllProducts,
  getProductById,
  addProduct,
  deleteProduct,
  
  // Categories
  getAllCategories,
  getCategoryById,
  addCategory,
  updateCategory,
  deleteCategory,
  
  // Orders
  getAllOrders,
  getOrderById,
  getOrdersByUserId,
  createOrder,
  updateOrderStatus,
  deleteOrder,
  
  // Inventory
  getAllInventories,
  getInventoryById,
  getInventoryByProductId,
  addInventory,
  updateInventory,
  deleteInventory,
  
  // Users
  getAllUsers,
  getUserById,
};
