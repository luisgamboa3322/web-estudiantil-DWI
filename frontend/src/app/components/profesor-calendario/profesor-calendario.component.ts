import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';

interface CalendarEvent {
  id?: number;
  title: string;
  description: string;
  type: 'class' | 'exam' | 'assignment' | 'meeting' | 'other';
  date: string;
  time: string;
  courseId?: number;
}

interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  events: CalendarEvent[];
  isToday: boolean;
  isSelected: boolean;
}

interface Notification {
  id: number;
  titulo: string;
  mensaje: string;
  tipo: 'info' | 'success' | 'warning' | 'error';
  fecha: Date;
  leida: boolean;
}

@Component({
  selector: 'app-profesor-calendario',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-calendario.component.html',
  styleUrls: ['./profesor-calendario.component.css']
})
export class ProfesorCalendarioComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private router = inject(Router);

  user: any = null;
  sidebarVisible = false;
  
  // Estado del calendario
  currentView: 'month' | 'week' | 'day' = 'month';
  currentDate = new Date();
  currentDay = new Date();
  
  // Datos del calendario
  calendarDays: CalendarDay[] = [];
  weekDays: Date[] = [];
  hours = Array.from({length: 24}, (_, i) => i);
  dayNames = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  monthNames = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
                'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  
  // Eventos y notificaciones
  events: CalendarEvent[] = [];
  notificaciones: Notification[] = [];
  misCursos: any[] = [];
  
  // Estado del modal
  showEventModal = false;
  editingEvent: CalendarEvent | null = null;
  eventForm: CalendarEvent = {
    title: '',
    description: '',
    type: 'class',
    date: '',
    time: ''
  };

  ngOnInit() {
    this.loadUserData();
    this.loadCursos();
    this.generateCalendarDays();
    this.loadEvents();
    this.loadNotifications();
  }

  loadUserData() {
    this.user = this.authService.getCurrentUser();
  }

  loadCursos() {
    // Cargar cursos del profesor desde el backend
    fetch('http://localhost:8083/profesor/api/cursos', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      }
    })
    .then(response => response.json())
    .then(data => {
      this.misCursos = data;
    })
    .catch(error => {
      console.error('Error cargando cursos:', error);
    });
  }

  loadEvents() {
    // Cargar eventos del calendario desde el backend
    const teacherId = this.user?.id;
    if (teacherId) {
      fetch(`http://localhost:8083/profesor/api/eventos/${teacherId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
        }
      })
      .then(response => response.json())
      .then(data => {
        this.events = data;
        this.generateCalendarDays(); // Regenerar días con eventos
      })
      .catch(error => {
        console.error('Error cargando eventos:', error);
        // Usar datos de ejemplo si no hay eventos
        this.events = [
          {
            id: 1,
            title: 'Clase de Matemáticas',
            description: 'Algebra básica',
            type: 'class',
            date: '2025-11-12',
            time: '09:00'
          }
        ];
      });
    }
  }

  loadNotifications() {
    // Datos de ejemplo para notificaciones
    this.notificaciones = [
      {
        id: 1,
        titulo: 'Nueva entrega de tarea',
        mensaje: 'El estudiante Juan Pérez ha entregado la tarea de la semana 3',
        tipo: 'info',
        fecha: new Date(),
        leida: false
      },
      {
        id: 2,
        titulo: 'Recordatorio de examen',
        mensaje: 'El examen de Matemática Básica está programado para mañana',
        tipo: 'warning',
        fecha: new Date(Date.now() - 24 * 60 * 60 * 1000),
        leida: true
      }
    ];
  }

  generateCalendarDays() {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
    
    // Primer día del mes y cuántos días tiene el mes
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    
    // Día de la semana del primer día (0 = Domingo, 1 = Lunes, etc.)
    const firstDayOfWeek = firstDay.getDay();
    
    // Crear array de días
    this.calendarDays = [];
    
    // Días del mes anterior para completar la primera semana
    const prevMonth = new Date(year, month, 0);
    const daysInPrevMonth = prevMonth.getDate();
    
    for (let i = firstDayOfWeek - 1; i >= 0; i--) {
      const date = new Date(year, month - 1, daysInPrevMonth - i);
      this.calendarDays.push({
        date,
        isCurrentMonth: false,
        events: this.getEventsForDate(date),
        isToday: this.isToday(date),
        isSelected: false
      });
    }
    
    // Días del mes actual
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(year, month, day);
      this.calendarDays.push({
        date,
        isCurrentMonth: true,
        events: this.getEventsForDate(date),
        isToday: this.isToday(date),
        isSelected: false
      });
    }
    
    // Días del mes siguiente para completar la última semana
    const remainingCells = 42 - this.calendarDays.length; // 6 semanas x 7 días
    for (let day = 1; day <= remainingCells; day++) {
      const date = new Date(year, month + 1, day);
      this.calendarDays.push({
        date,
        isCurrentMonth: false,
        events: this.getEventsForDate(date),
        isToday: this.isToday(date),
        isSelected: false
      });
    }
  }

  generateWeekDays() {
    const startOfWeek = new Date(this.currentDate);
    startOfWeek.setDate(this.currentDate.getDate() - this.currentDate.getDay());
    
    this.weekDays = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(startOfWeek);
      day.setDate(startOfWeek.getDate() + i);
      this.weekDays.push(day);
    }
  }

  getEventsForDate(date: Date): CalendarEvent[] {
    return this.events.filter(event => {
      const eventDate = new Date(event.date);
      return eventDate.toDateString() === date.toDateString();
    });
  }

  getDayEvents(date: Date): CalendarEvent[] {
    return this.getEventsForDate(date);
  }

  isToday(date: Date): boolean {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  }

  getDayClass(day: CalendarDay): string {
    const classes = ['min-h-[100px]'];
    if (!day.isCurrentMonth) {
      classes.push('bg-gray-50');
    }
    if (day.isToday) {
      classes.push('bg-blue-50');
    }
    if (day.isSelected) {
      classes.push('ring-2 ring-orange-500');
    }
    return classes.join(' ');
  }

  getEventClass(event: CalendarEvent): string {
    const baseClasses = 'text-xs p-1 rounded truncate cursor-pointer hover:opacity-80';
    
    switch (event.type) {
      case 'class':
        return `${baseClasses} bg-green-100 text-green-800`;
      case 'exam':
        return `${baseClasses} bg-orange-100 text-orange-800`;
      case 'assignment':
        return `${baseClasses} bg-red-100 text-red-800`;
      case 'meeting':
        return `${baseClasses} bg-blue-100 text-blue-800`;
      default:
        return `${baseClasses} bg-gray-100 text-gray-800`;
    }
  }

  getNotificationIconClass(tipo: string): string {
    switch (tipo) {
      case 'success':
        return 'w-8 h-8 bg-green-100 text-green-600';
      case 'warning':
        return 'w-8 h-8 bg-yellow-100 text-yellow-600';
      case 'error':
        return 'w-8 h-8 bg-red-100 text-red-600';
      default:
        return 'w-8 h-8 bg-blue-100 text-blue-600';
    }
  }

  getNotificationIcon(tipo: string): string {
    switch (tipo) {
      case 'success':
        return 'check-circle';
      case 'warning':
        return 'alert-triangle';
      case 'error':
        return 'x-circle';
      default:
        return 'info';
    }
  }

  getTotalEstudiantes(cursoId: number): number {
    // Implementar lógica para obtener el número de estudiantes por curso
    return 25; // Valor de ejemplo
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
    navLinks.forEach(link => {
      link.classList.remove('bg-orange-500', 'text-white', 'font-semibold');
      link.classList.add('hover:bg-gray-700');
    });

    const activeLink = document.querySelector(`[data-target="${target}"]`);
    if (activeLink) {
      activeLink.classList.add('bg-orange-500', 'text-white', 'font-semibold');
      activeLink.classList.remove('hover:bg-gray-700');
    }

    if (target === 'week') {
      this.generateWeekDays();
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

  setCalendarView(view: 'month' | 'week' | 'day') {
    this.currentView = view;
    if (view === 'week') {
      this.generateWeekDays();
    }
  }

  navigateCalendar(direction: 'prev' | 'next' | 'today') {
    const currentYear = this.currentDate.getFullYear();
    const currentMonth = this.currentDate.getMonth();

    if (direction === 'today') {
      this.currentDate = new Date();
    } else if (direction === 'prev') {
      if (this.currentView === 'month') {
        this.currentDate = new Date(currentYear, currentMonth - 1, 1);
      } else {
        this.currentDate.setDate(this.currentDate.getDate() - 7);
      }
    } else if (direction === 'next') {
      if (this.currentView === 'month') {
        this.currentDate = new Date(currentYear, currentMonth + 1, 1);
      } else {
        this.currentDate.setDate(this.currentDate.getDate() + 7);
      }
    }

    this.generateCalendarDays();
    if (this.currentView === 'week') {
      this.generateWeekDays();
    }
  }

  filterByPeriod(period: string) {
    // Implementar filtrado por período
    console.log('Filtrar por período:', period);
  }

  // Métodos de eventos
  showAddEventModal() {
    this.editingEvent = null;
    this.eventForm = {
      title: '',
      description: '',
      type: 'class',
      date: '',
      time: ''
    };
    this.showEventModal = true;
  }

  editEvent(event: CalendarEvent) {
    this.editingEvent = event;
    this.eventForm = { ...event };
    this.showEventModal = true;
  }

  deleteEvent(event: CalendarEvent) {
    if (confirm('¿Estás seguro de que quieres eliminar este evento?')) {
      fetch(`http://localhost:8083/profesor/api/eventos/${event.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
        }
      })
      .then(response => response.json())
      .then(data => {
        this.events = this.events.filter(e => e.id !== event.id);
        this.generateCalendarDays();
        alert('Evento eliminado exitosamente');
      })
      .catch(error => {
        console.error('Error eliminando evento:', error);
        alert('Error al eliminar el evento');
      });
    }
  }

  closeEventModal() {
    this.showEventModal = false;
    this.editingEvent = null;
  }

  saveEvent() {
    const eventData = {
      ...this.eventForm,
      teacherId: this.user?.id
    };

    const method = this.editingEvent ? 'PUT' : 'POST';
    const url = this.editingEvent
      ? `http://localhost:8083/profesor/api/eventos/${this.editingEvent.id}`
      : 'http://localhost:8083/profesor/api/eventos';

    fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
      },
      body: JSON.stringify(eventData)
    })
    .then(response => response.json())
    .then(savedEvent => {
      if (this.editingEvent) {
        const index = this.events.findIndex(e => e.id === this.editingEvent!.id);
        if (index !== -1) {
          this.events[index] = savedEvent;
        }
      } else {
        this.events.push(savedEvent);
      }
      
      this.generateCalendarDays();
      this.closeEventModal();
      alert(`Evento ${this.editingEvent ? 'actualizado' : 'creado'} exitosamente`);
    })
    .catch(error => {
      console.error('Error guardando evento:', error);
      alert('Error al guardar el evento');
    });
  }

  markAsRead(notificationId: number) {
    const notification = this.notificaciones.find(n => n.id === notificationId);
    if (notification) {
      notification.leida = true;
    }
  }

  // Métodos utilitarios
  getCalendarTitle(): string {
    return `${this.monthNames[this.currentDate.getMonth()]} ${this.currentDate.getFullYear()}`;
  }

  formatHour(hour: number): string {
    return `${hour.toString().padStart(2, '0')}:00`;
  }

  formatEventTime(event: CalendarEvent): string {
    return `${event.date} a las ${event.time}`;
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getUserName(): string {
    return this.user?.userName || 'Profesor';
  }

  getUserInitials(): string {
    const name = this.getUserName();
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
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
}
