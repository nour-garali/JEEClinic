import { Routes } from '@angular/router';
import { HomeComponent } from './home.component';
import { Login } from './components/auth/login/login';
import { Register } from './components/auth/register/register';
import { PatientDashboard } from './components/dashboards/patient-dashboard/patient-dashboard';
import { MedecinDashboard } from './components/dashboards/medecin-dashboard/medecin-dashboard';
import { AdminDashboard } from './components/dashboards/admin-dashboard/admin-dashboard';
import { SecretaireDashboard } from './components/dashboards/secretaire-dashboard/secretaire-dashboard';
import { RegisterDoctorComponent } from './components/auth/register-doctor/register-doctor';
import { RegisterSecretaryComponent } from './components/auth/register-secretary/register-secretary';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'register-doctor', component: RegisterDoctorComponent },
  { path: 'register-secretary', component: RegisterSecretaryComponent },
  { 
    path: 'patient', 
    component: PatientDashboard, 
    canActivate: [authGuard],
    data: { role: 'PATIENT' } 
  },
  { 
    path: 'medecin', 
    component: MedecinDashboard, 
    canActivate: [authGuard],
    data: { role: 'MEDECIN' } 
  },
  { 
    path: 'admin', 
    component: AdminDashboard, 
    canActivate: [authGuard],
    data: { role: 'ADMIN' } 
  },
  { 
    path: 'secretaire', 
    component: SecretaireDashboard, 
    canActivate: [authGuard],
    data: { role: 'SECRETAIRE' } 
  },
  { path: '**', redirectTo: '/login' }
];
