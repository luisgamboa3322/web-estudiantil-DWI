import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private http = inject(HttpClient);
  
  loading = false;
  errorMessage = '';
  successMessage = '';
  showPassword = false;
  
  form = {
    email: '',
    password: ''
  };

  ngOnInit() {
    // Verificar si hay mensajes de error o éxito en la URL
    this.route.queryParams.subscribe(params => {
      if (params['error']) {
        this.errorMessage = 'Credenciales inválidas. Verifique su email y contraseña.';
        this.hideMessageAfterDelay('error');
      }
      if (params['logout']) {
        this.successMessage = 'Has cerrado sesión exitosamente';
        this.hideMessageAfterDelay('success');
      }
    });

    // Ocultar mensajes después de 5 segundos
    setTimeout(() => {
      this.hideAllMessages();
    }, 5000);
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.loading) return;
    
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login(this.form).subscribe({
      next: (response) => {
        if (response.success) {
          this.successMessage = 'Login exitoso, redirigiendo...';
          
          // Redirigir después de un breve delay
          setTimeout(() => {
            this.router.navigate(['/select-dashboard']);
          }, 1000);
        } else {
          this.errorMessage = 'Credenciales inválidas. Verifique su email y contraseña.';
          this.hideMessageAfterDelay('error');
        }
      },
      error: (error) => {
        console.error('Error de login:', error);
        this.errorMessage = error?.error?.message || 'Error de conexión. Intente nuevamente.';
        this.hideMessageAfterDelay('error');
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  private hideMessageAfterDelay(type: 'error' | 'success') {
    setTimeout(() => {
      if (type === 'error') {
        this.errorMessage = '';
      } else {
        this.successMessage = '';
      }
    }, 5000);
  }

  private hideAllMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }
}