<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { useCityStore } from '../stores/city';
import logoWide from '../assets/logoWide.png';
import { Home, Heart, History, Settings, MapPin, LogOut, Search, User, Bell, Globe, Moon, Sun } from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const cityStore = useCityStore();

// Sidebar state
const activeMenuItem = ref('settings');

// Settings state
const notificationsEnabled = ref(true);
const autoDetectLocation = ref(true);
const theme = ref<'light' | 'dark'>('light');
const language = ref('fr');

onMounted(() => {
  // Load settings from localStorage
  const savedNotifications = localStorage.getItem('notificationsEnabled');
  if (savedNotifications !== null) {
    notificationsEnabled.value = JSON.parse(savedNotifications);
  }
  
  const savedAutoDetect = localStorage.getItem('autoDetectLocation');
  if (savedAutoDetect !== null) {
    autoDetectLocation.value = JSON.parse(savedAutoDetect);
  }
  
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    theme.value = savedTheme as 'light' | 'dark';
  }
  
  const savedLanguage = localStorage.getItem('language');
  if (savedLanguage) {
    language.value = savedLanguage;
  }
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

const saveSettings = () => {
  localStorage.setItem('notificationsEnabled', JSON.stringify(notificationsEnabled.value));
  localStorage.setItem('autoDetectLocation', JSON.stringify(autoDetectLocation.value));
  localStorage.setItem('theme', theme.value);
  localStorage.setItem('language', language.value);
  alert('Settings saved successfully!');
};

const clearAllData = () => {
  if (confirm('Are you sure you want to clear all local data? This action is irreversible.')) {
    localStorage.removeItem('favoriteNews');
    localStorage.removeItem('readingHistory');
    localStorage.removeItem('favoriteCities');
    localStorage.removeItem('currentCityId');
    alert('All local data has been cleared.');
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
              'bg-[#7A1F1F] text-white'
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
            <!-- Right Side -->
            <div class="flex items-center gap-4 ml-auto">
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
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Settings</h1>
          <p class="text-gray-600">Manage your preferences and application settings</p>
        </div>

        <!-- Settings Sections -->
        <div class="space-y-6">
          <!-- Notifications -->
          <section class="bg-white rounded-3xl p-6 shadow-md">
            <div class="flex items-center gap-3 mb-4">
              <Bell :size="24" class="text-[#7A1F1F]" />
              <h2 class="text-xl font-bold text-gray-900">Notifications</h2>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium text-gray-900">Enable notifications</p>
                <p class="text-sm text-gray-500">Receive notifications for new news articles</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  v-model="notificationsEnabled"
                  type="checkbox"
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-[#7A1F1F]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#7A1F1F]"></div>
              </label>
            </div>
          </section>

          <!-- Location -->
          <section class="bg-white rounded-3xl p-6 shadow-md">
            <div class="flex items-center gap-3 mb-4">
              <MapPin :size="24" class="text-[#7A1F1F]" />
              <h2 class="text-xl font-bold text-gray-900">Localisation</h2>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium text-gray-900">Auto-detect location</p>
                <p class="text-sm text-gray-500">Automatically detect your city on startup</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  v-model="autoDetectLocation"
                  type="checkbox"
                  class="sr-only peer"
                />
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-[#7A1F1F]/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#7A1F1F]"></div>
              </label>
            </div>
          </section>

          <!-- Appearance -->
          <section class="bg-white rounded-3xl p-6 shadow-md">
            <div class="flex items-center gap-3 mb-4">
              <Sun :size="24" class="text-[#7A1F1F]" />
              <h2 class="text-xl font-bold text-gray-900">Apparence</h2>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Theme</label>
                <div class="flex gap-4">
                  <button
                    @click="theme = 'light'"
                    :class="[
                      'flex items-center gap-2 px-4 py-2 rounded-lg transition-colors',
                      theme === 'light' 
                        ? 'bg-[#7A1F1F] text-white' 
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    ]"
                  >
                    <Sun :size="18" />
                    <span>Light</span>
                  </button>
                  <button
                    @click="theme = 'dark'"
                    :class="[
                      'flex items-center gap-2 px-4 py-2 rounded-lg transition-colors',
                      theme === 'dark' 
                        ? 'bg-[#7A1F1F] text-white' 
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    ]"
                  >
                    <Moon :size="18" />
                    <span>Dark</span>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <!-- Language -->
          <section class="bg-white rounded-3xl p-6 shadow-md">
            <div class="flex items-center gap-3 mb-4">
              <Globe :size="24" class="text-[#7A1F1F]" />
              <h2 class="text-xl font-bold text-gray-900">Langue</h2>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Select language</label>
              <select
                v-model="language"
                class="w-full px-4 py-2 border-2 border-gray-200 rounded-lg focus:border-[#7A1F1F] focus:outline-none"
              >
                <option value="fr">Français</option>
                <option value="ar">العربية</option>
                <option value="en">English</option>
              </select>
            </div>
          </section>

          <!-- Account Info -->
          <section v-if="authStore.isAuthenticated" class="bg-white rounded-3xl p-6 shadow-md">
            <div class="flex items-center gap-3 mb-4">
              <User :size="24" class="text-[#7A1F1F]" />
              <h2 class="text-xl font-bold text-gray-900">Account</h2>
            </div>
            <div class="space-y-3">
              <div>
                <p class="text-sm text-gray-500">Username</p>
                <p class="font-medium text-gray-900">{{ authStore.currentUser?.username || 'N/A' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">Email</p>
                <p class="font-medium text-gray-900">{{ authStore.currentUser?.email || 'N/A' }}</p>
              </div>
            </div>
          </section>

          <!-- Danger Zone -->
          <section class="bg-white rounded-3xl p-6 shadow-md border-2 border-red-200">
            <h2 class="text-xl font-bold text-red-600 mb-4">Danger Zone</h2>
            <div class="space-y-4">
              <div>
                <p class="font-medium text-gray-900 mb-2">Clear all local data</p>
                <p class="text-sm text-gray-500 mb-3">Deletes all your favorites, history, and locally stored preferences</p>
                <button
                  @click="clearAllData"
                  class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                  Clear Data
                </button>
              </div>
            </div>
          </section>

          <!-- Save Button -->
          <div class="flex justify-end">
            <button
              @click="saveSettings"
              class="px-6 py-3 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors font-medium"
            >
              Save Settings
            </button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

