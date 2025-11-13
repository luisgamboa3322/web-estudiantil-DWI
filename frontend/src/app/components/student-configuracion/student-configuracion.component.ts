import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';

@Component({
  selector: 'app-student-configuracion',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './student-configuracion.component.html',
  styleUrls: ['./student-configuracion.component.css']
})
export class StudentConfiguracionComponent implements OnInit, AfterViewInit {
  private studentService = inject(StudentService);
  private router = inject(Router);

  student: any = {};
  studentName: string = '';
  
  // Formulario de perfil
  nombre: string = '';
  email: string = '';
  codigo: string = '';
  
  // Formulario de contraseña
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  
  // Notificaciones
  notificacionesCalificaciones: boolean = true;
  notificacionesAnuncios: boolean = true;
  notificacionesRecordatorios: boolean = false;

  isLoading = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  ngOnInit() {
    this.loadStudentData();
  }

  ngAfterViewInit() {
    this.initLucideIcons();
  }

  initLucideIcons() {
    setTimeout(() => {
      if (typeof (window as any).lucide !== 'undefined') {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }

  loadStudentData() {
    this.isLoading = true;
    this.studentService.getDashboardData().subscribe({
      next: (data) => {
        this.student = data.student || {};
        this.studentName = data.studentName || 'Estudiante';
        this.nombre = this.student.nombre || '';
        this.email = this.student.email || '';
        this.codigo = this.student.codigo || '';
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando datos del estudiante:', error);
        this.studentName = 'Estudiante';
        this.isLoading = false;
      }
    });
  }

  guardarPerfil() {
    if (!this.nombre.trim() || !this.email.trim()) {
      this.mensajeError = 'Por favor completa todos los campos';
      return;
    }

    this.isLoading = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.studentService.actualizarPerfil({
      nombre: this.nombre,
      email: this.email
    }).subscribe({
      next: (data) => {
        this.student = data;
        this.mensajeExito = 'Perfil actualizado correctamente';
        this.isLoading = false;
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error actualizando perfil:', error);
        this.mensajeError = 'Error al actualizar el perfil';
        this.isLoading = false;
        setTimeout(() => {
          this.mensajeError = '';
        }, 3000);
      }
    });
  }

  cambiarPassword() {
    if (!this.newPassword || !this.confirmPassword) {
      this.mensajeError = 'Por favor completa todos los campos de contraseña';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.mensajeError = 'Las contraseñas no coinciden';
      return;
    }

    if (this.newPassword.length < 6) {
      this.mensajeError = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }

    this.isLoading = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.studentService.actualizarPerfil({
      password: this.newPassword
    }).subscribe({
      next: (data) => {
        this.mensajeExito = 'Contraseña cambiada correctamente';
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        this.isLoading = false;
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error cambiando contraseña:', error);
        this.mensajeError = 'Error al cambiar la contraseña';
        this.isLoading = false;
        setTimeout(() => {
          this.mensajeError = '';
        }, 3000);
      }
    });
  }

  toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      sidebar.classList.toggle('hidden');
      sidebar.classList.toggle('absolute');
      sidebar.classList.toggle('z-20');
      sidebar.classList.toggle('w-full');
    }
  }
}
