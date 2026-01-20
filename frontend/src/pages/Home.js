import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBagIcon, TruckIcon, ShieldCheckIcon, CreditCardIcon, ShoppingCartIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../context/AuthContext';
import { productAPI } from '../services/api';
import { useCart } from '../context/CartContext';

const Home = () => {
  const { user } = useAuth();
  const { addToCart } = useCart();
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addedToCart, setAddedToCart] = useState(null);

  useEffect(() => {
    if (user) {
      fetchFeaturedProducts();
    } else {
      setLoading(false);
    }
  }, [user]);

  const fetchFeaturedProducts = async () => {
    try {
      setLoading(true);
      const response = await productAPI.getAll({ page: 0, size: 8 });
      setFeaturedProducts(response.data.data.content);
    } catch (error) {
      console.error('Error fetching products:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (product) => {
    addToCart(product);
    setAddedToCart(product.id);
    setTimeout(() => setAddedToCart(null), 2000);
  };

  // Show featured products for logged-in users
  if (user) {
    return (
      <div className="min-h-screen bg-gray-50">
        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-primary-600 to-primary-700 text-white py-12">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <h1 className="text-4xl font-bold mb-2">Welcome back, {user.username}! 👋</h1>
            <p className="text-xl text-primary-100">Discover amazing products just for you</p>
          </div>
        </div>

        {/* Featured Products Section */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex justify-between items-center mb-8">
            <div>
              <h2 className="text-3xl font-bold text-gray-900">Featured Products</h2>
              <p className="text-gray-600 mt-2">Check out our latest and most popular items</p>
            </div>
            <Link
              to="/products"
              className="px-6 py-3 bg-primary-600 text-white rounded-lg font-semibold hover:bg-primary-700 transition-colors"
            >
              View All Products
            </Link>
          </div>

          {loading ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {[...Array(8)].map((_, i) => (
                <div key={i} className="bg-white rounded-xl shadow-sm p-4 animate-pulse">
                  <div className="bg-gray-200 h-48 rounded-lg mb-4"></div>
                  <div className="bg-gray-200 h-6 rounded mb-2"></div>
                  <div className="bg-gray-200 h-4 rounded w-2/3"></div>
                </div>
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {featuredProducts.map((product) => (
                <div
                  key={product.id}
                  className="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow duration-200 overflow-hidden"
                >
                  <Link to={`/products/${product.id}`}>
                    <div className="bg-gradient-to-br from-primary-100 to-primary-200 h-48 flex items-center justify-center">
                      <div className="text-6xl">📦</div>
                    </div>
                  </Link>
                  <div className="p-4">
                    <Link to={`/products/${product.id}`}>
                      <h3 className="font-semibold text-lg text-gray-900 mb-1 hover:text-primary-600 transition-colors">
                        {product.name}
                      </h3>
                    </Link>
                    <p className="text-sm text-gray-500 mb-2">{product.categoryName}</p>
                    <p className="text-gray-600 text-sm mb-3 line-clamp-2">{product.description}</p>
                    <div className="flex items-center justify-between">
                      <span className="text-2xl font-bold text-primary-600">
                        ${product.price.toFixed(2)}
                      </span>
                      <button
                        onClick={() => handleAddToCart(product)}
                        disabled={addedToCart === product.id}
                        className={`p-2 rounded-lg transition-all duration-200 ${
                          addedToCart === product.id
                            ? 'bg-green-500 text-white'
                            : 'bg-primary-600 text-white hover:bg-primary-700'
                        }`}
                      >
                        <ShoppingCartIcon className="h-5 w-5" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Quick Links Section */}
        <div className="bg-white py-12 mt-8">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <Link
                to="/orders"
                className="p-6 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:shadow-md transition-all"
              >
                <h3 className="text-xl font-semibold text-gray-900 mb-2">My Orders</h3>
                <p className="text-gray-600">Track and manage your orders</p>
              </Link>
              <Link
                to="/profile"
                className="p-6 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:shadow-md transition-all"
              >
                <h3 className="text-xl font-semibold text-gray-900 mb-2">My Profile</h3>
                <p className="text-gray-600">Update your account information</p>
              </Link>
              <Link
                to="/cart"
                className="p-6 border-2 border-gray-200 rounded-xl hover:border-primary-500 hover:shadow-md transition-all"
              >
                <h3 className="text-xl font-semibold text-gray-900 mb-2">Shopping Cart</h3>
                <p className="text-gray-600">Review items in your cart</p>
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show landing page for non-logged-in users
  const features = [
    {
      icon: ShoppingBagIcon,
      title: 'Wide Selection',
      description: 'Browse thousands of products across multiple categories',
    },
    {
      icon: TruckIcon,
      title: 'Fast Delivery',
      description: 'Get your orders delivered quickly and safely',
    },
    {
      icon: ShieldCheckIcon,
      title: 'Secure Shopping',
      description: 'Shop with confidence with our secure payment system',
    },
    {
      icon: CreditCardIcon,
      title: 'Easy Payments',
      description: 'Multiple payment options for your convenience',
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-white to-gray-50">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-extrabold text-gray-900 mb-6">
              Welcome to{' '}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary-600 to-primary-400">
                Commerce Smart
              </span>
            </h1>
            <p className="text-xl md:text-2xl text-gray-600 mb-8 max-w-3xl mx-auto">
              Your one-stop destination for quality products at amazing prices
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/products"
                className="px-8 py-4 bg-primary-600 text-white rounded-xl font-semibold text-lg hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-1"
              >
                Start Shopping
              </Link>
              <Link
                to="/register"
                className="px-8 py-4 bg-white text-primary-600 border-2 border-primary-600 rounded-xl font-semibold text-lg hover:bg-primary-50 transition-all duration-200"
              >
                Create Account
              </Link>
            </div>
          </div>
        </div>

        {/* Decorative background */}
        <div className="absolute inset-0 -z-10">
          <div className="absolute inset-0 bg-gradient-to-br from-primary-50 via-transparent to-transparent opacity-50"></div>
          <div className="absolute top-0 right-0 w-96 h-96 bg-primary-200 rounded-full blur-3xl opacity-20"></div>
          <div className="absolute bottom-0 left-0 w-96 h-96 bg-primary-300 rounded-full blur-3xl opacity-20"></div>
        </div>
      </div>

      {/* Features Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-gray-900 mb-4">Why Choose Us?</h2>
          <p className="text-xl text-gray-600">Experience the best online shopping with these amazing features</p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="bg-white rounded-xl p-8 shadow-sm hover:shadow-md transition-shadow duration-200"
            >
              <div className="bg-primary-100 w-16 h-16 rounded-xl flex items-center justify-center mb-6">
                <feature.icon className="h-8 w-8 text-primary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-3">{feature.title}</h3>
              <p className="text-gray-600">{feature.description}</p>
            </div>
          ))}
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-700 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
            Ready to Start Shopping?
          </h2>
          <p className="text-xl text-primary-100 mb-8">
            Join thousands of satisfied customers today
          </p>
          <Link
            to="/products"
            className="inline-block px-8 py-4 bg-white text-primary-600 rounded-xl font-semibold text-lg hover:bg-gray-100 transition-all duration-200 shadow-lg"
          >
            Browse Products
          </Link>
        </div>
      </div>

      {/* Stats Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center">
            <div className="text-5xl font-bold text-primary-600 mb-2">10K+</div>
            <div className="text-gray-600 text-lg">Happy Customers</div>
          </div>
          <div className="text-center">
            <div className="text-5xl font-bold text-primary-600 mb-2">5K+</div>
            <div className="text-gray-600 text-lg">Products</div>
          </div>
          <div className="text-center">
            <div className="text-5xl font-bold text-primary-600 mb-2">98%</div>
            <div className="text-gray-600 text-lg">Satisfaction Rate</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
