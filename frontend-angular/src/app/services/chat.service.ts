import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

export interface ChatMessage {
  id?: number;
  rendezVousId: number;
  sender: 'PATIENT' | 'SECRETAIRE';
  contenu: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = '/api/messages';
  private currentEventSource: EventSource | null = null;

  constructor(private http: HttpClient, private zone: NgZone) {}

  getMessages(rvId: number): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/rendezvous/${rvId}`);
  }

  sendMessage(rvId: number, sender: 'PATIENT' | 'SECRETAIRE', contenu: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${this.apiUrl}/rendezvous/${rvId}?sender=${sender}`, contenu);
  }

  subscribeToChat(rvId: number): Observable<ChatMessage> {
    // Fermer toute connexion SSE précédente
    this.closeConnection();

    const subject = new Subject<ChatMessage>();
    const token = localStorage.getItem('jwt_token') || sessionStorage.getItem('jwt_token') || '';

    // Passer le token en query param car EventSource ne supporte pas les headers custom
    this.currentEventSource = new EventSource(`${this.apiUrl}/subscribe/${rvId}?token=${token}`);

    this.currentEventSource.addEventListener('message', (event: any) => {
      this.zone.run(() => {
        try {
          const message: ChatMessage = JSON.parse(event.data);
          subject.next(message);
        } catch (e) {
          console.error('Erreur parsing message SSE:', e);
        }
      });
    });

    this.currentEventSource.onerror = () => {
      console.warn('SSE chat connection error, reconnecting in 3s...');
      this.closeConnection();
      // Reconnexion automatique après 3 secondes
      setTimeout(() => {
        if (!subject.closed) {
          this.reconnect(rvId, subject, token);
        }
      }, 3000);
    };

    return subject.asObservable();
  }

  private reconnect(rvId: number, subject: Subject<ChatMessage>, token: string) {
    this.currentEventSource = new EventSource(`${this.apiUrl}/subscribe/${rvId}?token=${token}`);

    this.currentEventSource.addEventListener('message', (event: any) => {
      this.zone.run(() => {
        try {
          const message: ChatMessage = JSON.parse(event.data);
          subject.next(message);
        } catch (e) {
          console.error('Erreur parsing message SSE:', e);
        }
      });
    });

    this.currentEventSource.onerror = () => {
      this.closeConnection();
    };
  }

  closeConnection() {
    if (this.currentEventSource) {
      this.currentEventSource.close();
      this.currentEventSource = null;
    }
  }
}
