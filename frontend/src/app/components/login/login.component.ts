import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  showPassword = false;
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    console.log('LoginComponent iniciado');
    this.clearMessages();
  }

  onSubmit() {
    console.log('onSubmit() llamado con email:', this.email);
    
    if (this.isLoading) return;
    
    this.clearMessages();

    if (!this.email || !this.password) {
      this.errorMessage = 'Por favor, complete todos los campos';
      console.log('Error: Campos incompletos');
      return;
    }

    this.isLoading = true;
    console.log('Iniciando login con:', this.email);

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        console.log('Login exitoso! Respuesta:', response);
        this.isLoading = false;
        this.successMessage = 'Login exitoso, redirigiendo...';
        
        // Verificar usuario actual después del login
        const currentUser = this.authService.getCurrentUser();
        console.log('Usuario actual después del login:', currentUser);
        
        // Agregar más logging
        console.log('Roles del usuario:', currentUser?.roles);
        console.log('Permisos del usuario:', currentUser?.permissions);
        console.log('Múltiples roles:', currentUser?.hasMultipleRoles);
        
        // Redireccionar según los roles del usuario
        setTimeout(() => {
          if (response.hasMultipleRoles || (currentUser && currentUser.hasMultipleRoles)) {
            console.log('Redirigiendo a select-dashboard (múltiples roles)');
            this.router.navigate(['/select-dashboard']);
          } else if (currentUser && currentUser.roles?.includes('admin')) {
            console.log('Redirigiendo a admin/dashboard');
            this.router.navigate(['/admin/dashboard']);
          } else if (currentUser && currentUser.roles?.includes('teacher')) {
            console.log('Redirigiendo a profesor/dashboard');
            this.router.navigate(['/profesor/dashboard']);
          } else if (currentUser && currentUser.roles?.includes('student')) {
            console.log('Redirigiendo a student/dashboard');
            this.router.navigate(['/student/dashboard']);
          } else {
            console.log('No se pudo determinar el rol, redirigiendo a select-dashboard como fallback');
            this.router.navigate(['/select-dashboard']);
          }
        }, 1000);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error en login:', error);
        
        if (error.status === 0) {
          this.errorMessage = 'No se puede conectar con el servidor. Verifique que Spring Boot esté corriendo en puerto 8083';
        } else if (error.status === 401) {
          this.errorMessage = 'Credenciales inválidas';
        } else if (error.status === 403) {
          this.errorMessage = 'Acceso denegado';
        } else if (error.status === 404) {
          this.errorMessage = 'Servicio no encontrado. Verifique la URL del backend';
        } else {
          this.errorMessage = `Error del servidor: ${error.status} - ${error.message}`;
        }
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  // Credenciales de prueba para desarrollo
  useAdminCredentials() {
    this.email = 'admin@example.com';
    this.password = 'admin123';
    this.clearMessages();
  }

  useTeacherCredentials() {
    this.email = 'prof@example.com';
    this.password = 'teacher123';
    this.clearMessages();
  }

  useStudentCredentials() {
    this.email = 'student@example.com';
    this.password = 'student123';
    this.clearMessages();
  }

  // Método para probar conexión al backend
  testBackendConnection() {
    console.log('Probando conexión al backend...');
    this.errorMessage = '';
    
    this.authService.login('test@test.com', 'test').subscribe({
      next: (response) => {
        console.log('Backend conectado:', response);
      },
      error: (error) => {
        console.error('Error de conexión:', error);
        if (error.status === 0) {
          this.errorMessage = 'Backend no disponible. Asegúrate de que Spring Boot esté corriendo en puerto 8083';
        }
      }
    });
  }
}