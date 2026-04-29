import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

export interface Notification {
  id: number;
  message: string;
  lu: boolean;
  date: string;
  rendezVousId?: number;
  pourSecretaire?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = '/api/notifications';
  private notificationSubject = new Subject<Notification>();
  private currentEventSource: EventSource | null = null;

  constructor(private http: HttpClient, private zone: NgZone) {}

  // --- API GETTERS ---

  getNotifications(patientId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  getUnreadNotifications(patientId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/patient/${patientId}/unread`);
  }

  getSecretaireNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/secretaire`);
  }

  getUnreadSecretaireNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/secretaire/unread`);
  }

  getMedecinNotifications(medecinId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/medecin/${medecinId}`);
  }

  getUnreadMedecinNotifications(medecinId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/medecin/${medecinId}/unread`);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {});
  }

  // --- SSE SUBSCRIPTIONS ---

  subscribeToPatientNotifications(patientId: number): Observable<Notification> {
    this.closeConnection();
    const token = this.getToken();
    this.currentEventSource = new EventSource(`${this.apiUrl}/subscribe/${patientId}?token=${token}`);
    this.setupEventSource('PATIENT', patientId);
    return this.notificationSubject.asObservable();
  }

  subscribeToSecretaireNotifications(): Observable<Notification> {
    this.closeConnection();
    const token = this.getToken();
    this.currentEventSource = new EventSource(`${this.apiUrl}/subscribe/secretaire?token=${token}`);
    this.setupEventSource('SECRETAIRE');
    return this.notificationSubject.asObservable();
  }

  subscribeToMedecinNotifications(medecinId: number): Observable<Notification> {
    this.closeConnection();
    const token = this.getToken();
    this.currentEventSource = new EventSource(`${this.apiUrl}/subscribe/medecin/${medecinId}?token=${token}`);
    this.setupEventSource('MEDECIN', medecinId);
    return this.notificationSubject.asObservable();
  }

  private setupEventSource(role: 'PATIENT' | 'SECRETAIRE' | 'MEDECIN', id?: number) {
    if (!this.currentEventSource) return;

    this.currentEventSource.addEventListener('notification', (event: any) => {
      this.zone.run(() => {
        try {
          const notification = JSON.parse(event.data);
          this.notificationSubject.next(notification);
        } catch (e) {
          console.error("Erreur parsing notification SSE:", e);
        }
      });
    });

    this.currentEventSource.onerror = (error) => {
      console.warn(`SSE notification error (${role}):`, error);
      this.closeConnection();
      
      // Auto-reconnect after 3s
      setTimeout(() => {
        if (!this.notificationSubject.closed) {
          if (role === 'PATIENT' && id) this.subscribeToPatientNotifications(id);
          if (role === 'SECRETAIRE') this.subscribeToSecretaireNotifications();
          if (role === 'MEDECIN' && id) this.subscribeToMedecinNotifications(id);
        }
      }, 3000);
    };
  }

  private getToken(): string {
    return localStorage.getItem('jwt_token') || sessionStorage.getItem('jwt_token') || '';
  }

  closeConnection() {
    if (this.currentEventSource) {
      this.currentEventSource.close();
      this.currentEventSource = null;
    }
  }
}
