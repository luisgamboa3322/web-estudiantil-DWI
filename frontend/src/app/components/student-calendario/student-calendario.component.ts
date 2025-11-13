import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { StudentService } from '../../services/student.service';

interface CalendarEvent {
  id?: number;
  titulo: string;
  descripcion?: string;
  tipo: string;
  fecha: string;
  hora?: string;
  curso?: string;
}

@Component({
  selector: 'app-student-calendario',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-calendario.component.html',
  styleUrls: ['./student-calendario.component.css']
})
export class StudentCalendarioComponent implements OnInit, AfterViewInit {
  private studentService = inject(StudentService);
  private router = inject(Router);

  studentName: string = '';
  currentDate = new Date();
  currentMonth = this.currentDate.getMonth();
  currentYear = this.currentDate.getFullYear();
  
  monthNames = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
                'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  
  eventos: CalendarEvent[] = [];
  proximosEventos: CalendarEvent[] = [];

  ngOnInit() {
    this.loadStudentData();
    this.loadEventos();
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

  loadStudentData() {
    this.studentService.getDashboardData().subscribe({
      next: (data) => {
        this.studentName = data.studentName || 'Estudiante';
      },
      error: (error) => {
        console.error('Error cargando datos del estudiante:', error);
        this.studentName = 'Estudiante';
      }
    });
  }

  loadEventos() {
    // Por ahora usamos datos de ejemplo, luego se conectará al backend
    // TODO: Crear endpoint /student/api/eventos en el backend
    const token = localStorage.getItem('auth_token');
    
    fetch('http://localhost:8083/student/api/eventos', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      throw new Error('Error cargando eventos');
    })
    .then(data => {
      this.eventos = data;
      this.proximosEventos = this.getProximosEventos();
      this.generateCalendar();
    })
    .catch(error => {
      console.error('Error cargando eventos:', error);
      // Datos de ejemplo
      this.eventos = [
        {
          id: 1,
          titulo: 'Examen Parcial',
          descripcion: 'Herramientas de Desarrollo',
          tipo: 'exam',
          fecha: this.formatDateForEvent(new Date(this.currentYear, this.currentMonth, 9)),
          hora: '8:00 PM - 10:00 PM',
          curso: 'Herramientas de Desarrollo'
        },
        {
          id: 2,
          titulo: 'Foro S05: Consultas',
          descripcion: 'Diseño de Productos y Servicios',
          tipo: 'forum',
          fecha: this.formatDateForEvent(new Date(this.currentYear, this.currentMonth, 13)),
          hora: '11:59 PM',
          curso: 'Diseño de Productos y Servicios'
        },
        {
          id: 3,
          titulo: 'Entrega Tarea 2',
          descripcion: 'Desarrollo Web Integrado',
          tipo: 'assignment',
          fecha: this.formatDateForEvent(new Date(this.currentYear, this.currentMonth, 18)),
          hora: '11:59 PM',
          curso: 'Desarrollo Web Integrado'
        }
      ];
      this.proximosEventos = this.getProximosEventos();
      this.generateCalendar();
    });
  }

  formatDateForEvent(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getProximosEventos(): CalendarEvent[] {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    
    return this.eventos
      .filter(evento => {
        const fechaEvento = new Date(evento.fecha);
        fechaEvento.setHours(0, 0, 0, 0);
        return fechaEvento >= hoy;
      })
      .sort((a, b) => new Date(a.fecha).getTime() - new Date(b.fecha).getTime())
      .slice(0, 4);
  }

  generateCalendar() {
    // El calendario se genera en el HTML con los eventos
    this.initLucideIcons();
  }

  getDaysInMonth(): number[] {
    const daysInMonth = new Date(this.currentYear, this.currentMonth + 1, 0).getDate();
    return Array.from({ length: daysInMonth }, (_, i) => i + 1);
  }

  getFirstDayOfMonth(): number {
    const firstDay = new Date(this.currentYear, this.currentMonth, 1).getDay();
    // Ajustar para que Lunes sea 0
    return firstDay === 0 ? 6 : firstDay - 1;
  }

  getEventsForDay(day: number): CalendarEvent[] {
    const dateStr = this.formatDateForEvent(new Date(this.currentYear, this.currentMonth, day));
    return this.eventos.filter(e => e.fecha === dateStr);
  }

  isToday(day: number): boolean {
    const today = new Date();
    return day === today.getDate() &&
           this.currentMonth === today.getMonth() &&
           this.currentYear === today.getFullYear();
  }

  previousMonth() {
    if (this.currentMonth === 0) {
      this.currentMonth = 11;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.generateCalendar();
  }

  nextMonth() {
    if (this.currentMonth === 11) {
      this.currentMonth = 0;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.generateCalendar();
  }

  goToToday() {
    const today = new Date();
    this.currentMonth = today.getMonth();
    this.currentYear = today.getFullYear();
    this.generateCalendar();
  }

  getEventColorClass(tipo: string): string {
    switch (tipo) {
      case 'exam':
        return 'bg-red-100 text-red-800';
      case 'forum':
        return 'bg-orange-100 text-orange-800';
      case 'assignment':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-green-100 text-green-800';
    }
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
