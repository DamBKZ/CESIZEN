import { HttpInterceptorFn, HttpErrorResponse, HttpBackend, HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { throwError, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { UserStore } from '../stores/user.store';
import { ToastService } from '../../shared/services/toast.service';
import { NotificationService } from '../../shared/services/notification.service';
import { ApiService } from '../services/api.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userStore = inject(UserStore);
  const backend = inject(HttpBackend);
  const http = new HttpClient(backend);
  const api = inject(ApiService);
  const toast = inject(ToastService);
  const notify = inject(NotificationService);

  function readCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'));
    return match ? decodeURIComponent(match[2]) : null;
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {

        const xsrf = readCookie('XSRF-TOKEN');
        const options: any = { withCredentials: true };
        if (xsrf) {
          options.headers = { 'X-XSRF-TOKEN': xsrf };
        }

        const refreshUrl = api.url('/auth/refresh');
        return http.post(refreshUrl, {}, options).pipe(
          switchMap((res: any) => {
            const newToken = res?.accessToken;
            if (!newToken) {
              userStore.clear();
              toast.error('Session expirée, reconnectez-vous');
              notify.jobError('Refresh token invalide — reconnectez-vous');
              router.navigate(['/login']);
              return throwError(() => error);
            }

            userStore.login(newToken);

            const retryReq = req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } });
            return next(retryReq);
          }),
          catchError((err) => {
            userStore.clear();
            toast.error('Session expirée, reconnectez-vous');
            notify.jobError('Refresh token invalide — reconnectez-vous');
            router.navigate(['/login']);
            return throwError(() => err);
          })
        );
      }

      if (error.status === 403) {
        router.navigate(['/forbidden']);
      }

      if (error.status === 404) {
        console.warn('Ressource introuvable :', req.url);
      }

      if (error.status >= 500) {
        console.error('Erreur serveur :', error);
        toast.error('Erreur serveur, réessayez plus tard');
      }

      if (error.status === 0) {
        toast.error('Erreur réseau — vérifiez votre connexion');
      }

      return throwError(() => error);
    })
  );
};
