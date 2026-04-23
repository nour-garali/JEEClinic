import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Secteur } from './secteur.service';

export interface Secretaire {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  password?: string;
  role?: string;
  status?: string;
  secteur?: Secteur;
}

@Injectable({
  providedIn: 'root'
})
export class SecretaireService {
  private apiUrl = '/api/secretaires';

  constructor(private http: HttpClient) {}

  getAllSecretaires(): Observable<Secretaire[]> {
    return this.http.get<Secretaire[]>(this.apiUrl);
  }

  getSecretaireById(id: number): Observable<Secretaire> {
    return this.http.get<Secretaire>(`${this.apiUrl}/${id}`);
  }

  createSecretaire(secretaire: Secretaire): Observable<Secretaire> {
    return this.http.post<Secretaire>(this.apiUrl, secretaire);
  }

  updateSecretaire(id: number, secretaire: Secretaire): Observable<Secretaire> {
    return this.http.put<Secretaire>(`${this.apiUrl}/${id}`, secretaire);
  }

  deleteSecretaire(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  resendInvitation(id: number): Observable<void> {
    return this.http.post<void>(`/api/invitations/resend/secretaire/${id}`, {});
  }
}
