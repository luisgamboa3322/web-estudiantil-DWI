// Componente de demonstração simples
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-simple-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-5">
      <h1>Dashboard Principal</h1>
      <p>Bienvenido al sistema!</p>
    </div>
  `,
  styles: []
})
export class SimpleDashboardComponent {}