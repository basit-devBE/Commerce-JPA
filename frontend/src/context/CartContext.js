import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { cartAPI } from '../services/api';

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  const [cartId, setCartId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Check if user is authenticated
  const isAuthenticated = () => {
    return !!localStorage.getItem('authToken');
  };

  // Fetch cart from server
  const fetchCart = useCallback(async () => {
    if (!isAuthenticated()) {
      // Load from localStorage for non-authenticated users
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        setCartItems(JSON.parse(savedCart));
      }
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await cartAPI.get();
      const cartData = response.data.data;
      setCartId(cartData.id);
      // Map server response to local cart format
      const items = cartData.items.map(item => ({
        id: item.productId,
        name: item.productName,
        price: item.productPrice,
        sku: item.productSku,
        quantity: item.quantity,
      }));
      setCartItems(items);
    } catch (err) {
      console.error('Error fetching cart:', err);
      setError('Failed to load cart');
      // Fallback to localStorage
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        setCartItems(JSON.parse(savedCart));
      }
    } finally {
      setLoading(false);
    }
  }, []);

  // Load cart on mount and when auth changes
  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  // Save to localStorage as fallback for non-authenticated users
  useEffect(() => {
    if (!isAuthenticated()) {
      localStorage.setItem('cart', JSON.stringify(cartItems));
    }
  }, [cartItems]);

  const addToCart = async (product, quantity = 1) => {
    if (!isAuthenticated()) {
      // Local-only for non-authenticated users
      setCartItems((prevItems) => {
        const existingItem = prevItems.find((item) => item.id === product.id);
        
        if (existingItem) {
          return prevItems.map((item) =>
            item.id === product.id
              ? { ...item, quantity: item.quantity + quantity }
              : item
          );
        }
        
        return [...prevItems, { ...product, quantity }];
      });
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await cartAPI.addItem({ productId: product.id, quantity });
      const cartData = response.data.data;
      const items = cartData.items.map(item => ({
        id: item.productId,
        name: item.productName,
        price: item.productPrice,
        sku: item.productSku,
        quantity: item.quantity,
      }));
      setCartItems(items);
    } catch (err) {
      console.error('Error adding to cart:', err);
      setError('Failed to add item to cart');
    } finally {
      setLoading(false);
    }
  };

  const removeFromCart = async (productId) => {
    if (!isAuthenticated()) {
      setCartItems((prevItems) => prevItems.filter((item) => item.id !== productId));
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const response = await cartAPI.removeItem(productId);
      const cartData = response.data.data;
      const items = cartData.items.map(item => ({
        id: item.productId,
        name: item.productName,
        price: item.productPrice,
        sku: item.productSku,
        quantity: item.quantity,
      }));
      setCartItems(items);
    } catch (err) {
      console.error('Error removing from cart:', err);
      setError('Failed to remove item from cart');
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (productId, quantity) => {
    if (quantity <= 0) {
      await removeFromCart(productId);
      return;
    }

    if (!isAuthenticated()) {
      setCartItems((prevItems) =>
        prevItems.map((item) =>
          item.id === productId ? { ...item, quantity } : item
        )
      );
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      const response = await cartAPI.updateItem(productId, { quantity });
      const cartData = response.data.data;
      const items = cartData.items.map(item => ({
        id: item.productId,
        name: item.productName,
        price: item.productPrice,
        sku: item.productSku,
        quantity: item.quantity,
      }));
      setCartItems(items);
    } catch (err) {
      console.error('Error updating cart:', err);
      setError('Failed to update cart');
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async () => {
    if (!isAuthenticated()) {
      setCartItems([]);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await cartAPI.clear();
      setCartItems([]);
    } catch (err) {
      console.error('Error clearing cart:', err);
      setError('Failed to clear cart');
    } finally {
      setLoading(false);
    }
  };

  // Sync local cart to server after login
  const syncCartToServer = async () => {
    const localCart = localStorage.getItem('cart');
    if (!localCart || !isAuthenticated()) return;

    const localItems = JSON.parse(localCart);
    if (localItems.length === 0) return;

    try {
      // Add each local item to the server cart
      for (const item of localItems) {
        await cartAPI.addItem({ productId: item.id, quantity: item.quantity });
      }
      // Clear localStorage cart after sync
      localStorage.removeItem('cart');
      // Refresh cart from server
      await fetchCart();
    } catch (err) {
      console.error('Error syncing cart to server:', err);
    }
  };

  const getCartTotal = () => {
    return cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
  };

  const getCartCount = () => {
    return cartItems.reduce((count, item) => count + item.quantity, 0);
  };

  return (
    <CartContext.Provider
      value={{
        cartItems,
        cartId,
        loading,
        error,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        getCartTotal,
        getCartCount,
        fetchCart,
        syncCartToServer,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
