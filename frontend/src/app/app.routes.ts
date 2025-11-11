import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { ProfesorDashboardComponent } from './components/profesor-dashboard/profesor-dashboard.component';
import { StudentDashboardComponent } from './components/student-dashboard/student-dashboard.component';
import { SelectDashboardComponent } from './components/select-dashboard/select-dashboard.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent
  },
  {
    path: 'profesor/dashboard',
    component: ProfesorDashboardComponent
  },
  {
    path: 'student/dashboard',
    component: StudentDashboardComponent
  },
  {
    path: 'select-dashboard',
    component: SelectDashboardComponent
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
