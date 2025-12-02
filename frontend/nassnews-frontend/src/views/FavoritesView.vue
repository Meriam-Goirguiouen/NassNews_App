<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useNewsStore } from '../stores/news';
import { useEventStore } from '../stores/event';
import NewsCard from '../components/common/NewsCard.vue';
import EventCard from '../components/common/EventCard.vue';
import logoWide from '../assets/logoWide.png';
import { Home, Heart, History, Settings, MapPin, LogOut, Search, User, Calendar } from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const newsStore = useNewsStore();
const eventStore = useEventStore();

// Sidebar state
const activeMenuItem = ref('favorites');
const searchQuery = ref('');
const activeTab = ref<'news' | 'events'>('news');

// Favorite news and events
const favoriteNewsIds = ref<string[]>([]);
const favoriteEventIds = ref<string[]>([]);
const isLoadingFavorites = ref(false);

onMounted(async () => {
  isLoadingFavorites.value = true;
  
  // Fetch all news and events from database
  await Promise.all([
    newsStore.fetchAllNews(),
    eventStore.fetchAllEvents()
  ]);
  
  // Load favorites from API if user is authenticated
  if (authStore.isAuthenticated && authStore.currentUser?.id) {
    try {
      const userId = String(authStore.currentUser.id);
      const [newsFavorites, eventFavorites] = await Promise.all([
        newsStore.getFavoriteNews(userId),
        eventStore.getFavoriteEvents(userId)
      ]);
      favoriteNewsIds.value = newsFavorites.map(id => String(id));
      favoriteEventIds.value = eventFavorites.map(id => String(id));
    } catch (e) {
      console.error('Error loading favorites:', e);
      // Fallback to localStorage
      loadFromLocalStorage();
    }
  } else {
    // Fallback to localStorage for non-authenticated users
    loadFromLocalStorage();
  }
  
  isLoadingFavorites.value = false;
});

function loadFromLocalStorage() {
  const savedNews = localStorage.getItem('favoriteNews');
  const savedEvents = localStorage.getItem('favoriteEvents');
  
  if (savedNews) {
    try {
      favoriteNewsIds.value = JSON.parse(savedNews);
    } catch (e) {
      console.error('Error loading favorites from localStorage:', e);
    }
  }
  
  if (savedEvents) {
    try {
      favoriteEventIds.value = JSON.parse(savedEvents);
    } catch (e) {
      console.error('Error loading favorite events from localStorage:', e);
    }
  }
}

// Filter news to show only favorites
const favoriteNews = computed(() => {
  let filtered = newsStore.newsList.filter(news => 
    favoriteNewsIds.value.includes(String(news.id))
  );
  
  // Apply search filter if query exists
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(news =>
      news.title.toLowerCase().includes(query) ||
      news.summary.toLowerCase().includes(query) ||
      (news.category && news.category.toLowerCase().includes(query))
    );
  }
  
  return filtered;
});

// Filter events to show only favorites
const favoriteEvents = computed(() => {
  let filtered = eventStore.eventsList.filter(event => 
    favoriteEventIds.value.includes(String(event.id))
  );
  
  // Apply search filter if query exists
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(event =>
      event.title.toLowerCase().includes(query) ||
      (event.description && event.description.toLowerCase().includes(query)) ||
      (event.type && event.type.toLowerCase().includes(query))
    );
  }
  
  return filtered;
});

// Toggle favorite news status
const toggleFavoriteNews = async (newsId: string | number) => {
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    // Fallback to localStorage
    const idStr = String(newsId);
    const index = favoriteNewsIds.value.indexOf(idStr);
    if (index > -1) {
      favoriteNewsIds.value.splice(index, 1);
    } else {
      favoriteNewsIds.value.push(idStr);
    }
    localStorage.setItem('favoriteNews', JSON.stringify(favoriteNewsIds.value));
    return;
  }

  const userId = String(authStore.currentUser.id);
  const isFavorited = favoriteNewsIds.value.includes(String(newsId));
  
  try {
    let success = false;
    if (isFavorited) {
      success = await newsStore.removeFavoriteNews(userId, newsId);
    } else {
      success = await newsStore.addFavoriteNews(userId, newsId);
    }
    
    if (success) {
      const idStr = String(newsId);
      const index = favoriteNewsIds.value.indexOf(idStr);
      if (isFavorited) {
        if (index > -1) favoriteNewsIds.value.splice(index, 1);
      } else {
        if (index === -1) favoriteNewsIds.value.push(idStr);
      }
    }
  } catch (error) {
    console.error('Error toggling favorite news:', error);
  }
};

