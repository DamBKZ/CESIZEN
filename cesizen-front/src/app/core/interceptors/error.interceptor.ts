import { HttpBackend, HttpClient, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { UserStore } from '../stores/user.store';
import { ToastService } from '../../shared/services/toast.service';
import { ApiService } from '../services/api.service';

function readCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)'));
  return match ? decodeURIComponent(match[2]) : null;
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userStore = inject(UserStore);
  const backend = inject(HttpBackend);
  const http = new HttpClient(backend);
  const api = inject(ApiService);
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthRequest = req.url.includes('/auth/login')
        || req.url.includes('/auth/refresh')
        || req.url.includes('/auth/logout')
        || req.url.includes('/auth/reset-password');

      if (error.status === 401 && !isAuthRequest) {
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
              router.navigate(['/login']);
              return throwError(() => error);
            }

            userStore.login(newToken);

            let retryReq = req.clone({
              withCredentials: true,
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });

            const retryXsrf = readCookie('XSRF-TOKEN');
            const isMutatingRequest = !['GET', 'HEAD', 'OPTIONS'].includes(req.method.toUpperCase());

            if (isMutatingRequest && retryXsrf) {
              retryReq = retryReq.clone({
                setHeaders: {
                  'X-XSRF-TOKEN': retryXsrf
                }
              });
            }

            return next(retryReq);
          }),
          catchError((err) => {
            userStore.clear();
            toast.error('Session expirée, reconnectez-vous');
            router.navigate(['/login']);
            return throwError(() => err);
          })
        );
      }

      if (error.status === 403) {
        toast.error('Accès refusé');
        router.navigate(['/login']);
      }

      if (error.status >= 500) {
        toast.error('Erreur serveur, réessayez plus tard');
      }

      if (error.status === 0) {
        toast.error('Erreur réseau — vérifiez votre connexion');
      }

      return throwError(() => error);
    })
  );
};
