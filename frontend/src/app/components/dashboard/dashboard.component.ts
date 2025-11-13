import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DashboardService, DashboardData } from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private router = inject(Router);

  dashboardData: DashboardData | null = null;
  currentUser: any = null;
  isLoading = false;
  errorMessage = '';

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    this.isLoading = true;
    this.errorMessage = '';

    this.dashboardService.getDashboard().subscribe({
      next: (data: DashboardData) => {
        this.dashboardData = data;
        // El usuario actual lo obtenemos del servicio de auth
        this.currentUser = this.authService.getCurrentUser();
      },
      error: (error) => {
        console.error('Error loading dashboard:', error);
        this.errorMessage = 'Error de conexión con el servidor';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Error logging out:', error);
        // Forzar navegación al login incluso si hay error
        this.router.navigate(['/login']);
      }
    });
  }

  getUserTypeName(): string {
    if (!this.currentUser) return '';
    
    switch (this.currentUser.userType) {
      case 'ADMIN':
        return 'Administrador';
      case 'TEACHER':
        return 'Profesor';
      case 'STUDENT':
        return 'Estudiante';
      default:
        return 'Usuario';
    }
  }

  getCourseStatusClass(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'ACTIVO':
        return 'status-active';
      case 'INACTIVO':
        return 'status-inactive';
      default:
        return 'status-unknown';
    }
  }

  getTotalStudents(): number {
    return this.dashboardData?.students.length || 0;
  }

  getTotalProfessors(): number {
    return this.dashboardData?.professors.length || 0;
  }

  getTotalCourses(): number {
    return this.dashboardData?.cursos.length || 0;
  }

  getTotalAssignments(): number {
    return this.dashboardData?.asignaciones.length || 0;
  }
}