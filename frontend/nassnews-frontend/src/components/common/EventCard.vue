<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import type { Event } from '../../types';
import { Heart } from 'lucide-vue-next';
import { useAuthStore } from '../../stores/auth';
import { useEventStore } from '../../stores/event';

interface Props {
  event: Event;
  showFavoriteButton?: boolean;
  isFavorited?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showFavoriteButton: false,
  isFavorited: undefined
});

const emit = defineEmits<{
  favorite: [eventId: string | number];
}>();

const authStore = useAuthStore();
const eventStore = useEventStore();
const favoriteEventIds = ref<string[]>([]);
const isFavoriteLoading = ref(false);

onMounted(async () => {
  // Load favorites from API if user is authenticated
  if (authStore.isAuthenticated && authStore.currentUser?.id) {
    try {
      const favorites = await eventStore.getFavoriteEvents(String(authStore.currentUser.id));
      favoriteEventIds.value = favorites.map(id => String(id));
    } catch (e) {
      console.error('Error loading favorite events:', e);
    }
  } else {
    // Fallback to localStorage for non-authenticated users
    const saved = localStorage.getItem('favoriteEvents');
    if (saved) {
      try {
        favoriteEventIds.value = JSON.parse(saved);
      } catch (e) {
        console.error('Error loading favorite events from localStorage:', e);
      }
    }
  }
});

const isFavorited = computed(() => {
  if (props.isFavorited !== undefined) {
    return props.isFavorited;
  }
  return favoriteEventIds.value.includes(String(props.event.id));
});

const toggleFavorite = async (e: Event) => {
  e.stopPropagation();
  
  if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
    // Fallback to localStorage for non-authenticated users
    const idStr = String(props.event.id);
    const index = favoriteEventIds.value.indexOf(idStr);
    if (index > -1) {
      favoriteEventIds.value.splice(index, 1);
    } else {
      favoriteEventIds.value.push(idStr);
    }
    localStorage.setItem('favoriteEvents', JSON.stringify(favoriteEventIds.value));
    emit('favorite', props.event.id);
    return;
  }

  // Use API for authenticated users
  if (isFavoriteLoading.value) return;
  
  isFavoriteLoading.value = true;
  const userId = String(authStore.currentUser.id);
  const eventId = props.event.id;
  const currentlyFavorited = isFavorited.value;

  try {
    let success = false;
    if (currentlyFavorited) {
      success = await eventStore.removeFavoriteEvent(userId, eventId);
    } else {
      success = await eventStore.addFavoriteEvent(userId, eventId);
    }

    if (success) {
      // Update local state
      const idStr = String(eventId);
      const index = favoriteEventIds.value.indexOf(idStr);
      if (currentlyFavorited) {
        if (index > -1) {
          favoriteEventIds.value.splice(index, 1);
        }
      } else {
        if (index === -1) {
          favoriteEventIds.value.push(idStr);
        }
      }
      emit('favorite', eventId);
    }
  } catch (error) {
    console.error('Error toggling favorite event:', error);
  } finally {
    isFavoriteLoading.value = false;
  }
};

const formattedDate = computed(() => {
  const date = new Date(props.event.date);
  return {
    day: date.getDate().toString().padStart(2, '0'),
    month: date.toLocaleDateString('fr-MA', { month: 'short' }).toUpperCase(),
  };
});
</script>

<template>
  <article class="bg-white rounded-3xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 flex cursor-pointer transform hover:-translate-y-1 relative">
    <button
      v-if="showFavoriteButton"
      @click="toggleFavorite"
      class="absolute top-4 right-4 p-2 bg-white rounded-full shadow-lg hover:bg-gray-50 transition-colors z-10"
      :title="isFavorited ? 'Remove from favorites' : 'Add to favorites'"
    >
      <Heart :size="20" :class="isFavorited ? 'text-red-500 fill-red-500' : 'text-gray-400'" />
    </button>
    <div class="bg-[#7A1F1F] text-white flex flex-col items-center justify-center p-6 min-w-[100px]">
      <div class="text-3xl font-bold">{{ formattedDate.day }}</div>
      <div class="text-sm font-semibold">{{ formattedDate.month }}</div>
    </div>

    <div class="flex-1 p-6">
      <div class="inline-block bg-[#F4EDE4] text-[#7A1F1F] px-3 py-1 rounded-full text-xs font-semibold mb-3">
        {{ event.type }}
      </div>
      <h3 class="text-lg font-bold text-gray-900 mb-2 line-clamp-2">
        {{ event.title }}
      </h3>
      <p v-if="event.description" class="text-gray-600 text-sm mb-3 line-clamp-2">
        {{ event.description }}
      </p>
      <div class="space-y-1 text-xs text-gray-500">
        <div>📍 {{ event.location }}</div>
        <div v-if="event.time">🕐 {{ event.time }}</div>
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
</style>

