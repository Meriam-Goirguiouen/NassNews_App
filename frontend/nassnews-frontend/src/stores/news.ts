import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { News } from '../types';

export const useNewsStore = defineStore('news', () => {
  const newsList = ref<News[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Helper function to map backend Actualite to frontend News
  function mapActualiteToNews(actualite: any): News {
    // Handle date - could be Date object or ISO string
    let dateStr = actualite.datePublication;
    if (dateStr instanceof Date) {
      dateStr = dateStr.toISOString();
    } else if (typeof dateStr === 'string' && !dateStr.includes('T')) {
      // If it's just a date string, add time
      dateStr = dateStr + 'T00:00:00';
    }
    
    return {
      id: actualite.id || actualite._id,
      title: actualite.titre,
      summary: actualite.contenu ? (actualite.contenu.length > 150 ? actualite.contenu.substring(0, 150) + '...' : actualite.contenu) : '',
      content: actualite.contenu,
      datePublication: dateStr,
      cityId: actualite.villeId,
      imageUrl: actualite.imageUrl,
      author: actualite.source,
      category: actualite.categorie,
    };
  }

  async function fetchByCityId(cityId: string) {
    loading.value = true;
    error.value = null;
    try {
      console.log('Fetching news for city ID:', cityId);
      const response = await fetch(`http://localhost:8080/api/actualites?villeId=${cityId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Failed to fetch news. Status:', response.status, 'Error:', errorText);
        throw new Error(`Failed to fetch news: ${response.statusText}`);
      }

      const data = await response.json();
      console.log('Received news data:', data);
      console.log('Number of news items:', data.length);
      
      // Map backend Actualite to frontend News
      newsList.value = data.map(mapActualiteToNews);
      console.log('Mapped news list:', newsList.value);
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch news';
      console.error('Error fetching news:', err);
      newsList.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function fetchAllNews() {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('http://localhost:8080/api/actualites', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch news');
      }

      const data = await response.json();
      // Map backend Actualite to frontend News
      newsList.value = data.map(mapActualiteToNews);
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
    fetchAllNews,
    getTodaysNews,
  };
});

