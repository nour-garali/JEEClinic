import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AlertService } from '../../../services/alert.service';

@Component({
  selector: 'app-register-secretary',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register-secretary.html',
  styleUrl: './register-secretary.css'
})
export class RegisterSecretaryComponent implements OnInit {
  token: string | null = null;
  isValidating: boolean = true;
  isTokenValid: boolean = false;
  errorMessage: string = '';
  
  newPassword = '';
  confirmPassword = '';
  isSubmitting: boolean = false;

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    if (!this.token) {
      this.errorMessage = "Lien d'invitation invalide ou manquant.";
      this.isValidating = false;
      return;
    }

    this.http.get<boolean>(`/api/invitations/validate?token=${this.token}`).subscribe({
      next: (isValid) => {
        this.isTokenValid = isValid;
        this.isValidating = false;
        if (!isValid) {
          this.errorMessage = "Ce lien d'invitation a expiré ou a déjà été utilisé.";
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = "Impossible de joindre le serveur.";
        this.isValidating = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.alertService.warning("Les mots de passe ne correspondent pas.");
      return;
    }
    
    if (this.newPassword.length < 6) {
      this.alertService.warning("Le mot de passe doit comporter au moins 6 caractères.");
      return;
    }

    this.isSubmitting = true;
    this.http.post('/api/invitations/accept', {
      token: this.token,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.alertService.success("Compte configuré avec succès !");
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.alertService.error("Une erreur est survenue.");
        this.isSubmitting = false;
      }
    });
  }
}
