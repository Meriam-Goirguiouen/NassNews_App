<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useCityStore } from '../../stores/city';
import { useEventStore } from '../../stores/event';
import { useRouter } from 'vue-router';
import { Calendar, MessageSquare, Plus, LogOut, BarChart3, AlertCircle, MapPin, Clock, X, Building2, Edit, Trash2 } from 'lucide-vue-next';

const authStore = useAuthStore();
const cityStore = useCityStore();
const eventStore = useEventStore();
const router = useRouter();
const showAddEventMenu = ref(false);
const showEditEventMenu = ref(false);
const activeMenuItem = ref('dashboard');
const editingEventId = ref<string | null>(null);
const detectedCity = ref<{ id: string; name: string; region: string } | null>(null);
const detectingLocation = ref(false);
const locationError = ref<string | null>(null);
const lastDetectedCoords = ref<{ latitude: number; longitude: number } | null>(null);

// Event form data - matching Evenement entity fields
// Note: villeId is no longer in the form - it's automatically set from detected location
const eventForm = ref({
  titre: '',        // maps to titre in Evenement
  description: '',  // maps to description in Evenement
  lieu: '',         // maps to lieu in Evenement
  dateEvenement: '', // maps to dateEvenement in Evenement
  typeEvenement: '', // maps to typeEvenement in Evenement
});

const eventTypes = ['Cultural', 'Sports', 'Public', 'Educational', 'Entertainment', 'Business', 'Other'];
const formErrors = ref<Record<string, string>>({});

// Computed properties for events
const adminEvents = computed(() => eventStore.eventsList);
const adminCityId = computed(() => authStore.currentUser?.cityId || cityStore.currentCityId);

// Detect user location and city
const detectUserLocation = async () => {
  if (!navigator.geolocation) {
    locationError.value = 'Geolocation is not supported by your browser';
    return;
  }

  detectingLocation.value = true;
  locationError.value = null;

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      try {
        const { latitude, longitude } = position.coords;
        // Store coordinates for later use
        lastDetectedCoords.value = { latitude, longitude };
        
        console.log('Calling detectCityFromLocation with:', { latitude, longitude });
        const city = await cityStore.detectCityFromLocation(latitude, longitude);
        console.log('detectCityFromLocation returned:', city);
        console.log('cityStore.error:', cityStore.error);
        
        if (city && city.id) {
          detectedCity.value = {
            id: city.id,
            name: city.name,
            region: city.region
          };
          
          // Ensure currentCityId is set
          if (cityStore.currentCityId !== city.id) {
            cityStore.setCurrentCity(city.id);
          }
          
          console.log('City detected and set:', { 
            detectedCity: detectedCity.value, 
            currentCityId: cityStore.currentCityId,
            currentCity: cityStore.currentCity 
          });
          
          // Clear any previous errors
          locationError.value = null;
          
          // Fetch events for the detected city
          await eventStore.fetchByCityId(city.id);
        } else {
          // Show more specific error message
          const errorMsg = cityStore.error || 'Could not detect your city from coordinates.';
          locationError.value = `${errorMsg} (Lat: ${latitude.toFixed(4)}, Lng: ${longitude.toFixed(4)})`;
          console.error('City detection failed:', {
            city,
            error: cityStore.error,
            coordinates: { latitude, longitude }
          });
        }
      } catch (err: any) {
        console.error('Error detecting city:', err);
        locationError.value = 'Failed to detect city. Please select manually.';
      } finally {
        detectingLocation.value = false;
      }
    },
    (error) => {
      console.error('Geolocation error:', error);
      locationError.value = 'Location access denied. Please select your city manually.';
      detectingLocation.value = false;
      
      // Fallback to saved city or first available city
      const adminCityIdValue = adminCityId.value ? String(adminCityId.value) : null;
      const defaultCityId = adminCityIdValue || cityStore.currentCityId || (cityStore.cities.length > 0 ? cityStore.cities[0].id : null);
      if (defaultCityId) {
        eventStore.fetchByCityId(String(defaultCityId));
      }
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  );
};

