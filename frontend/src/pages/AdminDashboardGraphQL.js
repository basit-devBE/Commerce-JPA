import React, { useState, useEffect } from 'react';
import * as graphqlAPI from '../services/graphqlApi';
import { performanceAPI } from '../services/api';
import { PlusIcon, PencilIcon, TrashIcon, ChevronLeftIcon, ChevronRightIcon, ArrowPathIcon, AdjustmentsHorizontalIcon } from '@heroicons/react/24/outline';
import ErrorAlert from '../components/ErrorAlert';

// GraphQL client for dynamic queries
const executeGraphQL = async (query, variables = {}) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch('http://localhost:8080/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
    },
    body: JSON.stringify({ query, variables }),
  });
  const result = await response.json();
  if (result.errors) {
    // Extract the first error and create a meaningful error message
    const error = result.errors[0];
    let errorMessage = error.message;
    
    // Remove UUID from INTERNAL_ERROR messages
    if (errorMessage.includes('INTERNAL_ERROR for')) {
      errorMessage = 'An internal error occurred. Please try again or contact support.';
    }
    
    // Create error with additional context
    const customError = new Error(errorMessage);
    customError.graphQLErrors = result.errors;
    customError.path = error.path;
    customError.extensions = error.extensions;
    
    throw customError;
  }
  return result.data;
};

// Available fields for each entity type
const AVAILABLE_FIELDS = {
  products: [
    { key: 'id', label: 'ID', default: true },
    { key: 'name', label: 'Name', default: true },
    { key: 'categoryName', label: 'Category', default: true },
    { key: 'sku', label: 'SKU', default: true },
    { key: 'price', label: 'Price', default: true },
    { key: 'quantity', label: 'Quantity', default: true },
    { key: 'createdAt', label: 'Created At', default: false },
    { key: 'updatedAt', label: 'Updated At', default: false },
  ],
  categories: [
    { key: 'id', label: 'ID', default: true },
    { key: 'name', label: 'Name', default: true },
    { key: 'description', label: 'Description', default: true },
  ],
  orders: [
    { key: 'id', label: 'ID', default: true },
    { key: 'userId', label: 'User ID', default: true },
    { key: 'userName', label: 'User Name', default: false },
    { key: 'userEmail', label: 'User Email', default: false },
    { key: 'totalAmount', label: 'Total', default: true },
    { key: 'status', label: 'Status', default: true },
    { key: 'createdAt', label: 'Created At', default: true },
    { key: 'updatedAt', label: 'Updated At', default: false },
  ],
  inventory: [
    { key: 'id', label: 'ID', default: true },
    { key: 'productId', label: 'Product ID', default: false },
    { key: 'productName', label: 'Product', default: true },
    { key: 'quantity', label: 'Quantity', default: true },
    { key: 'location', label: 'Location', default: true },
  ],
  users: [
    { key: 'id', label: 'ID', default: true },
    { key: 'firstName', label: 'First Name', default: true },
    { key: 'lastName', label: 'Last Name', default: true },
    { key: 'email', label: 'Email', default: true },
    { key: 'role', label: 'Role', default: true },
    { key: 'phoneNumber', label: 'Phone', default: false },
    { key: 'address', label: 'Address', default: false },
  ],
  reviews: [
    { key: 'id', label: 'ID', default: true },
    { key: 'productId', label: 'Product ID', default: false },
    { key: 'productName', label: 'Product', default: true },
    { key: 'userId', label: 'User ID', default: false },
    { key: 'userName', label: 'User', default: true },
    { key: 'userEmail', label: 'Email', default: false },
    { key: 'rating', label: 'Rating', default: true },
    { key: 'comment', label: 'Comment', default: true },
    { key: 'createdAt', label: 'Created At', default: true },
    { key: 'updatedAt', label: 'Updated At', default: false },
  ],
};

