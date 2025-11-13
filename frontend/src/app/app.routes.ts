import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  
  // Rutas de autenticación
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  
  // Componente de debug para diagnóstico
  { path: 'debug', loadComponent: () => import('./components/debug.component').then(m => m.DebugComponent) },
  
  // Selección de dashboard
  { path: 'select-dashboard', loadComponent: () => import('./components/select-dashboard/select-dashboard.component').then(m => m.SelectDashboardComponent) },
  
  // Dashboards específicos
  { path: 'admin/dashboard', loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
  { path: 'profesor/dashboard', loadComponent: () => import('./components/profesor-dashboard/profesor-dashboard.component').then(m => m.ProfesorDashboardComponent) },
  { path: 'student/dashboard', loadComponent: () => import('./components/student-dashboard/student-dashboard.component').then(m => m.StudentDashboardComponent) },
  
  // Rutas de administrador
  { path: 'redirect/admin', loadComponent: () => import('./components/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
  
  // Rutas de profesor
  { path: 'profesor/calendario', loadComponent: () => import('./components/profesor-calendario/profesor-calendario.component').then(m => m.ProfesorCalendarioComponent) },
  { path: 'profesor/chat', loadComponent: () => import('./components/profesor-chat/profesor-chat.component').then(m => m.ProfesorChatComponent) },
  { path: 'profesor/configuracion', loadComponent: () => import('./components/profesor-configuracion/profesor-configuracion.component').then(m => m.ProfesorConfiguracionComponent) },
  { path: 'profesor/gestion-curso', loadComponent: () => import('./components/profesor-gestion-curso/profesor-gestion-curso.component').then(m => m.ProfesorGestionCursoComponent) },
  { path: 'redirect/profesor', loadComponent: () => import('./components/profesor-dashboard/profesor-dashboard.component').then(m => m.ProfesorDashboardComponent) },
  
  // Rutas de estudiante
  { path: 'student/calendario', loadComponent: () => import('./components/student-calendario/student-calendario.component').then(m => m.StudentCalendarioComponent) },
  { path: 'student/chat', loadComponent: () => import('./components/student-chat/student-chat.component').then(m => m.StudentChatComponent) },
  { path: 'student/configuracion', loadComponent: () => import('./components/student-configuracion/student-configuracion.component').then(m => m.StudentConfiguracionComponent) },
  { path: 'student/curso/:id', loadComponent: () => import('./components/student-curso-detalle/student-curso-detalle.component').then(m => m.StudentCursoDetalleComponent) },
  { path: 'redirect/student', loadComponent: () => import('./components/student-dashboard/student-dashboard.component').then(m => m.StudentDashboardComponent) },
  
  // Ruta de error
  { path: 'error/acceso-denegado', loadComponent: () => import('./components/error-acceso-denegado/error-acceso-denegado.component').then(m => m.ErrorAccesoDenegadoComponent) },
  
  // Ruta de logout
  { path: 'logout', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  
  // Ruta por defecto para rutas no encontradas
  { path: '**', redirectTo: '/login' }
];