// Fetch cities and events on mount
onMounted(async () => {
  await cityStore.fetchCities();
  cityStore.loadSavedCity();
  
  // Try to detect location first
  await detectUserLocation();
  
  // If location detection failed or no city detected, use fallback
  if (!detectedCity.value) {
    const adminCityIdValue = adminCityId.value ? String(adminCityId.value) : null;
    const defaultCityId = adminCityIdValue || cityStore.currentCityId || (cityStore.cities.length > 0 ? cityStore.cities[0].id : null);
    if (defaultCityId) {
      // Fetch events for this city
      await eventStore.fetchByCityId(String(defaultCityId));
    }
  }

  // Set up scroll observer to update active menu item
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const sectionId = entry.target.id;
          if (sectionId === 'dashboard-section') {
            activeMenuItem.value = 'dashboard';
          } else if (sectionId === 'events-section') {
            activeMenuItem.value = 'my-events';
          }
        }
      });
    },
    { threshold: 0.3, rootMargin: '-100px 0px -50% 0px' }
  );

  const dashboardSection = document.getElementById('dashboard-section');
  const eventsSection = document.getElementById('events-section');
  
  if (dashboardSection) observer.observe(dashboardSection);
  if (eventsSection) observer.observe(eventsSection);
});

const validateForm = () => {
  formErrors.value = {};
  let isValid = true;

  if (!eventForm.value.titre.trim()) {
    formErrors.value.titre = 'Title is required';
    isValid = false;
  }

  if (!eventForm.value.description.trim()) {
    formErrors.value.description = 'Description is required';
    isValid = false;
  }

  if (!eventForm.value.lieu.trim()) {
    formErrors.value.lieu = 'Location is required';
    isValid = false;
  }

  if (!eventForm.value.dateEvenement) {
    formErrors.value.dateEvenement = 'Date is required';
    isValid = false;
  } else {
    const selectedDate = new Date(eventForm.value.dateEvenement);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (selectedDate < today) {
      formErrors.value.dateEvenement = 'Date cannot be in the past';
      isValid = false;
    }
  }

  if (!eventForm.value.typeEvenement) {
    formErrors.value.typeEvenement = 'Event type is required';
    isValid = false;
  }

  // City validation is no longer needed - it's automatically set from detected location

  return isValid;
};

