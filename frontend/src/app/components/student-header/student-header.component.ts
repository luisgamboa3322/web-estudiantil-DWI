import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-student-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-header.component.html',
  styleUrl: './student-header.component.css'
})
export class StudentHeaderComponent {
  private authService = inject(AuthService);
  
  user: any = null;
  showNotifications = false;
  showUserMenu = false;

  ngOnInit() {
    this.user = this.authService.getCurrentUserValue();
  }

  toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
      if (sidebar.classList.contains('hidden')) {
        sidebar.classList.remove('hidden');
        sidebar.classList.add('absolute', 'z-20', 'w-full');
      } else {
        sidebar.classList.add('hidden');
        sidebar.classList.remove('absolute', 'z-20', 'w-full');
      }
    }
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        window.location.href = '/login';
      },
      error: (error) => {
        console.error('Error logging out:', error);
        window.location.href = '/login';
      }
    });
  }
}