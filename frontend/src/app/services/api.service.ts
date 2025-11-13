import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, Admin, Professor, Student, Curso, StudentCurso, ApiResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8083/api';

  constructor(private http: HttpClient) {}

  // ============== ADMIN API ==============
  getAdminDashboard(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/admin/dashboard`);
  }

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.apiUrl}/admin/students`);
  }

  getProfessors(): Observable<Professor[]> {
    return this.http.get<Professor[]>(`${this.apiUrl}/admin/profesores`);
  }

  getCourses(): Observable<Curso[]> {
    return this.http.get<Curso[]>(`${this.apiUrl}/admin/cursos`);
  }

  createStudent(student: Student): Observable<Student> {
    return this.http.post<Student>(`${this.apiUrl}/admin/students`, student);
  }

  updateStudent(id: number, student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/admin/students/${id}`, student);
  }

  deleteStudent(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/students/${id}`);
  }

  createProfessor(professor: Professor): Observable<Professor> {
    return this.http.post<Professor>(`${this.apiUrl}/admin/profesores`, professor);
  }

  updateProfessor(id: number, professor: Professor): Observable<Professor> {
    return this.http.put<Professor>(`${this.apiUrl}/admin/profesores/${id}`, professor);
  }

  deleteProfessor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/profesores/${id}`);
  }

  createAdmin(admin: Admin): Observable<Admin> {
    return this.http.post<Admin>(`${this.apiUrl}/admin/admins`, admin);
  }

  updateAdmin(id: number, admin: Admin): Observable<Admin> {
    return this.http.put<Admin>(`${this.apiUrl}/admin/admins/${id}`, admin);
  }

  deleteAdmin(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/admins/${id}`);
  }

  assignCourseToStudent(studentId: number, courseId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/asignaciones`, { studentId, cursoId: courseId });
  }

  unassignCourseFromStudent(assignationId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/asignaciones/${assignationId}`);
  }

  // ============== TEACHER API ==============
  getTeacherDashboard(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`http://localhost:8083/profesor/api/dashboard`);
  }

  getTeacherCourses(): Observable<Curso[]> {
    return this.http.get<Curso[]>(`http://localhost:8083/profesor/cursos`);
  }

  updateTeacherProfile(changes: any): Observable<Professor> {
    return this.http.put<Professor>(`http://localhost:8083/profesor/profile`, changes);
  }

  // ============== STUDENT API ==============
  getStudentDashboard(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`http://localhost:8083/student/api/dashboard`);
  }

  getStudentCourses(): Observable<StudentCurso[]> {
    return this.http.get<StudentCurso[]>(`http://localhost:8083/student/api/cursos-asignados`);
  }

  getCourseDetail(courseId: number): Observable<Curso> {
    return this.http.get<Curso>(`http://localhost:8083/student/api/curso/${courseId}`);
  }

  getCourseWeeks(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8083/student/cursos/${courseId}/semanas`);
  }

  getWeekContent(weekId: number): Observable<{ materials: any[], tasks: any[] }> {
    // Este endpoint no existe, necesitamos hacer dos llamadas separadas
    return this.http.get<{ materials: any[], tasks: any[] }>(`http://localhost:8083/student/semanas/${weekId}/contenido`);
  }

  submitTask(taskId: number, content: string): Observable<any> {
    return this.http.post(`http://localhost:8083/student/tareas/${taskId}/entregar`, { contenido: content });
  }

  downloadMaterial(materialId: number): Observable<Blob> {
    return this.http.get(`http://localhost:8083/student/materiales/${materialId}/download`, { responseType: 'blob' });
  }

  // ============== GENERAL API ==============
  createCourse(course: Curso): Observable<Curso> {
    return this.http.post<Curso>(`${this.apiUrl}/cursos`, course);
  }

  updateCourse(id: number, course: Curso): Observable<Curso> {
    return this.http.put<Curso>(`${this.apiUrl}/cursos/${id}`, course);
  }

  deleteCourse(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cursos/${id}`);
  }

  // Dashboard redirection
  getDashboardAccess(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dashboard/access`);
  }

  redirectToDashboard(dashboardType: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/redirect/${dashboardType}`);
  }
}