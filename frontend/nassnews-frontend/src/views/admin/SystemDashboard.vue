<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { useUserManagementStore } from '../../stores/userManagement';
import { useCityStore } from '../../stores/city';
import logoWide from '../../assets/logoWide.png';
import { 
  Users, 
  UserCheck, 
  UserX, 
  MapPin, 
  Plus, 
  Edit, 
  Trash2, 
  LogOut, 
  BarChart3, 
  AlertTriangle,
  Activity,
  Server,
  Shield,
  FileText,
  RefreshCw,
  X
} from 'lucide-vue-next';

const router = useRouter();
const authStore = useAuthStore();
const userStore = useUserManagementStore();
const cityStore = useCityStore();

// Active section
const activeSection = ref('overview');
const searchQuery = ref('');

// City management
const showCityModal = ref(false);
const editingCity = ref<any>(null);
const cityForm = ref({
  nom: '',
  region: '',
  coordonnees: '',
  population: 0
});

// Admin Communal management
const showAdminModal = ref(false);
const adminForm = ref({
  nom: '',
  email: '',
  password: '',
  confirmPassword: '',
  villeId: ''
});
const communalAdmins = ref<any[]>([]);
const isLoadingAdmins = ref(false);

// Statistics
const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  suspendedUsers: 0,
  totalCities: 0,
  totalNews: 0,
  totalEvents: 0,
  communalAdmins: 0,
  systemAdmins: 0
});

// Error logs (mock for now)
const errorLogs = ref([
  { id: 1, timestamp: new Date(), level: 'ERROR', message: 'Database connection timeout', service: 'Database' },
  { id: 2, timestamp: new Date(Date.now() - 3600000), level: 'WARN', message: 'High memory usage detected', service: 'Server' },
  { id: 3, timestamp: new Date(Date.now() - 7200000), level: 'INFO', message: 'Scheduled backup completed', service: 'Backup' },
]);

onMounted(async () => {
  await loadAllData();
  calculateStats();
});

const loadAllData = async () => {
  await Promise.all([
    userStore.fetchUsers(),
    cityStore.fetchCities(),
    fetchCommunalAdmins(),
    // Note: News and events would need to be fetched for all cities or aggregated
  ]);
};

const fetchCommunalAdmins = async () => {
  isLoadingAdmins.value = true;
  try {
    const response = await fetch('http://localhost:8080/api/utilisateurs/admin-communal', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response.ok) {
      communalAdmins.value = await response.json();
    } else {
      console.error('Failed to fetch communal admins');
      communalAdmins.value = [];
    }
  } catch (error) {
    console.error('Error fetching communal admins:', error);
    communalAdmins.value = [];
  } finally {
    isLoadingAdmins.value = false;
  }
};

const calculateStats = () => {
  stats.value = {
    totalUsers: userStore.users.length,
    activeUsers: userStore.activeUsers.length,
    suspendedUsers: userStore.suspendedUsers.length,
    totalCities: cityStore.cities.length,
    totalNews: 0, // Would need to fetch from backend
    totalEvents: 0, // Would need to fetch from backend
    communalAdmins: communalAdmins.value.length,
    systemAdmins: userStore.systemAdmins.length,
  };
};

// User management
const handleActivateUser = async (userId: number) => {
  if (confirm('Are you sure you want to activate this user?')) {
    try {
      await userStore.activateUser(userId);
      await fetchCommunalAdmins();
      calculateStats();
    } catch (error: any) {
      alert(error.message || 'Failed to activate user');
    }
  }
};

const handleSuspendUser = async (userId: number) => {
  if (confirm('Are you sure you want to suspend this user?')) {
    try {
      await userStore.suspendUser(userId);
      await fetchCommunalAdmins();
      calculateStats();
    } catch (error: any) {
      alert(error.message || 'Failed to suspend user');
    }
  }
};

const handleDeleteUser = async (userId: number) => {
  if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
    try {
      await userStore.deleteUser(userId);
      await fetchCommunalAdmins();
      calculateStats();
    } catch (error: any) {
      alert(error.message || 'Failed to delete user');
    }
  }
};

