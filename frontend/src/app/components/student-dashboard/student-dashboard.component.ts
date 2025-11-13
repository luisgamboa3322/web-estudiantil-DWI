import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIf, NgFor } from '@angular/common';
import { Router } from '@angular/router';
import { StudentHeaderComponent } from '../student-header/student-header.component';
import { StudentSidebarComponent } from '../student-sidebar/student-sidebar.component';
import { StudentService } from '../../services/student.service';

interface Asignacion {
  id: number;
  curso: {
    id: number;
    nombre: string;
    codigo: string;
    descripcion?: string;
    profesor?: {
      nombre: string;
    };
  };
  fechaAsignacion: string;
  estado: string;
}

interface Actividad {
  tipo: string;
  titulo: string;
  curso: string;
  vencimiento: string;
  estado: string;
  color: string;
}

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.css']
})
export class StudentDashboardComponent implements OnInit {
  private studentService = inject(StudentService);
  private router = inject(Router);
  
  // User data
  student: any = {};
  studentName: string = '';
  cursosAsignados: Asignacion[] = [];
  mostrarMensajeNoCursos: boolean = false;
  isLoading: boolean = true;
  currentPath = '/student/dashboard';
  
  // Notification state
  mostrarNotificaciones = false;
  notificaciones: any[] = [
    {
      id: 1,
      titulo: "Nueva calificación publicada",
      mensaje: "Tu calificación para el examen parcial de Herramientas de Desarrollo ha sido publicada.",
      fecha: "Hace 2 horas",
      leida: false
    },
    {
      id: 2,
      titulo: "Recordatorio de entrega",
      mensaje: "Recuerda que la tarea 2 de Desarrollo Web Integrado vence en 3 días.",
      fecha: "Hace 1 día",
      leida: true
    }
  ];
  
  // Actividades semanales
  actividades: Actividad[] = [
    {
      tipo: "Foro no calificado",
      titulo: "S05 - Foro de Consultas",
      curso: "DISEÑO DE PRODUCTOS Y SERVICIOS",
      vencimiento: "13/09/2025 a las 11:59 PM",
      estado: "Por entregar",
      color: "orange"
    },
    {
      tipo: "Evaluación no calificada",
      titulo: "Repasemos lo aprendido: Cuestionario",
      curso: "SEGURIDAD INFORMATICA", 
      vencimiento: "14/09/2025 a las 11:59 PM",
      estado: "Por entregar",
      color: "orange"
    },
    {
      tipo: "Foro no calificado",
      titulo: "S04 - Foro de Consultas",
      curso: "DISEÑO DE PRODUCTOS Y SERVICIOS",
      vencimiento: "08/09/2025 a las 11:59 PM",
      estado: "Vencida",
      color: "red"
    }
  ];

  ngOnInit() {
    this.loadDashboardData();
    this.initializeLucideIcons();
  }

  loadDashboardData() {
    this.isLoading = true;
    this.studentService.getDashboardData().subscribe({
      next: (data) => {
        console.log('✅ Datos del dashboard estudiante:', data);
        this.studentName = data.studentName || 'Estudiante';
        this.student = data.student || {};
        this.cursosAsignados = data.cursosAsignados || [];
        this.mostrarMensajeNoCursos = data.mostrarMensajeNoCursos || false;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('❌ Error cargando dashboard:', error);
        this.isLoading = false;
        // Mostrar datos de fallback
        this.studentName = 'Estudiante';
        this.cursosAsignados = [];
        this.mostrarMensajeNoCursos = true;
      }
    });
  }

  initializeLucideIcons() {
    setTimeout(() => {
      if (typeof lucide !== 'undefined') {
        lucide.createIcons();
      }
    }, 100);
  }

  navigateToCourse(cursoId: number) {
    // Navegar al detalle del curso usando Angular Router
    this.router.navigate(['/student/curso', cursoId]);
  }

  getCourseProgress(): number {
    // Calcular progreso del curso (placeholder)
    return Math.floor(Math.random() * 100);
  }

  getCourseImage(): string {
    return 'https://placehold.co/600x400/FFDDC1/4A4A4A?text=Curso';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  // UI State Management
  toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      sidebar.classList.toggle('hidden');
      sidebar.classList.toggle('absolute');
      sidebar.classList.toggle('z-20');
      sidebar.classList.toggle('w-full');
    }
  }

  toggleNotifications() {
    this.mostrarNotificaciones = !this.mostrarNotificaciones;
    // Mark notifications as read when opened
    if (!this.mostrarNotificaciones) {
      this.notificaciones.forEach(n => n.leida = true);
    }
  }

  // Notification helpers
  get tieneNotificacionesNoLeidas(): boolean {
    return this.notificaciones.some(n => !n.leida);
  }

  // Navigation
  irACurso(cursoId: number) {
    this.router.navigate(['/student/curso', cursoId]);
  }
}

declare const lucide: any;