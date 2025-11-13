import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';

interface Chat {
  id: number;
  nombre: string;
  ultimoMensaje: string;
  hora: string;
  noLeidos?: number;
  avatar?: string;
  tipo: 'individual' | 'grupo' | 'soporte';
  enLinea?: boolean;
}

interface Mensaje {
  id: number;
  remitente: string;
  contenido: string;
  hora: string;
  esMio: boolean;
  avatar?: string;
}

@Component({
  selector: 'app-student-chat',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './student-chat.component.html',
  styleUrls: ['./student-chat.component.css']
})
export class StudentChatComponent implements OnInit, AfterViewInit {
  private studentService = inject(StudentService);
  private router = inject(Router);

  studentName: string = '';
  chats: Chat[] = [];
  chatSeleccionado: Chat | null = null;
  mensajes: Mensaje[] = [];
  nuevoMensaje: string = '';
  busquedaChat: string = '';

  ngOnInit() {
    this.loadStudentData();
    this.loadChats();
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

  loadChats() {
    // TODO: Crear endpoint /student/api/chats en el backend
    const token = localStorage.getItem('auth_token');
    
    fetch('http://localhost:8083/student/api/chats', {
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
      throw new Error('Error cargando chats');
    })
    .then(data => {
      this.chats = data;
      if (this.chats.length > 0) {
        this.seleccionarChat(this.chats[0]);
      }
    })
    .catch(error => {
      console.error('Error cargando chats:', error);
      // Datos de ejemplo
      this.chats = [
        {
          id: 1,
          nombre: 'Soporte Técnico',
          ultimoMensaje: 'Perfecto, estaremos atentos para ayudar...',
          hora: '11:30',
          noLeidos: 2,
          avatar: 'ST',
          tipo: 'soporte',
          enLinea: true
        },
        {
          id: 2,
          nombre: 'Grupo: Desarrollo Web',
          ultimoMensaje: 'Ana: ¿Alguien tiene el enlace de la clase?',
          hora: '10:15',
          tipo: 'grupo',
          avatar: 'DW'
        },
        {
          id: 3,
          nombre: 'Juan Gonzales',
          ultimoMensaje: 'Escribiendo...',
          hora: '09:45',
          tipo: 'individual',
          avatar: 'JG'
        }
      ];
      if (this.chats.length > 0) {
        this.seleccionarChat(this.chats[0]);
      }
    });
  }

  seleccionarChat(chat: Chat) {
    this.chatSeleccionado = chat;
    this.loadMensajes(chat.id);
    // Marcar como leído
    chat.noLeidos = 0;
  }

  loadMensajes(chatId: number) {
    // TODO: Crear endpoint /student/api/chats/{chatId}/mensajes en el backend
    const token = localStorage.getItem('auth_token');
    
    fetch(`http://localhost:8083/student/api/chats/${chatId}/mensajes`, {
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
      throw new Error('Error cargando mensajes');
    })
    .then(data => {
      this.mensajes = data;
      this.initLucideIcons();
    })
    .catch(error => {
      console.error('Error cargando mensajes:', error);
      // Datos de ejemplo
      this.mensajes = [
        {
          id: 1,
          remitente: 'Soporte Técnico',
          contenido: '¡Hola Luis! ¿En qué podemos ayudarte el día de hoy?',
          hora: '11:28',
          esMio: false,
          avatar: 'ST'
        },
        {
          id: 2,
          remitente: this.studentName,
          contenido: 'Hola, tengo una consulta sobre mis calificaciones del curso de Herramientas de Desarrollo.',
          hora: '11:29',
          esMio: true
        },
        {
          id: 3,
          remitente: 'Soporte Técnico',
          contenido: 'Claro, un momento por favor mientras verifico el sistema. ¿Podrías confirmarme tu código de estudiante?',
          hora: '11:29',
          esMio: false,
          avatar: 'ST'
        }
      ];
      this.initLucideIcons();
    });
  }

  enviarMensaje() {
    if (!this.nuevoMensaje.trim() || !this.chatSeleccionado) return;

    const token = localStorage.getItem('auth_token');
    
    fetch(`http://localhost:8083/student/api/chats/${this.chatSeleccionado.id}/mensajes`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ contenido: this.nuevoMensaje })
    })
    .then(response => {
      if (response.ok) {
        return response.json();
      }
      throw new Error('Error enviando mensaje');
    })
    .then(data => {
      // Agregar mensaje a la lista
      this.mensajes.push({
        id: Date.now(),
        remitente: this.studentName,
        contenido: this.nuevoMensaje,
        hora: new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }),
        esMio: true
      });
      this.nuevoMensaje = '';
      this.initLucideIcons();
    })
    .catch(error => {
      console.error('Error enviando mensaje:', error);
      // Agregar mensaje localmente aunque falle
      this.mensajes.push({
        id: Date.now(),
        remitente: this.studentName,
        contenido: this.nuevoMensaje,
        hora: new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }),
        esMio: true
      });
      this.nuevoMensaje = '';
      this.initLucideIcons();
    });
  }

  getChatsFiltrados(): Chat[] {
    if (!this.busquedaChat.trim()) {
      return this.chats;
    }
    return this.chats.filter(chat => 
      chat.nombre.toLowerCase().includes(this.busquedaChat.toLowerCase())
    );
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
