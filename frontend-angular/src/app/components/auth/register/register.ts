import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  username = '';
  email = '';
  password = '';
  tel = '';
  dateNaissance = '';
  errorMessage = '';
  today = new Date().toISOString().split('T')[0];

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.register({
      username: this.username,
      email: this.email,
      password: this.password,
      tel: this.tel,
      dateNaissance: this.dateNaissance
    }).subscribe({
      next: () => {
        // Déconnecter l'utilisateur (nettoyer le token généré lors de l'inscription) pour forcer le login
        this.authService.logout();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration error details:', err);
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else if (err.error && typeof err.error === 'object') {
          // Si le backend renvoie un map d'erreurs de validation
          this.errorMessage = Object.values(err.error).join(', ');
        } else {
          this.errorMessage = 'Erreur lors de l’inscription. Vérifiez vos informations.';
        }
      }
    });
  }
}
