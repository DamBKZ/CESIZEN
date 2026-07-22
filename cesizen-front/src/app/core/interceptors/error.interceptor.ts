import {
  HttpBackend,
  HttpClient,
  HttpErrorResponse,
  HttpEvent,
  HttpHeaders,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';

import { inject } from '@angular/core';
import { Router } from '@angular/router';

import {
  BehaviorSubject,
  catchError,
  filter,
  finalize,
  Observable,
  switchMap,
  take,
  throwError
} from 'rxjs';

import { UserStore } from '../stores/user.store';
import { ToastService } from '../../shared/services/toast.service';

interface RefreshTokenResponse {
  accessToken: string;
}

const SAFE_METHODS = new Set([
  'GET',
  'HEAD',
  'OPTIONS'
]);

let refreshInProgress = false;

const refreshedTokenSubject =
  new BehaviorSubject<string | null>(null);

function readCookie(name: string): string | null {
  const prefix = `${encodeURIComponent(name)}=`;

  const cookie = document.cookie
    .split(';')
    .map(value => value.trim())
    .find(value => value.startsWith(prefix));

  if (!cookie) {
    return null;
  }

  const value = cookie.substring(prefix.length);

  try {
    return decodeURIComponent(value);
  } catch {
    return value;
  }
}

function isAuthRequest(url: string): boolean {
  return url.includes('/auth/login')
    || url.includes('/auth/refresh')
    || url.includes('/auth/logout')
    || url.includes('/auth/reset-password');
}

function addAuthenticationHeaders(
  request: HttpRequest<unknown>,
  accessToken: string
): HttpRequest<unknown> {
  const headers: Record<string, string> = {
    Authorization: `Bearer ${accessToken}`
  };

  const method = request.method.toUpperCase();
  const isMutatingRequest = !SAFE_METHODS.has(method);

  if (isMutatingRequest) {
    const xsrfToken = readCookie('XSRF-TOKEN');

    if (xsrfToken) {
      headers['X-XSRF-TOKEN'] = xsrfToken;
    }
  }

  return request.clone({
    withCredentials: true,
    setHeaders: headers
  });
}

function createRefreshHeaders(): HttpHeaders {
  const xsrfToken = readCookie('XSRF-TOKEN');

  if (!xsrfToken) {
    return new HttpHeaders();
  }

  return new HttpHeaders({
    'X-XSRF-TOKEN': xsrfToken
  });
}

export const errorInterceptor: HttpInterceptorFn = (
  request,
  next
): Observable<HttpEvent<unknown>> => {
  const router = inject(Router);
  const userStore = inject(UserStore);
  const toast = inject(ToastService);
  const backend = inject(HttpBackend);
  const rawHttp = new HttpClient(backend);

  const expireSession = (): void => {
    userStore.clear();

    toast.error(
      'Session expirée, reconnectez-vous'
    );

    void router.navigate(['/login']);
  };

  const refreshAndRetry =
    (): Observable<HttpEvent<unknown>> => {
      if (refreshInProgress) {
        return refreshedTokenSubject.pipe(
          filter(
            (token): token is string =>
              token !== null
          ),
          take(1),
          switchMap(token =>
            next(
              addAuthenticationHeaders(
                request,
                token
              )
            )
          )
        );
      }

      refreshInProgress = true;
      refreshedTokenSubject.next(null);

      return rawHttp
        .post<RefreshTokenResponse>(
          '/auth/refresh',
          {},
          {
            withCredentials: true,
            headers: createRefreshHeaders()
          }
        )
        .pipe(
          switchMap(response => {
            const newToken =
              response.accessToken?.trim();

            if (!newToken) {
              expireSession();

              return throwError(() =>
                new Error(
                  'Access token manquant après refresh.'
                )
              );
            }

            userStore.login(newToken);
            refreshedTokenSubject.next(newToken);

            return next(
              addAuthenticationHeaders(
                request,
                newToken
              )
            );
          }),

          catchError(refreshError => {
            expireSession();
            refreshedTokenSubject.next(null);

            return throwError(() => refreshError);
          }),

          finalize(() => {
            refreshInProgress = false;
          })
        );
    };

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (
        error.status === 401
        && !isAuthRequest(request.url)
      ) {
        return refreshAndRetry();
      }

      if (error.status === 403) {
        toast.error('Accès refusé');
      } else if (error.status >= 500) {
        toast.error(
          'Erreur serveur, réessayez plus tard'
        );
      } else if (error.status === 0) {
        toast.error(
          'Erreur réseau — vérifiez votre connexion'
        );
      }

      return throwError(() => error);
    })
  );
};
