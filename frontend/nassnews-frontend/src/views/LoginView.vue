<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import AppButton from '../components/common/AppButton.vue';
import logoWide from '../assets/logoWide.png';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');

const handleLogin = async () => {
  const success = await authStore.login({
    email: email.value,
    password: password.value,
  });

  if (success && authStore.currentUser) {
    // Check if there's a redirect query parameter
    const redirectPath = route.query.redirect as string;
    
    if (redirectPath) {
      // Redirect to the originally requested path
      router.push(redirectPath);
      return;
    }
    
    // Redirect based on role if no redirect parameter
    console.log('Login successful !');
    console.log('user role:', authStore.currentUser.role);
    console.log('Token:', authStore.token);
    console.log('Email:', authStore.currentUser.email);
    
    if (authStore.isCitizen) {
      router.push('/feed');
    } else if (authStore.isCommunalAdmin) {
      router.push('/admin/communal');
    } else if (authStore.isSystemAdmin) {
      router.push('/admin/system');
    } else {
      // Default redirect if role doesn't match
      console.warn('Unknown user role, redirecting to feed');
      router.push('/feed');
    }
  } else {
    console.error('Login failed:', authStore.error);
  }
};
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4] flex items-center justify-center px-4">
    <div class="max-w-md w-full">
      <!-- Logo/Brand -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-block mb-4">
          <img :src="logoWide" alt="NassNews Logo" class="h-16 w-auto mx-auto" />
        </router-link>
        <p class="text-gray-600 mt-2">Sign in to your account</p>
      </div>

      <!-- Login Card -->
      <div class="bg-white rounded-3xl shadow-xl p-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6 text-center">Login</h2>

        <!-- Error Message -->
        <div v-if="authStore.error" class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded">
          <p class="text-red-700 text-sm">{{ authStore.error }}</p>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-semibold text-gray-700 mb-2">
              Email
            </label>
            <input
              id="email"
              v-model="email"
              type="email"
              required
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              placeholder="your@email.com"
            />
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-semibold text-gray-700 mb-2">
              Password
            </label>
            <input
              id="password"
              v-model="password"
              type="password"
              required
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              placeholder="••••••••"
            />
          </div>

          <!-- Forgot Password Link -->
          <div class="text-right">
            <a href="#" class="text-sm text-[#7A1F1F] hover:underline">
              Forgot password?
            </a>
          </div>

          <!-- Submit Button -->
          <AppButton
            type="submit"
            variant="primary"
            size="lg"
            :full-width="true"
            :disabled="authStore.loading"
          >
            <span v-if="authStore.loading">Logging in...</span>
            <span v-else>Log In</span>
          </AppButton>
        </form>

        <!-- Divider -->
        <div class="relative my-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-gray-200"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-4 bg-white text-gray-500">OR</span>
          </div>
        </div>

        <!-- Register Link -->
        <div class="text-center">
          <p class="text-gray-600 text-sm">
            Don't have an account?
            <router-link to="/register" class="text-[#7A1F1F] font-semibold hover:underline">
              Sign up
            </router-link>
          </p>
        </div>
      </div>

      <!-- Back to Home -->
      <div class="text-center mt-6">
        <router-link to="/" class="text-gray-600 text-sm hover:text-[#7A1F1F] transition-colors">
          ← Back to home
        </router-link>
      </div>
    </div>
  </div>
</template>