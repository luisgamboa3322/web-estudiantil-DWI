import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { User, AuthResponse, LoginRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private apiUrl = 'http://localhost:8083/api/auth';

  constructor(private http: HttpClient) {
    this.loadCurrentUser();
  }

  login(email: string, password: string): Observable<AuthResponse> {
    const loginRequest: LoginRequest = { email, password };
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest)
      .pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem('auth_token', response.token);
          }
          this.setCurrentUserFromResponse(response);
        })
      );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {})
      .pipe(
        tap(() => {
          localStorage.removeItem('auth_token');
          this.currentUserSubject.next(null);
        })
      );
  }

  me(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`)
      .pipe(
        tap(response => this.setCurrentUserFromMeResponse(response))
      );
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    const user = this.getCurrentUser();
    return user !== null;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles.includes(role) || false;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return roles.some(role => user?.roles.includes(role)) || false;
  }

  hasPermission(permission: string): boolean {
    const user = this.getCurrentUser();
    return user?.permissions?.includes(permission) || false;
  }

  hasAnyPermission(permissions: string[]): boolean {
    const user = this.getCurrentUser();
    return permissions.some(permission => user?.permissions?.includes(permission)) || false;
  }

  canAccessAdmin(): boolean {
    return this.hasPermission('ACCESS_ADMIN_DASHBOARD');
  }

  canAccessTeacher(): boolean {
    return this.hasPermission('ACCESS_TEACHER_DASHBOARD');
  }

  canAccessStudent(): boolean {
    return this.hasPermission('ACCESS_STUDENT_DASHBOARD');
  }

  private mapUserType(role: string): string {
    switch (role) {
      case 'ADMIN': return 'admin';
      case 'TEACHER': return 'teacher';
      case 'STUDENT': return 'student';
      default: return 'user';
    }
  }

  private setCurrentUserFromResponse(response: AuthResponse): void {
    const mappedRoles = response.roles.map(role => this.mapUserType(role));
    const user: User = {
      email: response.email,
      nombre: response.email.split('@')[0], // Usar parte del email como nombre temporal
      codigo: response.email.split('@')[0], // Usar parte del email como código temporal
      roles: mappedRoles,
      permissions: response.permissions || [],
      hasMultipleRoles: response.hasMultipleRoles
    };
    
    this.currentUserSubject.next(user);
  }

  private setCurrentUserFromMeResponse(response: AuthResponse): void {
    const mappedRoles = response.roles.map(role => this.mapUserType(role));
    const user: User = {
      email: response.email,
      nombre: response.email.split('@')[0], // Usar parte del email como nombre temporal
      codigo: response.email.split('@')[0], // Usar parte del email como código temporal
      roles: mappedRoles,
      permissions: response.permissions || [],
      hasMultipleRoles: response.hasMultipleRoles
    };
    
    this.currentUserSubject.next(user);
  }

  private loadCurrentUser(): void {
    const token = localStorage.getItem('auth_token');
    if (token) {
      this.me().subscribe({
        next: (response) => {
          // User loaded successfully
        },
        error: () => {
          localStorage.removeItem('auth_token');
          this.currentUserSubject.next(null);
        }
      });
    }
  }
}