const handleSubmit = async () => {
  // Clear any previous errors
  formErrors.value = {};
  
  if (!validateForm()) {
    return;
  }

  try {
    // Automatically use the detected city or current city
    let villeIdForBackend: string = '';
    
    // For editing, keep the original city; for new events, use detected city
    if (editingEventId.value) {
      // When editing, find the original event to get its city
      const originalEvent = eventStore.eventsList.find(e => e.id === editingEventId.value);
      if (originalEvent && originalEvent.cityId) {
        villeIdForBackend = String(originalEvent.cityId);
      } else {
        // Fallback to detected city if original event not found
        villeIdForBackend = detectedCity.value?.id || cityStore.currentCity?.id || '';
      }
    } else {
      // For new events, use detected city
      console.log('Checking for city...', {
        detectedCity: detectedCity.value,
        currentCity: cityStore.currentCity,
        currentCityId: cityStore.currentCityId,
        citiesCount: cityStore.cities.length,
        cities: cityStore.cities.map(c => ({ id: c.id, name: c.name }))
      });
      
      // Priority 1: Use detectedCity.value.id if available
      if (detectedCity.value && detectedCity.value.id) {
        villeIdForBackend = detectedCity.value.id;
        console.log('✓ Using detected city ID:', villeIdForBackend);
      }
      // Priority 2: Use currentCity from store (set by detectCityFromLocation) - check this before currentCityId
      else if (cityStore.currentCity) {
        // Log currentCity details for debugging
        console.log('currentCity exists:', {
          currentCity: cityStore.currentCity,
          currentCityId: cityStore.currentCity.id,
          currentCityIdType: typeof cityStore.currentCity.id,
          currentCityKeys: Object.keys(cityStore.currentCity || {})
        });
        
        if (cityStore.currentCity.id) {
          villeIdForBackend = cityStore.currentCity.id;
          // Update detectedCity to match
          if (detectedCity.value) {
            detectedCity.value.id = cityStore.currentCity.id;
          }
          console.log('✓ Using current city from store:', cityStore.currentCity);
        } else {
          console.warn('currentCity exists but has no id:', cityStore.currentCity);
        }
      }
      // Priority 3: Use currentCityId from store (most reliable - set by detectCityFromLocation)
      else if (cityStore.currentCityId) {
        villeIdForBackend = cityStore.currentCityId;
        // Update detectedCity to have the ID
        if (detectedCity.value) {
          detectedCity.value.id = cityStore.currentCityId;
        }
        console.log('✓ Using currentCityId from store:', villeIdForBackend);
      }
      // Priority 4: Find city by name if detectedCity has name but no ID
      else if (detectedCity.value && detectedCity.value.name) {
        console.log('Attempting to find city by name:', detectedCity.value.name);
        console.log('Available cities:', cityStore.cities.map(c => ({ id: c.id, name: c.name, nameLength: c.name.length })));
        
        // Normalize strings for comparison (remove diacritics, normalize Unicode)
        const normalize = (str: string) => str.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
        const detectedNameNormalized = normalize(detectedCity.value.name);
        
        // Try exact match first (including whitespace)
        let foundCity = cityStore.cities.find(c => {
          const match = c.name === detectedCity.value!.name;
          if (match) console.log('Exact match found:', c);
          return match;
        });
        
        // If not found, try normalized match
        if (!foundCity) {
          foundCity = cityStore.cities.find(c => {
            const match = normalize(c.name) === detectedNameNormalized;
            if (match) console.log('Normalized match found:', c);
            return match;
          });
        }
        
        // If not found, try case-insensitive exact match
        if (!foundCity) {
          foundCity = cityStore.cities.find(c => {
            const match = c.name.toLowerCase().trim() === detectedCity.value!.name.toLowerCase().trim();
            if (match) console.log('Case-insensitive match found:', c);
            return match;
          });
        }
        
        // If still not found, try matching the first word (base name)
        if (!foundCity) {
          const baseName = detectedCity.value.name.split(/\s+/)[0].trim();
          console.log('Trying base name match:', baseName);
          foundCity = cityStore.cities.find(c => {
            const cityBaseName = c.name.split(/\s+/)[0].trim();
            const match = cityBaseName.toLowerCase() === baseName.toLowerCase() ||
                   c.name.toLowerCase().includes(baseName.toLowerCase()) ||
                   baseName.toLowerCase().includes(cityBaseName.toLowerCase());
            if (match) console.log('Base name match found:', c);
            return match;
          });
        }
        
        if (foundCity && foundCity.id) {
          villeIdForBackend = foundCity.id;
          // Update detectedCity with the ID
          detectedCity.value.id = foundCity.id;
          // Also update currentCityId in store
          cityStore.setCurrentCity(foundCity.id);
          console.log('✓ Found city by name:', foundCity);
        } else {
          console.warn('City not found by name:', detectedCity.value.name, 'Available cities:', cityStore.cities.map(c => c.name));
        }
      }
      
      if (!villeIdForBackend) {
        // Last resort: use stored coordinates if available, otherwise try to detect again
        console.log('No city found, attempting to use stored coordinates or detect location...');
        
        if (lastDetectedCoords.value) {
          // Use previously detected coordinates
          console.log('Using stored coordinates:', lastDetectedCoords.value);
          try {
            const detected = await cityStore.detectCityFromLocation(
              lastDetectedCoords.value.latitude,
              lastDetectedCoords.value.longitude
            );
            
            if (detected && detected.id) {
              villeIdForBackend = detected.id;
              detectedCity.value = {
                id: detected.id,
                name: detected.name,
                region: detected.region
              };
              console.log('✓ City detected from stored coordinates:', detectedCity.value);
            } else {
              throw new Error('Could not detect city from stored coordinates.');
            }
          } catch (err: any) {
            console.error('Error using stored coordinates:', err);
            // Fall through to try geolocation again
          }
        }
        
        // If stored coordinates didn't work, try geolocation
        if (!villeIdForBackend && navigator.geolocation) {
          try {
            const position = await new Promise<GeolocationPosition>((resolve, reject) => {
              navigator.geolocation.getCurrentPosition(resolve, reject, {
                enableHighAccuracy: true,
                timeout: 5000,
                maximumAge: 0
              });
            });
            
            const detected = await cityStore.detectCityFromLocation(
              position.coords.latitude,
              position.coords.longitude
            );
            
            if (detected && detected.id) {
              villeIdForBackend = detected.id;
              detectedCity.value = {
                id: detected.id,
                name: detected.name,
                region: detected.region
              };
              console.log('✓ City detected from geolocation:', detectedCity.value);
            } else {
              throw new Error('Could not detect your location. Please allow location access.');
            }
          } catch (geoError: any) {
            console.error('Geolocation error during submit:', geoError);
            throw new Error('Could not detect your location. Please allow location access.');
          }
        } else if (!villeIdForBackend) {
          throw new Error('Location services are not available. Please enable location access.');
        }
      }
    }
    
    console.log('Final villeIdForBackend:', villeIdForBackend);
    
    if (!villeIdForBackend || villeIdForBackend.trim() === '') {
      throw new Error('City is required. Please allow location access to detect your city.');
    }

    if (editingEventId.value) {
      // Update existing event (keep original city)
      await eventStore.updateEvent(editingEventId.value, {
        titre: eventForm.value.titre,
        description: eventForm.value.description,
        lieu: eventForm.value.lieu,
        dateEvenement: eventForm.value.dateEvenement,
        typeEvenement: eventForm.value.typeEvenement,
        villeId: villeIdForBackend
      });
    } else {
      // Create new event (use detected city)
      await eventStore.createEvent({
        titre: eventForm.value.titre,
        description: eventForm.value.description,
        lieu: eventForm.value.lieu,
        dateEvenement: eventForm.value.dateEvenement,
        typeEvenement: eventForm.value.typeEvenement,
        villeId: villeIdForBackend
      });
    }

    // Reset form and close modal
    resetForm();
    showAddEventMenu.value = false;
    showEditEventMenu.value = false;
    editingEventId.value = null;
  } catch (error: any) {
    console.error('Error saving event:', error);
    alert(error.message || 'Failed to save event. Please try again.');
  }
};

