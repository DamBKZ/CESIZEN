import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    // Gestion des erreurs globales
    catchError((error: HttpErrorResponse) => {

      // 401 → Token expiré ou invalide
      if (error.status === 401) {
        localStorage.removeItem('access_token');
        router.navigate(['/login']);
      }

      // 403 → Accès interdit
      if (error.status === 403) {
        router.navigate(['/forbidden']);
      }

      // 404 → Ressource non trouvée
      if (error.status === 404) {
        console.warn('Ressource introuvable :', req.url);
      }

      // 500 → Erreur serveur
      if (error.status >= 500) {
        console.error('Erreur serveur :', error);
      }

      // Toujours relancer l’erreur pour que les services puissent réagir si besoin
      throw error;
    })
  );
};
