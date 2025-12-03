<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import type { News } from '../../types';
import { Heart } from 'lucide-vue-next';
import { useAuthStore } from '../../stores/auth';
import { useNewsStore } from '../../stores/news';

interface Props {
  news: News;
  showFavoriteButton?: boolean;
  isFavorited?: boolean; // Allow parent to pass favorite status
}

const props = withDefaults(defineProps<Props>(), {
  showFavoriteButton: false,
  isFavorited: undefined
});

const emit = defineEmits<{
  favorite: [newsId: string | number];
}>();

const authStore = useAuthStore();
const newsStore = useNewsStore();
const favoriteNewsIds = ref<string[]>([]);
const isFavoriteLoading = ref(false);

onMounted(async () => {
  // Ensure auth store is initialized
  authStore.checkAuth();
  
  // Load favorites from API if user is authenticated
  if (authStore.isAuthenticated && authStore.currentUser?.id) {
    try {
      const favorites = await newsStore.getFavoriteNews(String(authStore.currentUser.id));
      favoriteNewsIds.value = favorites.map(id => String(id));
      console.log('Loaded favorite news IDs:', favoriteNewsIds.value);
    } catch (e) {
      console.error('Error loading favorites:', e);
      // Fallback to localStorage
      const saved = localStorage.getItem('favoriteNews');
      if (saved) {
        try {
          favoriteNewsIds.value = JSON.parse(saved);
        } catch (parseError) {
          console.error('Error loading favorites from localStorage:', parseError);
        }
      }
    }
  } else {
    // Fallback to localStorage for non-authenticated users
    const saved = localStorage.getItem('favoriteNews');
    if (saved) {
      try {
        favoriteNewsIds.value = JSON.parse(saved);
      } catch (e) {
        console.error('Error loading favorites from localStorage:', e);
      }
    }
  }
});

const isFavorited = computed(() => {
  if (props.isFavorited !== undefined) {
    return props.isFavorited;
  }
  return favoriteNewsIds.value.includes(String(props.news.id));
});

const toggleFavorite = async (e: Event) => {
  e.stopPropagation();
  e.preventDefault();
  
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    // Fallback to localStorage for non-authenticated users
    const idStr = String(props.news.id);
    const index = favoriteNewsIds.value.indexOf(idStr);
    if (index > -1) {
      favoriteNewsIds.value.splice(index, 1);
    } else {
      favoriteNewsIds.value.push(idStr);
    }
    localStorage.setItem('favoriteNews', JSON.stringify(favoriteNewsIds.value));
    emit('favorite', props.news.id);
    return;
  }

  // Use API for authenticated users
  if (isFavoriteLoading.value) return;
  
  // Validate user ID
  if (!authStore.currentUser?.id) {
    console.error('User ID is missing');
    return;
  }
  
  isFavoriteLoading.value = true;
  const userId = String(authStore.currentUser.id);
  const newsId = props.news.id;
  const idStr = String(newsId);
  const currentlyFavorited = isFavorited.value;

  console.log('Toggling favorite - User ID:', userId, 'Currently favorited:', currentlyFavorited, 'News ID:', newsId);
  console.log('Current user object:', authStore.currentUser);

  // Optimistic update - update UI immediately using array replacement for reactivity
  if (currentlyFavorited) {
    // Remove from favorites
    favoriteNewsIds.value = favoriteNewsIds.value.filter(id => id !== idStr);
    console.log('Optimistically removed from favorites. Current list:', [...favoriteNewsIds.value]);
  } else {
    // Add to favorites
    if (!favoriteNewsIds.value.includes(idStr)) {
      favoriteNewsIds.value = [...favoriteNewsIds.value, idStr];
      console.log('Optimistically added to favorites. Current list:', [...favoriteNewsIds.value]);
    }
  }

  try {
    let success = false;
    if (currentlyFavorited) {
      console.log('Calling removeFavoriteNews API...');
      success = await newsStore.removeFavoriteNews(userId, newsId);
      console.log('removeFavoriteNews result:', success);
    } else {
      console.log('Calling addFavoriteNews API...');
      success = await newsStore.addFavoriteNews(userId, newsId);
      console.log('addFavoriteNews result:', success);
    }

    if (!success) {
      // Revert optimistic update if API call failed
      console.log('API call failed, reverting optimistic update');
      if (currentlyFavorited) {
        // Was removing, so add it back
        if (!favoriteNewsIds.value.includes(idStr)) {
          favoriteNewsIds.value = [...favoriteNewsIds.value, idStr];
        }
      } else {
        // Was adding, so remove it
        favoriteNewsIds.value = favoriteNewsIds.value.filter(id => id !== idStr);
      }
      console.error('Failed to update favorite in database');
    } else {
      // Success - keep the optimistic update (already done above)
      console.log('API call succeeded, keeping optimistic update');
      console.log('Current favorites list:', favoriteNewsIds.value);
      emit('favorite', newsId);
    }
  } catch (error) {
    // Revert optimistic update on error
    console.error('Error toggling favorite, reverting:', error);
    if (currentlyFavorited) {
      // Was removing, so add it back
      if (!favoriteNewsIds.value.includes(idStr)) {
        favoriteNewsIds.value = [...favoriteNewsIds.value, idStr];
      }
    } else {
      // Was adding, so remove it
      favoriteNewsIds.value = favoriteNewsIds.value.filter(id => id !== idStr);
    }
  } finally {
    isFavoriteLoading.value = false;
    console.log('Final favorites list:', favoriteNewsIds.value, 'Is favorited:', favoriteNewsIds.value.includes(idStr));
  }
};

const formattedDate = computed(() => {
  const date = new Date(props.news.datePublication);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
});
</script>

<template>
  <article class="bg-white rounded-3xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 cursor-pointer transform hover:-translate-y-1">
    <div class="relative h-48 overflow-hidden bg-gray-200">
      <img
        v-if="news.imageUrl"
        :src="news.imageUrl"
        :alt="news.title"
        class="w-full h-full object-cover"
      />
      <div v-if="news.category" class="absolute top-4 right-4 bg-[#7A1F1F] text-white px-3 py-1 rounded-full text-xs font-semibold">
        {{ news.category }}
      </div>
      <button
        v-if="showFavoriteButton"
        @click="toggleFavorite"
        class="absolute top-4 left-4 p-2 bg-white rounded-full shadow-lg hover:bg-gray-50 transition-colors z-20 border border-gray-200"
        :title="isFavorited ? 'Remove from favorites' : 'Add to favorites'"
        :disabled="isFavoriteLoading"
      >
        <Heart 
          :size="20" 
          :class="[
            isFavorited ? 'text-red-500 fill-red-500' : 'text-gray-700 fill-transparent',
            isFavoriteLoading ? 'opacity-50' : ''
          ]" 
          :stroke-width="isFavorited ? 2.5 : 2"
        />
      </button>
    </div>

    <div class="p-6">
      <h3 class="text-xl font-bold text-gray-900 mb-3 line-clamp-2">
        {{ news.title }}
      </h3>
      <p class="text-gray-600 text-sm mb-4 line-clamp-3">
        {{ news.summary }}
      </p>
      <div class="flex items-center justify-between text-xs text-gray-500">
        <span>{{ formattedDate }}</span>
        <span v-if="news.author">{{ news.author }}</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>


