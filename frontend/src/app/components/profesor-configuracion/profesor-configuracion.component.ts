import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profesor-configuracion',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-configuracion.component.html',
  styleUrls: ['./profesor-configuracion.component.css']
})
export class ProfesorConfiguracionComponent {
  private authService = inject(AuthService);
  
  user: any = null;
  editing = false;
  
  profile = {
    nombre: '',
    email: '',
    codigo: '',
    especialidad: ''
  };

  passwordData = {
    current: '',
    new: '',
    confirm: ''
  };

  notifications = {
    calificaciones: true,
    anuncios: true,
    recordatorios: false
  };

  ngOnInit() {
    this.user = this.authService.getCurrentUser();
    this.loadProfile();
  }

  private loadProfile() {
    // Simular carga de datos del perfil
    this.profile = {
      nombre: this.user?.nombre || 'Profesor',
      email: this.user?.email || 'profesor@ejemplo.com',
      codigo: 'PROF001',
      especialidad: 'Desarrollo Web'
    };
  }

  saveProfile() {
    // Aquí se implementaría la lógica para guardar el perfil
    console.log('Guardando perfil:', this.profile);
    this.editing = false;
    alert('Perfil actualizado correctamente');
  }

  changePassword() {
    if (this.passwordData.new !== this.passwordData.confirm) {
      alert('Las contraseñas no coinciden');
      return;
    }
    
    // Aquí se implementaría la lógica para cambiar la contraseña
    console.log('Cambiando contraseña');
    this.passwordData = { current: '', new: '', confirm: '' };
    alert('Contraseña cambiada correctamente');
  }

  saveNotificationSettings() {
    // Aquí se implementaría la lógica para guardar las notificaciones
    console.log('Guardando configuración de notificaciones:', this.notifications);
    alert('Configuración de notificaciones guardada');
  }

  cancelEdit() {
    this.editing = false;
    this.loadProfile();
  }
}