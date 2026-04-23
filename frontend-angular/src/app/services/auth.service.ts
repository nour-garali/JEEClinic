import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  role: string;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';
  private currentUserRoleSubject = new BehaviorSubject<string | null>(this.getRole());

  public currentUserRole$ = this.currentUserRoleSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: any
  ) {}

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token && this.isBrowser()) {
          localStorage.setItem('auth_token', response.token);
          localStorage.setItem('user_role', response.role);
          if (response.userId) {
            localStorage.setItem('user_id', response.userId.toString());
          }
          this.currentUserRoleSubject.next(response.role);
        }
      })
    );
  }

  register(userData: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap(response => {
        if (response.token && this.isBrowser()) {
          localStorage.setItem('auth_token', response.token);
          localStorage.setItem('user_role', response.role);
          if (response.userId) {
            localStorage.setItem('user_id', response.userId.toString());
          }
          this.currentUserRoleSubject.next(response.role);
        }
      })
    );
  }

  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_role');
      localStorage.removeItem('user_id');
    }
    this.currentUserRoleSubject.next(null);
  }

  getToken(): string | null {
    if (this.isBrowser()) {
      return localStorage.getItem('auth_token');
    }
    return null;
  }

  getRole(): string | null {
    if (this.isBrowser()) {
      return localStorage.getItem('user_role');
    }
    return null;
  }

  getUserId(): number | null {
    if (this.isBrowser()) {
      const id = localStorage.getItem('user_id');
      return id ? parseInt(id, 10) : null;
    }
    return null;
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}
