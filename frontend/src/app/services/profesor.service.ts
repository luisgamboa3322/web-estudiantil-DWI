import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Curso {
  id: number;
  nombre: string;
  codigo: string;
  descripcion: string;
  estado: string;
  profesor?: {
    id: number;
    nombre: string;
    email: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ProfesorService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8083/profesor';

  // Obtener datos del dashboard del profesor
  getDashboardData(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/dashboard`);
  }

  // Obtener cursos asignados al profesor actual
  getCursosByProfesor(): Observable<Curso[]> {
    // Los cursos vienen en el dashboard, pero también podemos obtenerlos directamente
    return this.http.get<Curso[]>(`${this.baseUrl}/cursos`);
  }

  // Obtener detalle de un curso específico
  getCursoDetalle(cursoId: number): Observable<Curso> {
    return this.http.get<Curso>(`${this.baseUrl}/cursos/${cursoId}`);
  }

  // Obtener semanas de un curso
  getSemanasByCurso(cursoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/cursos/${cursoId}/semanas`);
  }

  // Obtener materiales de una semana
  getMaterialesBySemana(semanaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/semanas/${semanaId}/materiales`);
  }

  // Obtener tareas de una semana
  getTareasBySemana(semanaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/semanas/${semanaId}/tareas`);
  }

  // Crear nueva semana
  createSemana(cursoId: number, semanaData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/cursos/${cursoId}/semanas`, semanaData);
  }

  // Crear nuevo material
  createMaterial(semanaId: number, materialData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/semanas/${semanaId}/materiales`, materialData);
  }

  // Crear nueva tarea
  createTarea(semanaId: number, tareaData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/semanas/${semanaId}/tareas`, tareaData);
  }

  // Eliminar material
  deleteMaterial(materialId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/materiales/${materialId}`);
  }

  // Eliminar tarea
  deleteTarea(tareaId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/tareas/${tareaId}`);
  }

  // Obtener entregas de una tarea
  getEntregasByTarea(tareaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tareas/${tareaId}/entregas`);
  }

  // Calificar entrega
  calificarEntrega(entregaId: number, calificacion: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/entregas/${entregaId}/calificar`, { calificacion });
  }

  // Actualizar perfil del profesor
  actualizarPerfil(professorData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/profile`, professorData);
  }
}