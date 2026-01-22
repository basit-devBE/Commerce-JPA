import React, { useState, useEffect, useCallback } from 'react';
import { productAPI, categoryAPI, orderAPI, inventoryAPI, userAPI } from '../services/api';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [users, setUsers] = useState([]);
  const [dbMetrics, setDbMetrics] = useState({});
  const [cacheMetrics, setCacheMetrics] = useState({});
  const [loading, setLoading] = useState(false);
  const [editingOrder, setEditingOrder] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [editingInventory, setEditingInventory] = useState(null);
  const [newQuantity, setNewQuantity] = useState('');
  const [editingUser, setEditingUser] = useState(null);
  const [newUserData, setNewUserData] = useState({});
  const [showAddProductModal, setShowAddProductModal] = useState(false);
  const [showAddCategoryModal, setShowAddCategoryModal] = useState(false);
  const [showAddInventoryModal, setShowAddInventoryModal] = useState(false);
  const [newProduct, setNewProduct] = useState({ name: '', categoryId: '', price: '', sku: '', description: '' });
  const [newCategory, setNewCategory] = useState({ name: '', description: '' });
  const [newInventory, setNewInventory] = useState({ productId: '', quantity: '', location: '' });

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      if (activeTab === 'products') {
        const response = await productAPI.getAll({ page: 0, size: 100 });
        setProducts(response.data.data.content);
      } else if (activeTab === 'categories') {
        const response = await categoryAPI.getAll({ page: 0, size: 100 });
        setCategories(response.data.data.content);
      } else if (activeTab === 'orders') {
        const response = await orderAPI.getAll({ page: 0, size: 100 });
        setOrders(response.data.data.content);
      } else if (activeTab === 'inventory') {
        const response = await inventoryAPI.getAll({ page: 0, size: 100 });
        setInventory(response.data.data.content);
      } else if (activeTab === 'users') {
        const response = await userAPI.getAll({ page: 0, size: 100 });
        setUsers(response.data.data.content);
      } else if (activeTab === 'performance') {
        const [dbRes, cacheRes] = await Promise.all([
          fetch('http://localhost:8080/api/performance/db-metrics', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` }
          }),
          fetch('http://localhost:8080/api/performance/cache-metrics', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` }
          })
        ]);
        const dbData = await dbRes.json();
        const cacheData = await cacheRes.json();
        setDbMetrics(dbData.data || {});
        setCacheMetrics(cacheData.data || {});
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  // Fetch categories and products on mount for dropdowns
  useEffect(() => {
    const fetchCategoriesAndProducts = async () => {
      try {
        const [categoriesRes, productsRes] = await Promise.all([
          categoryAPI.getAll({ page: 0, size: 100 }),
          productAPI.getAll({ page: 0, size: 100 })
        ]);
        setCategories(categoriesRes.data.data.content);
        setProducts(productsRes.data.data.content);
      } catch (error) {
        console.error('Error fetching categories and products:', error);
      }
    };
    fetchCategoriesAndProducts();
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleUpdateOrderStatus = async (orderId, status) => {
    try {
      await orderAPI.updateStatus(orderId, { status });
      setEditingOrder(null);
      setNewStatus('');
      fetchData(); // Refresh orders
    } catch (error) {
      console.error('Error updating order status:', error);
      alert('Failed to update order status');
    }
  };

  const handleUpdateInventory = async (inventoryId, quantity) => {
    try {
      await inventoryAPI.update(inventoryId, { quantity: parseInt(quantity) });
      setEditingInventory(null);
      setNewQuantity('');
      fetchData(); // Refresh inventory
    } catch (error) {
      console.error('Error updating inventory:', error);
      alert('Failed to update inventory quantity');
    }
  };

  const handleUpdateUser = async (userId, data) => {
    try {
      await userAPI.update(userId, data);
      setEditingUser(null);
      setNewUserData({});
      fetchData(); // Refresh users
    } catch (error) {
      console.error('Error updating user:', error);
      alert('Failed to update user');
    }
  };

  const handleDeleteUser = async (userId) => {
    if (!window.confirm('Are you sure you want to delete this user?')) {
      return;
    }
    try {
      await userAPI.delete(userId);
      fetchData(); // Refresh users
    } catch (error) {
      console.error('Error deleting user:', error);
      alert('Failed to delete user');
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    try {
      await productAPI.create(newProduct);
      setShowAddProductModal(false);
      setNewProduct({ name: '', categoryId: '', price: '', sku: '', description: '' });
      fetchData();
    } catch (error) {
      console.error('Error adding product:', error);
      alert('Failed to add product');
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    try {
      await categoryAPI.create(newCategory);
      setShowAddCategoryModal(false);
      setNewCategory({ name: '', description: '' });
      fetchData();
    } catch (error) {
      console.error('Error adding category:', error);
      alert('Failed to add category');
    }
  };

  const handleAddInventory = async (e) => {
    e.preventDefault();
    try {
      await inventoryAPI.create(newInventory);
      setShowAddInventoryModal(false);
      setNewInventory({ productId: '', quantity: '', location: '' });
      fetchData();
    } catch (error) {
      console.error('Error adding inventory:', error);
      alert('Failed to add inventory');
    }
  };

  const tabs = [
    { id: 'products', name: 'Products' },
    { id: 'categories', name: 'Categories' },
    { id: 'inventory', name: 'Inventory' },
    { id: 'orders', name: 'Orders' },
    { id: 'users', name: 'Users' },
    { id: 'performance', name: 'Performance' },
  ];

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Admin Dashboard</h1>
          <p className="text-gray-600">Manage your e-commerce store</p>
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-xl shadow-sm mb-6">
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-primary-600 text-primary-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {tab.name}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-6">
            {loading ? (
              <div className="text-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
                <p className="mt-4 text-gray-600">Loading...</p>
              </div>
            ) : (
              <>
                {/* Products Tab */}
                {activeTab === 'products' && (
                  <div>
                    <div className="flex justify-between items-center mb-6">
                      <h2 className="text-xl font-semibold text-gray-900">Products Management</h2>
                      <button 
                        onClick={() => setShowAddProductModal(true)}
                        className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                      >
                        <PlusIcon className="h-5 w-5" />
                        Add Product
                      </button>
                    </div>
                    <div className="overflow-x-auto">
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                          {products.map((product) => (
                            <tr key={product.id}>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm font-medium text-gray-900">{product.name}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-500">{product.categoryName}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-900">${product.price?.toFixed(2)}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                                  product.quantity > 10 ? 'bg-green-100 text-green-800' :
                                  product.quantity > 0 ? 'bg-yellow-100 text-yellow-800' :
                                  'bg-red-100 text-red-800'
                                }`}>
                                  {product.quantity ?? 'N/A'}
                                </span>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                <button className="text-primary-600 hover:text-primary-900 mr-3">
                                  <PencilIcon className="h-5 w-5" />
                                </button>
                                <button className="text-red-600 hover:text-red-900">
                                  <TrashIcon className="h-5 w-5" />
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {/* Categories Tab */}
                {activeTab === 'categories' && (
                  <div>
                    <div className="flex justify-between items-center mb-6">
                      <h2 className="text-xl font-semibold text-gray-900">Categories Management</h2>
                      <button 
                        onClick={() => setShowAddCategoryModal(true)}
                        className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                      >
                        <PlusIcon className="h-5 w-5" />
                        Add Category
                      </button>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                      {categories.map((category) => (
                        <div key={category.id} className="bg-gray-50 rounded-lg p-4">
                          <div className="flex justify-between items-start mb-2">
                            <h3 className="text-lg font-semibold text-gray-900">{category.name}</h3>
                            <div className="flex gap-2">
                              <button className="text-primary-600 hover:text-primary-900">
                                <PencilIcon className="h-5 w-5" />
                              </button>
                              <button className="text-red-600 hover:text-red-900">
                                <TrashIcon className="h-5 w-5" />
                              </button>
                            </div>
                          </div>
                          <p className="text-sm text-gray-600">{category.description}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Inventory Tab */}
                {activeTab === 'inventory' && (
                  <div>
                    <div className="flex justify-between items-center mb-6">
                      <h2 className="text-xl font-semibold text-gray-900">Inventory Management</h2>
                      <button 
                        onClick={() => setShowAddInventoryModal(true)}
                        className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
                      >
                        <PlusIcon className="h-5 w-5" />
                        Add Inventory
                      </button>
                    </div>
                    <div className="overflow-x-auto">
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                          {inventory.map((item) => (
                            <tr key={item.id}>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm font-medium text-gray-900">#{item.id}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-900">{item.productName}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingInventory === item.id ? (
                                  <input
                                    type="number"
                                    min="0"
                                    value={newQuantity}
                                    onChange={(e) => setNewQuantity(e.target.value)}
                                    className="w-24 text-sm border border-gray-300 rounded px-2 py-1"
                                  />
                                ) : (
                                  <div className="flex items-center gap-2">
                                    <span className={`text-sm font-medium ${
                                      item.quantity === 0 ? 'text-red-600' :
                                      item.quantity < 10 ? 'text-yellow-600' :
                                      'text-green-600'
                                    }`}>
                                      {item.quantity}
                                    </span>
                                    {item.quantity === 0 && (
                                      <span className="px-2 py-0.5 text-xs bg-red-100 text-red-800 rounded-full">Out of Stock</span>
                                    )}
                                    {item.quantity > 0 && item.quantity < 10 && (
                                      <span className="px-2 py-0.5 text-xs bg-yellow-100 text-yellow-800 rounded-full">Low Stock</span>
                                    )}
                                  </div>
                                )}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-500">{item.location}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingInventory === item.id ? (
                                  <div className="flex gap-2">
                                    <button
                                      onClick={() => handleUpdateInventory(item.id, newQuantity)}
                                      className="text-green-600 hover:text-green-900 text-sm font-medium"
                                    >
                                      Save
                                    </button>
                                    <button
                                      onClick={() => {
                                        setEditingInventory(null);
                                        setNewQuantity('');
                                      }}
                                      className="text-gray-600 hover:text-gray-900 text-sm font-medium"
                                    >
                                      Cancel
                                    </button>
                                  </div>
                                ) : (
                                  <button
                                    onClick={() => {
                                      setEditingInventory(item.id);
                                      setNewQuantity(item.quantity.toString());
                                    }}
                                    className="text-primary-600 hover:text-primary-900"
                                  >
                                    <PencilIcon className="h-5 w-5" />
                                  </button>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {/* Orders Tab */}
                {activeTab === 'orders' && (
                  <div>
                    <h2 className="text-xl font-semibold text-gray-900 mb-6">Orders Management</h2>
                    <div className="overflow-x-auto">
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Order ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Customer</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Items</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                          {orders.map((order) => (
                            <tr key={order.id}>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm font-medium text-gray-900">#{order.id}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-500">{order.userName}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-900">${order.totalAmount.toFixed(2)}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingOrder === order.id ? (
                                  <select
                                    value={newStatus || order.status}
                                    onChange={(e) => setNewStatus(e.target.value)}
                                    className="text-sm border border-gray-300 rounded px-2 py-1"
                                  >
                                    <option value="PENDING">PENDING</option>
                                    <option value="PROCESSING">PROCESSING</option>
                                    <option value="SHIPPED">SHIPPED</option>
                                    <option value="DELIVERED">DELIVERED</option>
                                    <option value="CANCELLED">CANCELLED</option>
                                  </select>
                                ) : (
                                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                                    order.status === 'DELIVERED' ? 'bg-green-100 text-green-800' :
                                    order.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                                    order.status === 'SHIPPED' ? 'bg-purple-100 text-purple-800' :
                                    order.status === 'PROCESSING' ? 'bg-blue-100 text-blue-800' :
                                    'bg-yellow-100 text-yellow-800'
                                  }`}>
                                    {order.status}
                                  </span>
                                )}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm text-gray-500">{order.items?.length || 0} items</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingOrder === order.id ? (
                                  <div className="flex gap-2">
                                    <button
                                      onClick={() => handleUpdateOrderStatus(order.id, newStatus || order.status)}
                                      className="text-green-600 hover:text-green-900 text-sm font-medium"
                                    >
                                      Save
                                    </button>
                                    <button
                                      onClick={() => {
                                        setEditingOrder(null);
                                        setNewStatus('');
                                      }}
                                      className="text-gray-600 hover:text-gray-900 text-sm font-medium"
                                    >
                                      Cancel
                                    </button>
                                  </div>
                                ) : (
                                  <button
                                    onClick={() => {
                                      setEditingOrder(order.id);
                                      setNewStatus(order.status);
                                    }}
                                    className="text-primary-600 hover:text-primary-900"
                                  >
                                    <PencilIcon className="h-5 w-5" />
                                  </button>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {/* Users Tab */}
                {activeTab === 'users' && (
                  <div>
                    <h2 className="text-xl font-semibold text-gray-900 mb-6">User Management</h2>
                    <div className="overflow-x-auto">
                      <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                          <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                          {users.map((user) => (
                            <tr key={user.id}>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm font-medium text-gray-900">#{user.id}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingUser === user.id ? (
                                  <input
                                    type="text"
                                    value={newUserData.name || ''}
                                    onChange={(e) => setNewUserData({ ...newUserData, name: e.target.value })}
                                    className="text-sm border border-gray-300 rounded px-2 py-1 w-full"
                                    placeholder="Name"
                                  />
                                ) : (
                                  <div className="text-sm text-gray-900">{user.name}</div>
                                )}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingUser === user.id ? (
                                  <input
                                    type="email"
                                    value={newUserData.email || ''}
                                    onChange={(e) => setNewUserData({ ...newUserData, email: e.target.value })}
                                    className="text-sm border border-gray-300 rounded px-2 py-1 w-full"
                                    placeholder="Email"
                                  />
                                ) : (
                                  <div className="text-sm text-gray-500">{user.email}</div>
                                )}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingUser === user.id ? (
                                  <select
                                    value={newUserData.role || user.role}
                                    onChange={(e) => setNewUserData({ ...newUserData, role: e.target.value })}
                                    className="text-sm border border-gray-300 rounded px-2 py-1"
                                  >
                                    <option value="CUSTOMER">CUSTOMER</option>
                                    <option value="ADMIN">ADMIN</option>
                                  </select>
                                ) : (
                                  <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                                    user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'
                                  }`}>
                                    {user.role}
                                  </span>
                                )}
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                {editingUser === user.id ? (
                                  <div className="flex gap-2">
                                    <button
                                      onClick={() => handleUpdateUser(user.id, newUserData)}
                                      className="text-green-600 hover:text-green-900 text-sm font-medium"
                                    >
                                      Save
                                    </button>
                                    <button
                                      onClick={() => {
                                        setEditingUser(null);
                                        setNewUserData({});
                                      }}
                                      className="text-gray-600 hover:text-gray-900 text-sm font-medium"
                                    >
                                      Cancel
                                    </button>
                                  </div>
                                ) : (
                                  <div className="flex gap-2">
                                    <button
                                      onClick={() => {
                                        setEditingUser(user.id);
                                        setNewUserData({ name: user.name, email: user.email, role: user.role });
                                      }}
                                      className="text-primary-600 hover:text-primary-900"
                                    >
                                      <PencilIcon className="h-5 w-5" />
                                    </button>
                                    <button
                                      onClick={() => handleDeleteUser(user.id)}
                                      className="text-red-600 hover:text-red-900"
                                    >
                                      <TrashIcon className="h-5 w-5" />
                                    </button>
                                  </div>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {/* Performance Tab */}
                {activeTab === 'performance' && (
                  <div>
                    <h2 className="text-xl font-semibold text-gray-900 mb-6">Performance Metrics</h2>
                    
                    <div className="mb-8">
                      <h3 className="text-lg font-semibold text-gray-900 mb-4">Database Queries</h3>
                      <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                          <thead className="bg-gray-50">
                            <tr>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Query</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Count</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Avg Time</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Min Time</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Max Time</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total Time</th>
                            </tr>
                          </thead>
                          <tbody className="bg-white divide-y divide-gray-200">
                            {Object.entries(dbMetrics).map(([key, metrics]) => (
                              <tr key={key}>
                                <td className="px-6 py-4 text-sm font-medium text-gray-900">{key}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{metrics.count}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{metrics.avgTime?.toFixed(2)} {metrics.unit}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{metrics.minTime} {metrics.unit}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{metrics.maxTime} {metrics.unit}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{metrics.totalTime} {metrics.unit}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>

                    <div>
                      <h3 className="text-lg font-semibold text-gray-900 mb-4">Cache Statistics</h3>
                      <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                          <thead className="bg-gray-50">
                            <tr>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Cache Key</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hits</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Misses</th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hit Rate</th>
                            </tr>
                          </thead>
                          <tbody className="bg-white divide-y divide-gray-200">
                            {Object.entries(cacheMetrics).map(([key, metrics]) => (
                              <tr key={key}>
                                <td className="px-6 py-4 text-sm font-medium text-gray-900">{key}</td>
                                <td className="px-6 py-4 text-sm text-green-600">{metrics.hits}</td>
                                <td className="px-6 py-4 text-sm text-red-600">{metrics.misses}</td>
                                <td className="px-6 py-4 text-sm text-gray-900">{metrics.hitRate?.toFixed(2)}%</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      {/* Add Product Modal */}
      {showAddProductModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">Add New Product</h3>
            <form onSubmit={handleAddProduct}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                  <input
                    type="text"
                    required
                    value={newProduct.name}
                    onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                  <select
                    required
                    value={newProduct.categoryId}
                    onChange={(e) => setNewProduct({ ...newProduct, categoryId: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  >
                    <option value="">Select a category</option>
                    {categories.map((cat) => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">SKU</label>
                  <input
                    type="text"
                    required
                    value={newProduct.sku}
                    onChange={(e) => setNewProduct({ ...newProduct, sku: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Price</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newProduct.price}
                    onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                  <textarea
                    required
                    value={newProduct.description}
                    onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    rows="3"
                  />
                </div>
              </div>
              <div className="flex gap-3 mt-6">
                <button
                  type="submit"
                  className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700"
                >
                  Add Product
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowAddProductModal(false);
                    setNewProduct({ name: '', categoryId: '', price: '', sku: '', description: '' });
                  }}
                  className="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Category Modal */}
      {showAddCategoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">Add New Category</h3>
            <form onSubmit={handleAddCategory}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                  <input
                    type="text"
                    required
                    value={newCategory.name}
                    onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                  <textarea
                    required
                    value={newCategory.description}
                    onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                    rows="3"
                  />
                </div>
              </div>
              <div className="flex gap-3 mt-6">
                <button
                  type="submit"
                  className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700"
                >
                  Add Category
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowAddCategoryModal(false);
                    setNewCategory({ name: '', description: '' });
                  }}
                  className="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Inventory Modal */}
      {showAddInventoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">Add Inventory Item</h3>
            <form onSubmit={handleAddInventory}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Product</label>
                  <select
                    required
                    value={newInventory.productId}
                    onChange={(e) => setNewInventory({ ...newInventory, productId: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  >
                    <option value="">Select a product</option>
                    {products.map((product) => (
                      <option key={product.id} value={product.id}>{product.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
                  <input
                    type="number"
                    required
                    min="0"
                    value={newInventory.quantity}
                    onChange={(e) => setNewInventory({ ...newInventory, quantity: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
                  <input
                    type="text"
                    required
                    value={newInventory.location}
                    onChange={(e) => setNewInventory({ ...newInventory, location: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
              </div>
              <div className="flex gap-3 mt-6">
                <button
                  type="submit"
                  className="flex-1 bg-primary-600 text-white py-2 rounded-lg hover:bg-primary-700"
                >
                  Add Inventory
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowAddInventoryModal(false);
                    setNewInventory({ productId: '', quantity: '', location: '' });
                  }}
                  className="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;
