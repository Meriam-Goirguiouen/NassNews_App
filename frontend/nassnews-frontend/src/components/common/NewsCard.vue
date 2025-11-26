<script setup lang="ts">
import { computed } from 'vue';
import type { News } from '../../types';

interface Props {
  news: News;
}

const props = defineProps<Props>();

const formattedDate = computed(() => {
  const date = new Date(props.news.datePublication);
  return date.toLocaleDateString('fr-MA', { year: 'numeric', month: 'long', day: 'numeric' });
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

