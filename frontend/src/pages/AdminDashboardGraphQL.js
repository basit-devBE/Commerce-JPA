import React, { useState, useEffect, useCallback } from 'react';
import * as graphqlAPI from '../services/graphqlApi';
import { PlusIcon, PencilIcon, TrashIcon, ArrowPathIcon } from '@heroicons/react/24/outline';
import ErrorAlert from '../components/ErrorAlert';

const AdminDashboardGraphQL = () => {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
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

  // Fetch initial data for dropdowns (only once on mount)
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [categoriesData, productsData] = await Promise.all([
          graphqlAPI.getAllCategories(),
          graphqlAPI.getAllProducts()
        ]);
        setCategories(categoriesData.allCategories || []);
        setProducts(productsData.allProducts || []);
      } catch (error) {
        console.error('Error fetching initial data:', error);
      }
    };
    fetchInitialData();
  }, []);

  // Fetch data based on active tab
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        if (activeTab === 'products') {
          const data = await graphqlAPI.getAllProducts();
          setProducts(data.allProducts || []);
        } else if (activeTab === 'categories') {
          const data = await graphqlAPI.getAllCategories();
          setCategories(data.allCategories || []);
        } else if (activeTab === 'orders') {
          const data = await graphqlAPI.getAllOrders();
          setOrders(data.allOrders || []);
        } else if (activeTab === 'inventory') {
          const data = await graphqlAPI.getAllInventories();
          setInventory(data.allInventories || []);
        } else if (activeTab === 'users') {
          const data = await graphqlAPI.getAllUsers();
          setUsers(data.getAllUsers || []);
        }
      } catch (err) {
        console.error('Error fetching data:', err);
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, [activeTab]);

  // Manual refresh function for after mutations
  const refreshCurrentTab = async () => {
    setLoading(true);
    try {
      if (activeTab === 'products') {
        const data = await graphqlAPI.getAllProducts();
        setProducts(data.allProducts || []);
      } else if (activeTab === 'categories') {
        const data = await graphqlAPI.getAllCategories();
        setCategories(data.allCategories || []);
      } else if (activeTab === 'orders') {
        const data = await graphqlAPI.getAllOrders();
        setOrders(data.allOrders || []);
      } else if (activeTab === 'inventory') {
        const data = await graphqlAPI.getAllInventories();
        setInventory(data.allInventories || []);
      } else if (activeTab === 'users') {
        const data = await graphqlAPI.getAllUsers();
        setUsers(data.getAllUsers || []);
      }
    } catch (err) {
      console.error('Error refreshing data:', err);
    } finally {
      setLoading(false);
    }
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

  const handleDeleteOrder = async (orderId) => {
    if (!window.confirm('Are you sure you want to delete this order?')) {
      return;
    }
    try {
      await graphqlAPI.deleteOrder(orderId);
      setError(null);
      refreshCurrentTab();
    } catch (err) {
      console.error('Error deleting order:', err);
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

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Price</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantity</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {products.map((product) => (
              <tr key={product.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm">{product.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">{product.name}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">{product.categoryName}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">{product.sku}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">${product.price?.toFixed(2)}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">{product.quantity}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  <button
                    onClick={() => handleDeleteProduct(product.id)}
                    className="text-red-600 hover:text-red-800"
                  >
                    <TrashIcon className="w-5 h-5" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
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

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {categories.map((category) => (
              <tr key={category.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm">{category.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {editingCategory === category.id ? (
                    <input
                      type="text"
                      value={newCategoryData.name}
                      onChange={(e) => setNewCategoryData({ ...newCategoryData, name: e.target.value })}
                      className="border rounded px-2 py-1"
                    />
                  ) : (
                    category.name
                  )}
                </td>
                <td className="px-6 py-4 text-sm">
                  {editingCategory === category.id ? (
                    <input
                      type="text"
                      value={newCategoryData.description}
                      onChange={(e) => setNewCategoryData({ ...newCategoryData, description: e.target.value })}
                      className="border rounded px-2 py-1 w-full"
                    />
                  ) : (
                    category.description
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
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
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderOrders = () => (
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

      <div className="grid gap-4">
        {orders.map((order) => (
          <div key={order.id} className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-semibold">Order #{order.id}</h3>
                <p className="text-sm text-gray-600">User ID: {order.userId}</p>
                <p className="text-sm font-bold text-gray-800">Total: ${order.totalAmount?.toFixed(2)}</p>
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
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                      order.status === 'DELIVERED' ? 'bg-green-100 text-green-800' :
                      order.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                      'bg-yellow-100 text-yellow-800'
                    }`}>
                      {order.status}
                    </span>
                    <button
                      onClick={() => {
                        setEditingOrder(order.id);
                        setNewStatus(order.status);
                      }}
                      className="text-blue-600 hover:text-blue-800"
                    >
                      <PencilIcon className="w-5 h-5" />
                    </button>
                    <button
                      onClick={() => handleDeleteOrder(order.id)}
                      className="text-red-600 hover:text-red-800"
                    >
                      <TrashIcon className="w-5 h-5" />
                    </button>
                  </>
                )}
              </div>
            </div>
            
            <div className="border-t pt-4">
              <h4 className="font-medium mb-2">Order Items:</h4>
              <div className="space-y-2">
                {order.items?.map((item) => (
                  <div key={item.id} className="flex justify-between text-sm">
                    <span>{item.productName} x {item.quantity}</span>
                    <span className="font-medium">${item.totalPrice?.toFixed(2)}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

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

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantity</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Location</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {inventory.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm">{item.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">{item.productName}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  {editingInventory === item.id ? (
                    <input
                      type="number"
                      value={newQuantity}
                      onChange={(e) => setNewQuantity(e.target.value)}
                      className="border rounded px-2 py-1 w-20"
                      placeholder={item.quantity}
                    />
                  ) : (
                    <span className={item.quantity < 10 ? 'text-red-600 font-bold' : ''}>
                      {item.quantity}
                    </span>
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  {editingInventory === item.id ? (
                    <input
                      type="text"
                      value={newLocation}
                      onChange={(e) => setNewLocation(e.target.value)}
                      className="border rounded px-2 py-1"
                      placeholder={item.location}
                    />
                  ) : (
                    item.location
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
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
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderUsers = () => (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Users (GraphQL)</h2>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm">{user.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  {user.firstName} {user.lastName}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">{user.email}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                    user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' :
                    user.role === 'SELLER' ? 'bg-blue-100 text-blue-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {user.role}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
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
              {['products', 'categories', 'orders', 'inventory', 'users'].map((tab) => (
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
