// Data Models matching Spring Boot Entities

export interface City {
  id: number;
  name: string;
  region: string;
  population?: number;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CITIZEN' | 'ADMIN_COMMUNAL' | 'ADMIN_SYSTEM';
  favoriteCities?: City[];
  cityId?: number;
}

export interface News {
  id: number;
  title: string;
  summary: string;
  content?: string;
  datePublication: string;
  cityId: number;
  imageUrl?: string;
  author?: string;
  category?: string;
}

export interface Event {
  id: number;
  title: string;
  date: string;
  location: string;
  cityId: number;
  type: string;
  description?: string;
  imageUrl?: string;
  time?: string;
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

