import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-select-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './select-dashboard.component.html',
  styleUrls: ['./select-dashboard.component.css']
})
export class SelectDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  user: any = null;
  isLoading = true;
  errorMessage = '';

  // Boolean properties para mostrar/ocultar botones según roles
  hasAdminAccess = false;
  hasTeacherAccess = false;
  hasStudentAccess = false;

  ngOnInit() {
    this.loadUserDashboardAccess();
  }

  loadUserDashboardAccess() {
    this.isLoading = true;
    this.errorMessage = '';

    this.user = this.authService.getCurrentUserValue();
    
    if (!this.user) {
      this.router.navigate(['/login']);
      return;
    }

    // Verificar accesos basados en roles del usuario
    this.hasAdminAccess = this.hasRole(['ROLE_ADMIN', 'ACCESS_ADMIN_DASHBOARD']);
    this.hasTeacherAccess = this.hasRole(['ROLE_TEACHER', 'ACCESS_TEACHER_DASHBOARD']);
    this.hasStudentAccess = this.hasRole(['ROLE_STUDENT', 'ACCESS_STUDENT_DASHBOARD']);

    // Si no tiene acceso a ningún dashboard, mostrar error
    if (!this.hasAdminAccess && !this.hasTeacherAccess && !this.hasStudentAccess) {
      this.errorMessage = 'No tienes acceso a ningún dashboard. Contacta al administrador.';
    }

    // Si solo tiene acceso a un dashboard, redirigir directamente
    const dashboardCount = (this.hasAdminAccess ? 1 : 0) + 
                          (this.hasTeacherAccess ? 1 : 0) + 
                          (this.hasStudentAccess ? 1 : 0);

    if (dashboardCount === 1) {
      if (this.hasAdminAccess) {
        this.navigateToDashboard('/admin/dashboard');
        return;
      } else if (this.hasTeacherAccess) {
        this.navigateToDashboard('/profesor/dashboard');
        return;
      } else if (this.hasStudentAccess) {
        this.navigateToDashboard('/student/dashboard');
        return;
      }
    }

    this.isLoading = false;
  }

  hasRole(roles: string[]): boolean {
    if (!this.user || !this.user.roles) {
      return false;
    }
    
    return roles.some(role => this.user.roles.includes(role));
  }

  navigateToDashboard(route: string) {
    this.router.navigate([route]);
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Error logging out:', error);
        this.router.navigate(['/login']);
      }
    });
  }

  getUserName(): string {
    return this.user?.userName || this.user?.nombre || 'Usuario';
  }

  getUserEmail(): string {
    return this.user?.email || '';
  }
}