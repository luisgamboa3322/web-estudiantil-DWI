import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-select-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
      <div class="bg-white rounded-lg shadow-xl p-8 max-w-md w-full mx-4">
        <div class="text-center">
          <div class="mb-6">
            <div class="mx-auto h-12 w-12 bg-green-100 rounded-full flex items-center justify-center">
              <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
          </div>
          
          <h2 class="text-2xl font-bold text-gray-900 mb-2">Login Exitoso!</h2>
          <p class="text-gray-600 mb-6">Bienvenido, {{ getUserName() }}</p>
          
          <div class="mb-6">
            <p class="text-sm text-gray-500">Has iniciado sesión como:</p>
            <p class="font-semibold text-gray-800">{{ getUserEmail() }}</p>
          </div>

          <div class="space-y-3">
            <button 
              *ngIf="canAccessAdmin()" 
              (click)="goToDashboard('admin')"
              class="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-4 rounded-lg transition duration-200">
              <svg class="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              </svg>
              Dashboard Administrador
            </button>

            <button 
              *ngIf="canAccessTeacher()" 
              (click)="goToDashboard('teacher')"
              class="w-full bg-green-600 hover:bg-green-700 text-white font-medium py-3 px-4 rounded-lg transition duration-200">
              <svg class="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
              Dashboard Docente
            </button>

            <button 
              *ngIf="canAccessStudent()" 
              (click)="goToDashboard('student')"
              class="w-full bg-purple-600 hover:bg-purple-700 text-white font-medium py-3 px-4 rounded-lg transition duration-200">
              <svg class="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path>
              </svg>
              Dashboard Estudiante
            </button>
          </div>

          <div class="mt-6 pt-6 border-t border-gray-200">
            <button 
              (click)="logout()"
              class="w-full text-gray-600 hover:text-gray-800 font-medium py-2 px-4 transition duration-200">
              <svg class="w-5 h-5 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
              </svg>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .bg-gradient-to-br {
      background: linear-gradient(to bottom right, #3B82F6, #8B5CF6);
    }
  `]
})
export class SelectDashboardComponent {
  user: any = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUser();
  }

  loadUser() {
    this.user = this.authService.getCurrentUser();
    console.log('SelectDashboard - Usuario actual:', this.user);
    
    if (!this.user) {
      console.log('No hay usuario, redirigiendo a login');
      this.router.navigate(['/login']);
      return;
    }

    console.log('Usuario autenticado, permisos disponibles:');
    console.log('Admin:', this.authService.canAccessAdmin());
    console.log('Teacher:', this.authService.canAccessTeacher());
    console.log('Student:', this.authService.canAccessStudent());

    // Si solo tiene acceso a un dashboard, redirigir automáticamente
    const accesses = [
      this.authService.canAccessAdmin(),
      this.authService.canAccessTeacher(),
      this.authService.canAccessStudent()
    ];
    const availableCount = accesses.filter(Boolean).length;

    if (availableCount === 1) {
      if (this.authService.canAccessAdmin()) {
        this.goToDashboard('admin');
      } else if (this.authService.canAccessTeacher()) {
        this.goToDashboard('teacher');
      } else if (this.authService.canAccessStudent()) {
        this.goToDashboard('student');
      }
    }
  }

  goToDashboard(role: string) {
    console.log(`Navegando a dashboard ${role}`);
    switch(role) {
      case 'admin':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'teacher':
        this.router.navigate(['/profesor/dashboard']);
        break;
      case 'student':
        this.router.navigate(['/student/dashboard']);
        break;
    }
  }

  logout() {
    console.log('Logout solicitado');
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logout exitoso, redirigiendo a login');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Error en logout:', error);
        // Aún así, redirigir a login
        this.router.navigate(['/login']);
      }
    });
  }

  getUserName(): string {
    if (!this.user) return 'Usuario';
    return this.user.nombre || this.user.email?.split('@')[0] || 'Usuario';
  }

  getUserEmail(): string {
    return this.user?.email || 'No disponible';
  }

  canAccessAdmin(): boolean {
    return this.authService.canAccessAdmin();
  }

  canAccessTeacher(): boolean {
    return this.authService.canAccessTeacher();
  }

  canAccessStudent(): boolean {
    return this.authService.canAccessStudent();
  }
}