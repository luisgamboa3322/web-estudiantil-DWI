import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-profesor-header',
  standalone: true,
  templateUrl: './profesor-header.component.html',
  styleUrls: ['./profesor-header.component.css']
})
export class ProfesorHeaderComponent {
  @Input() profesor: any;

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
