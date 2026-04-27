import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { AlertService } from '../../../services/alert.service';
import { ChatService, ChatMessage } from '../../../services/chat.service';
import { NotificationService } from '../../../services/notification.service';
import { HttpClient } from '@angular/common/http';
import { FactureService } from '../../../services/facture.service';
import { Facture } from '../../../models/facture.model';

@Component({
  selector: 'app-secretaire-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './secretaire-dashboard.html',
  styleUrl: './secretaire-dashboard.css'
})
export class SecretaireDashboard implements OnInit {
  currentView: 'appointments' | 'patients' | 'profile' | 'payments' = 'appointments';
  
  appointments: any[] = [];
  patients: any[] = [];
  factures: Facture[] = [];
  
  // Filter logic
  selectedDate: string = new Date().toISOString().split('T')[0];
  today: Date = new Date();
  
  stats = {
    todayAppointments: 0,
    totalPatients: 0,
    pendingConfirmations: 0
  };

  selectedRv: any = null;
  proposition = {
    date: '',
    heure: ''
  };

  activeChatRvId: number | null = null;
  chatMessages: ChatMessage[] = [];
  newMessage = '';

  notifications: any[] = [];
  unreadNotifications: number = 0;
  showNotifications = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private alertService: AlertService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private chatService: ChatService,
    private notificationService: NotificationService,
    private factureService: FactureService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.loadNotifications();

    this.notificationService.subscribeToSecretaireNotifications().subscribe((notif: any) => {
      this.notifications.unshift(notif);
      if (!notif.lu) {
        this.unreadNotifications++;
      }
      this.alertService.success('Nouvelle Notification', notif.message);
      this.cdr.detectChanges();
    });
  }

  loadNotifications(): void {
    this.notificationService.getSecretaireNotifications().subscribe((data: any[]) => {
      this.notifications = data;
      this.unreadNotifications = this.notifications.filter(n => !n.lu).length;
      this.cdr.detectChanges();
    });
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }

  markAsRead(notification: any): void {
    if (!notification.lu) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        notification.lu = true;
        this.unreadNotifications--;
        this.cdr.detectChanges();
      });
    }
    
    // Affiche le contenu
    this.alertService.success('Notification', notification.message);
    this.showNotifications = false;

    // Si on a un rendezVousId, on peut peut-être ouvrir le chat ou rafraîchir
    if (notification.rendezVousId && notification.message.includes('message')) {
       this.openChat(notification.rendezVousId);
    }
  }

  loadDashboardData(): void {
    // These would typically call specialized services
    // For now, we'll fetch from the general endpoints
    this.http.get<any[]>('/api/rendezvous').subscribe(data => {
      this.appointments = data;
      const today = new Date().toISOString().split('T')[0];
      this.stats.todayAppointments = data.filter(a => a.date === today).length;
      this.stats.pendingConfirmations = data.filter(a => a.statut === 'EN_ATTENTE').length;
      this.cdr.detectChanges();
    });

    this.http.get<any[]>('/api/patients').subscribe(data => {
      this.patients = data;
      this.stats.totalPatients = data.length;
      this.cdr.detectChanges();
    });
  }

  get filteredAppointments(): any[] {
    if (!this.selectedDate) return this.appointments;
    return this.appointments.filter(a => a.date === this.selectedDate);
  }

  resetToToday(): void {
    this.selectedDate = new Date().toISOString().split('T')[0];
    this.cdr.detectChanges();
  }

  showAll(): void {
    this.selectedDate = '';
    this.cdr.detectChanges();
  }

  setView(view: 'appointments' | 'patients' | 'profile' | 'payments'): void {
    this.currentView = view;
    if (view === 'payments') {
      this.loadFactures();
    }
  }

  loadFactures(): void {
    this.factureService.getAllFactures().subscribe(data => {
      this.factures = data.sort((a, b) => {
          const dateA = new Date(a.dateCreation).getTime();
          const dateB = new Date(b.dateCreation).getTime();
          return dateB - dateA;
      });
      this.cdr.detectChanges();
    });
  }

  payerFacture(id: number): void {
     this.factureService.payerFacture(id).subscribe({
       next: () => {
         this.alertService.success('Paiement Validé', 'La facture a été marquée comme payée avec succès.');
         this.loadFactures();
       },
       error: () => {
         this.alertService.error('Erreur', 'Impossible de valider le paiement.');
       }
     });
  }

  updateAppointmentStatus(id: number, statut: 'CONFIRME' | 'ANNULE') {
    this.http.patch(`/api/rendezvous/${id}/statut?statut=${statut}`, {}).subscribe({
      next: () => {
        this.alertService.success('Succès', `Le rendez-vous a été ${statut.toLowerCase()}.`);
        this.loadDashboardData(); // Reload to reflect changes
      },
      error: () => {
        this.alertService.error('Erreur', 'Impossible de mettre à jour le statut.');
      }
    });
  }

  openProposeModal(rv: any) {
    this.selectedRv = rv;
    this.proposition.date = '';
    this.proposition.heure = '';
  }

  proposerNouveauCreneau() {
    if (!this.proposition.date || !this.proposition.heure) {
      this.alertService.error('Erreur', 'Veuillez choisir une date et une heure.');
      return;
    }

    const payload = new URLSearchParams();
    payload.set('date', this.proposition.date);
    payload.set('heure', this.proposition.heure + ':00');

    this.http.post(`/api/rendezvous/${this.selectedRv.id}/proposer?${payload.toString()}`, {}).subscribe({
      next: () => {
        this.alertService.success('Proposé', 'Une nouvelle proposition a été envoyée au patient.');
        this.selectedRv = null;
        this.loadDashboardData();
      },
      error: () => {
        this.alertService.error('Erreur', 'Impossible d\'envoyer la proposition.');
      }
    });
  }

  openChat(rvId: number) {
    this.activeChatRvId = rvId;
    this.chatService.getMessages(rvId).subscribe(msgs => {
      this.chatMessages = msgs;
      this.cdr.detectChanges();
    });

    this.chatService.subscribeToChat(rvId).subscribe(msg => {
      if (this.activeChatRvId === rvId) {
        if (!this.chatMessages.some(m => m.id === msg.id)) {
          this.chatMessages.push(msg);
          this.cdr.detectChanges();
        }
      }
    });
  }

  closeChat() {
    this.chatService.closeConnection();
    this.activeChatRvId = null;
    this.chatMessages = [];
  }

  sendChatMessage() {
    if (!this.newMessage.trim()) return;
    this.chatService.sendMessage(this.activeChatRvId!, 'SECRETAIRE', this.newMessage).subscribe((msg) => {
      if (!this.chatMessages.some(m => m.id === msg.id)) {
        this.chatMessages.push(msg);
        this.cdr.detectChanges();
      }
      this.newMessage = '';
    });
  }

  async logout() {
    const result = await this.alertService.confirm('Déconnexion', 'Voulez-vous vous déconnecter ?');
    if (result.isConfirmed) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }
}
