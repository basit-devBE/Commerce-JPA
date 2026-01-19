import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productAPI } from '../services/api';
import { useCart } from '../context/CartContext';
import { ShoppingCartIcon, ArrowLeftIcon } from '@heroicons/react/24/outline';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const { addToCart } = useCart();
  const [added, setAdded] = useState(false);

  const fetchProduct = useCallback(async () => {
    try {
      setLoading(true);
      const response = await productAPI.getById(id);
      setProduct(response.data.data);
    } catch (error) {
      console.error('Error fetching product:', error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchProduct();
  }, [fetchProduct]);

  const handleAddToCart = () => {
    addToCart(product, quantity);
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 py-12 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading product...</p>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="min-h-screen bg-gray-50 py-12 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600">Product not found</p>
          <button
            onClick={() => navigate('/products')}
            className="mt-4 text-primary-600 hover:text-primary-700"
          >
            Back to Products
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <button
          onClick={() => navigate('/products')}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-8"
        >
          <ArrowLeftIcon className="h-5 w-5" />
          Back to Products
        </button>

        <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Product Image */}
            <div className="bg-gradient-to-br from-primary-100 to-primary-200 h-96 lg:h-full flex items-center justify-center">
              <div className="text-9xl">📦</div>
            </div>

            {/* Product Info */}
            <div className="p-8 lg:p-12">
              <div className="mb-6">
                <span className="inline-block px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm font-medium mb-4">
                  {product.categoryName}
                </span>
                <h1 className="text-4xl font-bold text-gray-900 mb-4">{product.name}</h1>
                <p className="text-3xl font-bold text-primary-600 mb-6">
                  ${product.price?.toFixed(2)}
                </p>
              </div>

              {/* Stock Info */}
              <div className="mb-8">
                {product.quantity !== undefined && (
                  <div className="flex items-center gap-2">
                    <span
                      className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
                        product.quantity > 0
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {product.quantity > 0 ? `${product.quantity} in stock` : 'Out of stock'}
                    </span>
                  </div>
                )}
              </div>

              {/* Quantity Selector */}
              {product.quantity > 0 && (
                <div className="mb-8">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Quantity
                  </label>
                  <div className="flex items-center gap-4">
                    <button
                      onClick={() => setQuantity(Math.max(1, quantity - 1))}
                      className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                    >
                      -
                    </button>
                    <span className="text-lg font-medium w-12 text-center">{quantity}</span>
                    <button
                      onClick={() => setQuantity(Math.min(product.quantity, quantity + 1))}
                      className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                    >
                      +
                    </button>
                  </div>
                </div>
              )}

              {/* Add to Cart Button */}
              <button
                onClick={handleAddToCart}
                disabled={product.quantity === 0}
                className={`w-full py-4 px-6 rounded-xl font-medium text-lg transition-all duration-200 flex items-center justify-center gap-3 ${
                  added
                    ? 'bg-green-500 text-white'
                    : product.quantity === 0
                    ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                    : 'bg-primary-600 text-white hover:bg-primary-700 shadow-lg hover:shadow-xl'
                }`}
              >
                <ShoppingCartIcon className="h-6 w-6" />
                {added ? 'Added to Cart!' : product.quantity === 0 ? 'Out of Stock' : 'Add to Cart'}
              </button>

              {/* Product Details */}
              <div className="mt-8 pt-8 border-t border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Product Details</h3>
                <dl className="space-y-2">
                  <div className="flex justify-between">
                    <dt className="text-gray-600">SKU:</dt>
                    <dd className="text-gray-900 font-medium">Not available</dd>
                  </div>
                  <div className="flex justify-between">
                    <dt className="text-gray-600">Category:</dt>
                    <dd className="text-gray-900 font-medium">{product.categoryName}</dd>
                  </div>
                  <div className="flex justify-between">
                    <dt className="text-gray-600">Availability:</dt>
                    <dd className="text-gray-900 font-medium">
                      {product.quantity > 0 ? 'In Stock' : 'Out of Stock'}
                    </dd>
                  </div>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
