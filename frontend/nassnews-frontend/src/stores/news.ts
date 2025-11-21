import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { News } from '../types';

export const useNewsStore = defineStore('news', () => {
  const newsList = ref<News[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchByCityId(cityId: number) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/actualites?villeId=${cityId.toString()}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch news');
      }

      newsList.value = await response.json();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch news';
      console.error('Error fetching news:', err);
      newsList.value = [];
    } finally {
      loading.value = false;
    }
  }

  function getTodaysNews() {
    const today = new Date().toISOString().split('T')[0];
    return newsList.value.filter((news) => 
      news.datePublication.startsWith(today)
    );
  }

  return {
    newsList,
    loading,
    error,
    fetchByCityId,
    getTodaysNews,
  };
});

