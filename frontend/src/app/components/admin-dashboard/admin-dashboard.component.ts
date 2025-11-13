import { Component, OnInit, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

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

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  user: any = null;
  
  // Datos de usuarios (con nombres en español para el HTML)
  students: Student[] = [];
  estudiantes: Student[] = [];
  professors: Professor[] = [];
  profesores: Professor[] = [];
  admins: Admin[] = [];
  administradores: Admin[] = [];
  cursos: Curso[] = [];
  asignaciones: Asignacion[] = [];
  
  // Datos filtrados para búsqueda
  filteredEstudiantes: Student[] = [];
  cursosConProfesor: Curso[] = [];
  
  // Búsqueda
  searchStudent = '';
  
  // Formularios
  estudianteForm: any = {};
  profesorForm: any = {};
  adminForm: any = {};
  cursoForm: any = {};
  assignForm: any = {};
  
  // Estado
  isLoading = true;
  errorMessage = '';
  sidebarVisible = false;

  ngOnInit() {
    this.loadDashboard();
    this.setupEventListeners();
  }

  ngAfterViewInit() {
    // Asegurar que los iconos se inicialicen después de que la vista esté completamente renderizada
    setTimeout(() => {
      this.initLucideIcons();
    }, 300);
  }

  setupEventListeners() {
    setTimeout(() => {
      this.initNavigation();
      this.initModalHandlers();
      this.initLucideIcons();
    }, 100);
  }

  initNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const targetId = (link as HTMLElement).getAttribute('data-target');
        if (targetId) {
          this.switchView(targetId);
        }
      });
    });
  }

  initModalHandlers() {
    document.addEventListener('click', (event) => {
      const target = event.target as HTMLElement;
      if (target && target.classList.contains('modal-backdrop')) {
        this.closeAllModals();
      }
    });
  }

  initLucideIcons() {
    if (typeof window !== 'undefined') {
      // Intentar múltiples formas de acceder a lucide
      const lucide = (window as any).lucide || (window as any).lucideIcons;
      if (lucide && typeof lucide.createIcons === 'function') {
        try {
          lucide.createIcons();
          console.log('✅ Iconos de Lucide inicializados correctamente');
        } catch (error) {
          console.warn('⚠️ Error al inicializar iconos de Lucide:', error);
        }
      } else {
        console.warn('⚠️ Lucide no está disponible. Asegúrate de que el script esté cargado en index.html');
      }
    }
  }

  loadDashboard() {
    this.isLoading = true;
    this.errorMessage = '';

    const apiUrl = 'http://localhost:8083/admin/api/dashboard';
    
    fetch(apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      console.log('🚀 Datos reales del dashboard admin:', data);
      
      this.students = data.students || [];
      this.professors = data.professors || [];
      this.admins = data.admins || [];
      this.cursos = data.cursos || [];
      this.asignaciones = data.asignaciones || [];

      this.estudiantes = [...this.students];
      this.profesores = [...this.professors];
      this.administradores = [...this.admins];

      this.user = this.authService.getCurrentUser();
      this.isLoading = false;

      this.applyFilters();
      
      // Reinicializar iconos después de cargar datos
      setTimeout(() => {
        this.initLucideIcons();
      }, 200);
    })
    .catch(error => {
      console.error('❌ Error cargando dashboard:', error);
      this.errorMessage = 'Error cargando datos del dashboard.';
      this.isLoading = false;
      
      this.showFallbackData();
    });
  }

  showFallbackData() {
    this.students = [
      { id: 1, nombre: 'Luis Francisco', codigo: 'u001', email: 'student@example.com' }
    ];
    this.professors = [
      { id: 1, nombre: 'Juan Pérez', codigo: 'P0001', email: 'prof@example.com', especialidad: 'Matemáticas' }
    ];
    this.admins = [
      { id: 1, nombre: 'Admin', codigo: 'A0001', email: 'admin@example.com' }
    ];
    this.cursos = [
      {
        id: 1,
        nombre: 'DESARROLLO WEB INTEGRADO',
        codigo: 'DWI-001',
        descripcion: 'Curso completo de desarrollo web',
        estado: 'ACTIVO',
        profesor: { id: 1, nombre: 'Juan Pérez', especialidad: 'Matemáticas' }
      }
    ];
    this.asignaciones = [];

    this.estudiantes = [...this.students];
    this.profesores = [...this.professors];
    this.administradores = [...this.admins];
    this.applyFilters();
  }

  // Métodos de navegación
  switchView(target: string) {
    const contentViews = document.querySelectorAll('.content-view');
    const navLinks = document.querySelectorAll('.nav-link');

    contentViews.forEach(view => {
      view.classList.add('hidden');
    });

    const targetView = document.getElementById(`${target}-content`);
    if (targetView) {
      targetView.classList.remove('hidden');
    }

    navLinks.forEach(l => {
      l.classList.remove('bg-orange-500', 'text-white', 'font-semibold');
      l.classList.add('hover:bg-gray-700');
    });

    const activeLink = document.querySelector(`[data-target="${target}"]`);
    if (activeLink) {
      activeLink.classList.add('bg-orange-500', 'text-white', 'font-semibold');
      activeLink.classList.remove('hover:bg-gray-700');
    }

    if (window.innerWidth < 768) {
      this.toggleSidebar();
    }
    
    // Reinicializar iconos después de cambiar de vista
    setTimeout(() => {
      this.initLucideIcons();
    }, 100);
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

  // Métodos para filtros
  filtrarEstudiantes(value: string) {
    this.searchStudent = value;
    this.applyFilters();
  }

  applyFilters() {
    if (this.searchStudent) {
      this.filteredEstudiantes = this.estudiantes.filter(est =>
        est.nombre.toLowerCase().includes(this.searchStudent.toLowerCase()) ||
        est.codigo.toLowerCase().includes(this.searchStudent.toLowerCase()) ||
        est.email.toLowerCase().includes(this.searchStudent.toLowerCase())
      );
    } else {
      this.filteredEstudiantes = [...this.estudiantes];
    }

    this.cursosConProfesor = this.cursos.filter(curso => curso.profesor);
  }

  // Métodos para gestión de modales
  openStudentModal() {
    this.estudianteForm = {};
    this.openModal('student-modal');
  }

  openTeacherModal() {
    this.profesorForm = {};
    this.openModal('teacher-modal');
  }

  openAdminModal() {
    this.adminForm = {};
    this.openModal('admin-modal');
  }

  openCourseModal() {
    this.cursoForm = {};
    this.openModal('course-modal');
  }

  openAssignCourseModal() {
    this.assignForm = {};
    this.openModal('assign-course-modal');
  }

  openModal(modalId: string) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.remove('hidden');
      modal.classList.add('flex');
    }
  }

  closeModal(modalId: string) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    }
  }

  closeAllModals() {
    const modals = document.querySelectorAll('.modal-backdrop');
    modals.forEach(modal => {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    });
  }

  // Métodos para gestión de estudiantes
  editEstudiante(student: Student) {
    this.estudianteForm = { ...student };
    this.openModal('edit-student-modal');
  }

  deleteEstudiante(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este estudiante?')) {
      this.removeStudent(id);
    }
  }

  saveEstudiante() {
    const studentData = {
      nombre: this.estudianteForm.nombre,
      codigo: this.estudianteForm.codigo,
      email: this.estudianteForm.email,
      password: this.estudianteForm.password
    };

    this.createStudent(studentData);
    this.closeModal('student-modal');
  }

  updateEstudiante() {
    const id = this.estudianteForm.id;
    const studentData = {
      nombre: this.estudianteForm.nombre,
      codigo: this.estudianteForm.codigo,
      email: this.estudianteForm.email,
      password: this.estudianteForm.password || undefined
    };

    this.updateStudent(id, studentData);
    this.closeModal('edit-student-modal');
  }

  // Métodos para gestión de profesores
  editProfesor(professor: Professor) {
    this.profesorForm = { ...professor };
    this.openModal('edit-teacher-modal');
  }

  deleteProfesor(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este profesor?')) {
      this.removeProfessor(id);
    }
  }

  saveProfesor() {
    const professorData = {
      nombre: this.profesorForm.nombre,
      codigo: this.profesorForm.codigo,
      email: this.profesorForm.email,
      especialidad: this.profesorForm.especialidad,
      password: this.profesorForm.password
    };

    this.createProfessor(professorData);
    this.closeModal('teacher-modal');
  }

  updateProfesor() {
    const id = this.profesorForm.id;
    const professorData = {
      nombre: this.profesorForm.nombre,
      codigo: this.profesorForm.codigo,
      email: this.profesorForm.email,
      especialidad: this.profesorForm.especialidad,
      password: this.profesorForm.password || undefined
    };

    this.updateProfessor(id, professorData);
    this.closeModal('edit-teacher-modal');
  }

  // Métodos para gestión de administradores
  editAdmin(admin: Admin) {
    this.adminForm = { ...admin };
    this.openModal('edit-admin-modal');
  }

  deleteAdmin(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este administrador?')) {
      this.removeAdmin(id);
    }
  }

  saveAdmin() {
    const adminData = {
      nombre: this.adminForm.nombre,
      codigo: this.adminForm.codigo,
      email: this.adminForm.email,
      password: this.adminForm.password
    };

    this.createAdmin(adminData);
    this.closeModal('admin-modal');
  }

  updateAdmin() {
    const id = this.adminForm.id;
    const adminData = {
      nombre: this.adminForm.nombre,
      codigo: this.adminForm.codigo,
      email: this.adminForm.email,
      password: this.adminForm.password || undefined
    };

    this.updateAdminData(id, adminData);
    this.closeModal('edit-admin-modal');
  }

  // Métodos para gestión de cursos
  editCurso(curso: Curso) {
    this.cursoForm = {
      ...curso,
      profesorId: curso.profesor?.id || ''
    };
    this.openModal('edit-course-modal');
  }

  deleteCurso(id: number) {
    if (confirm('¿Estás seguro de que quieres eliminar este curso?')) {
      this.removeCourse(id);
    }
  }

  saveCurso() {
    const courseData = {
      nombre: this.cursoForm.nombre,
      codigo: this.cursoForm.codigo,
      descripcion: this.cursoForm.descripcion,
      estado: this.cursoForm.estado || 'ACTIVO',
      profesor: {
        id: this.cursoForm.profesorId ? parseInt(this.cursoForm.profesorId) : null
      }
    };

    this.createCourse(courseData);
    this.closeModal('course-modal');
  }

  updateCurso() {
    const id = this.cursoForm.id;
    const courseData = {
      id: id,
      nombre: this.cursoForm.nombre,
      codigo: this.cursoForm.codigo,
      descripcion: this.cursoForm.descripcion,
      estado: this.cursoForm.estado,
      profesor: {
        id: this.cursoForm.profesorId ? parseInt(this.cursoForm.profesorId) : null
      }
    };

    this.updateCourse(courseData);
    this.closeModal('edit-course-modal');
  }

  // Métodos para asignaciones
  assignCurso() {
    const assignData = {
      studentId: parseInt(this.assignForm.studentId),
      cursoId: parseInt(this.assignForm.cursoId)
    };

    this.createAssignment(assignData);
    this.closeModal('assign-course-modal');
  }

  unassignCurso(id: number) {
    if (confirm('¿Estás seguro de que quieres desasignar este curso?')) {
      this.removeAssignment(id);
    }
  }

  // Métodos CRUD para Estudiantes
  createStudent(studentData: any) {
    fetch('http://localhost:8083/admin/students', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(studentData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Estudiante creado:', data);
      alert('Estudiante creado exitosamente');
      this.loadDashboard();
      setTimeout(() => this.initLucideIcons(), 300);
    })
    .catch(error => {
      console.error('❌ Error creando estudiante:', error);
      alert('Error al crear estudiante. Verifica los datos e intenta nuevamente.');
    });
  }

  updateStudent(id: number, studentData: any) {
    fetch(`http://localhost:8083/admin/students/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(studentData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Estudiante actualizado:', data);
      alert('Estudiante actualizado exitosamente');
      this.loadDashboard();
      setTimeout(() => this.initLucideIcons(), 300);
    })
    .catch(error => {
      console.error('❌ Error actualizando estudiante:', error);
      alert('Error al actualizar estudiante.');
    });
  }

  removeStudent(id: number) {
    fetch(`http://localhost:8083/admin/students/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Estudiante eliminado:', data);
      alert('Estudiante eliminado exitosamente');
      this.loadDashboard();
      setTimeout(() => this.initLucideIcons(), 300);
    })
    .catch(error => {
      console.error('❌ Error eliminando estudiante:', error);
      alert('Error al eliminar estudiante.');
    });
  }

  // Métodos CRUD para Profesores
  createProfessor(professorData: any) {
    fetch('http://localhost:8083/admin/profesores', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(professorData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Profesor creado:', data);
      alert('Profesor creado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error creando profesor:', error);
      alert('Error al crear profesor.');
    });
  }

  updateProfessor(id: number, professorData: any) {
    fetch(`http://localhost:8083/admin/profesores/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(professorData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Profesor actualizado:', data);
      alert('Profesor actualizado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error actualizando profesor:', error);
      alert('Error al actualizar profesor.');
    });
  }

  removeProfessor(id: number) {
    fetch(`http://localhost:8083/admin/profesores/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Profesor eliminado:', data);
      alert('Profesor eliminado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error eliminando profesor:', error);
      alert('Error al eliminar profesor.');
    });
  }

  // Métodos CRUD para Administradores
  createAdmin(adminData: any) {
    fetch('http://localhost:8083/admin/admins', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(adminData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Administrador creado:', data);
      alert('Administrador creado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error creando administrador:', error);
      alert('Error al crear administrador.');
    });
  }

  updateAdminData(id: number, adminData: any) {
    fetch(`http://localhost:8083/admin/admins/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(adminData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Administrador actualizado:', data);
      alert('Administrador actualizado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error actualizando administrador:', error);
      alert('Error al actualizar administrador.');
    });
  }

  removeAdmin(id: number) {
    fetch(`http://localhost:8083/admin/admins/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Administrador eliminado:', data);
      alert('Administrador eliminado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error eliminando administrador:', error);
      alert('Error al eliminar administrador.');
    });
  }

  // Métodos CRUD para Cursos
  createCourse(courseData: any) {
    fetch('http://localhost:8083/admin/cursos', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(courseData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Curso creado:', data);
      alert('Curso creado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error creando curso:', error);
      alert('Error al crear curso.');
    });
  }

  updateCourse(courseData: any) {
    fetch(`http://localhost:8083/admin/cursos/update`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(courseData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Curso actualizado:', data);
      alert('Curso actualizado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error actualizando curso:', error);
      alert('Error al actualizar curso.');
    });
  }

  removeCourse(id: number) {
    fetch(`http://localhost:8083/admin/cursos/delete`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify({ id: id })
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Curso eliminado:', data);
      if (data.success) {
        alert('Curso eliminado exitosamente');
        this.loadDashboard();
      } else {
        alert('Error: ' + data.message);
      }
    })
    .catch(error => {
      console.error('❌ Error eliminando curso:', error);
      alert('Error al eliminar curso.');
    });
  }

  // Métodos CRUD para Asignaciones
  createAssignment(assignData: any) {
    fetch('http://localhost:8083/admin/asignaciones', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(assignData)
    })
    .then(response => response.json())
    .then(data => {
      console.log('✅ Asignación creada:', data);
      alert('Curso asignado exitosamente');
      this.loadDashboard();
    })
    .catch(error => {
      console.error('❌ Error creando asignación:', error);
      alert('Error al asignar curso.');
    });
  }

  removeAssignment(id: number) {
    fetch(`http://localhost:8083/admin/asignaciones/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => {
      if (response.ok) {
        alert('Curso desasignado exitosamente');
        this.loadDashboard();
      } else {
        alert('Error desasignando curso');
      }
    })
    .catch(error => {
      console.error('❌ Error eliminando asignación:', error);
      alert('Error de conexión');
    });
  }

  // Utilidades
  getUserName(): string {
    return this.user?.userName || 'Administrador';
  }
}