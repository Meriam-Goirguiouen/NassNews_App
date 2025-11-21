<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useCityStore } from '../stores/city';
import { useNewsStore } from '../stores/news';
import { useEventStore } from '../stores/event';
import NewsCard from '../components/common/NewsCard.vue';
import EventCard from '../components/common/EventCard.vue';
import logoWide from '../assets/logoWide.png';
import { Home, Heart, History, Settings, MapPin, LogOut, Search, User } from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const cityStore = useCityStore();
const newsStore = useNewsStore();
const eventStore = useEventStore();

// Sidebar state
const activeMenuItem = ref('home');
const searchQuery = ref('');
const selectedCityId = ref<number | null>(null);

// Fetch data on mount
onMounted(async () => {
  // Load cities
  await cityStore.fetchCities();
  cityStore.loadSavedCity();
  
  // Set default city
  if (cityStore.currentCityId) {
    selectedCityId.value = cityStore.currentCityId;
  } else if (cityStore.cities.length > 0) {
    selectedCityId.value = cityStore.cities[0].id;
    cityStore.setCurrentCity(cityStore.cities[0].id);
  }

  // Fetch news and events for selected city
  if (selectedCityId.value) {
    await Promise.all([
      newsStore.fetchByCityId(selectedCityId.value),
      eventStore.fetchByCityId(selectedCityId.value)
    ]);
  }
});

// Watch for city changes
watch(() => selectedCityId.value, async (newCityId) => {
  if (newCityId) {
    cityStore.setCurrentCity(newCityId);
    await Promise.all([
      newsStore.fetchByCityId(newCityId),
      eventStore.fetchByCityId(newCityId)
    ]);
  }
});

// Computed properties
const todaysNews = computed(() => newsStore.getTodaysNews());
const currentCity = computed(() => cityStore.currentCity);

// Filter cities by search query
const filteredCities = computed(() => {
  if (!searchQuery.value) return cityStore.cities;
  return cityStore.cities.filter(city =>
    city.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const handleLogout = () => {
  if (authStore.isAuthenticated) {
    authStore.logout();
    router.push('/');
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
            @click="activeMenuItem = 'home'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeMenuItem === 'home' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Home :size="20" />
            <span class="font-medium">Accueil</span>
          </button>

          <button
            @click="activeMenuItem = 'favorites'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeMenuItem === 'favorites' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Heart :size="20" />
            <span class="font-medium">Actualités Favoris</span>
          </button>

          <button
            @click="activeMenuItem = 'history'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeMenuItem === 'history' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <History :size="20" />
            <span class="font-medium">Historique</span>
          </button>

          <button
            @click="activeMenuItem = 'cities'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeMenuItem === 'cities' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <MapPin :size="20" />
            <span class="font-medium">Villes Favorites</span>
          </button>

          <button
            @click="activeMenuItem = 'settings'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeMenuItem === 'settings' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Settings :size="20" />
            <span class="font-medium">Paramètres</span>
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
                placeholder="Rechercher des actualités par ville..."
                class="w-full pl-12 pr-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none transition-colors"
              />
              <!-- City Dropdown -->
              <div v-if="searchQuery && filteredCities.length > 0" class="absolute top-full left-0 right-0 mt-2 bg-white border-2 border-gray-200 rounded-xl shadow-lg max-h-60 overflow-y-auto z-50">
                <button
                  v-for="city in filteredCities"
                  :key="city.id"
                  @click="selectedCityId = city.id; searchQuery = ''; cityStore.setCurrentCity(city.id)"
                  class="w-full text-left px-4 py-3 hover:bg-[#F4EDE4] transition-colors"
                >
                  {{ city.name }}
                </button>
              </div>
            </div>

            <!-- Right Side -->
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-2 text-gray-600">
                <User :size="20" />
                <span class="font-medium">{{ authStore.currentUser?.username || 'Utilisateur' }}</span>
              </div>
              <button
                v-if="authStore.isAuthenticated"
                @click="handleLogout"
                class="flex items-center gap-2 text-[#7A1F1F] hover:text-[#6A1A1A] transition-colors"
              >
                <LogOut :size="20" />
                <span class="font-medium">Déconnexion</span>
              </button>
              <router-link
                v-else
                to="/login"
                class="flex items-center gap-2 text-[#7A1F1F] hover:text-[#6A1A1A] transition-colors"
              >
                <span class="font-medium">Connexion</span>
              </router-link>
            </div>
          </div>
        </div>
      </nav>

      <!-- Main Content -->
      <main class="p-6">
        <!-- User Location -->
        <div v-if="currentCity" class="mb-6 bg-white rounded-3xl p-6 shadow-md">
          <div class="flex items-center gap-3">
            <MapPin :size="24" class="text-[#7A1F1F]" />
            <div>
              <p class="text-sm text-gray-500">Votre localisation</p>
              <p class="text-xl font-bold text-gray-900">{{ currentCity.name }}, {{ currentCity.region }}</p>
            </div>
          </div>
        </div>

        <!-- Today's News Section -->
        <section class="mb-8">
          <h2 class="text-3xl font-bold text-gray-900 mb-6">Actualités du Jour</h2>
          
          <div v-if="newsStore.loading" class="text-center py-12">
            <p class="text-gray-500">Chargement des actualités...</p>
          </div>
          
          <div v-else-if="newsStore.error" class="bg-red-50 text-red-600 p-4 rounded-xl">
            {{ newsStore.error }}
          </div>
          
          <div v-else-if="todaysNews.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <NewsCard v-for="news in todaysNews" :key="news.id" :news="news" />
          </div>
          
          <div v-else class="bg-white rounded-3xl p-12 text-center">
            <p class="text-gray-500">Aucune actualité aujourd'hui pour {{ currentCity?.name }}</p>
          </div>
        </section>

        <!-- Events Section -->
        <section>
          <h2 class="text-3xl font-bold text-gray-900 mb-6">Événements à Venir</h2>
          
          <div v-if="eventStore.loading" class="text-center py-12">
            <p class="text-gray-500">Chargement des événements...</p>
          </div>
          
          <div v-else-if="eventStore.error" class="bg-red-50 text-red-600 p-4 rounded-xl">
            {{ eventStore.error }}
          </div>
          
          <div v-else-if="eventStore.eventsList.length > 0" class="grid md:grid-cols-2 gap-6">
            <EventCard v-for="event in eventStore.eventsList" :key="event.id" :event="event" />
          </div>
          
          <div v-else class="bg-white rounded-3xl p-12 text-center">
            <p class="text-gray-500">Aucun événement à venir pour {{ currentCity?.name }}</p>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>
