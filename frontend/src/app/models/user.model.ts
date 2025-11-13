// Models/Interfaces para Angular
export interface User {
  id?: number;
  nombre: string;
  codigo: string;
  email: string;
  password?: string;
  especialidad?: string; // Para profesores
  roles: string[];
  permissions?: string[];
  hasMultipleRoles?: boolean;
}

export interface Admin extends User {
  // Admin hereda de User
}

export interface Professor extends User {
  especialidad: string;
}

export interface Student extends User {
  // Student hereda de User
}

export interface Curso {
  id?: number;
  nombre: string;
  codigo: string;
  descripcion?: string;
  estado: 'ACTIVO' | 'INACTIVO';
  profesor?: Professor;
}

export interface StudentCurso {
  id?: number;
  student: Student;
  curso: Curso;
  fechaAsignacion: string;
  estado: 'ACTIVO' | 'INACTIVO';
}

export interface Semana {
  id?: number;
  numeroSemana: number;
  titulo: string;
  descripcion?: string;
  curso: Curso;
}

export interface Material {
  id?: number;
  nombre: string;
  fileName?: string;
  fileType?: string;
  fileData?: string;
  descripcion?: string;
  semana: Semana;
  profesor: Professor;
  fechaCreacion: string;
}

export interface Tarea {
  id?: number;
  titulo: string;
  descripcion?: string;
  fechaLimite: string;
  puntosMaximos: number;
  semana: Semana;
  profesor: Professor;
  fechaCreacion: string;
}

export interface EntregaTarea {
  id?: number;
  contenido: string;
  fechaEntrega: string;
  calificacion: number;
  tarea: Tarea;
  student: Student;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  token: string;
  email: string;
  role: string; // Rol principal
  roles: string[];
  permissions: string[];
  authorities: any[];
  userType?: 'admin' | 'teacher' | 'student';
  hasMultipleRoles?: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}