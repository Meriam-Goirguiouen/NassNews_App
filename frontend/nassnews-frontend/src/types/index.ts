// Data Models matching Spring Boot Entities

export interface City {
  id: string; // MongoDB ObjectId string
  name: string;
  region: string;
  population?: number;
  coords?: string; // Coordinates from backend
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN_COMMUNAL' | 'ADMIN_SYSTEM';
  favoriteCities?: City[];
  cityId?: string | number; // Can be string (MongoDB ID) or number (for backward compatibility)
}

export interface News {
  id: string | number; // Can be string (MongoDB ObjectId) or number (for backward compatibility)
  title: string;
  summary: string;
  content?: string;
  datePublication: string;
  cityId: string | number; // Can be string (MongoDB ObjectId) or number (for backward compatibility)
  imageUrl?: string;
  author?: string;
  category?: string;
}

export interface Event {
  id: string; // MongoDB ObjectId string
  title: string;
  date: string;
  location: string;
  cityId: string; // MongoDB ObjectId string
  type: string;
  description?: string;
  imageUrl?: string;
  time?: string;
  status?: 'Upcoming' | 'Completed';
}

export type UserRole = 'USER' | 'ADMIN_COMMUNAL' | 'ADMIN_SYSTEM';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  role: UserRole;
  cityId?: number;
}


  id: string; // MongoDB ObjectId string
  title: string;

  date: string;

  location: string;

  cityId: string; // MongoDB ObjectId string
  type: string;

  description?: string;

  imageUrl?: string;

  time?: string;

  status?: 'Upcoming' | 'Completed';
}



export type UserRole = 'CITIZEN' | 'ADMIN_COMMUNAL' | 'ADMIN_SYSTEM';



export interface LoginRequest {

  email: string;

  password: string;

}



export interface RegisterRequest {

  username: string;

  email: string;

  password: string;

  role: UserRole;

  cityId?: number;

}