const handleEdit = (event: any) => {
  editingEventId.value = event.id;
  eventForm.value = {
    titre: event.title,
    description: event.description || '',
    lieu: event.location,
    dateEvenement: event.date.split('T')[0], // Extract date part if it includes time
    typeEvenement: event.type,
  };
  showAddEventMenu.value = true; // Show the modal
};

const handleDelete = async (eventId: string) => {
  if (!confirm('Are you sure you want to delete this event?')) {
    return;
  }

  try {
    await eventStore.deleteEvent(eventId);
  } catch (error: any) {
    console.error('Error deleting event:', error);
    alert(error.message || 'Failed to delete event. Please try again.');
  }
};

const resetForm = () => {
  editingEventId.value = null;
  eventForm.value = {
    titre: '',
    description: '',
    lieu: '',
    dateEvenement: '',
    typeEvenement: '',
  };
  formErrors.value = {};
};

const closeModal = () => {
  resetForm();
  showAddEventMenu.value = false;
  showEditEventMenu.value = false;
};

const scrollToSection = (sectionId: string, menuItem: string) => {
  activeMenuItem.value = menuItem;
  const element = document.getElementById(sectionId);
  if (element) {
    const offset = 20; // Small offset from top
    const elementPosition = element.getBoundingClientRect().top;
    const offsetPosition = elementPosition + window.pageYOffset - offset;

    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    });
  }
};

const goToDashboard = () => {
  scrollToSection('dashboard-section', 'dashboard');
};

const goToMyEvents = () => {
  scrollToSection('events-section', 'my-events');
};

