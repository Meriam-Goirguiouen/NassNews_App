<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import AppButton from '../components/common/AppButton.vue';
import logoWide from '../assets/logoWide.png';

const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const errorMessage = ref('');

const handleRegister = async () => {
  errorMessage.value = '';

  // Validation
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Passwords do not match';
    return;
  }

  if (password.value.length < 6) {
    errorMessage.value = 'Password must be at least 6 characters long';
    return;
  }

  try {
    // Only citizens can register through this form
    await authStore.register({
    username: username.value,
    email: email.value,
    password: password.value,
    confirmPassword: confirmPassword.value,
    role: 'USER',
    });

    // Registration successful, redirect to login
    router.push('/login');
  } catch (err) {
    errorMessage.value = authStore.error || 'Registration failed';
  }
};
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4] flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full">
      <!-- Logo/Brand -->
      <div class="text-center mb-8">
        <router-link to="/" class="inline-block mb-4">
          <img :src="logoWide" alt="NassNews Logo" class="h-16 w-auto mx-auto" />
        </router-link>
        <p class="text-gray-600 mt-2">Create your account</p>
      </div>

      <!-- Register Card -->
      <div class="bg-white rounded-3xl shadow-xl p-8">
        <h2 class="text-2xl font-bold text-gray-900 mb-6 text-center">Sign Up</h2>

        <!-- Error Message -->
        <div v-if="errorMessage" class="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded">
          <p class="text-red-700 text-sm">{{ errorMessage }}</p>
        </div>

        <!-- Register Form -->
        <form @submit.prevent="handleRegister" class="space-y-5">
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-semibold text-gray-700 mb-2">
              Username
            </label>
            <input
              id="username"
              v-model="username"
              type="text"
              required
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              placeholder="Your name"
            />
          </div>

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

          <!-- Confirm Password -->
          <div>
            <label for="confirmPassword" class="block text-sm font-semibold text-gray-700 mb-2">
              Confirm Password
            </label>
            <input
              id="confirmPassword"
              v-model="confirmPassword"
              type="password"
              required
              class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              placeholder="••••••••"
            />
          </div>


          <!-- Submit Button -->
          <AppButton
            type="submit"
            variant="primary"
            size="lg"
            :full-width="true"
            :disabled="authStore.loading"
          >
            <span v-if="authStore.loading">Registering...</span>
            <span v-else>Sign Up</span>
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

        <!-- Login Link -->
        <div class="text-center">
          <p class="text-gray-600 text-sm">
            Already have an account?
            <router-link to="/login" class="text-[#7A1F1F] font-semibold hover:underline">
              Log in
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