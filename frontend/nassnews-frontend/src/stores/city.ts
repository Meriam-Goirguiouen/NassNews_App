import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { City } from '../types';

export const useCityStore = defineStore('city', () => {
  const cities = ref<City[]>([]);
  const currentCityId = ref<number | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const currentCity = computed(() => 
    cities.value.find((city) => city.id === currentCityId.value) || null
  );

  async function fetchCities() {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('http://localhost:8080/api/cities', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to fetch cities');
      }

      cities.value = await response.json();
      
      // Set first city as default if none selected
      if (!currentCityId.value && cities.value.length > 0) {
        currentCityId.value = cities.value[0].id;
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch cities';
      console.error('Error fetching cities:', err);
      // Keep empty array on error
      cities.value = [];
    } finally {
      loading.value = false;
    }
  }

  function setCurrentCity(cityId: number) {
    currentCityId.value = cityId;
    localStorage.setItem('currentCityId', cityId.toString());
  }

  function getCityById(id: number): City | undefined {
    return cities.value.find((city) => city.id === id);
  }

  function loadSavedCity() {
    const saved = localStorage.getItem('currentCityId');
    if (saved) {
      currentCityId.value = parseInt(saved);
    }
  }

  return {
    cities,
    currentCityId,
    currentCity,
    loading,
    error,
    fetchCities,
    setCurrentCity,
    getCityById,
    loadSavedCity,
  };
});

