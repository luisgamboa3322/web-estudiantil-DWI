import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfesorSidebarComponent } from '../profesor-sidebar/profesor-sidebar.component';
import { ProfesorHeaderComponent } from '../profesor-header/profesor-header.component';

@Component({
  selector: 'app-profesor-chat',
  imports: [CommonModule, ProfesorSidebarComponent, ProfesorHeaderComponent],
  templateUrl: './profesor-chat.component.html',
  styleUrls: ['./profesor-chat.component.css']
})
export class ProfesorChatComponent {

}
