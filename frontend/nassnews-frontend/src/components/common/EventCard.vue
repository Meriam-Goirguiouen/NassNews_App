<script setup lang="ts">
import { computed } from 'vue';
import type { Event } from '../../types';

interface Props {
  event: Event;
}

const props = defineProps<Props>();

const formattedDate = computed(() => {
  const date = new Date(props.event.date);
  return {
    day: date.getDate().toString().padStart(2, '0'),
    month: date.toLocaleDateString('fr-MA', { month: 'short' }).toUpperCase(),
  };
});
</script>

<template>
  <article class="bg-white rounded-3xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 flex cursor-pointer transform hover:-translate-y-1">
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

