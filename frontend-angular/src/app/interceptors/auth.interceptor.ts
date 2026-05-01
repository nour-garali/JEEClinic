import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  
  // Only try to read localStorage if we are in the browser!
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('auth_token');
    
    // Si on a un token et qu'on va vers l'API, on l'attache
    if (token && req.url.includes('/api/')) {
      const clonedReq = req.clone({
        setHeaders: {
          Authorization: token
        }
      });
      return next(clonedReq);
    }
  }
  
  return next(req);
};
