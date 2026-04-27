import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { AlertService } from '../../../services/alert.service';
import { NotificationService, Notification } from '../../../services/notification.service';
import { ChatService, ChatMessage } from '../../../services/chat.service';
import { FactureListComponent } from '../../shared/facture-list/facture-list';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, FactureListComponent],
  templateUrl: './patient-dashboard.html',
  styleUrl: './patient-dashboard.css'
})
export class PatientDashboard implements OnInit {
  currentView: 'appointments' | 'book' | 'record' | 'factures' = 'appointments';
  userId: number | null = null;
  
  appointments: any[] = [];
  medicalHistory: any[] = [];
  secteurs: any[] = [];
  doctors: any[] = [];
  availableSlots: string[] = ['09:00', '09:30', '10:00', '10:30', '11:00', '11:30', '14:00', '14:30', '15:00', '15:30', '16:00', '16:30'];

  notifications: Notification[] = [];
  unreadCount = 0;
  showNotifications = false;

  activeChatRvId: number | null = null;
  chatMessages: ChatMessage[] = [];
  newMessage = '';

  patientInfo: any = {
    nom: 'Chargement...',
    dateNaissance: '',
    tel: '',
    dossierMedical: ''
  };

  newAppointment = {
    secteurId: null as number | null,
    doctorId: null as number | null,
    date: '',
    time: '',
    motive: ''
  };

  selectedProposition: any = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private alertService: AlertService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private chatService: ChatService
  ) {}

  ngOnInit(): void {
    const role = this.authService.getRole();
    if(role !== 'PATIENT') {
      this.router.navigate(['/login']);
      return;
    }
    
    this.userId = this.authService.getUserId();
    if(this.userId) {
      this.loadPatientData();
      this.loadSecteurs();
      this.initRealTime();
    } else {
      this.alertService.error('Erreur', 'Impossible de récupérer votre identifiant.');
      this.router.navigate(['/login']);
    }
  }

  loadPatientData() {
    this.http.get<any>(`/api/patients/${this.userId}`).subscribe(data => {
      this.patientInfo = data;
      this.cdr.detectChanges();
    });

    this.loadAppointments();
    this.statusCount();
    this.loadMedicalHistory();
  }

  statusCount() {
    // Optional helper if needed, but not strictly required for record view
  }

  loadAppointments() {
    this.http.get<any[]>(`/api/rendezvous/patient/${this.userId}`).subscribe(data => {
      this.appointments = data;
      this.cdr.detectChanges();
    });
  }

  loadMedicalHistory() {
    this.http.get<any[]>(`/api/consultations/patient/${this.userId}`).subscribe(data => {
      this.medicalHistory = data;
      this.cdr.detectChanges();
    });
  }

  loadSecteurs() {
    this.http.get<any[]>('/api/secteurs').subscribe(data => {
      this.secteurs = data;
    });
  }

  onSecteurChange() {
    this.newAppointment.doctorId = null;
    if (this.newAppointment.secteurId) {
      this.http.get<any[]>(`/api/medecins/secteur/${this.newAppointment.secteurId}`).subscribe(data => {
        this.doctors = data;
        this.cdr.detectChanges();
      });
    } else {
      this.doctors = [];
    }
  }

  setView(view: 'appointments' | 'book' | 'record' | 'factures') {
    this.currentView = view;
    if (view === 'appointments') {
      this.loadAppointments();
    } else if (view === 'record') {
      this.loadMedicalHistory();
    }
  }

  submitAppointment() {
    const payload = {
      patient: { id: this.userId },
      medecin: { id: this.newAppointment.doctorId },
      date: this.newAppointment.date,
      heure: `${this.newAppointment.time}:00`, // Format HH:mm:ss for backend LocalTime
      motif: this.newAppointment.motive || 'Non spécifié',
      statut: 'EN_ATTENTE'
    };

    this.http.post('/api/rendezvous', payload).subscribe({
      next: () => {
        this.alertService.success('Succès', 'Votre demande de rendez-vous est enregistrée. Elle est en attente de confirmation.');
        this.newAppointment = { secteurId: null, doctorId: null, date: '', time: '', motive: '' };
        this.setView('appointments');
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Ce créneau n\'est pas disponible pour ce médecin.';
        this.alertService.error('Erreur', errorMsg);
      }
    });
  }

  async logout() {
    const result = await this.alertService.confirm('Déconnexion', 'Voulez-vous quitter votre espace personnel ?');
    if (result.isConfirmed) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  formatDateShort(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }

  initRealTime() {
    this.notificationService.subscribeToPatientNotifications(this.userId!).subscribe((notif: Notification) => {
      this.notifications.unshift(notif);
      this.unreadCount++;
      this.alertService.success('Notification', notif.message);
      this.cdr.detectChanges();
    });

    this.loadInitialNotifications();
  }

  loadInitialNotifications() {
    this.notificationService.getNotifications(this.userId!).subscribe(data => {
      this.notifications = data;
      this.unreadCount = data.filter(n => !n.lu).length;
      this.cdr.detectChanges();
    });
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.notifications.forEach(n => {
        if (!n.lu) {
          this.notificationService.markAsRead(n.id).subscribe();
          n.lu = true;
        }
      });
      this.unreadCount = 0;
    }
  }

  handleNotificationClick(n: Notification) {
    this.showNotifications = false;
    if (n.rendezVousId) {
      this.setView('appointments');
      this.openChat(n.rendezVousId);
    }
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
    this.chatService.sendMessage(this.activeChatRvId!, 'PATIENT', this.newMessage).subscribe((msg) => {
      if (!this.chatMessages.some(m => m.id === msg.id)) {
        this.chatMessages.push(msg);
        this.cdr.detectChanges();
      }
      this.newMessage = '';
    });
  }

  openProposition(appt: any) {
    this.selectedProposition = appt;
  }

  closeProposition() {
    this.selectedProposition = null;
  }

  repondreProposition(rvId: number, action: 'ACCEPTER' | 'REFUSER') {
    this.http.post(`/api/rendezvous/${rvId}/repondre?action=${action}`, {}).subscribe(() => {
      this.alertService.success('Succès', `Vous avez ${action.toLowerCase()} la proposition.`);
      this.selectedProposition = null;
      this.loadAppointments();
      this.closeChat();
    });
  }
}
