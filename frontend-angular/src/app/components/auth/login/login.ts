import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res.role === 'PATIENT') {
          this.router.navigate(['/patient']);
        } else if (res.role === 'MEDECIN') {
          this.router.navigate(['/medecin']);
        } else if (res.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (res.role === 'SECRETAIRE') {
          this.router.navigate(['/secretaire']);
        }
      },
      error: () => {
        this.errorMessage = 'Identifiants (Email/Mot de passe) invalides ou erreur de connexion.';
      }
    });
  }
}
