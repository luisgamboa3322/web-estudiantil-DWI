import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-debug',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-5">
      <h2>Diagnóstico de Conexión Spring Boot</h2>
      
      <div class="alert alert-info">
        <h4>Prueba de conectividad con backend</h4>
        <p><strong>Backend URL:</strong> http://localhost:8083/api/auth/login</p>
      </div>

      <button (click)="testBackendConnection()" class="btn btn-primary" [disabled]="loading">
        {{ loading ? 'Probando...' : 'Probar Conexión Backend' }}
      </button>

      <div class="mt-3">
        <h4>Credenciales de prueba disponibles:</h4>
        <ul>
          <li><strong>Admin:</strong> admin&#64;example.com / admin123</li>
          <li><strong>Teacher:</strong> prof&#64;example.com / teacher123</li>
          <li><strong>Student:</strong> student&#64;example.com / student123</li>
        </ul>
      </div>

      <button (click)="testAdminLogin()" class="btn btn-success mt-3" [disabled]="loading">
        Test Admin Login
      </button>

      <button (click)="testTeacherLogin()" class="btn btn-warning mt-3" [disabled]="loading">
        Test Teacher Login
      </button>

      <button (click)="testStudentLogin()" class="btn btn-info mt-3" [disabled]="loading">
        Test Student Login
      </button>

      <div class="mt-4" *ngIf="result">
        <h4>Resultado:</h4>
        <div class="alert" [class.alert-success]="isSuccess" [class.alert-danger]="!isSuccess">
          <pre>{{ result | json }}</pre>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { padding: 20px; max-width: 800px; }
    .btn { margin-right: 10px; }
  `]
})
export class DebugComponent implements OnInit {
  loading = false;
  result: any;
  isSuccess = false;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    console.log('DebugComponent iniciado');
  }

  async testBackendConnection() {
    this.loading = true;
    this.result = null;

    try {
      console.log('Probando conexión al backend...');
      const response = await this.http.post('http://localhost:8083/api/auth/login', {
        email: 'admin@example.com',
        password: 'admin123'
      }).toPromise();

      this.result = response;
      this.isSuccess = true;
      console.log('✅ Conexión exitosa:', response);
    } catch (error: any) {
      this.result = {
        error: true,
        message: error.message || 'Error desconocido',
        status: error.status,
        url: error.url,
        details: error
      };
      this.isSuccess = false;
      console.error('❌ Error de conexión:', error);
    } finally {
      this.loading = false;
    }
  }

  async testAdminLogin() {
    await this.testLogin('admin@example.com', 'admin123', 'Admin');
  }

  async testTeacherLogin() {
    await this.testLogin('prof@example.com', 'teacher123', 'Teacher');
  }

  async testStudentLogin() {
    await this.testLogin('student@example.com', 'student123', 'Student');
  }

  async testLogin(email: string, password: string, role: string) {
    this.loading = true;
    this.result = null;

    try {
      console.log(`Probando login de ${role}...`);
      const response = await this.http.post('http://localhost:8083/api/auth/login', {
        email,
        password
      }).toPromise();

      this.result = {
        success: true,
        role,
        response: response,
        message: `Login de ${role} exitoso`
      };
      this.isSuccess = true;
      console.log(`✅ ${role} login exitoso:`, response);
    } catch (error: any) {
      this.result = {
        success: false,
        role,
        error: error.message || 'Error desconocido',
        status: error.status,
        url: error.url,
        details: error
      };
      this.isSuccess = false;
      console.error(`❌ ${role} login error:`, error);
    } finally {
      this.loading = false;
    }
  }
}