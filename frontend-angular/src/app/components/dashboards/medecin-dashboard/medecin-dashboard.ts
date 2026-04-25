import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { AlertService } from '../../../services/alert.service';
import { ConsultationService, ConsultationRequest } from '../../../services/consultation.service';
import { NotificationService, Notification } from '../../../services/notification.service';
import { FactureListComponent } from '../../shared/facture-list/facture-list';

@Component({
  selector: 'app-medecin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, FactureListComponent],
  templateUrl: './medecin-dashboard.html',
  styleUrl: './medecin-dashboard.css'
})
export class MedecinDashboard implements OnInit {
  currentView: 'overview' | 'rendezvous' | 'patients' | 'factures' = 'overview';
  medecinId: number | null = null;
  medecinName: string = 'Chargement...';

  appointments: any[] = [];
  patients: any[] = [];
  
  // Notification logic
  notifications: Notification[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;
  
  // Filtering logic
  selectedDate: string = new Date().toISOString().split('T')[0];

  // Consultation logic
  selectedAppointment: any = null;
  showConsultationModal: boolean = false;
  consultationData: any = {
    diagnostic: '',
    observations: '',
    ordonnance: '',
    prix: 0
  };

  // Patient history logic
  selectedPatient: any = null;
  patientHistory: any[] = [];
  showHistoryModal: boolean = false;
  activeAppointmentForSelectedPatient: any = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private alertService: AlertService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private consultationService: ConsultationService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    const role = this.authService.getRole();
    if (role !== 'MEDECIN') {
      this.router.navigate(['/login']);
      return;
    }

    this.medecinId = this.authService.getUserId();
    if (this.medecinId) {
      this.loadMedecinData();
      this.loadAppointments();
      this.loadPatients();
      this.initNotifications();
    } else {
      this.alertService.error('Erreur', 'Impossible de récupérer votre identifiant.');
      this.router.navigate(['/login']);
    }
  }

  loadMedecinData() {
    this.http.get<any>(`/api/medecins/${this.medecinId}`).subscribe(data => {
      this.medecinName = data.nom;
      this.cdr.detectChanges();
    });
  }

  loadAppointments() {
    this.http.get<any[]>(`/api/rendezvous/medecin/${this.medecinId}`).subscribe(data => {
      this.appointments = data.sort((a, b) => {
        const dateA = new Date(a.date + 'T' + a.heure);
        const dateB = new Date(b.date + 'T' + b.heure);
        return dateA.getTime() - dateB.getTime();
      });
      this.cdr.detectChanges();
    });
  }

  get filteredAppointments(): any[] {
    if (!this.selectedDate) return this.appointments;
    return this.appointments.filter(a => a.date === this.selectedDate);
  }

  resetToToday() {
    this.selectedDate = new Date().toISOString().split('T')[0];
    this.cdr.detectChanges();
  }

  showAll() {
    this.selectedDate = '';
    this.cdr.detectChanges();
  }

  loadPatients() {
    this.http.get<any[]>('/api/patients').subscribe(data => {
      this.patients = data;
      this.cdr.detectChanges();
    });
  }

  setView(view: 'overview' | 'rendezvous' | 'patients' | 'factures') {
    this.currentView = view;
    if (view === 'rendezvous') this.loadAppointments();
    if (view === 'patients') this.loadPatients();
  }

  // --- CONSULTATION LOGIC ---

  openConsultationModal(appt: any) {
    if (appt.statut !== 'CONFIRME') {
      this.alertService.error('Erreur', 'Vous ne pouvez consulter qu\'un rendez-vous confirmé.');
      return;
    }
    this.selectedAppointment = appt;
    this.consultationData = {
      diagnostic: '',
      observations: '',
      ordonnance: '',
      prix: 0
    };
    this.showConsultationModal = true;
  }

  closeConsultationModal() {
    this.showConsultationModal = false;
    this.selectedAppointment = null;
  }

