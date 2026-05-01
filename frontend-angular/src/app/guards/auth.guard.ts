import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    const expectedRole = route.data['role'];
    const currentRole = authService.getRole();

    if (expectedRole && expectedRole !== currentRole) {
      if (currentRole === 'PATIENT') return router.parseUrl('/patient');
      if (currentRole === 'MEDECIN') return router.parseUrl('/medecin');
      if (currentRole === 'ADMIN') return router.parseUrl('/admin');
      return router.parseUrl('/login');
    }
    return true;
  }

  return router.parseUrl('/login');
};
