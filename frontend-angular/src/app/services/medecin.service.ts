import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Medecin {
  id?: number;
  nom: string;
  specialite: string;
  disponibilite: boolean;
  username?: string;
  email?: string;
  password?: string;
  role?: string;
  status?: string;
  secteurs?: any[];
}

@Injectable({
  providedIn: 'root'
})
export class MedecinService {
  private apiUrl = '/api/medecins';

  constructor(private http: HttpClient) {}

  getAllMedecins(): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(this.apiUrl);
  }

  getMedecinById(id: number): Observable<Medecin> {
    return this.http.get<Medecin>(`${this.apiUrl}/${id}`);
  }

  createMedecin(medecin: Medecin): Observable<Medecin> {
    return this.http.post<Medecin>(this.apiUrl, medecin);
  }

  updateMedecin(id: number, medecin: Medecin): Observable<Medecin> {
    return this.http.put<Medecin>(`${this.apiUrl}/${id}`, medecin);
  }

  deleteMedecin(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  resendInvitation(id: number): Observable<void> {
    return this.http.post<void>(`/api/invitations/resend/medecin/${id}`, {});
  }
}
