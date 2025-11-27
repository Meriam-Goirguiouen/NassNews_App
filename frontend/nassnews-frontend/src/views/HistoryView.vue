<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useNewsStore } from '../stores/news';
import NewsCard from '../components/common/NewsCard.vue';
import logoWide from '../assets/logoWide.png';
import { Home, Heart, History, Settings, MapPin, LogOut, Search, User } from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const newsStore = useNewsStore();

// Sidebar state
const activeMenuItem = ref('history');
const searchQuery = ref('');

// Reading history (stored in localStorage for now)
const historyIds = ref<string[]>([]);

onMounted(() => {
  // Load history from localStorage
  const saved = localStorage.getItem('readingHistory');
  if (saved) {
    try {
      historyIds.value = JSON.parse(saved);
    } catch (e) {
      console.error('Error loading history:', e);
    }
  }
});

// Filter news to show only history
const historyNews = computed(() => {
  return newsStore.newsList.filter(news => 
    historyIds.value.includes(String(news.id))
  ).reverse(); // Most recent first
});

const handleLogout = () => {
  if (authStore.isAuthenticated) {
    authStore.logout();
    router.push('/');
  }
};

const navigateTo = (route: string) => {
  router.push(route);
};

const clearHistory = () => {
  if (confirm('Are you sure you want to clear all history?')) {
    historyIds.value = [];
    localStorage.removeItem('readingHistory');
  }
};
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4] flex">
    <!-- Left Sidebar -->
    <aside class="w-64 bg-white shadow-lg fixed left-0 top-0 h-screen z-40">
      <div class="p-6">
        <router-link to="/" class="block mb-8">
          <img :src="logoWide" alt="NassNews Logo" class="h-10 w-auto" />
        </router-link>

        <nav class="space-y-2">
          <button
            @click="navigateTo('/feed')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Home :size="20" />
            <span class="font-medium">Home</span>
          </button>

          <button
            @click="navigateTo('/favorites')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Heart :size="20" />
            <span class="font-medium">Favorite News</span>
          </button>

          <button
            @click="navigateTo('/history')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'bg-[#7A1F1F] text-white'
            ]"
          >
            <History :size="20" />
            <span class="font-medium">History</span>
          </button>

          <button
            @click="navigateTo('/favorite-cities')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <MapPin :size="20" />
            <span class="font-medium">Favorite Cities</span>
          </button>

          <button
            @click="navigateTo('/settings')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Settings :size="20" />
            <span class="font-medium">Settings</span>
          </button>
        </nav>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="flex-1 ml-64">
      <!-- Top Navbar -->
      <nav class="bg-white shadow-sm sticky top-0 z-30">
        <div class="px-6 py-4">
          <div class="flex items-center justify-between gap-4">
            <!-- Search Bar -->
            <div class="flex-1 max-w-md relative">
              <Search :size="20" class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search history..."
                class="w-full pl-12 pr-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              />
            </div>

            <!-- Right Side -->
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-2 text-gray-600">
                <User :size="20" />
                <span class="font-medium">{{ authStore.currentUser?.username || 'User' }}</span>
              </div>
              <button
                v-if="authStore.isAuthenticated"
                @click="handleLogout"
                class="flex items-center gap-2 text-[#7A1F1F] hover:text-[#6A1A1A] transition-colors"
              >
                <LogOut :size="20" />
                <span class="font-medium">Logout</span>
              </button>
              <router-link
                v-else
                to="/login"
                class="flex items-center gap-2 text-[#7A1F1F] hover:text-[#6A1A1A] transition-colors"
              >
                <span class="font-medium">Login</span>
              </router-link>
            </div>
          </div>
        </div>
      </nav>

      <!-- Main Content -->
      <main class="p-6">
        <div class="mb-6 flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 mb-2">History</h1>
            <p class="text-gray-600">Your recently viewed news</p>
          </div>
          <button
            v-if="historyNews.length > 0"
            @click="clearHistory"
            class="px-4 py-2 text-sm bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
          >
            Clear History
          </button>
        </div>

        <!-- History Section -->
        <section>
          <div v-if="historyNews.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <NewsCard v-for="news in historyNews" :key="news.id" :news="news" />
          </div>
          
          <div v-else class="bg-white rounded-3xl p-12 text-center">
            <History :size="48" class="mx-auto text-gray-300 mb-4" />
            <p class="text-gray-500 text-lg mb-2">No history</p>
            <p class="text-gray-400 text-sm">Your reading history will appear here</p>
            <router-link
              to="/feed"
              class="inline-block mt-4 px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              Browse News
            </router-link>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