const AdminDashboardGraphQL = () => {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [users, setUsers] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [dbMetrics, setDbMetrics] = useState({});
  const [cacheMetrics, setCacheMetrics] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // Search and filter states
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    products: { categoryId: '', minPrice: '', maxPrice: '' },
    orders: { status: '' }
  });
  const [debouncedFilters, setDebouncedFilters] = useState({
    products: { categoryId: '', minPrice: '', maxPrice: '' },
    orders: { status: '' }
  });
  
  // Debounce search term
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 500); // Wait 500ms after user stops typing
    
    return () => clearTimeout(timer);
  }, [searchTerm]);
  
  // Debounce filters (price inputs)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedFilters(filters);
    }, 500); // Wait 500ms after user stops typing
    
    return () => clearTimeout(timer);
  }, [filters]);
  
  // Field selection state for each tab
  const [selectedFields, setSelectedFields] = useState(() => {
    const defaults = {};
    Object.keys(AVAILABLE_FIELDS).forEach(tab => {
      defaults[tab] = AVAILABLE_FIELDS[tab]
        .filter(f => f.default)
        .map(f => f.key);
    });
    return defaults;
  });
  
  // Pagination state for each tab
  const [pagination, setPagination] = useState({
    products: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
    categories: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
    orders: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
    inventory: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
    users: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
    reviews: { page: 0, size: 10, totalPages: 0, totalItems: 0, hasNext: false, hasPrevious: false },
  });
  
  // Edit states
  const [editingOrder, setEditingOrder] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [editingInventory, setEditingInventory] = useState(null);
  const [newQuantity, setNewQuantity] = useState('');
  const [newLocation, setNewLocation] = useState('');
  const [editingCategory, setEditingCategory] = useState(null);
  const [newCategoryData, setNewCategoryData] = useState({ name: '', description: '' });
  
  // Modal states
  const [showAddProductModal, setShowAddProductModal] = useState(false);
  const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
  const [showAddInventoryModal, setShowAddInventoryModal] = useState(false);
  const [showAddOrderModal, setShowAddOrderModal] = useState(false);
  
  // Form states
  const [newProduct, setNewProduct] = useState({ name: '', categoryId: '', price: '', sku: '' });
  const [newCategory, setNewCategory] = useState({ name: '', description: '' });
  const [newInventory, setNewInventory] = useState({ productId: '', quantity: '', location: '' });
  const [newOrder, setNewOrder] = useState({ userId: '', items: [{ productId: '', quantity: '' }] });

  // Helper to update pagination for a specific tab
  const updatePagination = (tab, pageInfo) => {
    setPagination(prev => ({
      ...prev,
      [tab]: {
        ...prev[tab],
        totalPages: pageInfo.totalPages,
        totalItems: pageInfo.totalItems,
        hasNext: pageInfo.hasNext,
        hasPrevious: pageInfo.hasPrevious,
      }
    }));
  };

  // Build dynamic GraphQL query based on selected fields
  const buildDynamicQuery = (tab, fields, search = '') => {
    const fieldList = fields.join('\n        ');
    const searchParam = search ? ', $search: String' : '';
    const searchArg = search ? ', search: $search' : '';
    
    const queryMap = {
      products: `
        query ProductsPaginated($pagination: PaginationInput!${searchParam}) {
          productsPaginated(pagination: $pagination${searchArg}) {
            content {
              ${fieldList}
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
      `,
      categories: `
        query CategoriesPaginated($pagination: PaginationInput!${searchParam}) {
          categoriesPaginated(pagination: $pagination${searchArg}) {
            content {
              ${fieldList}
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
      `,
      orders: `
        query OrdersPaginated($pagination: PaginationInput!${searchParam}) {
          ordersPaginated(pagination: $pagination${searchArg}) {
            content {
              ${fieldList}
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
      `,
      inventory: `
        query InventoriesPaginated($pagination: PaginationInput!${searchParam}) {
          inventoriesPaginated(pagination: $pagination${searchArg}) {
            content {
              ${fieldList}
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
      `,
      users: `
        query UsersPaginated($pagination: PaginationInput!${searchParam}) {
          usersPaginated(pagination: $pagination${searchArg}) {
            content {
              ${fieldList}
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
      `,
    };
    return queryMap[tab];
  };

  // Fetch initial data for dropdowns (only once on mount)
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const query = `
          query {
            allCategories { id name description }
            allProducts { id name categoryName sku price quantity }
          }
        `;
        const data = await executeGraphQL(query);
        setCategories(data.allCategories || []);
        setProducts(data.allProducts || []);
      } catch (error) {
        console.error('Error fetching initial data:', error);
      }
    };
    fetchInitialData();
  }, []);

  // Fetch paginated data based on active tab with selected fields
  const fetchPaginatedData = async (tab, page = 0) => {
    setLoading(true);
    setError(null);
    const size = pagination[tab]?.size || 10;
    
    try {
      if (tab === 'products') {
        const productFilters = debouncedFilters.products;
        let search = debouncedSearchTerm;
        
        // If price range is specified, we'll handle it client-side for now
        // since the backend might not support it yet
        const data = await graphqlAPI.getProductsPaginated(
          page, 
          size, 
          'id', 
          'ASC',
          productFilters.categoryId || null,
          search || null
        );
        
        let content = data.productsPaginated?.content || [];
        
        // Client-side price filtering if specified
        if (productFilters.minPrice || productFilters.maxPrice) {
          content = content.filter(product => {
            const price = product.price;
            if (productFilters.minPrice && price < parseFloat(productFilters.minPrice)) return false;
            if (productFilters.maxPrice && price > parseFloat(productFilters.maxPrice)) return false;
            return true;
          });
        }
        
        setProducts(content);
        updatePagination('products', data.productsPaginated?.pageInfo || {});
        
      } else if (tab === 'orders') {
        const orderFilters = debouncedFilters.orders;
        const data = await graphqlAPI.getOrdersPaginated(
          page,
          size,
          'id',
          'DESC',
          orderFilters.status || null,
          debouncedSearchTerm || null
        );
        setOrders(data.ordersPaginated?.content || []);
        updatePagination('orders', data.ordersPaginated?.pageInfo || {});
        
      } else if (tab === 'categories') {
        const fields = selectedFields[tab] || [];
        const queryFields = fields.length > 0 ? fields : ['id'];
        const query = buildDynamicQuery(tab, queryFields, debouncedSearchTerm);
        const variables = { 
          pagination: { page, size, sortBy: 'id', sortDirection: 'ASC' }
        };
        if (debouncedSearchTerm) variables.search = debouncedSearchTerm;
        
        const data = await executeGraphQL(query, variables);
        setCategories(data.categoriesPaginated?.content || []);
        updatePagination('categories', data.categoriesPaginated?.pageInfo || {});
        
      } else if (tab === 'inventory') {
        const fields = selectedFields[tab] || [];
        const queryFields = fields.length > 0 ? fields : ['id'];
        const query = buildDynamicQuery(tab, queryFields, debouncedSearchTerm);
        const variables = { 
          pagination: { page, size, sortBy: 'id', sortDirection: 'ASC' }
        };
        if (debouncedSearchTerm) variables.search = debouncedSearchTerm;
        
        const data = await executeGraphQL(query, variables);
        setInventory(data.inventoriesPaginated?.content || []);
        updatePagination('inventory', data.inventoriesPaginated?.pageInfo || {});
        
      } else if (tab === 'users') {
        const fields = selectedFields[tab] || [];
        const queryFields = fields.length > 0 ? fields : ['id'];
        const query = buildDynamicQuery(tab, queryFields, debouncedSearchTerm);
        const variables = { 
          pagination: { page, size, sortBy: 'id', sortDirection: 'ASC' }
        };
        if (debouncedSearchTerm) variables.search = debouncedSearchTerm;
        
        const data = await executeGraphQL(query, variables);
        setUsers(data.usersPaginated?.content || []);
        updatePagination('users', data.usersPaginated?.pageInfo || {});
        
      } else if (tab === 'reviews') {
        const data = await graphqlAPI.getAllReviews();
        setReviews(data.allReviews || []);
        // Since we don't have paginated reviews endpoint yet, simulate pagination
        updatePagination('reviews', {
          totalPages: 1,
          totalItems: data.allReviews?.length || 0,
          hasNext: false,
          hasPrevious: false
        });
        
      } else if (tab === 'performance') {
        // Fetch performance metrics using REST API
        const [dbResponse, cacheResponse] = await Promise.all([
          performanceAPI.getDbMetrics(),
          performanceAPI.getCacheMetrics()
        ]);
        setDbMetrics(dbResponse.data.data || {});
        setCacheMetrics(cacheResponse.data.data || {});
        updatePagination('performance', {
          totalPages: 1,
          totalItems: 0,
          hasNext: false,
          hasPrevious: false
        });
      }
    } catch (err) {
      console.error('Error fetching data:', err);
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  // Toggle field selection
  const toggleField = (field) => {
    setSelectedFields(prev => {
      const current = prev[activeTab] || [];
      if (current.includes(field)) {
        return { ...prev, [activeTab]: current.filter(f => f !== field) };
      } else {
        return { ...prev, [activeTab]: [...current, field] };
      }
    });
  };

  // Fetch data based on active tab
  useEffect(() => {
    fetchPaginatedData(activeTab, pagination[activeTab]?.page || 0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab, selectedFields, debouncedSearchTerm, debouncedFilters]);

  // Page change handler
  const handlePageChange = (newPage) => {
    setPagination(prev => ({
      ...prev,
      [activeTab]: { ...prev[activeTab], page: newPage }
    }));
    fetchPaginatedData(activeTab, newPage);
  };

  // Manual refresh function for after mutations
  const refreshCurrentTab = async () => {
    // Re-fetch current page data after mutations
    await fetchPaginatedData(activeTab, pagination[activeTab]?.page || 0);
  };

  // ==================== PRODUCT HANDLERS ====================
  
  const handleAddProduct = async (e) => {
    e.preventDefault();
    try {
      await graphqlAPI.addProduct({
        name: newProduct.name,
        categoryId: newProduct.categoryId,
        sku: newProduct.sku,
        price: parseFloat(newProduct.price)
      });
      setShowAddProductModal(false);
      setNewProduct({ name: '', categoryId: '', price: '', sku: '' });
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error adding product:', err);
      setError(err);
    }
  };

  const handleDeleteProduct = async (productId) => {
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }
    try {
      await graphqlAPI.deleteProduct(productId);
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error deleting product:', err);
      setError(err);
    }
  };

  // ==================== CATEGORY HANDLERS ====================
  
  const handleAddCategory = async (e) => {
    e.preventDefault();
    try {
      await graphqlAPI.addCategory({
        name: newCategory.name,
        description: newCategory.description
      });
      setShowAddCategoryModal(false);
      setNewCategory({ name: '', description: '' });
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error adding category:', err);
      setError(err);
    }
  };

  const handleUpdateCategory = async (categoryId) => {
    try {
      await graphqlAPI.updateCategory(categoryId, {
        name: newCategoryData.name,
        description: newCategoryData.description
      });
      setEditingCategory(null);
      setNewCategoryData({ name: '', description: '' });
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error updating category:', err);
      setError(err);
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    if (!window.confirm('Are you sure you want to delete this category?')) {
      return;
    }
    try {
      await graphqlAPI.deleteCategory(categoryId);
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error deleting category:', err);
      setError(err);
    }
  };

  // ==================== ORDER HANDLERS ====================
  
  const handleAddOrder = async (e) => {
    e.preventDefault();
    try {
      const orderInput = {
        userId: newOrder.userId,
        items: newOrder.items.map(item => ({
          productId: item.productId,
          quantity: parseInt(item.quantity)
        }))
      };
      await graphqlAPI.createOrder(orderInput);
      setShowAddOrderModal(false);
      setNewOrder({ userId: '', items: [{ productId: '', quantity: '' }] });
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error adding order:', err);
      setError(err);
    }
  };

  const handleUpdateOrderStatus = async (orderId, status) => {
    try {
      await graphqlAPI.updateOrderStatus(orderId, { status });
      setEditingOrder(null);
      setNewStatus('');
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error updating order status:', err);
      setError(err);
    }
  };

  // ==================== INVENTORY HANDLERS ====================
  
  const handleAddInventory = async (e) => {
    e.preventDefault();
    try {
      await graphqlAPI.addInventory({
        productId: newInventory.productId,
        quantity: parseInt(newInventory.quantity),
        location: newInventory.location
      });
      setShowAddInventoryModal(false);
      setNewInventory({ productId: '', quantity: '', location: '' });
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error adding inventory:', err);
      setError(err);
    }
  };

  const handleUpdateInventory = async (inventoryId) => {
    try {
      const updateData = {};
      if (newQuantity) updateData.quantity = parseInt(newQuantity);
      if (newLocation) updateData.location = newLocation;
      
      await graphqlAPI.updateInventory(inventoryId, updateData);
      setEditingInventory(null);
      setNewQuantity('');
      setNewLocation('');
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error updating inventory:', err);
      setError(err);
    }
  };

  const handleDeleteInventory = async (inventoryId) => {
    if (!window.confirm('Are you sure you want to delete this inventory record?')) {
      return;
    }
    try {
      await graphqlAPI.deleteInventory(inventoryId);
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error deleting inventory:', err);
      setError(err);
    }
  };

  const handleDeleteReview = async (reviewId) => {
    if (!window.confirm('Are you sure you want to delete this review?')) {
      return;
    }
    try {
      await graphqlAPI.deleteReview(reviewId);
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error deleting review:', err);
      setError(err);
    }
  };

  const addOrderItem = () => {
    setNewOrder({
      ...newOrder,
      items: [...newOrder.items, { productId: '', quantity: '' }]
    });
  };

  const removeOrderItem = (index) => {
    setNewOrder({
      ...newOrder,
      items: newOrder.items.filter((_, i) => i !== index)
    });
  };

  const updateOrderItem = (index, field, value) => {
    const updatedItems = [...newOrder.items];
    updatedItems[index][field] = value;
    setNewOrder({ ...newOrder, items: updatedItems });
  };

  // ==================== RENDER FUNCTIONS ====================

  // Pagination component
  const PaginationControls = ({ tab }) => {
    const pageInfo = pagination[tab];
    if (!pageInfo || pageInfo.totalPages <= 1) return null;
    
    return (
      <div className="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6 mt-4 rounded-lg shadow">
        <div className="flex flex-1 justify-between sm:hidden">
          <button
            onClick={() => handlePageChange(pageInfo.page - 1)}
            disabled={!pageInfo.hasPrevious}
            className={`relative inline-flex items-center rounded-md px-4 py-2 text-sm font-medium ${
              pageInfo.hasPrevious 
                ? 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300' 
                : 'bg-gray-100 text-gray-400 cursor-not-allowed'
            }`}
          >
            Previous
          </button>
          <button
            onClick={() => handlePageChange(pageInfo.page + 1)}
            disabled={!pageInfo.hasNext}
            className={`relative ml-3 inline-flex items-center rounded-md px-4 py-2 text-sm font-medium ${
              pageInfo.hasNext 
                ? 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300' 
                : 'bg-gray-100 text-gray-400 cursor-not-allowed'
            }`}
          >
            Next
          </button>
        </div>
        <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
          <div>
            <p className="text-sm text-gray-700">
              Showing page <span className="font-medium">{pageInfo.page + 1}</span> of{' '}
              <span className="font-medium">{pageInfo.totalPages}</span> ({pageInfo.totalItems} total items)
            </p>
          </div>
          <div>
            <nav className="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
              <button
                onClick={() => handlePageChange(pageInfo.page - 1)}
                disabled={!pageInfo.hasPrevious}
                className={`relative inline-flex items-center rounded-l-md px-2 py-2 ring-1 ring-inset ring-gray-300 ${
                  pageInfo.hasPrevious 
                    ? 'bg-white text-gray-400 hover:bg-gray-50' 
                    : 'bg-gray-100 text-gray-300 cursor-not-allowed'
                }`}
              >
                <ChevronLeftIcon className="h-5 w-5" aria-hidden="true" />
              </button>
              
              {/* Page numbers */}
              {[...Array(Math.min(5, pageInfo.totalPages))].map((_, i) => {
                let pageNum;
                if (pageInfo.totalPages <= 5) {
                  pageNum = i;
                } else if (pageInfo.page < 3) {
                  pageNum = i;
                } else if (pageInfo.page > pageInfo.totalPages - 3) {
                  pageNum = pageInfo.totalPages - 5 + i;
                } else {
                  pageNum = pageInfo.page - 2 + i;
                }
                
                return (
                  <button
                    key={pageNum}
                    onClick={() => handlePageChange(pageNum)}
                    className={`relative inline-flex items-center px-4 py-2 text-sm font-semibold ring-1 ring-inset ring-gray-300 ${
                      pageNum === pageInfo.page
                        ? 'z-10 bg-blue-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600'
                        : 'bg-white text-gray-900 hover:bg-gray-50'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
              
              <button
                onClick={() => handlePageChange(pageInfo.page + 1)}
                disabled={!pageInfo.hasNext}
                className={`relative inline-flex items-center rounded-r-md px-2 py-2 ring-1 ring-inset ring-gray-300 ${
                  pageInfo.hasNext 
                    ? 'bg-white text-gray-400 hover:bg-gray-50' 
                    : 'bg-gray-100 text-gray-300 cursor-not-allowed'
                }`}
              >
                <ChevronRightIcon className="h-5 w-5" aria-hidden="true" />
              </button>
            </nav>
          </div>
        </div>
      </div>
    );
  };

  // Field Selector Component
  const FieldSelector = () => (
    <div className="bg-white rounded-lg shadow p-4 mb-4">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <AdjustmentsHorizontalIcon className="w-5 h-5 text-gray-500" />
          <h3 className="text-sm font-semibold text-gray-700">Select Fields to Display</h3>
        </div>
        <span className="text-xs text-gray-500">
          ({selectedFields[activeTab]?.length || 0} of {AVAILABLE_FIELDS[activeTab]?.length || 0} selected)
        </span>
      </div>
      <div className="flex flex-wrap gap-2">
        {AVAILABLE_FIELDS[activeTab]?.map((field) => (
          <button
            key={field.key}
            onClick={() => toggleField(field.key)}
            className={`px-3 py-1.5 rounded-full text-xs font-medium transition-colors ${
              selectedFields[activeTab]?.includes(field.key)
                ? 'bg-blue-100 text-blue-800 border-2 border-blue-300'
                : 'bg-gray-100 text-gray-600 border-2 border-transparent hover:bg-gray-200'
            }`}
          >
            {field.label}
          </button>
        ))}
      </div>
      <p className="text-xs text-gray-400 mt-2 italic">
        💡 GraphQL fetches only selected fields - reducing payload size and improving performance
      </p>
    </div>
  );

  // Helper to render cell value with proper formatting
  const renderCellValue = (item, fieldKey) => {
    const value = item[fieldKey];
    if (value === null || value === undefined) return '-';
    
    // Format based on field type
    if (fieldKey === 'price' || fieldKey === 'totalAmount') {
      return `$${parseFloat(value).toFixed(2)}`;
    }
    if (fieldKey === 'createdAt' || fieldKey === 'updatedAt') {
      return new Date(value).toLocaleDateString();
    }
    if (fieldKey === 'rating') {
      // Render star rating
      return (
        <div className="flex items-center gap-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <span key={star} className={star <= value ? 'text-yellow-400' : 'text-gray-300'}>
              ★
            </span>
          ))}
          <span className="ml-1 text-sm text-gray-600">({value})</span>
        </div>
      );
    }
    if (fieldKey === 'comment' && String(value).length > 100) {
      // Truncate long comments
      return String(value).substring(0, 100) + '...';
    }
    if (fieldKey === 'status') {
      const statusColors = {
        PENDING: 'bg-yellow-100 text-yellow-800',
        PROCESSING: 'bg-blue-100 text-blue-800',
        SHIPPED: 'bg-purple-100 text-purple-800',
        DELIVERED: 'bg-green-100 text-green-800',
        CANCELLED: 'bg-red-100 text-red-800',
      };
      return (
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[value] || 'bg-gray-100 text-gray-800'}`}>
          {value}
        </span>
      );
    }
    if (fieldKey === 'role') {
      const roleColors = {
        ADMIN: 'bg-purple-100 text-purple-800',
        SELLER: 'bg-blue-100 text-blue-800',
        CUSTOMER: 'bg-gray-100 text-gray-800',
      };
      return (
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${roleColors[value] || 'bg-gray-100 text-gray-800'}`}>
          {value}
        </span>
      );
    }
    return String(value);
  };

  // Dynamic Table Component
  const DynamicTable = ({ data, tab, actions }) => {
    const fields = selectedFields[tab] || [];
    const availableFields = AVAILABLE_FIELDS[tab] || [];
    
    if (fields.length === 0) {
      return (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-center text-yellow-700">
          Please select at least one field to display
        </div>
      );
    }

    return (
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {fields.map((fieldKey) => {
                const field = availableFields.find(f => f.key === fieldKey);
                return (
                  <th key={fieldKey} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    {field?.label || fieldKey}
                  </th>
                );
              })}
              {actions && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
              )}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50">
                {fields.map((fieldKey) => (
                  <td key={fieldKey} className="px-6 py-4 whitespace-nowrap text-sm">
                    {fieldKey === 'firstName' && item.lastName 
                      ? `${item.firstName} ${item.lastName}`
                      : fieldKey === 'lastName' && selectedFields[tab]?.includes('firstName')
                      ? null // Skip lastName if firstName is already showing combined
                      : renderCellValue(item, fieldKey)
                    }
                  </td>
                ))}
                {actions && (
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {actions(item)}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const renderProducts = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Products (GraphQL)</h2>
        <button
          onClick={() => setShowAddProductModal(true)}
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          <PlusIcon className="w-5 h-5" />
          Add Product
        </button>
      </div>

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow p-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Search */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
            <input
              type="text"
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          {/* Category Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              value={filters.products.categoryId}
              onChange={(e) => setFilters(prev => ({
                ...prev,
                products: { ...prev.products, categoryId: e.target.value }
              }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Categories</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>
          
          {/* Min Price */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Min Price</label>
            <input
              type="number"
              placeholder="$0"
              value={filters.products.minPrice}
              onChange={(e) => setFilters(prev => ({
                ...prev,
                products: { ...prev.products, minPrice: e.target.value }
              }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          {/* Max Price */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Max Price</label>
            <input
              type="number"
              placeholder="$999"
              value={filters.products.maxPrice}
              onChange={(e) => setFilters(prev => ({
                ...prev,
                products: { ...prev.products, maxPrice: e.target.value }
              }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
        
        {/* Clear Filters Button */}
        {(searchTerm || filters.products.categoryId || filters.products.minPrice || filters.products.maxPrice) && (
          <button
            onClick={() => {
              setSearchTerm('');
              setFilters(prev => ({
                ...prev,
                products: { categoryId: '', minPrice: '', maxPrice: '' }
              }));
            }}
            className="mt-3 text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Clear all filters
          </button>
        )}
      </div>

      <FieldSelector />

      <DynamicTable 
        data={products} 
        tab="products" 
        actions={(product) => (
          <button
            onClick={() => handleDeleteProduct(product.id)}
            className="text-red-600 hover:text-red-800"
          >
            <TrashIcon className="w-5 h-5" />
          </button>
        )}
      />
      
      <PaginationControls tab="products" />
    </div>
  );

  const renderCategories = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Categories (GraphQL)</h2>
        <button
          onClick={() => setShowAddCategoryModal(true)}
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          <PlusIcon className="w-5 h-5" />
          Add Category
        </button>
      </div>

      <FieldSelector />

      <DynamicTable 
        data={categories} 
        tab="categories" 
        actions={(category) => (
          <div className="flex gap-2">
            {editingCategory === category.id ? (
              <>
                <button
                  onClick={() => handleUpdateCategory(category.id)}
                  className="text-green-600 hover:text-green-800"
                >
                  Save
                </button>
                <button
                  onClick={() => {
                    setEditingCategory(null);
                    setNewCategoryData({ name: '', description: '' });
                  }}
                  className="text-gray-600 hover:text-gray-800"
                >
                  Cancel
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => {
                    setEditingCategory(category.id);
                    setNewCategoryData({ name: category.name, description: category.description });
                  }}
                  className="text-blue-600 hover:text-blue-800"
                >
                  <PencilIcon className="w-5 h-5" />
                </button>
                <button
                  onClick={() => handleDeleteCategory(category.id)}
                  className="text-red-600 hover:text-red-800"
                >
                  <TrashIcon className="w-5 h-5" />
                </button>
              </>
            )}
          </div>
        )}
      />
      
      <PaginationControls tab="categories" />
    </div>
  );

  const renderOrders = () => {
    const fields = selectedFields['orders'] || [];
    
    return (
      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-800">Orders (GraphQL)</h2>
          <button
            onClick={() => setShowAddOrderModal(true)}
            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            <PlusIcon className="w-5 h-5" />
            Create Order
          </button>
        </div>

        {/* Search and Filters */}
        <div className="bg-white rounded-lg shadow p-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Search */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Search (by user name, email, or order ID)
              </label>
              <input
                type="text"
                placeholder="Search orders..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            {/* Status Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select
                value={filters.orders.status}
                onChange={(e) => setFilters(prev => ({
                  ...prev,
                  orders: { ...prev.orders, status: e.target.value }
                }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Statuses</option>
                <option value="PENDING">PENDING</option>
                <option value="PROCESSING">PROCESSING</option>
                <option value="SHIPPED">SHIPPED</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>
          </div>
          
          {/* Clear Filters Button */}
          {(searchTerm || filters.orders.status) && (
            <button
              onClick={() => {
                setSearchTerm('');
                setFilters(prev => ({
                  ...prev,
                  orders: { status: '' }
                }));
              }}
              className="mt-3 text-sm text-blue-600 hover:text-blue-800 font-medium"
            >
              Clear all filters
            </button>
          )}
        </div>

        <FieldSelector />

        <div className="grid gap-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow p-6">
              <div className="flex justify-between items-start mb-4">
                <div className="space-y-1">
                  {fields.includes('id') && <h3 className="text-lg font-semibold">Order #{order.id}</h3>}
                  {fields.includes('userId') && <p className="text-sm text-gray-600">User ID: {order.userId}</p>}
                  {fields.includes('userName') && order.userName && <p className="text-sm text-gray-600">User: {order.userName}</p>}
                  {fields.includes('userEmail') && order.userEmail && <p className="text-sm text-gray-600">Email: {order.userEmail}</p>}
                  {fields.includes('totalAmount') && <p className="text-sm font-bold text-gray-800">Total: ${order.totalAmount?.toFixed(2)}</p>}
                  {fields.includes('createdAt') && order.createdAt && <p className="text-sm text-gray-500">Created: {new Date(order.createdAt).toLocaleDateString()}</p>}
                  {fields.includes('updatedAt') && order.updatedAt && <p className="text-sm text-gray-500">Updated: {new Date(order.updatedAt).toLocaleDateString()}</p>}
                </div>
                <div className="flex items-center gap-2">
                  {editingOrder === order.id ? (
                    <>
                      <select
                        value={newStatus}
                        onChange={(e) => setNewStatus(e.target.value)}
                        className="border rounded px-3 py-1"
                      >
                        <option value="">Select Status</option>
                        <option value="PENDING">PENDING</option>
                        <option value="PROCESSING">PROCESSING</option>
                        <option value="SHIPPED">SHIPPED</option>
                        <option value="DELIVERED">DELIVERED</option>
                        <option value="CANCELLED">CANCELLED</option>
                      </select>
                      <button
                        onClick={() => handleUpdateOrderStatus(order.id, newStatus)}
                        className="text-green-600 hover:text-green-800 font-medium"
                      >
                        Save
                      </button>
                      <button
                        onClick={() => {
                          setEditingOrder(null);
                          setNewStatus('');
                        }}
                        className="text-gray-600 hover:text-gray-800"
                      >
                        Cancel
                      </button>
                    </>
                  ) : (
                    <>
                      {fields.includes('status') && (
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                          order.status === 'DELIVERED' ? 'bg-green-100 text-green-800' :
                          order.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {order.status}
                        </span>
                      )}
                      <button
                        onClick={() => {
                          setEditingOrder(order.id);
                          setNewStatus(order.status);
                        }}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        <PencilIcon className="w-5 h-5" />
                      </button>
                    </>
                  )}
                </div>
              </div>
            
            {fields.includes('items') && order.items && order.items.length > 0 && (
              <div className="border-t pt-4">
                <h4 className="font-medium mb-2">Order Items:</h4>
                <div className="space-y-2">
                  {order.items.map((item) => (
                    <div key={item.id} className="flex justify-between text-sm">
                      <span>{item.productName} x {item.quantity}</span>
                      <span className="font-medium">${item.totalPrice?.toFixed(2)}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
      
      <PaginationControls tab="orders" />
    </div>
  );
};

  const renderInventory = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Inventory (GraphQL)</h2>
        <button
          onClick={() => setShowAddInventoryModal(true)}
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          <PlusIcon className="w-5 h-5" />
          Add Inventory
        </button>
      </div>

      <FieldSelector />

      <DynamicTable 
        data={inventory} 
        tab="inventory" 
        actions={(item) => (
          <div className="flex gap-2">
            {editingInventory === item.id ? (
              <>
                <button
                  onClick={() => handleUpdateInventory(item.id)}
                  className="text-green-600 hover:text-green-800"
                >
                  Save
                </button>
                <button
                  onClick={() => {
                    setEditingInventory(null);
                    setNewQuantity('');
                    setNewLocation('');
                  }}
                  className="text-gray-600 hover:text-gray-800"
                >
                  Cancel
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => {
                    setEditingInventory(item.id);
                    setNewQuantity(item.quantity);
                    setNewLocation(item.location);
                  }}
                  className="text-blue-600 hover:text-blue-800"
                >
                  <PencilIcon className="w-5 h-5" />
                </button>
                <button
                  onClick={() => handleDeleteInventory(item.id)}
                  className="text-red-600 hover:text-red-800"
                >
                  <TrashIcon className="w-5 h-5" />
                </button>
              </>
            )}
          </div>
        )}
      />
      
      <PaginationControls tab="inventory" />
    </div>
  );

  const renderUsers = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Users (GraphQL)</h2>
      </div>

      <FieldSelector />

      <DynamicTable data={users} tab="users" />
      
      <PaginationControls tab="users" />
    </div>
  );

  const renderReviews = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Reviews (GraphQL)</h2>
      </div>

      <FieldSelector />

      <DynamicTable 
        data={reviews} 
        tab="reviews"
        actions={(review) => (
          <button
            onClick={() => handleDeleteReview(review.id)}
            className="text-red-600 hover:text-red-800"
          >
            <TrashIcon className="w-5 h-5" />
          </button>
        )}
      />
      
      <PaginationControls tab="reviews" />
    </div>
  );

  const renderPerformance = () => {
    // Calculate aggregate DB metrics
    const dbEntries = Object.entries(dbMetrics);
    const totalQueries = dbEntries.reduce((sum, [_, metrics]) => sum + (metrics.count || 0), 0);
    const totalQueryTime = dbEntries.reduce((sum, [_, metrics]) => sum + (metrics.totalTime || 0), 0);
    const avgQueryTime = totalQueries > 0 ? totalQueryTime / totalQueries : 0;
    const slowestQuery = dbEntries.reduce((max, [name, metrics]) => 
      metrics.maxTime > (max?.maxTime || 0) ? { name, ...metrics } : max, null);
    const fastestQuery = dbEntries.reduce((min, [name, metrics]) => 
      metrics.minTime < (min?.minTime || Infinity) ? { name, ...metrics } : min, null);

    // Sort queries by max time for slowest queries display
    const sortedQueries = [...dbEntries]
      .sort((a, b) => (b[1].maxTime || 0) - (a[1].maxTime || 0))
      .slice(0, 5);

    // Calculate aggregate cache metrics
    const cacheEntries = Object.entries(cacheMetrics);
    const totalHits = cacheEntries.reduce((sum, [_, metrics]) => sum + (metrics.hits || 0), 0);
    const totalMisses = cacheEntries.reduce((sum, [_, metrics]) => sum + (metrics.misses || 0), 0);
    const totalRequests = totalHits + totalMisses;
    const overallHitRate = totalRequests > 0 ? (totalHits / totalRequests) * 100 : 0;

    return (
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-800">Performance Metrics (REST)</h2>
          <button
            onClick={async () => {
              try {
                await performanceAPI.clearMetrics();
                await fetchPaginatedData('performance');
              } catch (err) {
                console.error('Failed to clear metrics:', err);
              }
            }}
            className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
          >
            Clear Metrics
          </button>
        </div>

        {/* Database Metrics */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">Database Metrics</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <MetricCard 
              label="Total Queries" 
              value={totalQueries}
              icon="📊"
            />
            <MetricCard 
              label="Avg Query Time" 
              value={`${avgQueryTime.toFixed(2)} ms`}
              icon="⏱️"
            />
            <MetricCard 
              label="Slowest Query Time" 
              value={`${slowestQuery?.maxTime || 0} ms`}
              icon="🐌"
            />
            <MetricCard 
              label="Fastest Query Time" 
              value={`${fastestQuery?.minTime || 0} ms`}
              icon="⚡"
            />
            <MetricCard 
              label="Total Query Time" 
              value={`${totalQueryTime.toFixed(2)} ms`}
              icon="📈"
            />
            <MetricCard 
              label="Unique Queries" 
              value={dbEntries.length}
              icon="🔍"
            />
          </div>

          {sortedQueries.length > 0 && (
            <div className="mt-6">
              <h4 className="text-lg font-semibold text-gray-700 mb-3">Top 5 Slowest Queries</h4>
              <div className="space-y-2">
                {sortedQueries.map(([methodName, metrics], index) => (
                  <div key={index} className="bg-gray-50 p-3 rounded border border-gray-200">
                    <div className="flex justify-between items-center mb-1">
                      <span className="text-sm font-medium text-gray-700">{methodName}</span>
                      <span className="text-sm text-red-600 font-semibold">{metrics.maxTime} ms</span>
                    </div>
                    <div className="grid grid-cols-4 gap-2 text-xs text-gray-600 mt-2">
                      <div>Avg: {metrics.avgTime?.toFixed(2)} ms</div>
                      <div>Min: {metrics.minTime} ms</div>
                      <div>Total: {metrics.totalTime} ms</div>
                      <div>Count: {metrics.count}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Cache Metrics */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">Cache Metrics</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <MetricCard 
              label="Cache Hits" 
              value={totalHits}
              icon="✅"
            />
            <MetricCard 
              label="Cache Misses" 
              value={totalMisses}
              icon="❌"
            />
            <MetricCard 
              label="Overall Hit Rate" 
              value={`${overallHitRate.toFixed(2)}%`}
              icon="📊"
            />
            <MetricCard 
              label="Total Requests" 
              value={totalRequests}
              icon="🔄"
            />
          </div>

          {cacheEntries.length > 0 && (
            <div className="mt-6">
              <h4 className="text-lg font-semibold text-gray-700 mb-3">Cache Keys Performance</h4>
              <div className="space-y-2">
                {cacheEntries.map(([cacheKey, metrics], index) => (
                  <div key={index} className="bg-gray-50 p-3 rounded border border-gray-200">
                    <div className="flex justify-between items-center mb-1">
                      <span className="text-sm font-medium text-gray-700 break-all">{cacheKey}</span>
                      <span className={`text-sm font-semibold ${metrics.hitRate >= 80 ? 'text-green-600' : metrics.hitRate >= 50 ? 'text-yellow-600' : 'text-red-600'}`}>
                        {metrics.hitRate.toFixed(2)}%
                      </span>
                    </div>
                    <div className="grid grid-cols-3 gap-2 text-xs text-gray-600 mt-2">
                      <div>Hits: {metrics.hits}</div>
                      <div>Misses: {metrics.misses}</div>
                      <div>Total: {metrics.hits + metrics.misses}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    );
  };

  // Helper component for metric cards
  const MetricCard = ({ label, value, icon }) => (
    <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-4 rounded-lg border border-blue-200">
      <div className="flex items-center gap-2 mb-2">
        <span className="text-2xl">{icon}</span>
        <span className="text-sm font-medium text-gray-600">{label}</span>
      </div>
      <div className="text-2xl font-bold text-gray-900">{value}</div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Admin Dashboard</h1>
          <p className="text-gray-600">Powered by GraphQL ⚡</p>
        </div>

        {error && (
          <div className="mb-4">
            <ErrorAlert error={error} onClose={() => setError(null)} />
          </div>
        )}

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow mb-6">
          <div className="border-b border-gray-200">
            <nav className="flex space-x-8 px-6" aria-label="Tabs">
              {['products', 'categories', 'orders', 'inventory', 'users', 'reviews', 'performance'].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`py-4 px-1 border-b-2 font-medium text-sm capitalize ${
                    activeTab === tab
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {tab}
                </button>
              ))}
            </nav>
          </div>
        </div>

        {/* Content */}
        <div className="bg-gray-50 rounded-lg p-6">
          {loading ? (
            <div className="flex items-center justify-center py-12">
              <ArrowPathIcon className="w-8 h-8 animate-spin text-blue-600" />
              <span className="ml-2 text-gray-600">Loading...</span>
            </div>
          ) : (
            <>
              {activeTab === 'products' && renderProducts()}
              {activeTab === 'categories' && renderCategories()}
              {activeTab === 'orders' && renderOrders()}
              {activeTab === 'inventory' && renderInventory()}
              {activeTab === 'users' && renderUsers()}
              {activeTab === 'reviews' && renderReviews()}
              {activeTab === 'performance' && renderPerformance()}
            </>
          )}
        </div>
      </div>

      {/* Modals */}
      {showAddProductModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-xl font-bold mb-4">Add New Product</h3>
            <form onSubmit={handleAddProduct} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Name</label>
                <input
                  type="text"
                  required
                  value={newProduct.name}
                  onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Category</label>
                <select
                  required
                  value={newProduct.categoryId}
                  onChange={(e) => setNewProduct({ ...newProduct, categoryId: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                >
                  <option value="">Select Category</option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">SKU</label>
                <input
                  type="text"
                  required
                  value={newProduct.sku}
                  onChange={(e) => setNewProduct({ ...newProduct, sku: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Price</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  value={newProduct.price}
                  onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div className="flex gap-2 justify-end">
                <button
                  type="button"
                  onClick={() => setShowAddProductModal(false)}
                  className="px-4 py-2 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  Add Product
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showAddCategoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-xl font-bold mb-4">Add New Category</h3>
            <form onSubmit={handleAddCategory} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Name</label>
                <input
                  type="text"
                  required
                  value={newCategory.name}
                  onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Description</label>
                <textarea
                  required
                  value={newCategory.description}
                  onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                  rows="3"
                />
              </div>
              <div className="flex gap-2 justify-end">
                <button
                  type="button"
                  onClick={() => setShowAddCategoryModal(false)}
                  className="px-4 py-2 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  Add Category
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showAddInventoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-xl font-bold mb-4">Add Inventory</h3>
            <form onSubmit={handleAddInventory} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Product</label>
                <select
                  required
                  value={newInventory.productId}
                  onChange={(e) => setNewInventory({ ...newInventory, productId: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                >
                  <option value="">Select Product</option>
                  {products.map((prod) => (
                    <option key={prod.id} value={prod.id}>{prod.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Quantity</label>
                <input
                  type="number"
                  required
                  value={newInventory.quantity}
                  onChange={(e) => setNewInventory({ ...newInventory, quantity: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Location</label>
                <input
                  type="text"
                  required
                  value={newInventory.location}
                  onChange={(e) => setNewInventory({ ...newInventory, location: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div className="flex gap-2 justify-end">
                <button
                  type="button"
                  onClick={() => setShowAddInventoryModal(false)}
                  className="px-4 py-2 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  Add Inventory
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showAddOrderModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-[90vh] overflow-y-auto">
            <h3 className="text-xl font-bold mb-4">Create New Order</h3>
            <form onSubmit={handleAddOrder} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">User ID</label>
                <input
                  type="text"
                  required
                  value={newOrder.userId}
                  onChange={(e) => setNewOrder({ ...newOrder, userId: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium mb-2">Order Items</label>
                {newOrder.items.map((item, index) => (
                  <div key={index} className="flex gap-2 mb-2">
                    <select
                      required
                      value={item.productId}
                      onChange={(e) => updateOrderItem(index, 'productId', e.target.value)}
                      className="flex-1 border rounded px-3 py-2"
                    >
                      <option value="">Select Product</option>
                      {products.map((prod) => (
                        <option key={prod.id} value={prod.id}>{prod.name}</option>
                      ))}
                    </select>
                    <input
                      type="number"
                      required
                      placeholder="Qty"
                      value={item.quantity}
                      onChange={(e) => updateOrderItem(index, 'quantity', e.target.value)}
                      className="w-20 border rounded px-3 py-2"
                    />
                    {newOrder.items.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeOrderItem(index)}
                        className="text-red-600 hover:text-red-800"
                      >
                        <TrashIcon className="w-5 h-5" />
                      </button>
                    )}
                  </div>
                ))}
                <button
                  type="button"
                  onClick={addOrderItem}
                  className="text-blue-600 hover:text-blue-800 text-sm flex items-center gap-1"
                >
                  <PlusIcon className="w-4 h-4" />
                  Add Item
                </button>
              </div>
              
              <div className="flex gap-2 justify-end">
                <button
                  type="button"
                  onClick={() => setShowAddOrderModal(false)}
                  className="px-4 py-2 text-gray-700 bg-gray-200 rounded hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  Create Order
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboardGraphQL;
