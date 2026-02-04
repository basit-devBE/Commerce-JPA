import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [onLoginCallbacks, setOnLoginCallbacks] = useState([]);

  useEffect(() => {
    // Check if user is logged in
    const token = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('user');
    
    if (token && savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  // Register callback to be called after login
  const registerLoginCallback = useCallback((callback) => {
    setOnLoginCallbacks(prev => [...prev, callback]);
  }, []);

  const login = async (credentials) => {
    const response = await authAPI.login(credentials);
    const { token, ...userData } = response.data.data;
    
    localStorage.setItem('authToken', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    
    // Execute login callbacks (e.g., sync cart)
    for (const callback of onLoginCallbacks) {
      try {
        await callback();
      } catch (err) {
        console.error('Login callback error:', err);
      }
    }
    
    return response.data;
  };

  const register = async (userData) => {
    const response = await authAPI.register(userData);
    const { token, ...user } = response.data.data;
    
    localStorage.setItem('authToken', token);
    localStorage.setItem('user', JSON.stringify(user));
    setUser(user);
    
    // Execute login callbacks (e.g., sync cart)
    for (const callback of onLoginCallbacks) {
      try {
        await callback();
      } catch (err) {
        console.error('Login callback error:', err);
      }
    }
    
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = () => user?.role === 'ADMIN';
  const isSeller = () => user?.role === 'SELLER';
  const isCustomer = () => user?.role === 'CUSTOMER';

  return (
    <AuthContext.Provider value={{
      user,
      login,
      register,
      logout,
      isAdmin,
      isSeller,
      isCustomer,
      loading,
      registerLoginCallback,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
