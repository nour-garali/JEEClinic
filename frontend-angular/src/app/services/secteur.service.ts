import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Medecin } from './medecin.service';

export interface Secteur {
  id?: number;
  nom: string;
  description: string;
  medecins?: Medecin[];
}

@Injectable({
  providedIn: 'root'
})
export class SecteurService {
  private apiUrl = '/api/secteurs';

  constructor(private http: HttpClient) {}

  getAllSecteurs(): Observable<Secteur[]> {
    return this.http.get<Secteur[]>(this.apiUrl);
  }

  getSecteurById(id: number): Observable<Secteur> {
    return this.http.get<Secteur>(`${this.apiUrl}/${id}`);
  }

  createSecteur(secteur: Secteur): Observable<Secteur> {
    return this.http.post<Secteur>(this.apiUrl, secteur);
  }

  updateSecteur(id: number, secteur: Secteur): Observable<Secteur> {
    return this.http.put<Secteur>(`${this.apiUrl}/${id}`, secteur);
  }

  deleteSecteur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
