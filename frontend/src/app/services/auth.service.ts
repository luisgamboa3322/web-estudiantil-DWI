import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { User, LoginRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8083/api';
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor() {
    // NO verificar autenticación automáticamente para evitar dependencia circular
    // La verificación se hace manualmente desde los componentes cuando sea necesario
  }

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          console.log('Login response:', response); // Debug log
          if (response.success && response.email && response.roles) {
            this.setCurrentUserFromResponse(response);
            this.setAuthenticationStatus(true);
          } else {
            console.error('Login response missing expected fields:', response);
          }
        })
      );
  }

  logout(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/logout`, {})
      .pipe(
        tap(() => {
          this.clearUser();
          this.setAuthenticationStatus(false);
        })
      );
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/me`)
      .pipe(
        tap(response => {
          console.log('Get current user response:', response); // Debug log
          if (response.success && response.email && response.roles) {
            this.setCurrentUserFromMeResponse(response);
            this.setAuthenticationStatus(true);
          } else {
            this.setAuthenticationStatus(false);
          }
        })
      );
  }

  checkSession(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/check`);
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getCurrentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUserValue();
    return user ? user.roles.includes(role) : false;
  }

  hasPermission(permission: string): boolean {
    const user = this.getCurrentUserValue();
    return user ? user.permissions.includes(permission) : false;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUserValue();
    if (!user) return false;
    return roles.some(role => user.roles.includes(role));
  }

  hasAnyPermission(permissions: string[]): boolean {
    const user = this.getCurrentUserValue();
    if (!user) return false;
    return permissions.some(permission => user.permissions.includes(permission));
  }

  private checkAuthentication(): void {
    this.checkSession().subscribe({
      next: (response) => {
        if (response.authenticated) {
          this.getCurrentUser().subscribe();
        }
      },
      error: (error) => {
        console.error('Session check failed:', error);
        this.setAuthenticationStatus(false);
      }
    });
  }

  private setCurrentUserFromResponse(response: any): void {
    // Mapear roles del backend a roles del frontend
    const mappedRoles = response.roles.map((role: string) => this.mapUserType(role));
    const primaryRole = mappedRoles.length > 0 ? mappedRoles[0] : 'ADMIN';
    
    const user: User = {
      email: response.email,
      userName: response.email,
      nombre: response.email,
      userType: primaryRole,
      roles: mappedRoles,
      permissions: response.permissions || [],
      hasMultipleRoles: mappedRoles.length > 1,
      totalCourses: 0,
      userEmail: response.email
    };
    console.log('Setting user from login response:', user); // Debug log
    this.currentUserSubject.next(user);
  }

  private setCurrentUserFromMeResponse(response: any): void {
    // Mapear roles del backend a roles del frontend
    const mappedRoles = response.roles.map((role: string) => this.mapUserType(role));
    const primaryRole = mappedRoles.length > 0 ? mappedRoles[0] : 'ADMIN';
    
    const user: User = {
      email: response.email,
      userName: response.email,
      nombre: response.email,
      userType: primaryRole,
      roles: mappedRoles,
      permissions: response.permissions || [],
      hasMultipleRoles: mappedRoles.length > 1,
      totalCourses: 0,
      userEmail: response.email
    };
    console.log('Setting user from me response:', user); // Debug log
    this.currentUserSubject.next(user);
  }

  private mapUserType(role: string): 'ADMIN' | 'TEACHER' | 'STUDENT' {
    // Mapear roles del backend a tipos del frontend
    switch (role?.toUpperCase()) {
      case 'ROLE_ADMIN':
        return 'ADMIN';
      case 'ROLE_TEACHER':
      case 'TEACHER':
        return 'TEACHER';
      case 'ROLE_STUDENT':
      case 'STUDENT':
        return 'STUDENT';
      default:
        return 'ADMIN'; // Valor por defecto
    }
  }

  private setAuthenticationStatus(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
  }

  private clearUser(): void {
    this.currentUserSubject.next(null);
  }
}