// No need to watch villeId anymore - it's automatic
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4] flex">
    <!-- Sidebar Menu -->
    <aside class="w-64 bg-white shadow-lg fixed h-full z-40">
      <div class="p-6 border-b border-gray-200">
          <router-link to="/" class="text-2xl font-bold text-[#7A1F1F]">NassNews</router-link>
      </div>
      
      <nav class="p-4 space-y-2">
        <button
          @click="showAddEventMenu = !showAddEventMenu"
          class="w-full flex items-center gap-3 px-4 py-3 rounded-lg bg-[#7A1F1F] text-white hover:bg-[#7A1F1F]/90 transition-colors font-semibold"
        >
          <Plus class="w-5 h-5" />
          <span>Add Event</span>
        </button>
        
        <div class="pt-4 border-t border-gray-200">
          <div class="px-4 py-2 text-xs font-semibold text-gray-500 uppercase">Menu</div>
          <button
            @click="goToDashboard"
            :class="[
              'w-full flex items-center gap-3 px-4 py-2 rounded-lg transition-colors',
              activeMenuItem === 'dashboard'
                ? 'bg-[#7A1F1F] text-white font-semibold'
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <BarChart3 class="w-5 h-5" />
            <span>Dashboard</span>
          </button>
          <button
            @click="goToMyEvents"
            :class="[
              'w-full flex items-center gap-3 px-4 py-2 rounded-lg transition-colors',
              activeMenuItem === 'my-events'
                ? 'bg-[#7A1F1F] text-white font-semibold'
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Calendar class="w-5 h-5" />
            <span>My Events</span>
          </button>
        </div>
      </nav>
      
      <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200">
        <div class="px-4 py-2 mb-2">
          <p class="text-sm font-semibold text-gray-900">{{ authStore.currentUser?.username }}</p>
          <p class="text-xs text-gray-500">Communal Admin</p>
        </div>
        <button
          @click="authStore.logout(); router.push('/')"
          class="w-full flex items-center gap-3 px-4 py-2 rounded-lg text-gray-700 hover:bg-red-50 hover:text-red-600 transition-colors"
        >
          <LogOut class="w-5 h-5" />
          <span>Logout</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 ml-64">
      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <!-- Dashboard Section -->
        <div id="dashboard-section" class="mb-8">
          <!-- Header -->
          <div class="mb-8">
            <h1 class="text-4xl font-bold text-[#7A1F1F] mb-2">Communal Dashboard</h1>
            <p class="text-lg text-[#5F6B61] mb-4">Manage events and monitor comments for your location</p>
            
            <!-- Detected City Display -->
            <div class="bg-white rounded-xl p-4 shadow-md border-l-4 border-[#7A1F1F]">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <MapPin class="w-5 h-5 text-[#7A1F1F]" />
                  <div class="text-gray-700">
                    <span v-if="detectingLocation">Detecting your location...</span>
                    <span v-else-if="detectedCity">
                      Managing events for: {{ detectedCity.name }}, {{ detectedCity.region }}
                    </span>
                    <span v-else-if="locationError">{{ locationError }}</span>
                    <span v-else-if="cityStore.currentCity">
                      Managing events for: {{ cityStore.currentCity.name }}, {{ cityStore.currentCity.region }}
                    </span>
                    <span v-else>Please select your city</span>
                  </div>
                </div>
                <button
                  v-if="!detectedCity && !detectingLocation && !locationError"
                  @click="detectUserLocation"
                  class="px-4 py-2 text-sm bg-[#7A1F1F] text-white rounded-lg hover:bg-[#7A1F1F]/90 transition-colors"
                >
                  Detect Location
                </button>
              </div>
            </div>
          </div>

          <!-- Statistics at Top -->
        <div class="grid md:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-xl p-6 shadow-md">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600">Total Events</span>
              <Calendar class="w-5 h-5 text-[#7A1F1F]" />
            </div>
            <div class="text-3xl font-bold text-[#7A1F1F]">{{ adminEvents.length }}</div>
          </div>
          
          <div class="bg-white rounded-xl p-6 shadow-md">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600">Upcoming</span>
              <Clock class="w-5 h-5 text-[#7A1F1F]" />
            </div>
            <div class="text-3xl font-bold text-[#7A1F1F]">
              {{ adminEvents.filter((e: any) => {
                const eventDate = new Date(e.date);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                return eventDate >= today;
              }).length }}
            </div>
          </div>
          
          <div class="bg-white rounded-xl p-6 shadow-md">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600">Total Comments</span>
              <MessageSquare class="w-5 h-5 text-[#7A1F1F]" />
            </div>
            <div class="text-3xl font-bold text-[#7A1F1F]">-</div>
          </div>
          
          <div class="bg-white rounded-xl p-6 shadow-md">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600">Completed</span>
              <BarChart3 class="w-5 h-5 text-[#7A1F1F]" />
            </div>
            <div class="text-3xl font-bold text-[#7A1F1F]">
              {{ adminEvents.filter((e: any) => {
                const eventDate = new Date(e.date);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                return eventDate < today;
              }).length }}
            </div>
          </div>
        </div>
        </div>

        <!-- Events List Section -->
        <div id="events-section" class="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div class="bg-gradient-to-r from-[#7A1F1F] to-[#7A1F1F]/90 p-6">
            <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
                <div class="bg-white/20 rounded-full p-3">
                  <Calendar class="w-8 h-8 text-white" />
                </div>
                <div>
                  <h2 class="text-2xl font-bold text-white">My Events</h2>
                  <p class="text-white/90 text-sm">Events created by you</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="p-6">
            <div v-if="eventStore.loading" class="text-center py-12">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-[#7A1F1F]"></div>
              <p class="text-gray-500 mt-4">Loading events...</p>
            </div>
            
            <div v-else-if="eventStore.error" class="text-center py-12">
              <AlertCircle class="w-16 h-16 text-red-300 mx-auto mb-4" />
              <p class="text-red-500 text-lg">{{ eventStore.error }}</p>
            </div>
            
            <div v-else-if="adminEvents.length === 0" class="text-center py-12">
              <Calendar class="w-16 h-16 text-gray-300 mx-auto mb-4" />
              <p class="text-gray-500 text-lg">No events created yet</p>
              <p class="text-gray-400 text-sm mt-2">Click "Add Event" to create your first event</p>
            </div>
            
            <div v-else class="space-y-4">
              <div
                v-for="event in adminEvents"
                :key="event.id"
                class="border border-gray-200 rounded-xl p-6 hover:shadow-md transition-shadow"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                      <h3 class="text-xl font-bold text-gray-900">{{ event.title }}</h3>
                      <span
                        :class="[
                          'px-3 py-1 rounded-full text-xs font-semibold',
                          (() => {
                            const eventDate = new Date(event.date);
                            const today = new Date();
                            today.setHours(0, 0, 0, 0);
                            return eventDate >= today;
                          })()
                            ? 'bg-green-100 text-green-700' 
                            : 'bg-gray-100 text-gray-700'
                        ]"
                      >
                        {{
                          (() => {
                            const eventDate = new Date(event.date);
                            const today = new Date();
                            today.setHours(0, 0, 0, 0);
                            return eventDate >= today ? 'Upcoming' : 'Completed';
                          })()
                        }}
                      </span>
                    </div>
                    
                    <p v-if="event.description" class="text-gray-600 text-sm mb-4 line-clamp-2">{{ event.description }}</p>
                    
                    <div class="grid md:grid-cols-3 gap-4 mt-4">
                      <div class="flex items-center gap-2 text-gray-600">
                        <Calendar class="w-4 h-4 text-[#7A1F1F]" />
                        <span class="text-sm">{{ new Date(event.date).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
                      </div>
                      <div class="flex items-center gap-2 text-gray-600">
                        <MapPin class="w-4 h-4 text-[#7A1F1F]" />
                        <span class="text-sm">{{ event.location }}</span>
                      </div>
                      <div class="flex items-center gap-2 text-gray-600">
                        <span class="px-2 py-1 bg-[#F4EDE4] text-[#7A1F1F] rounded text-xs font-semibold">
                          {{ event.type }}
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="flex gap-2 ml-4">
                    <button
                      @click="handleEdit(event)"
                      class="px-4 py-2 bg-[#F4EDE4] text-[#7A1F1F] rounded-lg hover:bg-[#E6E6E6] transition-colors text-sm font-semibold flex items-center gap-2"
                    >
                      <Edit class="w-4 h-4" />
                      Edit
                    </button>
                    <button
                      @click="handleDelete(event.id)"
                      class="px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-semibold flex items-center gap-2"
                    >
                      <Trash2 class="w-4 h-4" />
                      Delete
            </button>
          </div>
        </div>
      </div>
            </div>
          </div>
        </div>


        <br>
        <!-- Comment Monitoring - Upcoming Feature -->
        <div class="bg-white rounded-2xl shadow-lg overflow-hidden border-2 border-[#7A1F1F]/10 mb-8">
          <div class="bg-gradient-to-r from-[#7A1F1F] to-[#7A1F1F]/90 p-6">
            <div class="flex items-center gap-4">
              <div class="bg-white/20 rounded-full p-3">
                <MessageSquare class="w-8 h-8 text-white" />
              </div>
              <div>
                <h2 class="text-2xl font-bold text-white">Comment Monitoring</h2>
                <p class="text-white/90 text-sm">Monitor all comments on your events</p>
              </div>
            </div>
          </div>
          
          <div class="p-6">
            <div class="space-y-4 mb-6">
              <div class="flex items-start gap-3 p-4 bg-[#F4EDE4] rounded-lg">
                <MessageSquare class="w-5 h-5 text-[#7A1F1F] mt-0.5 flex-shrink-0" />
                <div>
                  <p class="font-semibold text-gray-900">Real-Time Comments</p>
                  <p class="text-sm text-gray-600">View all comments left on your events in real-time</p>
                </div>
              </div>
              
              <div class="flex items-start gap-3 p-4 bg-[#F4EDE4] rounded-lg">
                <AlertCircle class="w-5 h-5 text-[#7A1F1F] mt-0.5 flex-shrink-0" />
                <div>
                  <p class="font-semibold text-gray-900">Moderation Tools</p>
                  <p class="text-sm text-gray-600">Manage and moderate inappropriate comments</p>
                </div>
              </div>
              
              <div class="flex items-start gap-3 p-4 bg-[#F4EDE4] rounded-lg">
                <BarChart3 class="w-5 h-5 text-[#7A1F1F] mt-0.5 flex-shrink-0" />
                <div>
                  <p class="font-semibold text-gray-900">Community Engagement</p>
                  <p class="text-sm text-gray-600">Analyze engagement and community feedback</p>
                </div>
              </div>
            </div>
            
            <div class="bg-yellow-50 border-2 border-[#D4AF37] rounded-lg p-4 flex items-start gap-3">
              <AlertCircle class="w-5 h-5 text-[#D4AF37] mt-0.5 flex-shrink-0" />
              <div>
                <p class="font-semibold text-[#D4AF37] mb-1">Upcoming Feature</p>
                <p class="text-sm text-gray-700">This feature will be available in an upcoming update.</p>
              </div>
            </div>
          </div>
      </div>
    </main>
    </div>

    <!-- Add/Edit Event Modal -->
    <div
      v-if="showAddEventMenu || showEditEventMenu"
      class="fixed inset-0 z-50 overflow-y-auto"
      @click.self="closeModal"
    >
      <!-- Backdrop -->
      <div class="fixed inset-0 bg-black/50 transition-opacity"></div>

      <!-- Modal -->
      <div class="flex min-h-full items-center justify-center p-4">
        <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
          <!-- Header -->
          <div class="sticky top-0 bg-gradient-to-r from-[#7A1F1F] to-[#7A1F1F]/90 p-6 flex items-center justify-between z-10">
            <div class="flex items-center gap-4">
              <div class="bg-white/20 rounded-full p-2">
                <Plus class="w-6 h-6 text-white" />
              </div>
              <div>
                <h2 class="text-2xl font-bold text-white">{{ editingEventId ? 'Edit Event' : 'Add New Event' }}</h2>
                <p class="text-white/90 text-sm">{{ editingEventId ? 'Update event details' : 'Create a new event for your location' }}</p>
              </div>
            </div>
            <button
              @click="closeModal"
              class="text-white hover:bg-white/20 rounded-full p-2 transition-colors"
            >
              <X class="w-6 h-6" />
            </button>
          </div>

          <!-- Form -->
          <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
            <!-- Title (titre) -->
            <div>
              <label for="titre" class="block text-sm font-semibold text-gray-700 mb-2">
                Event Title <span class="text-red-500">*</span>
              </label>
              <input
                id="titre"
                v-model="eventForm.titre"
                type="text"
                placeholder="Enter event title"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all"
                :class="{ 'border-red-500': formErrors.titre }"
              />
              <p v-if="formErrors.titre" class="mt-1 text-sm text-red-500">{{ formErrors.titre }}</p>
            </div>

            <!-- Description -->
            <div>
              <label for="description" class="block text-sm font-semibold text-gray-700 mb-2">
                Description <span class="text-red-500">*</span>
              </label>
              <textarea
                id="description"
                v-model="eventForm.description"
                rows="4"
                placeholder="Enter event description"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all resize-none"
                :class="{ 'border-red-500': formErrors.description }"
              ></textarea>
              <p v-if="formErrors.description" class="mt-1 text-sm text-red-500">{{ formErrors.description }}</p>
            </div>

            <!-- Location (lieu) and Date (dateEvenement) Row -->
            <div class="grid md:grid-cols-2 gap-4">
              <!-- Location (lieu) -->
              <div>
                <label for="lieu" class="block text-sm font-semibold text-gray-700 mb-2">
                  Location <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <MapPin class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="lieu"
                    v-model="eventForm.lieu"
                    type="text"
                    placeholder="Enter location"
                    class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all"
                    :class="{ 'border-red-500': formErrors.lieu }"
                  />
                </div>
                <p v-if="formErrors.lieu" class="mt-1 text-sm text-red-500">{{ formErrors.lieu }}</p>
              </div>

              <!-- Date (dateEvenement) -->
              <div>
                <label for="dateEvenement" class="block text-sm font-semibold text-gray-700 mb-2">
                  Event Date <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <Calendar class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="dateEvenement"
                    v-model="eventForm.dateEvenement"
                    type="date"
                    class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all"
                    :class="{ 'border-red-500': formErrors.dateEvenement }"
                    :min="new Date().toISOString().split('T')[0]"
                  />
                </div>
                <p v-if="formErrors.dateEvenement" class="mt-1 text-sm text-red-500">{{ formErrors.dateEvenement }}</p>
              </div>
            </div>

            <!-- Event Type (typeEvenement) -->
            <div>
              <label for="typeEvenement" class="block text-sm font-semibold text-gray-700 mb-2">
                Event Type <span class="text-red-500">*</span>
              </label>
              <select
                id="typeEvenement"
                v-model="eventForm.typeEvenement"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all bg-white"
                :class="{ 'border-red-500': formErrors.typeEvenement }"
              >
                <option value="">Select event type</option>
                <option v-for="type in eventTypes" :key="type" :value="type">{{ type }}</option>
              </select>
              <p v-if="formErrors.typeEvenement" class="mt-1 text-sm text-red-500">{{ formErrors.typeEvenement }}</p>
            </div>

            <!-- Detected City Display (Read-only) -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                City <span class="text-gray-400 text-xs">(Auto-detected)</span>
              </label>
              <div class="relative">
                <Building2 class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                <div class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                  <span v-if="detectingLocation">Detecting location...</span>
                  <span v-else-if="detectedCity">{{ detectedCity.name }}, {{ detectedCity.region }}</span>
                  <span v-else-if="cityStore.currentCity">{{ cityStore.currentCity.name }}, {{ cityStore.currentCity.region }}</span>
                  <span v-else-if="locationError" class="text-red-500">{{ locationError }}</span>
                  <span v-else class="text-yellow-600">Location not detected. Please allow location access.</span>
                </div>
              </div>
              <p class="mt-1 text-xs text-gray-500">
                Events will be automatically added to your detected city location.
              </p>
            </div>

            <!-- Form Actions -->
            <div class="flex gap-4 pt-4 border-t border-gray-200">
              <button
                type="button"
                @click="closeModal"
                class="flex-1 px-6 py-3 border-2 border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-semibold"
              >
                Cancel
              </button>
              <button
                type="submit"
                class="flex-1 px-6 py-3 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#7A1F1F]/90 transition-colors font-semibold"
              >
                {{ editingEventId ? 'Update Event' : 'Create Event' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

