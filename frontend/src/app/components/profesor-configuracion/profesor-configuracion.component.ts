import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { ProfesorService } from '../../services/profesor.service';

@Component({
  selector: 'app-profesor-configuracion',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-configuracion.component.html',
  styleUrls: ['./profesor-configuracion.component.css']
})
export class ProfesorConfiguracionComponent implements OnInit {
  private profesorService = inject(ProfesorService);
  
  profesor: any = {};
  currentPath = '/profesor/configuracion';
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
    this.loadProfile();
  }

  private loadProfile() {
    this.profesorService.getDashboardData().subscribe({
      next: (data) => {
        this.profesor = data.profesor || {};
        this.profile = {
          nombre: this.profesor.nombre || '',
          email: this.profesor.email || '',
          codigo: this.profesor.codigo || '',
          especialidad: this.profesor.especialidad || ''
        };
      },
      error: (error) => {
        console.error('Error cargando perfil:', error);
      }
    });
  }

  saveProfile() {
    this.profesorService.actualizarPerfil(this.profile).subscribe({
      next: (profesor) => {
        this.profesor = profesor;
        this.profile = {
          nombre: profesor.nombre || '',
          email: profesor.email || '',
          codigo: profesor.codigo || '',
          especialidad: profesor.especialidad || ''
        };
        this.editing = false;
        alert('Perfil actualizado correctamente');
      },
      error: (error) => {
        console.error('Error guardando perfil:', error);
        alert('Error al actualizar el perfil: ' + (error.error || 'Error desconocido'));
      }
    });
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