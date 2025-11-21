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

export default router;