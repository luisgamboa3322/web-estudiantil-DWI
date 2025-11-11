import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

interface Curso {
  id: number;
  nombre: string;
  codigo: string;
  descripcion: string;
  estado: string;
}

@Component({
  selector: 'app-profesor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profesor-dashboard.component.html',
  styleUrls: ['./profesor-dashboard.component.css']
})
export class ProfesorDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  user: any = null;
  cursos: Curso[] = [];
  cursoSeleccionado: Curso | null = null;
  sidebarVisible = false;

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    // Datos simulados (en producción vendrían del backend)
    this.cursos = [
      {
        id: 1,
        nombre: 'Desarrollo Web Integrado',
        codigo: 'DWI101',
        descripcion: 'Curso de desarrollo web moderno',
        estado: 'ACTIVO'
      },
      {
        id: 2,
        nombre: 'Bases de Datos',
        codigo: 'BD101',
        descripcion: 'Fundamentos de bases de datos',
        estado: 'ACTIVO'
      }
    ];

    this.user = this.authService.getCurrentUserValue();
  }

  // Métodos de navegación
  switchView(targetId: string) {
    const contentViews = document.querySelectorAll('.content-view');
    const navLinks = document.querySelectorAll('.nav-link');

    // Ocultar todas las vistas
    contentViews.forEach(view => {
      view.classList.add('hidden');
    });

    // Mostrar la vista seleccionada
    const targetView = document.getElementById(`${targetId}-content`);
    if (targetView) {
      targetView.classList.remove('hidden');
    }

    // Actualizar estado activo del link
    navLinks.forEach(l => {
      l.classList.remove('bg-orange-500', 'text-white', 'font-semibold');
      l.classList.add('hover:bg-gray-700');
    });

    const activeLink = document.querySelector(`[data-target="${targetId}"]`);
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

  // Método de logout
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

  // Métodos específicos del profesor
  gestionarCurso(curso: Curso) {
    this.cursoSeleccionado = curso;
    // Cambiar a la vista de gestión
    const cursosContent = document.getElementById('cursos-content');
    const gestionCursoContent = document.getElementById('gestion-curso-content');
    
    if (cursosContent && gestionCursoContent) {
      cursosContent.classList.add('hidden');
      gestionCursoContent.classList.remove('hidden');
    }
  }

  volverACursos() {
    this.cursoSeleccionado = null;
    // Volver a la vista de cursos
    const cursosContent = document.getElementById('cursos-content');
    const gestionCursoContent = document.getElementById('gestion-curso-content');
    
    if (cursosContent && gestionCursoContent) {
      gestionCursoContent.classList.add('hidden');
      cursosContent.classList.remove('hidden');
    }
    
    // Asegurarse de que el link de cursos quede activo
    this.switchView('cursos');
  }

  getUserName(): string {
    return this.user?.userName || 'Profesor';
  }

  // Inicializar iconos cuando se carga el componente
  ngAfterViewInit() {
    // Esperar a que el DOM esté listo
    setTimeout(() => {
      if (typeof (window as any).lucide !== 'undefined') {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }
}