// City management
const openCityModal = (city?: any) => {
  if (city) {
    editingCity.value = city;
    cityForm.value = {
      nom: city.name || city.nom || '',
      region: city.region || '',
      coordonnees: city.coords || city.coordonnees || '',
      population: city.population || 0
    };
  } else {
    editingCity.value = null;
    cityForm.value = {
      nom: '',
      region: '',
      coordonnees: '',
      population: 0
    };
  }
  showCityModal.value = true;
};

const closeCityModal = () => {
  showCityModal.value = false;
  editingCity.value = null;
  cityForm.value = {
    nom: '',
    region: '',
    coordonnees: '',
    population: 0
  };
};

const handleSaveCity = async () => {
  try {
    const cityData = {
      nom: cityForm.value.nom,
      region: cityForm.value.region,
      coordonnees: cityForm.value.coordonnees,
      population: cityForm.value.population
    };

    if (editingCity.value) {
      // Update existing city
      const response = await fetch(`http://localhost:8080/api/cities/${editingCity.value.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cityData),
      });
      if (!response.ok) throw new Error('Failed to update city');
    } else {
      // Create new city
      const response = await fetch('http://localhost:8080/api/cities', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(cityData),
      });
      if (!response.ok) throw new Error('Failed to create city');
    }

    await cityStore.fetchCities();
    calculateStats();
    closeCityModal();
  } catch (error: any) {
    alert(error.message || 'Failed to save city');
  }
};

const handleDeleteCity = async (cityId: string) => {
  if (confirm('Are you sure you want to delete this city? This action cannot be undone.')) {
    try {
      const response = await fetch(`http://localhost:8080/api/cities/${cityId}`, {
        method: 'DELETE',
      });
      if (!response.ok) throw new Error('Failed to delete city');
      await cityStore.fetchCities();
      calculateStats();
    } catch (error: any) {
      alert(error.message || 'Failed to delete city');
    }
  }
};

// Filtered lists
const filteredUsers = computed(() => {
  if (!searchQuery.value) return userStore.users;
  const query = searchQuery.value.toLowerCase();
  return userStore.users.filter(u => 
    u.nom.toLowerCase().includes(query) || 
    u.email.toLowerCase().includes(query)
  );
});

const filteredCities = computed(() => {
  if (!searchQuery.value) return cityStore.cities;
  const query = searchQuery.value.toLowerCase();
  return cityStore.cities.filter(c => 
    c.name.toLowerCase().includes(query) || 
    c.region.toLowerCase().includes(query)
  );
});

const handleLogout = () => {
  authStore.logout();
  router.push('/');
};

const refreshData = async () => {
  await loadAllData();
  calculateStats();
};

// Admin Communal management
const openAdminModal = () => {
  adminForm.value = {
    nom: '',
    email: '',
    password: '',
    confirmPassword: '',
    villeId: ''
  };
  showAdminModal.value = true;
};

const closeAdminModal = () => {
  showAdminModal.value = false;
  adminForm.value = {
    nom: '',
    email: '',
    password: '',
    confirmPassword: '',
    villeId: ''
  };
};

const handleCreateAdminCommunal = async () => {
  // Validation
  if (!adminForm.value.nom || !adminForm.value.email || !adminForm.value.password || !adminForm.value.villeId) {
    alert('Please fill in all required fields');
    return;
  }

  if (adminForm.value.password !== adminForm.value.confirmPassword) {
    alert('Passwords do not match');
    return;
  }

  if (adminForm.value.password.length < 6) {
    alert('Password must be at least 6 characters long');
    return;
  }

  try {
    const response = await fetch('http://localhost:8080/api/utilisateurs/admin-communal', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nom: adminForm.value.nom,
        email: adminForm.value.email,
        password: adminForm.value.password,
        villeId: adminForm.value.villeId
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: 'Failed to create admin communal' }));
      throw new Error(errorData.message || 'Failed to create admin communal');
    }

    await fetchCommunalAdmins();
    await userStore.fetchUsers();
    calculateStats();
    closeAdminModal();
    alert('Admin communal created successfully!');
  } catch (error: any) {
    alert(error.message || 'Failed to create admin communal');
  }
};

const getCityName = (villeId: string) => {
  const city = cityStore.cities.find(c => c.id === villeId);
  return city ? city.name : 'Unknown City';
};
</script>

