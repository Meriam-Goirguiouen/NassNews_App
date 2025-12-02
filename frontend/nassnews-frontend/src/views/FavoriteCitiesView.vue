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
const searchQuery = ref('');
const showAllCities = ref(false);

// Favorite cities (stored in localStorage for now)
const favoriteCityIds = ref<string[]>([]);

onMounted(async () => {
  // Load cities from database
  await cityStore.fetchCities();
  
  // Load favorites from API if user is authenticated
  if (authStore.isAuthenticated && authStore.currentUser?.id) {
    try {
      const userId = String(authStore.currentUser.id);
      const favorites = await cityStore.getFavoriteCities(userId);
      // Filter out null values and "null" strings
      favoriteCityIds.value = favorites.filter(id => id && id !== 'null' && id.trim() !== '');
      console.log('Loaded favorite city IDs from API:', favoriteCityIds.value);
    } catch (e) {
      console.error('Error loading favorite cities from API:', e);
      // Fallback to localStorage
      loadFromLocalStorage();
    }
  } else {
    // Fallback to localStorage for non-authenticated users
    loadFromLocalStorage();
  }
});

function loadFromLocalStorage() {
  const saved = localStorage.getItem('favoriteCities');
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      // Ensure it's an array and contains only strings, filter out null values
      if (Array.isArray(parsed)) {
        favoriteCityIds.value = parsed
          .map(id => String(id))
          .filter(id => id && id !== 'null' && id.trim() !== '');
        console.log('Loaded favorite city IDs from localStorage:', favoriteCityIds.value);
      } else {
        console.warn('Invalid favorite cities format, resetting');
        favoriteCityIds.value = [];
      }
    } catch (e) {
      console.error('Error loading favorite cities:', e);
      favoriteCityIds.value = [];
    }
  } else {
    favoriteCityIds.value = [];
  }
}

// Filter cities to show only favorites
const favoriteCities = computed(() => {
  const favoriteIds = favoriteCityIds.value.map(id => String(id));
  const filtered = cityStore.cities.filter(city => {
    const cityIdStr = getCityId(city);
    if (!cityIdStr) return false;
    const isFavorite = favoriteIds.includes(cityIdStr);
    return isFavorite;
  });
  return filtered;
});

// Filtered favorite cities by search
const filteredFavoriteCities = computed(() => {
  let filtered = favoriteCities.value;
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(city =>
      city.name.toLowerCase().includes(query) ||
      city.region.toLowerCase().includes(query)
    );
  }
  return filtered;
});

// All cities filtered by search (for browsing)
const filteredAllCities = computed(() => {
  let filtered = cityStore.cities;
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(city =>
      city.name.toLowerCase().includes(query) ||
      city.region.toLowerCase().includes(query)
    );
  }
  return filtered;
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

// Helper function to get city ID (handles both id and _id)
const getCityId = (city: any): string => {
  if (!city) {
    console.error('City is null or undefined');
    return '';
  }
  const id = city.id || (city as any)._id;
  if (!id) {
    console.error('City missing ID:', city);
    return '';
  }
  return String(id);
};

const toggleFavorite = async (cityId: string | number | undefined) => {
  // Check if cityId is valid
  if (!cityId) {
    console.error('City ID is undefined or null. City object:', cityId);
    console.log('Available cities:', cityStore.cities.map(c => ({ id: c.id, name: c.name })));
    return;
  }
  
  // Ensure cityId is a string
  const idStr = String(cityId).trim();
  
  if (!idStr || idStr === 'undefined' || idStr === 'null' || idStr === '') {
    console.error('Invalid city ID after conversion:', cityId, '->', idStr);
    return;
  }
  
  // Check if user is authenticated
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    console.warn('User not authenticated, using localStorage only');
    // Fallback to localStorage for non-authenticated users
    const currentFavorites = [...favoriteCityIds.value];
    const index = currentFavorites.indexOf(idStr);
    
    if (index > -1) {
      currentFavorites.splice(index, 1);
    } else {
      currentFavorites.push(idStr);
    }
    
    favoriteCityIds.value = currentFavorites.filter(id => id && id !== 'null' && id.trim() !== '');
    localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
    return;
  }
  
  const userId = String(authStore.currentUser.id);
  const isCurrentlyFavorite = favoriteCityIds.value.includes(idStr);
  
  // Optimistic UI update
  const previousFavorites = [...favoriteCityIds.value];
  
  if (isCurrentlyFavorite) {
    // Optimistically remove
    favoriteCityIds.value = previousFavorites.filter(id => id !== idStr);
  } else {
    // Optimistically add
    favoriteCityIds.value = [...previousFavorites, idStr].filter(id => id && id !== 'null' && id.trim() !== '');
  }
  
  try {
    let success = false;
    if (isCurrentlyFavorite) {
      success = await cityStore.removeFavoriteCity(userId, idStr);
    } else {
      success = await cityStore.addFavoriteCity(userId, idStr);
    }
    
    if (!success) {
      // Revert optimistic update on failure
      favoriteCityIds.value = previousFavorites;
      console.error('Failed to update favorite city in database');
    } else {
      // Sync with backend to get the cleaned list (without nulls)
      const updatedFavorites = await cityStore.getFavoriteCities(userId);
      favoriteCityIds.value = updatedFavorites.filter(id => id && id !== 'null' && id.trim() !== '');
      // Also update localStorage as backup
      localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
    }
  } catch (error) {
    // Revert optimistic update on error
    favoriteCityIds.value = previousFavorites;
    console.error('Error toggling favorite city:', error);
  }
};