// Toggle favorite event status
const toggleFavoriteEvent = async (eventId: string | number) => {
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    // Fallback to localStorage
    const idStr = String(eventId);
    const index = favoriteEventIds.value.indexOf(idStr);
    if (index > -1) {
      favoriteEventIds.value.splice(index, 1);
    } else {
      favoriteEventIds.value.push(idStr);
    }
    localStorage.setItem('favoriteEvents', JSON.stringify(favoriteEventIds.value));
    return;
  }

  const userId = String(authStore.currentUser.id);
  const isFavorited = favoriteEventIds.value.includes(String(eventId));
  
  try {
    let success = false;
    if (isFavorited) {
      success = await eventStore.removeFavoriteEvent(userId, eventId);
    } else {
      success = await eventStore.addFavoriteEvent(userId, eventId);
    }
    
    if (success) {
      const idStr = String(eventId);
      const index = favoriteEventIds.value.indexOf(idStr);
      if (isFavorited) {
        if (index > -1) favoriteEventIds.value.splice(index, 1);
      } else {
        if (index === -1) favoriteEventIds.value.push(idStr);
      }
    }
  } catch (error) {
    console.error('Error toggling favorite event:', error);
  }
};

// Check if news is favorited
const isNewsFavorited = (newsId: string | number) => {
  return favoriteNewsIds.value.includes(String(newsId));
};

// Check if event is favorited
const isEventFavorited = (eventId: string | number) => {
  return favoriteEventIds.value.includes(String(eventId));
};

const handleLogout = () => {
  if (authStore.isAuthenticated) {
    authStore.logout();
    router.push('/');
  }
};

const navigateTo = (route: string) => {
  // Check authentication before navigating to protected routes
  if (route === '/feed') {
    authStore.checkAuth();
    if (!authStore.isAuthenticated || !authStore.token) {
      router.push({ path: '/login', query: { redirect: '/feed' } });
      return;
    }
  }
  router.push(route);
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
              'bg-[#7A1F1F] text-white'
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
                placeholder="Search your favorites..."
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
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Favorites</h1>
          <p class="text-gray-600">Your saved news articles and events</p>
        </div>

        <!-- Tabs -->
        <div class="mb-6 flex gap-4 border-b border-gray-200">
          <button
            @click="activeTab = 'news'"
            :class="[
              'px-6 py-3 font-medium transition-colors border-b-2',
              activeTab === 'news' 
                ? 'text-[#7A1F1F] border-[#7A1F1F]' 
                : 'text-gray-500 border-transparent hover:text-gray-700'
            ]"
          >
            <div class="flex items-center gap-2">
              <Heart :size="18" />
              <span>News ({{ favoriteNews.length }})</span>
            </div>
          </button>
          <button
            @click="activeTab = 'events'"
            :class="[
              'px-6 py-3 font-medium transition-colors border-b-2',
              activeTab === 'events' 
                ? 'text-[#7A1F1F] border-[#7A1F1F]' 
                : 'text-gray-500 border-transparent hover:text-gray-700'
            ]"
          >
            <div class="flex items-center gap-2">
              <Calendar :size="18" />
              <span>Events ({{ favoriteEvents.length }})</span>
            </div>
          </button>
        </div>

        <!-- Favorites Section -->
        <section>
          <div v-if="isLoadingFavorites || newsStore.loading" class="text-center py-12">
            <p class="text-gray-500">Loading favorites...</p>
          </div>
          
          <div v-else-if="newsStore.error" class="bg-red-50 text-red-600 p-4 rounded-xl">
            {{ newsStore.error }}
          </div>
          
          <!-- News Tab -->
          <div v-else-if="activeTab === 'news'">
            <div v-if="favoriteNews.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="news in favoriteNews" :key="news.id" class="relative">
                <NewsCard :news="news" :show-favorite-button="true" :is-favorited="isNewsFavorited(news.id)" />
              </div>
            </div>
            
            <div v-else class="bg-white rounded-3xl p-12 text-center">
              <Heart :size="48" class="mx-auto text-gray-300 mb-4" />
              <p class="text-gray-500 text-lg mb-2">No favorite news</p>
              <p class="text-gray-400 text-sm">Start adding news articles to your favorites</p>
              <button
                @click="navigateTo('/feed')"
                class="inline-block mt-4 px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
              >
                Browse News
              </button>
            </div>
          </div>

          <!-- Events Tab -->
          <div v-else-if="activeTab === 'events'">
            <div v-if="favoriteEvents.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="event in favoriteEvents" :key="event.id" class="relative">
                <EventCard :event="event" />
                <button
                  @click="toggleFavoriteEvent(event.id)"
                  class="absolute top-4 right-4 p-2 bg-white rounded-full shadow-lg hover:bg-gray-50 transition-colors z-10"
                  :title="isEventFavorited(event.id) ? 'Remove from favorites' : 'Add to favorites'"
                >
                  <Heart :size="20" :class="isEventFavorited(event.id) ? 'text-red-500 fill-red-500' : 'text-gray-400'" />
                </button>
              </div>
            </div>
            
            <div v-else class="bg-white rounded-3xl p-12 text-center">
              <Calendar :size="48" class="mx-auto text-gray-300 mb-4" />
              <p class="text-gray-500 text-lg mb-2">No favorite events</p>
              <p class="text-gray-400 text-sm">Start adding events to your favorites</p>
              <button
                @click="navigateTo('/feed')"
                class="inline-block mt-4 px-6 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
              >
                Browse Events
              </button>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

