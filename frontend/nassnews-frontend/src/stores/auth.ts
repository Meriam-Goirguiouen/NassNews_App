import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User, LoginRequest, RegisterRequest } from '../types';

export const useAuthStore = defineStore('utilisateurs', () => {
  const currentUser = ref<User | null>(null);
  const token = ref<string | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Computed properties
  const isAuthenticated = computed(() => !!token.value && !!currentUser.value);
  const userRole = computed(() => currentUser.value?.role || '');
  const isCitizen = computed(() => currentUser.value?.role === 'USER');
  const isCommunalAdmin = computed(() => currentUser.value?.role === 'ADMIN_COMMUNAL');
  const isSystemAdmin = computed(() => currentUser.value?.role === 'ADMIN_SYSTEM');

  // Login
  async function login(credentials: LoginRequest) {
    loading.value = true;
    error.value = null;
    try {
     // console.log('DATA SENT TO BACKEND:', credentials);
      const response = await fetch('http://localhost:8080/api/utilisateurs/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();

      // Adapter la structure du user
      currentUser.value = data.user || {
        id: data.id,
        username: data.username,
        email: data.email,
        role: data.role || 'USER',
        favoriteCities: data.favoriteCities || [],
      };

      token.value = data.token || data.accessToken || 'mock-token';

      // Stockage local
      if (token.value) {
        localStorage.setItem('token', token.value);
      }
      if (currentUser.value) {
        localStorage.setItem('currentUser', JSON.stringify(currentUser.value));
        localStorage.setItem('role', currentUser.value.role);
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

  // Register
  async function register(data: RegisterRequest) {
    loading.value = true;
    error.value = null;
    try {
      //console.log('DATA SENT TO BACKEND:', data);
      const response = await fetch('http://localhost:8080/api/utilisateurs/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
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

  // Logout
  function logout() {
    token.value = null;
    currentUser.value = null;
    error.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('role');
  }

  // Check auth on app start
  function checkAuth() {
    const savedToken = localStorage.getItem('token');
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
