import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';

interface Semana {
  id: number;
  numeroSemana: number;
  titulo?: string;
}

interface Material {
  id: number;
  nombre: string;
  descripcion?: string;
  fechaCreacion: string;
  fileName?: string;
}

interface Tarea {
  id: number;
  titulo: string;
  descripcion?: string;
  fechaLimite: string;
  puntosMaximos: number;
}

interface EstadoEntrega {
  entregada: boolean;
  calificada?: boolean;
  calificacion?: number;
}

@Component({
  selector: 'app-student-curso-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './student-curso-detalle.component.html',
  styleUrls: ['./student-curso-detalle.component.css']
})
export class StudentCursoDetalleComponent implements OnInit, AfterViewInit {
  private studentService = inject(StudentService);
  router = inject(Router);
  private route = inject(ActivatedRoute);

  cursoId: number = 0;
  curso: any = {};
  studentName: string = '';
  semanas: Semana[] = [];
  semanaSeleccionada: Semana | null = null;
  materiales: Material[] = [];
  tareas: Tarea[] = [];
  estadosTareas: Map<number, { estado: string; clase: string; calificacion?: number }> = new Map();
  
  // Modal de entrega
  mostrarModalEntrega = false;
  tareaSeleccionada: Tarea | null = null;
  contenidoEntrega: string = '';
  
  isLoading = false;

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.cursoId = +params['id'];
      this.loadCursoDetalle();
    });
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

  loadCursoDetalle() {
    this.isLoading = true;
    this.studentService.getCursoDetalle(this.cursoId).subscribe({
      next: (data) => {
        this.curso = data.curso || {};
        this.studentName = data.studentName || 'Estudiante';
        this.loadSemanas();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando detalle del curso:', error);
        this.isLoading = false;
      }
    });
  }

  loadSemanas() {
    this.studentService.getSemanasByCurso(this.cursoId).subscribe({
      next: (semanas) => {
        this.semanas = semanas;
        if (this.semanas.length > 0) {
          this.seleccionarSemana(this.semanas[0]);
        }
        this.initLucideIcons();
      },
      error: (error) => {
        console.error('Error cargando semanas:', error);
      }
    });
  }

  seleccionarSemana(semana: Semana) {
    this.semanaSeleccionada = semana;
    this.loadContenidoSemana(semana.id);
  }

  loadContenidoSemana(semanaId: number) {
    this.isLoading = true;
    
    // Cargar materiales y tareas en paralelo
    Promise.all([
      this.studentService.getMaterialesBySemana(semanaId).toPromise(),
      this.studentService.getTareasBySemana(semanaId).toPromise()
    ]).then(([materiales, tareas]) => {
      this.materiales = materiales || [];
      this.tareas = tareas || [];
      // Cargar estados de todas las tareas
      this.loadEstadosTareas();
      this.isLoading = false;
      this.initLucideIcons();
    }).catch(error => {
      console.error('Error cargando contenido de la semana:', error);
      this.isLoading = false;
    });
  }

  async loadEstadosTareas() {
    this.estadosTareas.clear();
    for (const tarea of this.tareas) {
      const estado = await this.getEstadoTareaAsync(tarea);
      this.estadosTareas.set(tarea.id, estado);
    }
  }

  async descargarMaterial(materialId: number) {
    try {
      const blob = await this.studentService.descargarMaterial(materialId).toPromise();
      if (blob) {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'material';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      }
    } catch (error) {
      console.error('Error descargando material:', error);
      alert('Error al descargar el material');
    }
  }

  async abrirModalEntrega(tarea: Tarea) {
    // Verificar estado de entrega
    try {
      const estado = await this.studentService.getEstadoEntrega(tarea.id).toPromise();
      if (estado && estado.entregada) {
        alert('Ya has entregado esta tarea');
        return;
      }
      
      const fechaLimite = new Date(tarea.fechaLimite);
      const ahora = new Date();
      if (fechaLimite < ahora) {
        alert('Esta tarea ya venció');
        return;
      }
      
      this.tareaSeleccionada = tarea;
      this.contenidoEntrega = '';
      this.mostrarModalEntrega = true;
    } catch (error) {
      console.error('Error verificando estado de entrega:', error);
      this.tareaSeleccionada = tarea;
      this.contenidoEntrega = '';
      this.mostrarModalEntrega = true;
    }
  }

  cerrarModalEntrega() {
    this.mostrarModalEntrega = false;
    this.tareaSeleccionada = null;
    this.contenidoEntrega = '';
  }

  async entregarTarea() {
    if (!this.tareaSeleccionada || !this.contenidoEntrega.trim()) {
      alert('Por favor escribe algo en tu entrega');
      return;
    }

    try {
      await this.studentService.entregarTarea(this.tareaSeleccionada.id, this.contenidoEntrega).toPromise();
      alert('Tarea entregada exitosamente');
      this.cerrarModalEntrega();
      if (this.semanaSeleccionada) {
        this.loadContenidoSemana(this.semanaSeleccionada.id);
      }
    } catch (error: any) {
      console.error('Error entregando tarea:', error);
      alert('Error: ' + (error.error || 'Error al entregar la tarea'));
    }
  }

  getEstadoTarea(tarea: Tarea): { estado: string; clase: string; calificacion?: number } {
    return this.estadosTareas.get(tarea.id) || { estado: 'Pendiente', clase: 'bg-gray-100 text-gray-600' };
  }

  async getEstadoTareaAsync(tarea: Tarea): Promise<{ estado: string; clase: string; calificacion?: number }> {
    try {
      const estado = await this.studentService.getEstadoEntrega(tarea.id).toPromise();
      if (!estado) {
        const fechaLimite = new Date(tarea.fechaLimite);
        const ahora = new Date();
        if (fechaLimite < ahora) {
          return { estado: 'Vencida', clase: 'bg-red-100 text-red-600' };
        }
        return { estado: 'Pendiente', clase: 'bg-gray-100 text-gray-600' };
      }
      
      if (estado.entregada) {
        if (estado.calificada) {
          return { 
            estado: `Calificada (${estado.calificacion} pts)`, 
            clase: 'bg-green-100 text-green-600',
            calificacion: estado.calificacion
          };
        }
        return { estado: 'Entregada', clase: 'bg-blue-100 text-blue-600' };
      }
      
      const fechaLimite = new Date(tarea.fechaLimite);
      const ahora = new Date();
      if (fechaLimite < ahora) {
        return { estado: 'Vencida', clase: 'bg-red-100 text-red-600' };
      }
      
      return { estado: 'Pendiente', clase: 'bg-gray-100 text-gray-600' };
    } catch (error) {
      return { estado: 'Pendiente', clase: 'bg-gray-100 text-gray-600' };
    }
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

  toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      sidebar.classList.toggle('hidden');
      sidebar.classList.toggle('absolute');
      sidebar.classList.toggle('z-20');
      sidebar.classList.toggle('w-full');
    }
  }

  volverADashboard() {
    this.router.navigate(['/student/dashboard']);
  }
}
