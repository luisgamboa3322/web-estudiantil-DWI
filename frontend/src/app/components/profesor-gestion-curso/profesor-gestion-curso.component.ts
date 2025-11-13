import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { AuthService } from '../../services/auth.service';

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

interface Semana {
  id: number;
  numeroSemana: number;
  materiales: Material[];
  tareas: Tarea[];
}

interface Curso {
  id: number;
  nombre: string;
  codigo: string;
  descripcion: string;
  progreso: number;
  semanas: Semana[];
}

@Component({
  selector: 'app-profesor-gestion-curso',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-gestion-curso.component.html',
  styleUrls: ['./profesor-gestion-curso.component.css']
})
export class ProfesorGestionCursoComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  
  user: any = null;
  curso: Curso | null = null;
  selectedSemana: Semana | null = null;
  showAddMaterialModal = false;
  showAddTareaModal = false;
  showEditModal = false;
  
  newMaterial = {
    nombre: '',
    descripcion: '',
    file: null as File | null
  };
  
  newTarea = {
    titulo: '',
    descripcion: '',
    fechaLimite: '',
    puntosMaximos: 100
  };
  
  editingItem = {
    type: 'material' as 'material' | 'tarea',
    item: null as any
  };

  ngOnInit() {
    this.user = this.authService.getCurrentUser();
    this.loadCurso();
  }

  private loadCurso() {
    const cursoId = this.route.snapshot.paramMap.get('id');
    
    // Datos simulados (en producción vendrían del backend)
    this.curso = {
      id: 1,
      nombre: 'Desarrollo Web Integrado',
      codigo: 'DWI-2025',
      descripcion: 'Curso completo de desarrollo web con tecnologías modernas',
      progreso: 0,
      semanas: [
        {
          id: 1,
          numeroSemana: 1,
          materiales: [
            {
              id: 1,
              nombre: 'Syllabus del Curso',
              descripcion: 'Documento con objetivos y contenido del curso',
              fechaCreacion: '2025-11-01T10:00:00Z',
              fileName: 'syllabus.pdf'
            }
          ],
          tareas: [
            {
              id: 1,
              titulo: 'Cuestionario Inicial',
              descripcion: 'Evaluación de conocimientos previos',
              fechaLimite: '2025-11-10T23:59:59Z',
              puntosMaximos: 10
            }
          ]
        },
        {
          id: 2,
          numeroSemana: 2,
          materiales: [],
          tareas: []
        }
      ]
    };
    
    if (this.curso.semanas.length > 0) {
      this.selectedSemana = this.curso.semanas[0];
    }
  }

  selectSemana(semana: Semana) {
    this.selectedSemana = semana;
  }

  goBack() {
    this.router.navigate(['/profesor/dashboard']);
  }

  openAddMaterialModal() {
    this.newMaterial = { nombre: '', descripcion: '', file: null };
    this.showAddMaterialModal = true;
  }

  openAddTareaModal() {
    this.newTarea = { titulo: '', descripcion: '', fechaLimite: '', puntosMaximos: 100 };
    this.showAddTareaModal = true;
  }

  addMaterial() {
    if (!this.selectedSemana || !this.newMaterial.nombre.trim()) {
      return;
    }

    const material: Material = {
      id: Date.now(),
      nombre: this.newMaterial.nombre,
      descripcion: this.newMaterial.descripcion,
      fechaCreacion: new Date().toISOString(),
      fileName: this.newMaterial.file?.name
    };

    this.selectedSemana.materiales.push(material);
    this.showAddMaterialModal = false;
  }

  addTarea() {
    if (!this.selectedSemana || !this.newTarea.titulo.trim() || !this.newTarea.fechaLimite) {
      return;
    }

    const tarea: Tarea = {
      id: Date.now(),
      titulo: this.newTarea.titulo,
      descripcion: this.newTarea.descripcion,
      fechaLimite: this.newTarea.fechaLimite,
      puntosMaximos: this.newTarea.puntosMaximos
    };

    this.selectedSemana.tareas.push(tarea);
    this.showAddTareaModal = false;
  }

  deleteMaterial(material: Material) {
    if (confirm('¿Estás seguro de que quieres eliminar este material?')) {
      if (this.selectedSemana) {
        this.selectedSemana.materiales = this.selectedSemana.materiales.filter(m => m.id !== material.id);
      }
    }
  }

  deleteTarea(tarea: Tarea) {
    if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
      if (this.selectedSemana) {
        this.selectedSemana.tareas = this.selectedSemana.tareas.filter(t => t.id !== tarea.id);
      }
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.newMaterial.file = file;
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}