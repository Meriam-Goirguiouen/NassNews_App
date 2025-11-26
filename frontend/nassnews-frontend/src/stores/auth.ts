import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User, LoginRequest, RegisterRequest } from '../types';

export const useAuthStore = defineStore('auth', () => {
  const currentUser = ref<User | null>(null);
  const token = ref<string | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isAuthenticated = computed(() => !!token.value && !!currentUser.value);
  const userRole = computed(() => currentUser.value?.role);
  const isCitizen = computed(() => currentUser.value?.role === 'CITIZEN');
  const isCommunalAdmin = computed(() => currentUser.value?.role === 'ADMIN_COMMUNAL');
  const isSystemAdmin = computed(() => currentUser.value?.role === 'ADMIN_SYSTEM');

  async function login(credentials: LoginRequest) {
    loading.value = true;
    error.value = null;
    try {
      // Mock for now - replace with actual API call
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      const authToken = data.token || data.accessToken || 'mock-token';
      token.value = authToken;
      currentUser.value = data.user || data;
      
      localStorage.setItem('jwt_token', authToken);
      if (currentUser.value) {
        localStorage.setItem('currentUser', JSON.stringify(currentUser.value));
      }
      
      return true;
    } catch (err: any) {
      error.value = err.message || 'Login failed';
      console.error('Login error:', err);
      return false;
    } finally {
      loading.value = false;
    }
  }

  async function register(data: RegisterRequest) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Registration failed');
      }

      return await response.json();
    } catch (err: any) {
      error.value = err.message || 'Registration failed';
      console.error('Registration error:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('currentUser');
    currentUser.value = null;
    token.value = null;
    error.value = null;
  }

  function checkAuth() {
    const savedToken = localStorage.getItem('jwt_token');
    const savedUser = localStorage.getItem('currentUser');
    
    if (savedToken && savedUser) {
      token.value = savedToken;
      currentUser.value = JSON.parse(savedUser);
    }
  }

  return {
    currentUser,
    token,
    loading,
    error,
    isAuthenticated,
    userRole,
    isCitizen,
    isCommunalAdmin,
    isSystemAdmin,
    login,
    register,
    logout,
    checkAuth,
  };
});

