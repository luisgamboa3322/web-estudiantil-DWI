import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { ProfesorService } from '../../services/profesor.service';
import { Subscription } from 'rxjs';

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
  progreso?: number;
  semanas?: Semana[];
}

@Component({
  selector: 'app-profesor-gestion-curso',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-gestion-curso.component.html',
  styleUrls: ['./profesor-gestion-curso.component.css']
})
export class ProfesorGestionCursoComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private profesorService = inject(ProfesorService);
  
  private subscriptions = new Subscription();
  private materialesSubscription: Subscription | null = null;
  private tareasSubscription: Subscription | null = null;
  
  profesor: any = {};
  curso: Curso | null = null;
  semanas: any[] = [];
  selectedSemana: any | null = null;
  materiales: any[] = [];
  tareas: any[] = [];
  showAddMaterialModal = false;
  showAddTareaModal = false;
  showAddSemanaModal = false;
  isLoading = false;
  currentPath = '/profesor/gestion-curso';
  errorMessage = '';
  cursoId: number | null = null;
  isSubmitting = false;
  
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

  newSemana = {
    numeroSemana: 1,
    titulo: '',
    descripcion: ''
  };

  ngOnInit() {
    const cursoIdParam = this.route.snapshot.queryParams['id'];
    if (!cursoIdParam) {
      console.error('No se proporcionó ID de curso');
      this.router.navigate(['/profesor/dashboard']);
      return;
    }
    
    this.cursoId = +cursoIdParam;
    this.loadCurso();
  }

  ngOnDestroy() {
    // Limpiar todas las subscripciones para evitar memory leaks
    if (this.materialesSubscription) {
      this.materialesSubscription.unsubscribe();
    }
    if (this.tareasSubscription) {
      this.tareasSubscription.unsubscribe();
    }
    this.subscriptions.unsubscribe();
  }

  private loadCurso() {
    if (!this.cursoId) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    
    // Cargar datos del profesor y verificar acceso al curso
    const dashboardSub = this.profesorService.getDashboardData().subscribe({
      next: (data) => {
        this.profesor = data.profesor || {};
        
        // Verificar que el profesor tenga acceso a este curso
        const cursos = data.cursos || [];
        const tieneAcceso = cursos.some((c: any) => c.id === this.cursoId);
        
        if (!tieneAcceso) {
          this.errorMessage = 'No tienes acceso a este curso';
          this.isLoading = false;
          setTimeout(() => {
            this.router.navigate(['/profesor/dashboard']);
          }, 2000);
          return;
        }
        
        // Cargar detalle del curso
        this.loadCursoDetalle();
      },
      error: (error) => {
        console.error('Error cargando datos del profesor:', error);
        this.errorMessage = 'Error de conexión. Reintentando...';
        this.isLoading = false;
        // Reintentar después de 2 segundos
        setTimeout(() => this.loadCurso(), 2000);
      }
    });
    
    this.subscriptions.add(dashboardSub);
  }

  private loadCursoDetalle() {
    if (!this.cursoId) {
      return;
    }

    const cursoSub = this.profesorService.getCursoDetalle(this.cursoId).subscribe({
      next: (curso) => {
        this.curso = curso;
        this.loadSemanas(this.cursoId!);
      },
      error: (error) => {
        console.error('Error cargando curso:', error);
        if (error.status === 403 || error.status === 404) {
          this.errorMessage = 'No tienes acceso a este curso o el curso no existe';
          setTimeout(() => {
            this.router.navigate(['/profesor/dashboard']);
          }, 2000);
        } else {
          this.errorMessage = 'Error de conexión. Reintentando...';
          setTimeout(() => this.loadCursoDetalle(), 2000);
        }
        this.isLoading = false;
      }
    });
    
    this.subscriptions.add(cursoSub);
  }

  private loadSemanas(cursoId: number) {
    const semanasSub = this.profesorService.getSemanasByCurso(cursoId).subscribe({
      next: (semanas) => {
        this.semanas = semanas || [];
        if (this.semanas.length > 0 && !this.selectedSemana) {
          this.selectSemana(this.semanas[0]);
        }
        this.isLoading = false;
        this.errorMessage = '';
      },
      error: (error) => {
        console.error('Error cargando semanas:', error);
        this.semanas = [];
        this.isLoading = false;
        if (error.status === 0 || error.status >= 500) {
          this.errorMessage = 'Error de conexión. Reintentando...';
          setTimeout(() => this.loadSemanas(cursoId), 2000);
        } else {
          this.errorMessage = 'Error al cargar las semanas';
        }
      }
    });
    
    this.subscriptions.add(semanasSub);
  }

  selectSemana(semana: any) {
    this.selectedSemana = semana;
    this.loadContenidoSemana(semana.id);
  }

  private loadContenidoSemana(semanaId: number) {
    // Cancelar subscripciones anteriores de materiales y tareas para evitar duplicados
    if (this.materialesSubscription) {
      this.materialesSubscription.unsubscribe();
      this.materialesSubscription = null;
    }
    if (this.tareasSubscription) {
      this.tareasSubscription.unsubscribe();
      this.tareasSubscription = null;
    }
    
    // Limpiar arrays antes de cargar nuevos datos
    this.materiales = [];
    this.tareas = [];
    
    // Cargar materiales
    this.materialesSubscription = this.profesorService.getMaterialesBySemana(semanaId).subscribe({
      next: (materiales) => {
        // Asegurar que solo se asignen los datos una vez
        if (this.materialesSubscription) {
          this.materiales = materiales || [];
          this.errorMessage = '';
        }
      },
      error: (error) => {
        console.error('Error cargando materiales:', error);
        this.materiales = [];
        if (error.status === 0 || error.status >= 500) {
          // Error de conexión, reintentar solo una vez después de un delay
          setTimeout(() => {
            if (this.selectedSemana && this.selectedSemana.id === semanaId) {
              this.loadContenidoSemana(semanaId);
            }
          }, 2000);
        }
      }
    });

    // Cargar tareas
    this.tareasSubscription = this.profesorService.getTareasBySemana(semanaId).subscribe({
      next: (tareas) => {
        // Asegurar que solo se asignen los datos una vez
        if (this.tareasSubscription) {
          this.tareas = tareas || [];
          this.errorMessage = '';
        }
      },
      error: (error) => {
        console.error('Error cargando tareas:', error);
        this.tareas = [];
        if (error.status === 0 || error.status >= 500) {
          // Error de conexión, reintentar solo una vez después de un delay
          setTimeout(() => {
            if (this.selectedSemana && this.selectedSemana.id === semanaId) {
              this.loadContenidoSemana(semanaId);
            }
          }, 2000);
        }
      }
    });
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

  openAddSemanaModal() {
    this.newSemana = { numeroSemana: this.semanas.length + 1, titulo: '', descripcion: '' };
    this.showAddSemanaModal = true;
  }

  addSemana() {
    // Prevenir múltiples llamadas simultáneas
    if (this.isSubmitting || !this.curso || !this.newSemana.titulo.trim()) {
      return;
    }

    this.isSubmitting = true;

    const semanaData = {
      numeroSemana: this.newSemana.numeroSemana,
      titulo: this.newSemana.titulo,
      descripcion: this.newSemana.descripcion
    };

    const createSemanaSub = this.profesorService.createSemana(this.curso.id, semanaData).subscribe({
      next: (semana) => {
        this.isSubmitting = false;
        this.showAddSemanaModal = false;
        this.newSemana = { numeroSemana: this.semanas.length + 1, titulo: '', descripcion: '' };
        // Recargar semanas para asegurar que estén actualizadas desde la BD
        this.loadSemanas(this.curso!.id);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Error creando semana:', error);
        if (error.status === 0) {
          alert('Error de conexión. Por favor, verifica tu conexión a internet e intenta nuevamente.');
        } else if (error.status === 403) {
          alert('No tienes permiso para crear semanas en este curso.');
        } else {
          alert('Error al crear la semana: ' + (error.error?.message || error.error || 'Error desconocido'));
        }
      }
    });
    
    this.subscriptions.add(createSemanaSub);
  }

  addMaterial() {
    // Prevenir múltiples llamadas simultáneas
    if (this.isSubmitting || !this.selectedSemana || !this.newMaterial.nombre.trim()) {
      return;
    }

    this.isSubmitting = true;

    // Convertir archivo a base64 si existe
    let materialData: any = {
      nombre: this.newMaterial.nombre,
      descripcion: this.newMaterial.descripcion
    };

    const handleSuccess = () => {
      this.isSubmitting = false;
      this.showAddMaterialModal = false;
      this.newMaterial = { nombre: '', descripcion: '', file: null };
      // Recargar materiales para mostrar los datos actualizados desde la BD
      // Usar setTimeout para asegurar que la petición anterior se complete
      setTimeout(() => {
        if (this.selectedSemana) {
          this.loadContenidoSemana(this.selectedSemana.id);
        }
      }, 500);
    };

    const handleError = (error: any) => {
      this.isSubmitting = false;
      console.error('Error creando material:', error);
      if (error.status === 0) {
        alert('Error de conexión. Por favor, verifica tu conexión a internet e intenta nuevamente.');
      } else if (error.status === 403) {
        alert('No tienes permiso para crear materiales en este curso.');
      } else {
        alert('Error al crear el material: ' + (error.error?.message || error.error || 'Error desconocido'));
      }
    };

    if (this.newMaterial.file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = (reader.result as string).split(',')[1];
        materialData.fileName = this.newMaterial.file!.name;
        materialData.fileType = this.newMaterial.file!.type;
        materialData.fileData = base64;

        const createMaterialSub = this.profesorService.createMaterial(this.selectedSemana.id, materialData).subscribe({
          next: handleSuccess,
          error: handleError
        });
        
        this.subscriptions.add(createMaterialSub);
      };
      reader.onerror = () => {
        this.isSubmitting = false;
        alert('Error al leer el archivo. Por favor, intenta nuevamente.');
      };
      reader.readAsDataURL(this.newMaterial.file);
    } else {
      const createMaterialSub = this.profesorService.createMaterial(this.selectedSemana.id, materialData).subscribe({
        next: handleSuccess,
        error: handleError
      });
      
      this.subscriptions.add(createMaterialSub);
    }
  }

  addTarea() {
    // Prevenir múltiples llamadas simultáneas
    if (this.isSubmitting || !this.selectedSemana || !this.newTarea.titulo.trim() || !this.newTarea.fechaLimite) {
      return;
    }

    this.isSubmitting = true;

    const tareaData = {
      titulo: this.newTarea.titulo,
      descripcion: this.newTarea.descripcion,
      fechaLimite: new Date(this.newTarea.fechaLimite).toISOString(),
      puntosMaximos: this.newTarea.puntosMaximos
    };

    const createTareaSub = this.profesorService.createTarea(this.selectedSemana.id, tareaData).subscribe({
      next: (tarea) => {
        this.isSubmitting = false;
        this.showAddTareaModal = false;
        this.newTarea = { titulo: '', descripcion: '', fechaLimite: '', puntosMaximos: 100 };
        // Recargar tareas para mostrar los datos actualizados desde la BD
        // Usar setTimeout para asegurar que la petición anterior se complete
        setTimeout(() => {
          if (this.selectedSemana) {
            this.loadContenidoSemana(this.selectedSemana.id);
          }
        }, 500);
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Error creando tarea:', error);
        if (error.status === 0) {
          alert('Error de conexión. Por favor, verifica tu conexión a internet e intenta nuevamente.');
        } else if (error.status === 403) {
          alert('No tienes permiso para crear tareas en este curso.');
        } else {
          alert('Error al crear la tarea: ' + (error.error?.message || error.error || 'Error desconocido'));
        }
      }
    });
    
    this.subscriptions.add(createTareaSub);
  }

  deleteMaterial(material: Material) {
    if (confirm('¿Estás seguro de que quieres eliminar este material?')) {
      const deleteMaterialSub = this.profesorService.deleteMaterial(material.id).subscribe({
        next: () => {
          // Recargar materiales para mostrar los datos actualizados desde la BD
          // Usar setTimeout para asegurar que la petición anterior se complete
          setTimeout(() => {
            if (this.selectedSemana) {
              this.loadContenidoSemana(this.selectedSemana.id);
            }
          }, 500);
        },
        error: (error) => {
          console.error('Error eliminando material:', error);
          if (error.status === 0) {
            alert('Error de conexión. Por favor, verifica tu conexión a internet e intenta nuevamente.');
          } else if (error.status === 403) {
            alert('No tienes permiso para eliminar este material.');
          } else {
            alert('Error al eliminar el material: ' + (error.error?.message || error.error || 'Error desconocido'));
          }
        }
      });
      
      this.subscriptions.add(deleteMaterialSub);
    }
  }

  deleteTarea(tarea: Tarea) {
    if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
      const deleteTareaSub = this.profesorService.deleteTarea(tarea.id).subscribe({
        next: () => {
          // Recargar tareas para mostrar los datos actualizados desde la BD
          // Usar setTimeout para asegurar que la petición anterior se complete
          setTimeout(() => {
            if (this.selectedSemana) {
              this.loadContenidoSemana(this.selectedSemana.id);
            }
          }, 500);
        },
        error: (error) => {
          console.error('Error eliminando tarea:', error);
          if (error.status === 0) {
            alert('Error de conexión. Por favor, verifica tu conexión a internet e intenta nuevamente.');
          } else if (error.status === 403) {
            alert('No tienes permiso para eliminar esta tarea.');
          } else {
            alert('Error al eliminar la tarea: ' + (error.error?.message || error.error || 'Error desconocido'));
          }
        }
      });
      
      this.subscriptions.add(deleteTareaSub);
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