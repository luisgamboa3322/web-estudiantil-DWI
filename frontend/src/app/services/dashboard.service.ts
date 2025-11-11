import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardData {
  students: any[];
  professors: any[];
  admins: any[];
  cursos: any[];
  asignaciones: any[];
  stats: {
    totalStudents: number;
    totalProfessors: number;
    totalAdmins: number;
    totalCursos: number;
    totalAsignaciones: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8083/api';

  getDashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.apiUrl}/dashboard`);
  }

  getAdminDashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${this.apiUrl}/admin/dashboard`);
  }

  // Usar endpoints existentes del CursoController
  getCursos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cursos`);
  }

  // Usar endpoints existentes del AdminController
  getStudents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/students`);
  }

  getProfessors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/profesores`);
  }

  getAsignaciones(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/asignaciones`);
  }

  // CRUD Operations usando endpoints existentes
  createStudent(student: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/students`, student);
  }

  updateStudent(id: number, student: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/students/${id}`, student);
  }

  deleteStudent(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/admin/students/${id}`);
  }

  createProfessor(professor: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/profesores`, professor);
  }

  updateProfessor(id: number, professor: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/profesores/${id}`, professor);
  }

  deleteProfessor(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/admin/profesores/${id}`);
  }

  createCurso(curso: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cursos`, curso);
  }

  updateCurso(id: number, curso: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/cursos/${id}`, curso);
  }

  deleteCurso(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/cursos/${id}`);
  }

  createAsignacion(asignacion: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/asignaciones`, asignacion);
  }

  deleteAsignacion(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/admin/asignaciones/${id}`);
  }
}