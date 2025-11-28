import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { City } from '../types';

export const useCityStore = defineStore('city', () => {
  const cities = ref<City[]>([]);
  // Initialize as null, allows string (MongoDB ID) or null
  const currentCityId = ref<string | null>(null);
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
        throw new Error(`Error: ${response.statusText}`);
      }

      const rawData = await response.json();
      console.log('Raw cities data from backend (first item):', rawData[0]);

      // CLEAN MAPPING: Map MongoDB fields to Frontend fields
      // Spring Boot serializes @Id as 'id' in JSON, not '_id'
      cities.value = rawData.map((ville: any) => {
        // Get ID - Spring Boot uses 'id', MongoDB uses '_id'
        const villeId = ville.id || ville._id;
        if (!villeId) {
          console.error('City missing ID:', ville);
        }
        return {
          id: villeId || '',            // Spring Boot serializes @Id as 'id'
          name: ville.nom || '',          // Map 'nom' to 'name'
          region: ville.region || '',
          population: ville.population || 0,
          coords: ville.coordonnees || ''
        };
      }).filter((city: City) => city.id && city.id !== ''); // Filter out cities without valid IDs
      
      console.log('Mapped cities count:', cities.value.length);
      console.log('Sample mapped city:', cities.value[0]);
      
    } catch (err: any) {
      console.error('Error fetching cities:', err);
      error.value = err.message || 'Failed to fetch cities';
      cities.value = [];
    } finally {
      loading.value = false;
    }
  }

  function setCurrentCity(cityId: string) {
    currentCityId.value = cityId;
    localStorage.setItem('currentCityId', cityId);
  }

  function loadSavedCity() {
    const saved = localStorage.getItem('currentCityId');
    if (saved && cities.value.some(c => c.id === saved)) {
      currentCityId.value = saved;
    }
  }

  async function detectCityFromLocation(latitude: number, longitude: number): Promise<City | null> {
    loading.value = true;
    error.value = null;
    try {
      console.log('Detecting city from coordinates:', { latitude, longitude });
      
      const response = await fetch('http://localhost:8080/api/cities/detect-city', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          latitude,
          longitude
        }),
      });

      console.log('Response status:', response.status, response.statusText);

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Backend error response:', errorText);
        throw new Error(`Backend error (${response.status}): ${errorText || response.statusText}`);
      }

      const ville = await response.json();
      console.log('Backend returned ville:', ville);
      
      // Validate that we got the expected data
      // Backend may return 'id' or '_id', and 'nom' for city name
      const villeId = ville?._id || ville?.id;
      const villeNom = ville?.nom || ville?.name;
      
      if (!ville || !villeId || !villeNom) {
        console.error('Invalid response from backend - empty or incomplete ville:', ville);
        // Check if it's an empty Ville object (has _class but no id)
        if (ville && ville._class && !villeId) {
          throw new Error('Could not detect city from coordinates. The location service may be unavailable or the coordinates may not correspond to a known city.');
        }
        throw new Error('Invalid response from server: city data is incomplete');
      }
      
      // Map backend Ville to frontend City
      const detectedCity: City = {
        id: villeId,
        name: villeNom,
        region: ville.region || '',
        population: ville.population || 0,
        coords: ville.coordonnees || ''
      };

      console.log('Mapped to City:', detectedCity);

      // Check if this city is already in our list, if not add it
      const existingCity = cities.value.find(c => c.id === detectedCity.id);
      if (!existingCity) {
        cities.value.push(detectedCity);
        console.log('Added new city to list');
      } else {
        console.log('City already in list');
      }

      // Set as current city
      setCurrentCity(detectedCity.id);
      console.log('Set current city ID:', detectedCity.id);
      
      return detectedCity;
    } catch (err: any) {
      console.error('Error detecting city:', err);
      error.value = err.message || 'Failed to detect city';
      return null;
    } finally {
      loading.value = false;
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
    loadSavedCity,
    detectCityFromLocation,
  };
});