<template>
  <div class="min-h-screen bg-[#F4EDE4] flex">
    <!-- Left Sidebar -->
    <aside class="w-64 bg-white shadow-lg fixed left-0 top-0 h-screen z-40 overflow-y-auto">
      <div class="p-6">
        <router-link to="/" class="block mb-8">
          <img :src="logoWide" alt="NassNews Logo" class="h-10 w-auto" />
        </router-link>

        <nav class="space-y-2">
          <button
            @click="activeSection = 'overview'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'overview' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <BarChart3 :size="20" />
            <span class="font-medium">Overview</span>
          </button>

          <button
            @click="activeSection = 'users'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'users' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Users :size="20" />
            <span class="font-medium">User Management</span>
          </button>

          <button
            @click="activeSection = 'communal-admins'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'communal-admins' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Shield :size="20" />
            <span class="font-medium">Communal Admins</span>
          </button>

          <button
            @click="activeSection = 'cities'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'cities' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <MapPin :size="20" />
            <span class="font-medium">City Management</span>
          </button>

          <button
            @click="activeSection = 'monitoring'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'monitoring' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <Activity :size="20" />
            <span class="font-medium">Service Monitoring</span>
          </button>

          <button
            @click="activeSection = 'errors'"
            :class="[
              'w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-colors',
              activeSection === 'errors' 
                ? 'bg-[#7A1F1F] text-white' 
                : 'text-gray-700 hover:bg-[#F4EDE4]'
            ]"
          >
            <AlertTriangle :size="20" />
            <span class="font-medium">Error Reports</span>
          </button>
        </nav>

        <div class="mt-8 pt-8 border-t border-gray-200">
          <button
            @click="handleLogout"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-gray-700 hover:bg-[#F4EDE4] transition-colors"
          >
            <LogOut :size="20" />
            <span class="font-medium">Logout</span>
          </button>
        </div>
      </div>
    </aside>

    <!-- Main Content Area -->
    <div class="flex-1 ml-64">
      <!-- Top Navbar -->
      <nav class="bg-white shadow-sm sticky top-0 z-30">
        <div class="px-6 py-4">
          <div class="flex items-center justify-between">
            <div>
              <h1 class="text-2xl font-bold text-gray-900">System Administration</h1>
              <p class="text-sm text-gray-500">Welcome, {{ authStore.currentUser?.username }}</p>
            </div>
            <button
              @click="refreshData"
              class="flex items-center gap-2 px-4 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              <RefreshCw :size="18" />
              <span>Refresh</span>
            </button>
        </div>
      </div>
    </nav>

      <!-- Main Content -->
      <main class="p-6">
        <!-- Overview Section -->
        <section v-if="activeSection === 'overview'" class="space-y-6">
          <h2 class="text-3xl font-bold text-gray-900 mb-6">System Overview</h2>
          
          <!-- Statistics Cards -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-500">Total Users</p>
                  <p class="text-3xl font-bold text-gray-900">{{ stats.totalUsers }}</p>
                </div>
                <Users :size="40" class="text-[#7A1F1F]" />
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-500">Active Users</p>
                  <p class="text-3xl font-bold text-green-600">{{ stats.activeUsers }}</p>
                </div>
                <UserCheck :size="40" class="text-green-600" />
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-500">Total Cities</p>
                  <p class="text-3xl font-bold text-gray-900">{{ stats.totalCities }}</p>
                </div>
                <MapPin :size="40" class="text-[#7A1F1F]" />
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-500">Communal Admins</p>
                  <p class="text-3xl font-bold text-gray-900">{{ stats.communalAdmins }}</p>
                </div>
                <Shield :size="40" class="text-[#7A1F1F]" />
              </div>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="bg-white rounded-3xl p-6 shadow-md">
            <h3 class="text-xl font-bold text-gray-900 mb-4">Quick Actions</h3>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <button
                @click="activeSection = 'users'"
                class="p-4 border-2 border-gray-200 rounded-xl hover:border-[#7A1F1F] transition-colors text-left"
              >
                <Users :size="24" class="text-[#7A1F1F] mb-2" />
                <p class="font-medium text-gray-900">Manage Users</p>
              </button>
              <button
                @click="activeSection = 'cities'"
                class="p-4 border-2 border-gray-200 rounded-xl hover:border-[#7A1F1F] transition-colors text-left"
              >
                <MapPin :size="24" class="text-[#7A1F1F] mb-2" />
                <p class="font-medium text-gray-900">Manage Cities</p>
              </button>
              <button
                @click="activeSection = 'monitoring'"
                class="p-4 border-2 border-gray-200 rounded-xl hover:border-[#7A1F1F] transition-colors text-left"
              >
                <Activity :size="24" class="text-[#7A1F1F] mb-2" />
                <p class="font-medium text-gray-900">Monitor Services</p>
              </button>
              <button
                @click="activeSection = 'errors'"
                class="p-4 border-2 border-gray-200 rounded-xl hover:border-[#7A1F1F] transition-colors text-left"
              >
                <AlertTriangle :size="24" class="text-[#7A1F1F] mb-2" />
                <p class="font-medium text-gray-900">View Errors</p>
              </button>
            </div>
          </div>
        </section>

        <!-- User Management Section -->
        <section v-if="activeSection === 'users'" class="space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-3xl font-bold text-gray-900">User Management</h2>
          </div>

          <!-- Search -->
          <div class="bg-white rounded-3xl p-6 shadow-md">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search users by name or email..."
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <!-- Users Table -->
          <div class="bg-white rounded-3xl shadow-md overflow-hidden">
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="user in filteredUsers" :key="user.idUtilisateur" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ user.nom }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-gray-600">{{ user.email }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs rounded-full" :class="{
                        'bg-blue-100 text-blue-800': user.role === 'UTILISATEUR',
                        'bg-purple-100 text-purple-800': user.role === 'ADMIN_COMMUNAL',
                        'bg-red-100 text-red-800': user.role === 'ADMIN_SYSTEME'
                      }">
                        {{ user.role }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs rounded-full" :class="{
                        'bg-green-100 text-green-800': user.active !== false,
                        'bg-red-100 text-red-800': user.active === false
                      }">
                        {{ user.active !== false ? 'Active' : 'Suspended' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <button
                          v-if="user.active === false"
                          @click="handleActivateUser(user.idUtilisateur)"
                          class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors"
                          title="Activate"
                        >
                          <UserCheck :size="18" />
                        </button>
                        <button
                          v-else
                          @click="handleSuspendUser(user.idUtilisateur)"
                          class="p-2 text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
                          title="Suspend"
                        >
                          <UserX :size="18" />
                        </button>
                        <button
                          @click="handleDeleteUser(user.idUtilisateur)"
                          class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Delete"
                        >
                          <Trash2 :size="18" />
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </section>

        <!-- Communal Admins Section -->
        <section v-if="activeSection === 'communal-admins'" class="space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-3xl font-bold text-gray-900">Communal Administrator Management</h2>
            <button
              @click="openAdminModal()"
              class="flex items-center gap-2 px-4 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              <Plus :size="18" />
              <span>Add Admin Communal</span>
            </button>
          </div>

          <div v-if="isLoadingAdmins" class="bg-white rounded-3xl p-6 shadow-md text-center">
            <p class="text-gray-600">Loading admin communaux...</p>
          </div>

          <div v-else-if="communalAdmins.length === 0" class="bg-white rounded-3xl p-6 shadow-md text-center">
            <Shield :size="48" class="mx-auto text-gray-400 mb-4" />
            <p class="text-gray-600">No communal administrators found.</p>
            <p class="text-sm text-gray-500 mt-2">Click "Add Admin Communal" to create one.</p>
          </div>

          <div v-else class="bg-white rounded-3xl shadow-md overflow-hidden">
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Associated City</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="admin in communalAdmins" :key="admin.idUtilisateur" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ admin.nom }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-gray-600">{{ admin.email }}</td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <MapPin :size="16" class="text-[#7A1F1F]" />
                        <span class="text-gray-700">{{ getCityName(admin.villeAssociee) }}</span>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs rounded-full" :class="{
                        'bg-green-100 text-green-800': admin.active !== false,
                        'bg-red-100 text-red-800': admin.active === false
                      }">
                        {{ admin.active !== false ? 'Active' : 'Suspended' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center gap-2">
                        <button
                          v-if="admin.active === false"
                          @click="handleActivateUser(admin.idUtilisateur)"
                          class="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors"
                          title="Activate"
                        >
                          <UserCheck :size="18" />
                        </button>
                        <button
                          v-else
                          @click="handleSuspendUser(admin.idUtilisateur)"
                          class="p-2 text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
                          title="Suspend"
                        >
                          <UserX :size="18" />
                        </button>
                        <button
                          @click="handleDeleteUser(admin.idUtilisateur)"
                          class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Delete"
                        >
                          <Trash2 :size="18" />
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </section>

        <!-- City Management Section -->
        <section v-if="activeSection === 'cities'" class="space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-3xl font-bold text-gray-900">City Management</h2>
            <button
              @click="openCityModal()"
              class="flex items-center gap-2 px-4 py-2 bg-[#7A1F1F] text-white rounded-lg hover:bg-[#6A1A1A] transition-colors"
            >
              <Plus :size="18" />
              <span>Add City</span>
            </button>
          </div>

          <!-- Search -->
          <div class="bg-white rounded-3xl p-6 shadow-md">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search cities..."
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <!-- Cities Grid -->
          <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div
              v-for="city in filteredCities"
              :key="city.id"
              class="bg-white rounded-3xl p-6 shadow-md hover:shadow-lg transition-shadow"
            >
              <div class="flex items-start justify-between mb-4">
                <div class="flex items-center gap-3">
                  <MapPin :size="24" class="text-[#7A1F1F]" />
                  <div>
                    <h3 class="text-xl font-bold text-gray-900">{{ city.name }}</h3>
                    <p class="text-sm text-gray-500">{{ city.region }}</p>
                  </div>
                </div>
                <div class="flex items-center gap-2">
                  <button
                    @click="openCityModal(city)"
                    class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                    title="Edit"
                  >
                    <Edit :size="18" />
                  </button>
                  <button
                    @click="handleDeleteCity(city.id)"
                    class="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    title="Delete"
                  >
                    <Trash2 :size="18" />
                  </button>
                </div>
              </div>
              <div class="text-sm text-gray-600">
                <p v-if="city.population">Population: {{ city.population.toLocaleString() }}</p>
                <p v-if="city.coords">Coordinates: {{ city.coords }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- Service Monitoring Section -->
        <section v-if="activeSection === 'monitoring'" class="space-y-6">
          <h2 class="text-3xl font-bold text-gray-900">Service Monitoring</h2>

          <div class="grid md:grid-cols-2 gap-6">
            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center gap-3 mb-4">
                <Server :size="24" class="text-[#7A1F1F]" />
                <h3 class="text-xl font-bold text-gray-900">Database</h3>
              </div>
              <div class="space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Status</span>
                  <span class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">Online</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Response Time</span>
                  <span class="font-medium">45ms</span>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center gap-3 mb-4">
                <FileText :size="24" class="text-[#7A1F1F]" />
                <h3 class="text-xl font-bold text-gray-900">News Service</h3>
              </div>
              <div class="space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Status</span>
                  <span class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">Online</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Total Articles</span>
                  <span class="font-medium">{{ stats.totalNews }}</span>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center gap-3 mb-4">
                <Activity :size="24" class="text-[#7A1F1F]" />
                <h3 class="text-xl font-bold text-gray-900">Events Service</h3>
              </div>
              <div class="space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Status</span>
                  <span class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">Online</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Total Events</span>
                  <span class="font-medium">{{ stats.totalEvents }}</span>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-3xl p-6 shadow-md">
              <div class="flex items-center gap-3 mb-4">
                <MapPin :size="24" class="text-[#7A1F1F]" />
                <h3 class="text-xl font-bold text-gray-900">Location Service</h3>
              </div>
              <div class="space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Status</span>
                  <span class="px-2 py-1 text-xs rounded-full bg-green-100 text-green-800">Online</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-gray-600">Cities Available</span>
                  <span class="font-medium">{{ stats.totalCities }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Error Reports Section -->
        <section v-if="activeSection === 'errors'" class="space-y-6">
          <h2 class="text-3xl font-bold text-gray-900">Error Reports & Logs</h2>

          <div class="bg-white rounded-3xl shadow-md overflow-hidden">
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Timestamp</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Level</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Service</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Message</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="log in errorLogs" :key="log.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {{ log.timestamp.toLocaleString() }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span class="px-2 py-1 text-xs rounded-full" :class="{
                        'bg-red-100 text-red-800': log.level === 'ERROR',
                        'bg-yellow-100 text-yellow-800': log.level === 'WARN',
                        'bg-blue-100 text-blue-800': log.level === 'INFO'
                      }">
                        {{ log.level }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.service }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ log.message }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- Admin Communal Modal Popup -->
    <Transition name="modal">
      <div
        v-if="showAdminModal"
        class="fixed inset-0 bg-black bg-opacity-60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
        @click.self="closeAdminModal"
      >
        <div class="bg-white rounded-3xl p-8 max-w-lg w-full shadow-2xl transform transition-all animate-popup max-h-[90vh] overflow-y-auto">
          <div class="flex items-center justify-between mb-6 pb-4 border-b border-gray-200">
            <div class="flex items-center gap-3">
              <div class="p-2 bg-[#7A1F1F] bg-opacity-10 rounded-lg">
                <Shield :size="24" class="text-[#7A1F1F]" />
              </div>
              <h3 class="text-2xl font-bold text-gray-900">Add Admin Communal</h3>
            </div>
            <button
              @click="closeAdminModal"
              class="text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full p-1 transition-colors"
            >
              <X :size="24" />
            </button>
          </div>

        <form @submit.prevent="handleCreateAdminCommunal" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Full Name *</label>
            <input
              v-model="adminForm.nom"
              type="text"
              required
              placeholder="Enter admin's full name"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
            <input
              v-model="adminForm.email"
              type="email"
              required
              placeholder="admin@example.com"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Password *</label>
            <input
              v-model="adminForm.password"
              type="password"
              required
              placeholder="Minimum 6 characters"
              minlength="6"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Confirm Password *</label>
            <input
              v-model="adminForm.confirmPassword"
              type="password"
              required
              placeholder="Re-enter password"
              minlength="6"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Associated City *</label>
            <select
              v-model="adminForm.villeId"
              required
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            >
              <option value="">Select a city</option>
              <option
                v-for="city in cityStore.cities"
                :key="city.id"
                :value="city.id"
              >
                {{ city.name }}
              </option>
            </select>
          </div>

          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="closeAdminModal"
              class="flex-1 px-4 py-2 border-2 border-gray-200 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="flex-1 px-4 py-2 bg-[#7A1F1F] text-white rounded-xl hover:bg-[#6A1A1A] transition-colors"
            >
              Create Admin
            </button>
          </div>
        </form>
        </div>
      </div>
    </Transition>

    <!-- City Modal -->
    <div
      v-if="showCityModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click.self="closeCityModal"
    >
      <div class="bg-white rounded-3xl p-8 max-w-md w-full mx-4">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-2xl font-bold text-gray-900">
            {{ editingCity ? 'Edit City' : 'Add New City' }}
          </h3>
          <button
            @click="closeCityModal"
            class="text-gray-400 hover:text-gray-600"
          >
            <X :size="24" />
          </button>
        </div>

        <form @submit.prevent="handleSaveCity" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">City Name *</label>
            <input
              v-model="cityForm.nom"
              type="text"
              required
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Region *</label>
            <input
              v-model="cityForm.region"
              type="text"
              required
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Coordinates</label>
            <input
              v-model="cityForm.coordonnees"
              type="text"
              placeholder="lat,lng"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Population</label>
            <input
              v-model.number="cityForm.population"
              type="number"
              min="0"
              class="w-full px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#7A1F1F] focus:outline-none"
            />
          </div>

          <div class="flex gap-4 pt-4">
            <button
              type="button"
              @click="closeCityModal"
              class="flex-1 px-4 py-2 border-2 border-gray-200 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="flex-1 px-4 py-2 bg-[#7A1F1F] text-white rounded-xl hover:bg-[#6A1A1A] transition-colors"
            >
              {{ editingCity ? 'Update' : 'Create' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Modal transition animations */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .animate-popup,
.modal-leave-active .animate-popup {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.modal-enter-from .animate-popup {
  transform: scale(0.9) translateY(-20px);
  opacity: 0;
}

.modal-leave-to .animate-popup {
  transform: scale(0.95);
  opacity: 0;
}

/* Popup animation */
@keyframes popup {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-popup {
  animation: popup 0.3s ease-out;
}
</style>
