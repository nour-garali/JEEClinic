import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facture } from '../models/facture.model';

@Injectable({
  providedIn: 'root'
})
export class FactureService {
  private apiUrl = '/api/factures';

  constructor(private http: HttpClient) {}

  getAllFactures(): Observable<Facture[]> {
    return this.http.get<Facture[]>(this.apiUrl);
  }

  getFacturesByPatient(patientId: number): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  getFacturesByMedecin(medecinId: number): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${this.apiUrl}/medecin/${medecinId}`);
  }

  getFactureById(id: number): Observable<Facture> {
    return this.http.get<Facture>(`${this.apiUrl}/${id}`);
  }

  downloadFacturePdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }

  payerFacture(id: number): Observable<Facture> {
    return this.http.patch<Facture>(`${this.apiUrl}/${id}/payer`, {});
  }
}
