import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

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
  },
  {
    path: '/admin/system',
    name: 'SystemDashboard',
    component: () => import('../views/admin/SystemDashboard.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  // Routes protégées
  const protectedRoutes = ['/feed', '/admin/communal', '/admin/system'];

  if (protectedRoutes.includes(to.path) && !token) {
    return next('/login');
  }

  // Restrictions selon rôles
  if (to.path === '/admin/communal' && role !== 'ADMIN_COMMUNAL') {
    return next('/login');
  }

  if (to.path === '/admin/system' && role !== 'ADMIN_SYSTEM') {
    return next('/login');
  }

  next();
});

export default router;