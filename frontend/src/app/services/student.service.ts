import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CursoAsignado {
  id: number;
  curso: {
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
  };
  fechaAsignacion: string;
  estado: string;
}

export interface EstadoEntrega {
  entregada: boolean;
  calificada?: boolean;
  calificacion?: number;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8083/student';

  // Obtener datos del dashboard del estudiante
  getDashboardData(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/dashboard`);
  }

  // Obtener cursos asignados al estudiante actual
  getCursosAsignados(): Observable<CursoAsignado[]> {
    return this.http.get<CursoAsignado[]>(`${this.baseUrl}/api/cursos-asignados`);
  }

  // Obtener detalle de un curso específico
  getCursoDetalle(cursoId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/curso/${cursoId}`);
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

  // Obtener estado de entrega de una tarea
  getEstadoEntrega(tareaId: number): Observable<EstadoEntrega> {
    return this.http.get<EstadoEntrega>(`${this.baseUrl}/tareas/${tareaId}/entrega`);
  }

  // Entregar una tarea
  entregarTarea(tareaId: number, contenido: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/tareas/${tareaId}/entregar`, { contenido });
  }

  // Descargar material
  descargarMaterial(materialId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/materiales/${materialId}/download`, {
      responseType: 'blob'
    });
  }

  // Actualizar perfil del estudiante
  actualizarPerfil(studentData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/profile`, studentData);
  }
}