import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';
import { ProfesorService } from '../../services/profesor.service';

@Component({
  selector: 'app-profesor-chat',
  standalone: true,
  imports: [CommonModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-chat.component.html',
  styleUrls: ['./profesor-chat.component.css']
})
export class ProfesorChatComponent implements OnInit {
  private profesorService = inject(ProfesorService);
  
  profesor: any = {};
  currentPath = '/profesor/chat';

  ngOnInit() {
    this.loadProfesorData();
  }

  loadProfesorData() {
    this.profesorService.getDashboardData().subscribe({
      next: (data) => {
        this.profesor = data.profesor || {};
      },
      error: (error) => {
        console.error('Error cargando datos del profesor:', error);
      }
    });
  }
}
