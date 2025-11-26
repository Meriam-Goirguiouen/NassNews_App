<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useAuthStore } from '../../stores/auth';
import { useCityStore } from '../../stores/city';
import { useRouter } from 'vue-router';
import { Calendar, MessageSquare, Plus, LogOut, BarChart3, AlertCircle, MapPin, Clock, X, Building2 } from 'lucide-vue-next';

const authStore = useAuthStore();
const cityStore = useCityStore();
const router = useRouter();
const showAddEventMenu = ref(false);
const activeMenuItem = ref('dashboard');

// Event form data - matching Evenement entity fields
const eventForm = ref({
  titre: '',        // maps to titre in Evenement
  description: '',  // maps to description in Evenement
  lieu: '',         // maps to lieu in Evenement
  dateEvenement: '', // maps to dateEvenement in Evenement
  typeEvenement: '', // maps to typeEvenement in Evenement
  villeId: null as number | null // maps to villeId in Evenement
});

const eventTypes = ['Cultural', 'Sports', 'Public', 'Educational', 'Entertainment', 'Business', 'Other'];
const formErrors = ref<Record<string, string>>({});

// Mock data for events created by this admin
const adminEvents = ref([
  {
    id: 1,
    title: 'Community Festival 2025',
    date: '2025-03-15',
    location: 'City Center',
    type: 'Cultural',
    status: 'Upcoming'
  },
  {
    id: 2,
    title: 'Local Sports Tournament',
    date: '2025-02-20',
    location: 'Sports Complex',
    type: 'Sports',
    status: 'Upcoming'
  },
  {
    id: 3,
    title: 'Town Hall Meeting',
    date: '2025-01-10',
    location: 'City Hall',
    type: 'Public',
    status: 'Completed'
  }
]);

// Fetch cities on mount
onMounted(async () => {
  await cityStore.fetchCities();
  cityStore.loadSavedCity();
  // Set default city if available
  if (cityStore.currentCityId) {
    eventForm.value.villeId = cityStore.currentCityId;
  } else if (cityStore.cities.length > 0) {
    eventForm.value.villeId = cityStore.cities[0].id;
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

  if (!eventForm.value.villeId) {
    formErrors.value.villeId = 'City is required';
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }

  try {
    // API call matching Evenement entity structure
    const response = await fetch('http://localhost:8080/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        titre: eventForm.value.titre,
        description: eventForm.value.description,
        lieu: eventForm.value.lieu,
        dateEvenement: eventForm.value.dateEvenement,
        typeEvenement: eventForm.value.typeEvenement,
        villeId: eventForm.value.villeId
      })
    });

    if (!response.ok) {
      throw new Error('Failed to create event');
    }

    const createdEvent = await response.json();
    
    // Add to local array with mapped fields for display
    const newEvent = {
      id: createdEvent.idEvenement || adminEvents.value.length + 1,
      title: createdEvent.titre,
      date: createdEvent.dateEvenement,
      location: createdEvent.lieu,
      type: createdEvent.typeEvenement,
      status: new Date(createdEvent.dateEvenement) >= new Date() ? 'Upcoming' : 'Completed'
    };
    
    adminEvents.value.unshift(newEvent);
  } catch (error) {
    console.error('Error creating event:', error);
    alert('Failed to create event. Please try again.');
    return;
  }

  // Reset form and close modal
  resetForm();
  showAddEventMenu.value = false;
};

const resetForm = () => {
  eventForm.value = {
    titre: '',
    description: '',
    lieu: '',
    dateEvenement: '',
    typeEvenement: '',
    villeId: cityStore.currentCityId || (cityStore.cities.length > 0 ? cityStore.cities[0].id : null)
  };
  formErrors.value = {};
};

const closeModal = () => {
  resetForm();
  showAddEventMenu.value = false;
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
            <p class="text-lg text-[#5F6B61]">Manage events and monitor comments for your location</p>
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
              {{ adminEvents.filter((e: any) => e.status === 'Upcoming').length }}
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
              {{ adminEvents.filter((e: any) => e.status === 'Completed').length }}
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
            <div v-if="adminEvents.length === 0" class="text-center py-12">
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
                          event.status === 'Upcoming' 
                            ? 'bg-green-100 text-green-700' 
                            : 'bg-gray-100 text-gray-700'
                        ]"
                      >
                        {{ event.status }}
                      </span>
                    </div>
                    
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
                    <button class="px-4 py-2 bg-[#F4EDE4] text-[#7A1F1F] rounded-lg hover:bg-[#E6E6E6] transition-colors text-sm font-semibold">
                      Edit
                    </button>
                    <button class="px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-semibold">
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

    <!-- Add Event Modal -->
    <div
      v-if="showAddEventMenu"
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
                <h2 class="text-2xl font-bold text-white">Add New Event</h2>
                <p class="text-white/90 text-sm">Create a new event for your location</p>
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

            <!-- Event Type (typeEvenement) and City (villeId) Row -->
            <div class="grid md:grid-cols-2 gap-4">
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

              <!-- City (villeId) -->
              <div>
                <label for="villeId" class="block text-sm font-semibold text-gray-700 mb-2">
                  City <span class="text-red-500">*</span>
                </label>
                <div class="relative">
                  <Building2 class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <select
                    id="villeId"
                    v-model="eventForm.villeId"
                    class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7A1F1F] focus:border-transparent outline-none transition-all bg-white"
                    :class="{ 'border-red-500': formErrors.villeId }"
                  >
                    <option :value="null">Select city</option>
                    <option v-for="city in cityStore.cities" :key="city.id" :value="city.id">
                      {{ city.name }}
                    </option>
                  </select>
                </div>
                <p v-if="formErrors.villeId" class="mt-1 text-sm text-red-500">{{ formErrors.villeId }}</p>
              </div>
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
                Create Event
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

