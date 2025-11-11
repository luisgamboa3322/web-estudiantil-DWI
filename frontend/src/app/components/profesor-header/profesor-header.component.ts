import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-profesor-header',
  imports: [],
  templateUrl: './profesor-header.component.html',
  styleUrl: './profesor-header.component.css'
})
export class ProfesorHeaderComponent {
  @Input() profesor: any;
}
