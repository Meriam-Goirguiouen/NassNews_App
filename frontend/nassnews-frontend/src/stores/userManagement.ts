import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export interface SystemUser {
  idUtilisateur: number;
  nom: string;
  email: string;
  role: 'UTILISATEUR' | 'ADMIN_COMMUNAL' | 'ADMIN_SYSTEME';
  villesFavorites?: number[];
  active?: boolean; // For activation/suspension
}

export const useUserManagementStore = defineStore('userManagement', () => {
  const users = ref<SystemUser[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Computed properties
  const citizens = computed(() => 
    users.value.filter(u => u.role === 'UTILISATEUR')
  );
  
  const communalAdmins = computed(() => 
    users.value.filter(u => u.role === 'ADMIN_COMMUNAL')
  );
  
  const systemAdmins = computed(() => 
    users.value.filter(u => u.role === 'ADMIN_SYSTEME')
  );

  const activeUsers = computed(() => 
    users.value.filter(u => u.active !== false)
  );

  const suspendedUsers = computed(() => 
    users.value.filter(u => u.active === false)
  );

  // Fetch all users
  async function fetchUsers() {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('http://localhost:8080/api/utilisateurs', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      const data = await response.json();
      users.value = data.map((u: any) => ({
        idUtilisateur: u.idUtilisateur,
        nom: u.nom,
        email: u.email,
        role: u.role,
        villesFavorites: u.villesFavorites || [],
        active: u.active !== undefined ? u.active : true, // Default to active
      }));
    } catch (err: any) {
      console.error('Error fetching users:', err);
      error.value = err.message || 'Failed to fetch users';
      users.value = [];
    } finally {
      loading.value = false;
    }
  }

  // Update user (for activation/suspension)
  async function updateUser(userId: number, updates: Partial<SystemUser>) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/utilisateurs/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updates),
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      const updatedUser = await response.json();
      const index = users.value.findIndex(u => u.idUtilisateur === userId);
      if (index !== -1) {
        users.value[index] = {
          ...users.value[index],
          ...updatedUser,
          active: updatedUser.active !== undefined ? updatedUser.active : true,
        };
      }
      
      return updatedUser;
    } catch (err: any) {
      console.error('Error updating user:', err);
      error.value = err.message || 'Failed to update user';
      throw err;
    } finally {
      loading.value = false;
    }
  }

  // Activate user
  async function activateUser(userId: number) {
    return updateUser(userId, { active: true });
  }

  // Suspend user
  async function suspendUser(userId: number) {
    return updateUser(userId, { active: false });
  }

  // Delete user
  async function deleteUser(userId: number) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/utilisateurs/${userId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      users.value = users.value.filter(u => u.idUtilisateur !== userId);
    } catch (err: any) {
      console.error('Error deleting user:', err);
      error.value = err.message || 'Failed to delete user';
      throw err;
    } finally {
      loading.value = false;
    }
  }

  return {
    users,
    loading,
    error,
    citizens,
    communalAdmins,
    systemAdmins,
    activeUsers,
    suspendedUsers,
    fetchUsers,
    updateUser,
    activateUser,
    suspendUser,
    deleteUser,
  };
});

