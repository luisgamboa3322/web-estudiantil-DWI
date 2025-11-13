import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorService } from '../../services/profesor.service';

interface Curso {
  id: number;
  nombre: string;
  codigo: string;
  descripcion?: string;
  estado: string;
  profesor?: any;
}

@Component({
  selector: 'app-profesor-dashboard',
  standalone: true,
  imports: [CommonModule, ProfesorHeaderComponent, ProfesorSidebarComponent],
  templateUrl: './profesor-dashboard.component.html',
  styleUrls: ['./profesor-dashboard.component.css']
})
export class ProfesorDashboardComponent implements OnInit {
  private profesorService = inject(ProfesorService);
  private router = inject(Router);
  
  profesor: any = {};
  cursos: Curso[] = [];
  currentView = 'cursos';
  showGestionCurso = false;
  currentPath = '';
  isLoading: boolean = true;

  ngOnInit() {
    this.loadDashboardData();
    this.currentPath = '/profesor/dashboard';
  }

  loadDashboardData() {
    this.isLoading = true;
    this.profesorService.getDashboardData().subscribe({
      next: (data) => {
        console.log('✅ Datos del dashboard profesor:', data);
        this.profesor = data.profesor || {};
        this.cursos = data.cursos || [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Error cargando dashboard:', error);
        this.isLoading = false;
        // Mostrar datos de fallback
        this.profesor = {};
        this.cursos = [];
      }
    });
  }

  showView(view: string) {
    this.currentView = view;
    this.showGestionCurso = false;
    
    // Update navigation
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
      link.classList.remove('bg-orange-500', 'text-white', 'font-semibold');
    });
    
    const targetLink = document.querySelector(`[data-target="${view}"]`);
    if (targetLink) {
      targetLink.classList.add('bg-orange-500', 'text-white', 'font-semibold');
    }
  }

  showCourseManagement(cursoId: number) {
    // Navegar a la página de gestión de curso
    this.router.navigate(['/profesor/gestion-curso'], { queryParams: { id: cursoId } });
  }

  backToCourses() {
    this.showGestionCurso = false;
    this.currentView = 'cursos';
    this.showView('cursos');
  }

  getPlaceholderImage(text: string): string {
    const encoded = encodeURIComponent(text.replace(/\s+/g, '+'));
    return `https://placehold.co/600x400/F97316/FFFFFF?text=${encoded}`;
  }
}