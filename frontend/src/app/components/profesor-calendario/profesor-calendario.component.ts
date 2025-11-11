import { Component } from '@angular/core';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';

@Component({
  selector: 'app-profesor-calendario',
  imports: [ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-calendario.component.html',
  styleUrl: './profesor-calendario.component.css'
})
export class ProfesorCalendarioComponent {

}
