import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Event } from '../types';

export const useEventStore = defineStore('event', () => {
  const eventsList = ref<Event[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchByCityId(cityId: number) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/events?villeId=${cityId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch events');
      }

      eventsList.value = await response.json();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch events';
      console.error('Error fetching events:', err);
      eventsList.value = [];
    } finally {
      loading.value = false;
    }
  }

  return {
    eventsList,
    loading,
    error,
    fetchByCityId,
  };
});

