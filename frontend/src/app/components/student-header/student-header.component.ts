import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-student-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-header.component.html',
  styleUrls: ['./student-header.component.css']
})
export class StudentHeaderComponent {
  @Input() student: any;

  showNotifications = false;

  notificaciones = [
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

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
    // Marcar como leídas al abrir
    if (!this.showNotifications) {
      this.notificaciones.forEach(n => n.leida = true);
    }
  }

  getUnreadCount(): number {
    return this.notificaciones.filter(n => !n.leida).length;
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