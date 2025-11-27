<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useCityStore } from '../stores/city';
import logoWide from '../assets/logoWide.png';
import { Home, Heart, History, Settings, MapPin, LogOut, Search, User, Star, Trash2 } from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const cityStore = useCityStore();

// Sidebar state
const activeMenuItem = ref('cities');
const searchQuery = ref('');

// Favorite cities (stored in localStorage for now)
const favoriteCityIds = ref<string[]>([]);

onMounted(async () => {
  // Load cities
  await cityStore.fetchCities();
  
  // Load favorites from localStorage
  const saved = localStorage.getItem('favoriteCities');
  if (saved) {
    try {
      favoriteCityIds.value = JSON.parse(saved);
    } catch (e) {
      console.error('Error loading favorite cities:', e);
    }
  }
});

// Filter cities to show only favorites
const favoriteCities = computed(() => {
  return cityStore.cities.filter(city => 
    favoriteCityIds.value.includes(city.id)
  );
});

// Filtered cities by search
const filteredFavoriteCities = computed(() => {
  if (!searchQuery.value) return favoriteCities.value;
  return favoriteCities.value.filter(city =>
    city.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
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

const toggleFavorite = (cityId: string) => {
  const index = favoriteCityIds.value.indexOf(cityId);
  if (index > -1) {
    favoriteCityIds.value.splice(index, 1);
  } else {
    favoriteCityIds.value.push(cityId);
  }
  localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
};

const removeFavorite = (cityId: string) => {
  const index = favoriteCityIds.value.indexOf(cityId);
  if (index > -1) {
    favoriteCityIds.value.splice(index, 1);
    localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
  }
};

const selectCity = (cityId: string) => {
  cityStore.setCurrentCity(cityId);
  router.push('/feed');
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
              'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <History :size="20" />
            <span class="font-medium">History</span>
          </button>

          <button
            @click="navigateTo('/favorite-cities')"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              'bg-[#7A1F1F] text-white'
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
                placeholder="Search favorite cities..."
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
        <div class="mb-6">
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Favorite Cities</h1>
          <p class="text-gray-600">Manage your favorite cities for quick access</p>
        </div>

        <!-- Favorite Cities Section -->
        <section>
          <div v-if="filteredFavoriteCities.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="city in filteredFavoriteCities"
              :key="city.id"
              class="bg-white rounded-3xl p-6 shadow-md hover:shadow-lg transition-shadow cursor-pointer"
              @click="selectCity(city.id)"
            >
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3">
                  <MapPin :size="24" class="text-[#7A1F1F]" />
                  <div>
                    <h3 class="text-xl font-bold text-gray-900">{{ city.name }}</h3>
                    <p class="text-sm text-gray-500">{{ city.region }}</p>
                  </div>
                </div>
                <button
                  @click.stop="removeFavorite(city.id)"
                  class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                  title="Remove from favorites"
                >
                  <Trash2 :size="18" />
                </button>
              </div>
              <div class="flex items-center gap-2 text-sm text-gray-600">
                <Star :size="16" class="text-yellow-500 fill-yellow-500" />
                <span>Favorite city</span>
              </div>
            </div>
          </div>
          
          <div v-else class="bg-white rounded-3xl p-12 text-center">
            <MapPin :size="48" class="mx-auto text-gray-300 mb-4" />
            <p class="text-gray-500 text-lg mb-2">No favorite cities</p>
            <p class="text-gray-400 text-sm mb-4">Add cities to your favorites for quick access</p>
            <router-link
              to="/feed"
              class="inline-block px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              Browse Cities
            </router-link>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

