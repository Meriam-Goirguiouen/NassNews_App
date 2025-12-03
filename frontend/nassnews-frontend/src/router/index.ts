import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
  },
  {
    path: '/feed',
    name: 'Feed',
    component: () => import('../views/FeedView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/FavoritesView.vue'),
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('../views/HistoryView.vue'),
  },
  {
    path: '/favorite-cities',
    name: 'FavoriteCities',
    component: () => import('../views/FavoriteCitiesView.vue'),
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/SettingsView.vue'),
  },
  {
    path: '/admin/communal',
    name: 'CommunalDashboard',
    component: () => import('../views/admin/CommunalDashboard.vue'),
    meta: { requiresAuth: true, requiresRole: 'ADMIN_COMMUNAL' },
  },
  {
    path: '/admin/system',
    name: 'SystemDashboard',
    component: () => import('../views/admin/SystemDashboard.vue'),
    meta: { requiresAuth: true, requiresRole: 'ADMIN_SYSTEM' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();
  
  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    // Check token in localStorage first (for initial load)
    const token = localStorage.getItem("token");
    
    // If no token, redirect to login
    if (!token) {
      return next({ path: '/login', query: { redirect: to.fullPath } });
    }
    
    // Verify auth store is initialized
    if (!authStore.isAuthenticated) {
      // Try to restore auth from localStorage
      authStore.checkAuth();
    }
    
    // Double check authentication
    if (!authStore.isAuthenticated || !authStore.token) {
      return next({ path: '/login', query: { redirect: to.fullPath } });
    }
    
    // Check role requirements
    if (to.meta.requiresRole) {
      const requiredRole = to.meta.requiresRole as string;
      const userRole = authStore.userRole;
      
      if (userRole !== requiredRole) {
        return next({ path: '/login', query: { redirect: to.fullPath } });
      }
    }
  }

  next();
});

export default router;