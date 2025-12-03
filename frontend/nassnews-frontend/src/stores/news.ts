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

  // Favorite news operations
  async function addFavoriteNews(userId: string, newsId: string | number): Promise<boolean> {
    try {
      console.log('Adding favorite news - User ID:', userId, 'News ID:', newsId);
      const response = await fetch(`http://localhost:8080/api/utilisateurs/${userId}/favorites/news/${newsId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        let errorText = '';
        try {
          const errorData = await response.json();
          errorText = errorData.message || JSON.stringify(errorData);
        } catch {
          errorText = await response.text();
        }
        console.error('Failed to add favorite news. Status:', response.status, 'Error:', errorText);
        
        // If 400 Bad Request, it means user not found or invalid request
        if (response.status === 400) {
          console.error('User might not exist or request is invalid');
        }
        return false;
      }

      const data = await response.json();
      console.log('Add favorite response:', data);
      
      // Handle different response formats
      if (data.success === true || data.success === 'true') {
        return true;
      }
      // If response is OK but no success field, assume it worked
      if (response.status === 200) {
        return true;
      }
      return false;
    } catch (err: any) {
      console.error('Error adding favorite news:', err);
      return false;
    }
  }

  async function removeFavoriteNews(userId: string, newsId: string | number): Promise<boolean> {
    try {
      const response = await fetch(`http://localhost:8080/api/utilisateurs/${userId}/favorites/news/${newsId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Failed to remove favorite news. Status:', response.status, 'Error:', errorText);
        return false;
      }

      const data = await response.json();
      console.log('Remove favorite response:', data);
      
      // Handle different response formats
      if (data.success === true || data.success === 'true') {
        return true;
      }
      // If response is OK but no success field, assume it worked
      if (response.status === 200) {
        return true;
      }
      return false;
    } catch (err: any) {
      console.error('Error removing favorite news:', err);
      return false;
    }
  }

  async function getFavoriteNews(userId: string): Promise<string[]> {
    try {
      const response = await fetch(`http://localhost:8080/api/utilisateurs/${userId}/favorites/news`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        // If 404, return empty array (user might not have favorites yet)
        if (response.status === 404) {
          console.log('User has no favorites yet (404), returning empty array');
          return [];
        }
        throw new Error(`Failed to fetch favorite news: ${response.status}`);
      }

      const data = await response.json();
      return Array.isArray(data) ? data : [];
    } catch (err: any) {
      console.error('Error fetching favorite news:', err);
      return [];
    }
  }

  return {
    newsList,
    loading,
    error,
    fetchByCityId,
    fetchAllNews,
    getTodaysNews,
    addFavoriteNews,
    removeFavoriteNews,
    getFavoriteNews,
  };
});

