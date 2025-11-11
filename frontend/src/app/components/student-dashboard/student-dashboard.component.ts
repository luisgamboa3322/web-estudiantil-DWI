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
  profesor?: {
    nombre: string;
  };
}

interface Asignacion {
  curso: Curso;
  fechaAsignacion: Date;
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

interface Notificacion {
  id: number;
  titulo: string;
  mensaje: string;
  fecha: string;
  leida: boolean;
}

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.css']
})
export class StudentDashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  user: any = null;
  userName = 'Luis Francisco';
  cursosAsignados: Asignacion[] = [];
  actividades: Actividad[] = [];
  notificaciones: Notificacion[] = [];
  mostrarNotificaciones = false;
  sidebarVisible = false;

  ngOnInit() {
    this.loadDashboard();
  }

  loadDashboard() {
    // Datos simulados (en producción vendrían del backend)
    this.user = this.authService.getCurrentUserValue();
    
    this.cursosAsignados = [
      {
        curso: {
          id: 1,
          nombre: 'Desarrollo Web Integrado',
          codigo: 'DWI101',
          descripcion: 'Curso de desarrollo web moderno',
          estado: 'ACTIVO',
          profesor: { nombre: 'Prof. Juan Pérez' }
        },
        fechaAsignacion: new Date('2025-01-15'),
        estado: 'ACTIVO'
      },
      {
        curso: {
          id: 2,
          nombre: 'Seguridad Informática',
          codigo: 'SI101',
          descripcion: 'Fundamentos de seguridad',
          estado: 'ACTIVO',
          profesor: { nombre: 'Prof. María García' }
        },
        fechaAsignacion: new Date('2025-01-16'),
        estado: 'ACTIVO'
      }
    ];

    this.actividades = [
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
        titulo: "Repasemos lo aprendido: Cuestion...",
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

    this.notificaciones = [
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
      },
      {
        id: 3,
        titulo: "Nuevo anuncio",
        mensaje: "El profesor ha publicado un nuevo anuncio en el curso de Diseño de Productos y Servicios.",
        fecha: "Hace 2 días",
        leida: true
      }
    ];
  }

  // Métodos de UI
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

  toggleNotifications() {
    this.mostrarNotificaciones = !this.mostrarNotificaciones;
    
    // Marcar todas las notificaciones como leídas al abrir el modal
    if (this.mostrarNotificaciones) {
      this.notificaciones.forEach(n => n.leida = true);
    }
  }

  get tieneNotificacionesNoLeidas(): boolean {
    return this.notificaciones.some(n => !n.leida);
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

  // Métodos específicos del estudiante
  irACurso(cursoId: number) {
    // Navegar al detalle del curso
    this.router.navigate(['/student/curso', cursoId]);
  }

  getUserName(): string {
    return this.user?.userName || 'Estudiante';
  }

  // Inicializar iconos cuando se carga el componente
  ngAfterViewInit() {
    setTimeout(() => {
      if (typeof (window as any).lucide !== 'undefined') {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }
}