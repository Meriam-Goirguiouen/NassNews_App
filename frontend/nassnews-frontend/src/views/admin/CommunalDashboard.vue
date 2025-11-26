<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

onMounted(() => {
  if (!authStore.isAuthenticated || !authStore.isCommunalAdmin) {
    router.push('/login');
  }
});
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4]">
    <nav class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <router-link to="/" class="text-2xl font-bold text-[#7A1F1F]">NassNews</router-link>
          <div class="flex items-center gap-4">
            <span class="text-sm text-gray-600">{{ authStore.currentUser?.username }}</span>
            <button @click="authStore.logout(); router.push('/')" class="text-sm text-[#7A1F1F] hover:underline">
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    </nav>

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-8">Tableau de Bord Communal</h1>
      <div class="bg-white rounded-3xl p-12 text-center">
        <p class="text-gray-500">Communal Admin Dashboard - To be implemented</p>
      </div>
    </main>
  </div>
</template>

