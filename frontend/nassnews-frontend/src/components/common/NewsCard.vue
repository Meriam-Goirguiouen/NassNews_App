<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import type { News } from '../../types';
import { Heart } from 'lucide-vue-next';

interface Props {
  news: News;
  showFavoriteButton?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showFavoriteButton: false
});

const emit = defineEmits<{
  favorite: [newsId: string | number];
}>();

const favoriteNewsIds = ref<string[]>([]);

onMounted(() => {
  // Load favorites from localStorage
  const saved = localStorage.getItem('favoriteNews');
  if (saved) {
    try {
      favoriteNewsIds.value = JSON.parse(saved);
    } catch (e) {
      console.error('Error loading favorites:', e);
    }
  }
});

const isFavorited = computed(() => {
  return favoriteNewsIds.value.includes(String(props.news.id));
});

const toggleFavorite = (e: Event) => {
  e.stopPropagation();
  const idStr = String(props.news.id);
  const index = favoriteNewsIds.value.indexOf(idStr);
  if (index > -1) {
    favoriteNewsIds.value.splice(index, 1);
  } else {
    favoriteNewsIds.value.push(idStr);
  }
  localStorage.setItem('favoriteNews', JSON.stringify(favoriteNewsIds.value));
  emit('favorite', props.news.id);
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
        class="absolute top-4 left-4 p-2 bg-white rounded-full shadow-lg hover:bg-gray-50 transition-colors z-10"
        :title="isFavorited ? 'Remove from favorites' : 'Add to favorites'"
      >
        <Heart :size="20" :class="isFavorited ? 'text-red-500 fill-red-500' : 'text-gray-400'" />
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

