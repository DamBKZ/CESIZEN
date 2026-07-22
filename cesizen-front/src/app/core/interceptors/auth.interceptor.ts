import {
  HttpInterceptorFn
} from '@angular/common/http';

import { inject } from '@angular/core';

import {
  UserStore
} from '../stores/user.store';

const SAFE_METHODS = new Set([
  'GET',
  'HEAD',
  'OPTIONS'
]);

const PUBLIC_AUTH_ROUTES = [
  '/auth/login',
  '/auth/refresh',
  '/auth/reset-password/request',
  '/auth/reset-password/confirm'
];

function readCookie(name: string): string | null {
  const prefix = `${encodeURIComponent(name)}=`;

  const cookie = document.cookie
    .split(';')
    .map(value => value.trim())
    .find(value => value.startsWith(prefix));

  if (!cookie) {
    return null;
  }

  const encodedValue = cookie.substring(prefix.length);

  try {
    return decodeURIComponent(encodedValue);
  } catch {
    return encodedValue;
  }
}

function isSameOriginUrl(url: string): boolean {
  if (url.startsWith('/')) {
    return true;
  }

  try {
    return new URL(
      url,
      window.location.origin
    ).origin === window.location.origin;
  } catch {
    return false;
  }
}

function isPublicAuthRoute(url: string): boolean {
  return PUBLIC_AUTH_ROUTES.some(route =>
    url === route
    || url.startsWith(`${route}?`)
  );
}

export const authInterceptor: HttpInterceptorFn = (
  request,
  next
) => {
  const userStore = inject(UserStore);

  /*
   * Ne jamais transmettre les cookies ou le Bearer token
   * à une origine externe.
   */
  if (!isSameOriginUrl(request.url)) {
    return next(request);
  }

  const method = request.method.toUpperCase();
  const isMutatingRequest = !SAFE_METHODS.has(method);

  const token = userStore.token();
  const xsrfToken = isMutatingRequest
    ? readCookie('XSRF-TOKEN')
    : null;

  const headers: Record<string, string> = {};

  if (token && !isPublicAuthRoute(request.url)) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (xsrfToken) {
    headers['X-XSRF-TOKEN'] = xsrfToken;
  }

  const authenticatedRequest = request.clone({
    withCredentials: true,
    setHeaders: headers
  });

  return next(authenticatedRequest);
};
