import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ConsultationRequest {
  patientId: number;
  medecinId: number;
  rendezVousId: number;
  diagnostic: string;
  observations: string;
  ordonnance: string;
  prix: number;
}

@Injectable({
  providedIn: 'root'
})
export class ConsultationService {
  private apiUrl = '/api/consultations';

  constructor(private http: HttpClient) {}

  ajouterConsultation(request: ConsultationRequest): Observable<any> {
    return this.http.post(this.apiUrl, request);
  }

  getHistoriqueMedical(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  verrouillerConsultation(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/verrouiller`, {});
  }

  modifierConsultation(id: number, request: Partial<ConsultationRequest>): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, request);
  }
}
