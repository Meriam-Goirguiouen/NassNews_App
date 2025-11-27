import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Event } from '../types';

// Backend Evenement entity structure
interface Evenement {
  idEvenement: string; // MongoDB ObjectId string
  titre: string;
  description: string;
  lieu: string;
  dateEvenement: string; // ISO date string
  typeEvenement: string;
  villeId: string; // MongoDB ObjectId string
}

// Map backend Evenement to frontend Event
function mapEvenementToEvent(evenement: Evenement): Event {
  const eventDate = new Date(evenement.dateEvenement);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
    return {
      id: evenement.idEvenement,
      title: evenement.titre,
      date: evenement.dateEvenement,
      location: evenement.lieu,
      cityId: evenement.villeId, // Keep as string (MongoDB ObjectId)
      type: evenement.typeEvenement,
      description: evenement.description,
      status: eventDate >= today ? 'Upcoming' : 'Completed'
    };
}

export const useEventStore = defineStore('event', () => {
  const eventsList = ref<Event[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchByCityId(cityId: string) {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/events?villeId=${encodeURIComponent(cityId)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch events');
      }

      const evenements: Evenement[] = await response.json();
      eventsList.value = evenements.map(mapEvenementToEvent);
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch events';
      console.error('Error fetching events:', err);
      eventsList.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function createEvent(eventData: {
    titre: string;
    description: string;
    lieu: string;
    dateEvenement: string;
    typeEvenement: string;
    villeId: string;
  }): Promise<Event> {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch('http://localhost:8080/api/events', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(eventData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to create event');
      }

      const createdEvenement: Evenement = await response.json();
      const newEvent = mapEvenementToEvent(createdEvenement);
      eventsList.value.unshift(newEvent);
      return newEvent;
    } catch (err: any) {
      error.value = err.message || 'Failed to create event';
      console.error('Error creating event:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function updateEvent(eventId: string, eventData: {
    titre: string;
    description: string;
    lieu: string;
    dateEvenement: string;
    typeEvenement: string;
    villeId: string;
  }): Promise<Event> {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/events/${eventId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(eventData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to update event');
      }

      const updatedEvenement: Evenement = await response.json();
      const updatedEvent = mapEvenementToEvent(updatedEvenement);
      
      // Update in list
      const index = eventsList.value.findIndex(e => e.id === eventId);
      if (index !== -1) {
        eventsList.value[index] = updatedEvent;
      }
      
      return updatedEvent;
    } catch (err: any) {
      error.value = err.message || 'Failed to update event';
      console.error('Error updating event:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function deleteEvent(eventId: string): Promise<void> {
    loading.value = true;
    error.value = null;
    try {
      const response = await fetch(`http://localhost:8080/api/events/${eventId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete event');
      }

      // Remove from list
      eventsList.value = eventsList.value.filter(e => e.id !== eventId);
    } catch (err: any) {
      error.value = err.message || 'Failed to delete event';
      console.error('Error deleting event:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  return {
    eventsList,
    loading,
    error,
    fetchByCityId,
    createEvent,
    updateEvent,
    deleteEvent,
  };
});

