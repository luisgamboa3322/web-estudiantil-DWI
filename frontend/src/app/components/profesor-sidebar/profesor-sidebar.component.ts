import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profesor-sidebar',
  standalone: true,
  templateUrl: './profesor-sidebar.component.html',
  styleUrls: ['./profesor-sidebar.component.css']
})
export class ProfesorSidebarComponent {
  @Input() currentPath: string = '';

  constructor(private router: Router) {}

  navigateTo(path: string) {
    this.router.navigate([path]);
  }

  isActive(path: string): boolean {
    return this.currentPath.startsWith(path);
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
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
