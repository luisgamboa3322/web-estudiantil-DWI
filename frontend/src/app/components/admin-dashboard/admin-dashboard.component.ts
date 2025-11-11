import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';

interface Student {
  id: number;
  nombre: string;
  codigo: string;
  email: string;
}

interface Professor {
  id: number;
  nombre: string;
  codigo: string;
  email: string;
  especialidad: string;
}

interface Admin {
  id: number;
  nombre: string;
  codigo: string;
  email: string;
}

interface Curso {
  id: number;
  nombre: string;
  codigo: string;
  descripcion: string;
  estado: string;
  profesor?: {
    id: number;
    nombre: string;
    especialidad: string;
  };
}

interface Asignacion {
  id: number;
  student: Student;
  curso: Curso;
  fechaAsignacion: string;
  estado: string;
}

interface AdminStats {
  totalStudents: number;
  totalProfessors: number;
  totalCourses: number;
  totalAssignments: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private router = inject(Router);

  user: any = null;
  
  // Datos de usuarios
  students: Student[] = [];
  professors: Professor[] = [];
  admins: Admin[] = [];
  cursos: Curso[] = [];
  asignaciones: Asignacion[] = [];
  
  // Búsqueda
  searchStudent = '';
  
  // Estado
  isLoading = true;
  errorMessage = '';
  sidebarVisible = false;

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    this.isLoading = true;
    this.errorMessage = '';

    // Datos simulados (en producción vendrían del backend)
    this.students = [
      { id: 1, nombre: 'Juan Pérez', codigo: 'EST001', email: 'juan@email.com' },
      { id: 2, nombre: 'María García', codigo: 'EST002', email: 'maria@email.com' },
      { id: 3, nombre: 'Carlos López', codigo: 'EST003', email: 'carlos@email.com' }
    ];

    this.professors = [
      { id: 1, nombre: 'Dr. Roberto Silva', codigo: 'PROF001', email: 'roberto@email.com', especialidad: 'Matemáticas' },
      { id: 2, nombre: 'Dra. Ana Martínez', codigo: 'PROF002', email: 'ana@email.com', especialidad: 'Física' }
    ];

    this.admins = [
      { id: 1, nombre: 'Admin Principal', codigo: 'ADMIN001', email: 'admin@email.com' }
    ];

    this.cursos = [
      {
        id: 1,
        nombre: 'Matemáticas Básicas',
        codigo: 'MAT101',
        descripcion: 'Curso fundamental de matemáticas',
        estado: 'ACTIVO',
        profesor: { id: 1, nombre: 'Dr. Roberto Silva', especialidad: 'Matemáticas' }
      },
      {
        id: 2,
        nombre: 'Física General',
        codigo: 'FIS101',
        descripcion: 'Introducción a la física',
        estado: 'ACTIVO',
        profesor: { id: 2, nombre: 'Dra. Ana Martínez', especialidad: 'Física' }
      }
    ];

    this.asignaciones = [
      {
        id: 1,
        student: this.students[0],
        curso: this.cursos[0],
        fechaAsignacion: '2025-11-01T10:00:00Z',
        estado: 'ACTIVO'
      }
    ];

    this.user = this.authService.getCurrentUserValue();
    this.isLoading = false;
  }

  // Métodos de navegación
  switchView(target: string) {
    const contentViews = document.querySelectorAll('.content-view');
    const navLinks = document.querySelectorAll('.nav-link');

    // Ocultar todas las vistas
    contentViews.forEach(view => {
      view.classList.add('hidden');
    });

    // Mostrar la vista seleccionada
    const targetView = document.getElementById(`${target}-content`);
    if (targetView) {
      targetView.classList.remove('hidden');
    }

    // Actualizar estado activo del link
    navLinks.forEach(l => {
      l.classList.remove('bg-orange-500', 'text-white', 'font-semibold');
      l.classList.add('hover:bg-gray-700');
    });

    const activeLink = document.querySelector(`[data-target="${target}"]`);
    if (activeLink) {
      activeLink.classList.add('bg-orange-500', 'text-white', 'font-semibold');
      activeLink.classList.remove('hover:bg-gray-700');
    }

    // En móvil, ocultar la barra lateral
    if (window.innerWidth < 768) {
      this.toggleSidebar();
    }
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      if (this.sidebarVisible) {
        sidebar.classList.remove('hidden');
        sidebar.classList.add('absolute', 'z-20', 'w-full');
      } else {
        sidebar.classList.add('hidden');
        sidebar.classList.remove('absolute', 'z-20', 'w-full');
      }
    }
  }

  // Métodos de logout
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

  // Métodos para gestión de estudiantes
  openAddStudentModal() {
    console.log('Abrir modal añadir estudiante');
    // Aquí se abriría el modal
  }

  editStudent(student: Student) {
    console.log('Editar estudiante:', student);
    // Aquí se abriría el modal de edición
  }

  deleteStudent(student: Student) {
    if (confirm('¿Estás seguro de que quieres eliminar este estudiante?')) {
      console.log('Eliminar estudiante:', student);
      // Aquí se eliminaría el estudiante
    }
  }

  // Métodos para gestión de profesores
  openAddTeacherModal() {
    console.log('Abrir modal añadir profesor');
  }

  editTeacher(professor: Professor) {
    console.log('Editar profesor:', professor);
  }

  deleteTeacher(professor: Professor) {
    if (confirm('¿Estás seguro de que quieres eliminar este profesor?')) {
      console.log('Eliminar profesor:', professor);
    }
  }

  // Métodos para gestión de administradores
  openAddAdminModal() {
    console.log('Abrir modal añadir administrador');
  }

  editAdmin(admin: Admin) {
    console.log('Editar administrador:', admin);
  }

  deleteAdmin(admin: Admin) {
    if (confirm('¿Estás seguro de que quieres eliminar este administrador?')) {
      console.log('Eliminar administrador:', admin);
    }
  }

  // Métodos para gestión de cursos
  openAddCourseModal() {
    console.log('Abrir modal añadir curso');
  }

  editCourse(curso: Curso) {
    console.log('Editar curso:', curso);
  }

  deleteCourse(curso: Curso) {
    if (confirm('¿Estás seguro de que quieres eliminar este curso?')) {
      console.log('Eliminar curso:', curso);
    }
  }

  // Métodos para gestión de asignaciones
  openAssignCourseModal() {
    console.log('Abrir modal asignar curso');
  }

  unassignCourse(asignacion: Asignacion) {
    if (confirm('¿Estás seguro de que quieres desasignar este curso?')) {
      console.log('Desasignar curso:', asignacion);
    }
  }

  // Utilidades
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getUserName(): string {
    return this.user?.userName || 'Administrador';
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
  }
}