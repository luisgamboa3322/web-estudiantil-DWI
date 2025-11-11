export interface User {
  email: string;
  userName?: string;
  userEmail?: string;
  nombre: string;
  userType: 'ADMIN' | 'TEACHER' | 'STUDENT';
  roles: string[];
  permissions: string[];
  hasMultipleRoles: boolean;
  totalCourses?: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token?: string;
  type?: string;
  email: string;
  nombre: string;
  roles: string[];
  permissions: string[];
  userType: 'ADMIN' | 'TEACHER' | 'STUDENT';
  multiRole: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: string[];
  timestamp: string;
}