const removeFavorite = async (cityId: string, event?: Event) => {
  if (event) {
    event.stopPropagation();
    event.preventDefault();
  }
  
  const idStr = String(cityId);
  
  // Check if user is authenticated
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    // Fallback to localStorage for non-authenticated users
    const index = favoriteCityIds.value.indexOf(idStr);
    if (index > -1) {
      favoriteCityIds.value.splice(index, 1);
      localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
    }
    return;
  }
  
  const userId = String(authStore.currentUser.id);
  
  // Optimistic UI update
  const previousFavorites = [...favoriteCityIds.value];
  favoriteCityIds.value = previousFavorites.filter(id => id !== idStr);
  
  try {
    const success = await cityStore.removeFavoriteCity(userId, idStr);
    
    if (!success) {
      // Revert optimistic update on failure
      favoriteCityIds.value = previousFavorites;
      console.error('Failed to remove favorite city from database');
    } else {
      // Sync with backend to get the cleaned list
      const updatedFavorites = await cityStore.getFavoriteCities(userId);
      favoriteCityIds.value = updatedFavorites.filter(id => id && id !== 'null' && id.trim() !== '');
      localStorage.setItem('favoriteCities', JSON.stringify(favoriteCityIds.value));
    }
  } catch (error) {
    // Revert optimistic update on error
    favoriteCityIds.value = previousFavorites;
    console.error('Error removing favorite city:', error);
  }
};

// Check if city is favorited
const isFavorited = (cityId: string | number | undefined) => {
  if (!cityId) return false;
  const idStr = String(cityId);
  return favoriteCityIds.value.includes(idStr);
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

        <!-- All Cities Section (when browsing) -->
        <section v-if="showAllCities" class="mb-8">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-2xl font-bold text-gray-900">All Cities</h2>
            <button
              @click="showAllCities = false"
              class="px-4 py-2 text-sm bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Show Favorites Only
            </button>
          </div>
          <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="city in filteredAllCities"
              :key="`all-city-${getCityId(city)}`"
              class="bg-white rounded-3xl p-6 shadow-md hover:shadow-lg transition-shadow relative"
            >
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3 flex-1">
                  <MapPin :size="24" class="text-[#7A1F1F]" />
                  <div>
                    <h3 class="text-xl font-bold text-gray-900">{{ city.name }}</h3>
                    <p class="text-sm text-gray-500">{{ city.region }}</p>
                  </div>
                </div>
                <button
                  type="button"
                  @click.stop="toggleFavorite(getCityId(city))"
                  class="p-2 rounded-lg transition-colors z-20 relative flex-shrink-0 cursor-pointer pointer-events-auto"
                  :class="isFavorited(getCityId(city)) 
                    ? 'text-red-500 hover:bg-red-50' 
                    : 'text-gray-400 hover:bg-gray-50'"
                  :title="isFavorited(getCityId(city)) ? 'Remove from favorites' : 'Add to favorites'"
                >
                  <Star :size="20" :class="isFavorited(getCityId(city)) ? 'fill-yellow-500 text-yellow-500' : ''" />
                </button>
              </div>
              <div v-if="city.population" class="text-sm text-gray-600">
                Population: {{ city.population.toLocaleString() }}
              </div>
            </div>
          </div>
        </section>

        <!-- Favorite Cities Section -->
        <section v-if="!showAllCities">
          <div class="flex items-center justify-between mb-4">
            <h2 v-if="filteredFavoriteCities.length > 0" class="text-2xl font-bold text-gray-900">Your Favorite Cities</h2>
            <button
              @click="showAllCities = true"
              class="px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              Browse Cities
            </button>
          </div>
          <div v-if="filteredFavoriteCities.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="city in filteredFavoriteCities"
              :key="`fav-city-${city.id}`"
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
                  type="button"
                  @click.stop.prevent="removeFavorite(String(city.id), $event)"
                  class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors z-10 relative"
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
            <button
              @click="showAllCities = true"
              class="inline-block px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              Browse Cities
            </button>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