  submitConsultation() {
    const request: ConsultationRequest = {
      patientId: this.selectedAppointment.patient.id,
      medecinId: this.medecinId!,
      rendezVousId: this.selectedAppointment.id,
      diagnostic: this.consultationData.diagnostic,
      observations: this.consultationData.observations,
      ordonnance: this.consultationData.ordonnance,
      prix: this.consultationData.prix
    };

    this.consultationService.ajouterConsultation(request).subscribe({
      next: () => {
        this.alertService.success('Succès', 'La consultation a été enregistrée avec succès.');
        this.closeConsultationModal();
        this.loadAppointments();
        this.setView('rendezvous');
        if (this.showHistoryModal) {
            this.viewPatientHistory(this.selectedPatient); // Refresh history if open
        }
      },
      error: (err) => {
        this.alertService.error('Erreur', err.error?.message || 'Une erreur est survenue lors de l\'enregistrement.');
      }
    });
  }

  // --- HISTORY LOGIC ---

  viewPatientHistory(patient: any) {
    this.selectedPatient = patient;
    this.consultationService.getHistoriqueMedical(patient.id).subscribe(history => {
      this.patientHistory = history;
      const today = new Date().toISOString().split('T')[0];
      this.activeAppointmentForSelectedPatient = this.appointments.find(
          a => a.patient.id === patient.id && a.date === today && a.statut === 'CONFIRME'
      );
      this.showHistoryModal = true;
      this.cdr.detectChanges();
    });
  }

  startConsultationFromHistory() {
    if (this.activeAppointmentForSelectedPatient) {
        this.openConsultationModal(this.activeAppointmentForSelectedPatient);
    } else {
        this.alertService.error('Erreur', 'Aucun rendez-vous confirmé aujourd\'hui pour ce patient.');
    }
  }

  closeHistoryModal() {
    this.showHistoryModal = false;
    this.selectedPatient = null;
    this.patientHistory = [];
    this.activeAppointmentForSelectedPatient = null;
  }

  async logout() {
    const result = await this.alertService.confirm('Déconnexion', 'Voulez-vous quitter votre espace Docteur ?');
    if (result.isConfirmed) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  calculateAge(birthDate: string): number {
    if (!birthDate) return 0;
    const birth = new Date(birthDate);
    const now = new Date();
    let age = now.getFullYear() - birth.getFullYear();
    const m = now.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && now.getDate() < birth.getDate())) {
        age--;
    }
    return age;
  }

  getAppointmentsToday() {
    const today = new Date().toISOString().split('T')[0];
    return this.appointments.filter(a => a.date === today && a.statut === 'CONFIRME').length;
  }

  getFinishedConsultations() {
    return this.appointments.filter(a => a.statut === 'TERMINE').length;
  }

  // --- NOTIFICATION LOGIC ---

  initNotifications() {
    if (!this.medecinId) return;

    this.notificationService.getMedecinNotifications(this.medecinId).subscribe(data => {
      this.notifications = data;
      this.unreadCount = data.filter(n => !n.lu).length;
      this.cdr.detectChanges();
    });

    this.notificationService.subscribeToMedecinNotifications(this.medecinId).subscribe((notif: Notification) => {
      this.notifications.unshift(notif);
      this.unreadCount++;
      this.alertService.success('Nouvelle notification', notif.message);
      this.cdr.detectChanges();
    });
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications && this.unreadCount > 0) {
      this.notifications.forEach(n => {
        if (!n.lu) {
          this.notificationService.markAsRead(n.id).subscribe();
          n.lu = true;
        }
      });
      this.unreadCount = 0;
      this.cdr.detectChanges();
    }
  }

  handleNotificationClick(notif: Notification) {
    this.showNotifications = false;
    if (notif.rendezVousId) {
        this.setView('rendezvous');
        // Optionally scroll to or highlight the appointment
    }
  }
}
